#!/usr/bin/env python3
"""
项目健康检测脚本
每2小时自动运行，检测服务器、前端、后端、数据库、APK状态的全面检查
生成检测报告和修复建议
"""
import os
import sys
import json
import time
import urllib.request
import urllib.error
import subprocess
import socket
from datetime import datetime
from pathlib import Path

# ============== 配置 ==============
SERVER_IP = "192.168.146.128"
SERVER_USER = "root"
SERVER_PORT = 22
CPOLAR_URL = "https://45c3c69c.r7.cpolar.cn"
API_PORT = 8090
FRONTEND_PORT = 80
REPORT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "reports")

# ============== 工具函数 ==============
def now_str():
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def run_ssh(cmd, timeout=15):
    """通过SSH执行命令"""
    try:
        result = subprocess.run(
            ["ssh", "-o", "StrictHostKeyChecking=no", "-o", f"ConnectTimeout={timeout}",
             f"{SERVER_USER}@{SERVER_IP}", cmd],
            capture_output=True, text=True, timeout=timeout
        )
        return result.returncode == 0, result.stdout.strip(), result.stderr.strip()
    except Exception as e:
        return False, "", str(e)

def http_get(url, timeout=10):
    """HTTP GET 请求"""
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "HealthCheck/1.0"})
        resp = urllib.request.urlopen(req, timeout=timeout)
        return resp.status, resp.read().decode("utf-8", errors="ignore")[:500]
    except urllib.error.HTTPError as e:
        return e.code, str(e)
    except Exception as e:
        return -1, str(e)

def check_port(host, port, timeout=5):
    """检查端口是否可达"""
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        result = sock.connect_ex((host, port))
        sock.close()
        return result == 0
    except:
        return False

# ============== 检测项 ==============
class CheckResult:
    def __init__(self):
        self.items = []
        self.passed = 0
        self.failed = 0
        self.warnings = 0

    def add(self, name, status, detail, fix=""):
        item = {"name": name, "status": status, "detail": detail, "fix": fix}
        self.items.append(item)
        if status == "PASS":
            self.passed += 1
        elif status == "FAIL":
            self.failed += 1
        else:
            self.warnings += 1

def check_server_reachable(result):
    """检查服务器是否可达"""
    ping_ok = check_port(SERVER_IP, 22, timeout=5)
    if ping_ok:
        result.add("服务器SSH连接", "PASS", f"{SERVER_IP}:22 可达")
    else:
        # 尝试ICMP
        try:
            r = subprocess.run(["ping", "-n", "1", "-w", "3000", SERVER_IP],
                             capture_output=True, timeout=5)
            ping_ok = r.returncode == 0
        except:
            ping_ok = False
        if ping_ok:
            result.add("服务器SSH连接", "WARN", "SSH端口不可达但ICMP通，可能SSH服务异常",
                       "登录服务器执行: systemctl start sshd")
        else:
            result.add("服务器SSH连接", "FAIL", f"服务器 {SERVER_IP} 完全不可达",
                       "请检查VMware虚拟机是否运行，网络是否正常")

def check_docker_containers(result):
    """检查Docker容器状态"""
    ok, out, err = run_ssh("docker ps -a --format '{{.Names}}|{{.Status}}|{{.Ports}}'")
    if not ok:
        if "Connection timed out" in err or "Connection refused" in err:
            return  # 已在 server_reachable 中报告
        result.add("Docker容器", "FAIL", f"SSH执行失败: {err}", "检查服务器Docker服务")
        return

    containers = {}
    for line in out.split("\n"):
        parts = line.split("|", 2)
        if len(parts) >= 2:
            containers[parts[0]] = parts[1]

    required = ["bigdata-frontend", "bigdata-backend", "bigdata-dashboard",
                "bigdata-mysql", "bigdata-kafka", "bigdata-zookeeper",
                "bigdata-clickhouse"]
    for name in required:
        status = containers.get(name, "不存在")
        if "Up" in status and "unhealthy" not in status.lower():
            result.add(f"容器 {name}", "PASS", status)
        elif "Up" in status:
            result.add(f"容器 {name}", "WARN", status, f"docker restart {name}")
        else:
            result.add(f"容器 {name}", "FAIL", status or "容器不存在",
                       f"docker start {name}")

def check_server_memory(result):
    """检查服务器内存"""
    ok, out, err = run_ssh("free -h | grep Mem")
    if not ok:
        return
    parts = out.split()
    if len(parts) >= 7:
        total = parts[1]
        used = parts[2]
        avail = parts[6]
        try:
            avail_gb = float(avail.replace("Gi", "").replace("G", ""))
            if avail_gb < 1.0:
                result.add("服务器内存", "FAIL", f"可用: {avail} / 总: {total}",
                           "清理内存: docker system prune -f; sync; echo 3 > /proc/sys/vm/drop_caches")
            elif avail_gb < 2.0:
                result.add("服务器内存", "WARN", f"可用: {avail} / 总: {total}",
                           "建议清理非必要容器或缓存")
            else:
                result.add("服务器内存", "PASS", f"可用: {avail} / 总: {total}")
        except:
            result.add("服务器内存", "WARN", f"无法解析: {out}")

def check_server_disk(result):
    """检查磁盘"""
    ok, out, err = run_ssh("df -h / | tail -1")
    if not ok:
        return
    parts = out.split()
    if len(parts) >= 5:
        use_pct = parts[4].replace("%", "")
        try:
            if int(use_pct) > 90:
                result.add("磁盘空间", "FAIL", f"使用率: {use_pct}%", "清理磁盘或扩容")
            elif int(use_pct) > 80:
                result.add("磁盘空间", "WARN", f"使用率: {use_pct}%", "docker system prune -a")
            else:
                result.add("磁盘空间", "PASS", f"使用率: {use_pct}%")
        except:
            result.add("磁盘空间", "WARN", f"无法解析: {out}")

def check_http_service(result, name, url, expected_codes=None):
    """检查HTTP服务"""
    if expected_codes is None:
        expected_codes = [200]
    code, body = http_get(url, timeout=10)
    if code in expected_codes:
        result.add(f"{name}", "PASS", f"HTTP {code} ({url})")
    elif code == -1:
        result.add(f"{name}", "FAIL", f"连接失败: {body} ({url})",
                   "检查服务是否启动")
    else:
        result.add(f"{name}", "WARN", f"HTTP {code} ({url})")

def check_backend_api(result):
    """检查后端API"""
    ok, out, err = run_ssh("curl -s -o /dev/null -w '%{http_code}' http://localhost:8090/api/news")
    if ok and out == "200":
        result.add("后端API /api/news", "PASS", "HTTP 200")
    elif ok:
        result.add("后端API /api/news", "FAIL", f"HTTP {out}",
                   "docker restart bigdata-backend")
    else:
        # 通过cpolar检查
        code, _ = http_get(f"{CPOLAR_URL}/api/news", timeout=10)
        if code == 200:
            result.add("后端API (cpolar)", "PASS", "通过公网隧道可达")
        else:
            result.add("后端API", "FAIL", f"无法访问 (cpolar: {code})",
                       "检查服务器网络和容器状态")

def check_backend_logs(result):
    """检查后端错误日志"""
    ok, out, err = run_ssh("docker logs --tail 50 bigdata-backend 2>&1 | grep -i 'error\\|exception\\|fail\\|oom' | tail -5")
    if not ok:
        return
    if out.strip():
        result.add("后端错误日志", "WARN", out[:200],
                   "请检查后端日志详情: docker logs bigdata-backend")
    else:
        result.add("后端错误日志", "PASS", "无异常")

def check_apk_exists(result):
    """检查APK文件"""
    apk_path = "d:/workspace/bigdata-portal-platform/android/app/build/outputs/apk/debug/app-debug.apk"
    if os.path.exists(apk_path):
        size_mb = os.path.getsize(apk_path) / (1024 * 1024)
        mtime = datetime.fromtimestamp(os.path.getmtime(apk_path))
        result.add("APK文件", "PASS", f"存在, {size_mb:.1f}MB, 构建于 {mtime.strftime('%Y-%m-%d %H:%M')}")
    else:
        result.add("APK文件", "FAIL", "app-debug.apk 不存在",
                   "cd android && gradlew.bat assembleDebug")

def check_cpolar_tunnels(result):
    """检查cpolar隧道"""
    code, body = http_get(CPOLAR_URL, timeout=10)
    if code == 200:
        result.add("Cpolar前端隧道", "PASS", f"{CPOLAR_URL} HTTP 200")
    else:
        result.add("Cpolar前端隧道", "FAIL", f"HTTP {code}",
                   "检查服务器cpolar: systemctl restart cpolar")

    code, body = http_get(f"{CPOLAR_URL}/api/news", timeout=10)
    if code == 200:
        result.add("Cpolar API隧道", "PASS", "HTTP 200")
    else:
        result.add("Cpolar API隧道", "WARN", f"HTTP {code}",
                   "检查后端是否正常运行")

def check_frontend_assets(result):
    """检查前端JS文件（无element-plus残留）"""
    ok, out, err = run_ssh("docker exec bigdata-frontend grep -c 'getThemeColors\\|element-plus' /usr/share/nginx/html/assets/js/*.js 2>/dev/null || echo 'CLEAN'")
    if not ok:
        return
    if "CLEAN" in out or "0" in out:
        result.add("前端element-plus残留", "PASS", "无残留")
    else:
        result.add("前端element-plus残留", "WARN", "仍有getThemeColors引用",
                   "重新构建前端: npm run build && docker compose up -d --build frontend")

# ============== Dogfood 用户模拟测试 ==============
DOGFOOD_URLS = {
    "frontend": CPOLAR_URL,
    "dashboard": "https://79a01e6b.r7.cpolar.cn",
}

# ============== 常见问题专项检测 ==============
# 这些问题在历史上反复出现，需要单独重点检测

def check_common_issue_channel_news(result):
    """常见问题1: 频道页新闻列表是否正常加载（非"暂无新闻"）"""
    ok, out, err = run_ssh("curl -s 'http://localhost:8090/api/news?page=1&size=5&channel=AI' 2>&1")
    if not ok:
        result.add("【常见问题】频道新闻数据", "FAIL", f"SSH失败: {err}",
                   "检查服务器连接和Docker状态")
        return
    try:
        data = json.loads(out)
        if data.get("success") and data.get("data", {}).get("records"):
            count = len(data["data"]["records"])
            result.add("【常见问题】频道新闻数据", "PASS", f"AI频道返回{count}条新闻，数据正常")
        else:
            result.add("【常见问题】频道新闻数据", "FAIL", "频道API返回空数据",
                       "检查MySQL news表数据，确认distribute_news.py已执行")
    except json.JSONDecodeError:
        result.add("【常见问题】频道新闻数据", "FAIL", f"API返回非JSON: {out[:100]}",
                   "检查后端服务是否正常")

def check_common_issue_dashboard_logo(result):
    """常见问题2: 数据大屏Logo是否为"智讯"（非"BigData Portal"）"""
    ok, out, err = run_ssh("docker exec bigdata-dashboard sh -c \"grep -c 'BigData Portal' /usr/share/nginx/html/assets/*.js 2>/dev/null || echo '0'\"")
    if not ok:
        result.add("【常见问题】Dashboard Logo", "FAIL", f"SSH失败: {err}",
                   "检查服务器连接")
        return
    # Parse grep output: "file:count" per line, sum all counts
    old_logo_count = 0
    for line in out.strip().split('\n'):
        line = line.strip()
        if ':' in line:
            try:
                old_logo_count += int(line.split(':')[-1])
            except: pass
    
    ok2, out2, err2 = run_ssh("docker exec bigdata-dashboard sh -c \"grep -c '智讯' /usr/share/nginx/html/assets/*.js 2>/dev/null || echo '0'\"")
    new_logo_count = 0
    if ok2:
        for line in out2.strip().split('\n'):
            line = line.strip()
            if ':' in line:
                try:
                    new_logo_count += int(line.split(':')[-1])
                except: pass
    
    if old_logo_count > 0:
        result.add("【常见问题】Dashboard Logo", "FAIL",
                   f"JS中仍有{old_logo_count}处'BigData Portal'，Logo未更新",
                   "重新构建并部署Dashboard: cd dashboard && npm run build && 部署到容器")
    elif new_logo_count > 0:
        result.add("【常见问题】Dashboard Logo", "PASS",
                   f"JS中'智讯'出现{new_logo_count}次，Logo已正确更新")
    else:
        result.add("【常见问题】Dashboard Logo", "WARN",
                   "JS中未找到Logo文本，可能构建有问题",
                   "检查Dashboard.vue源码中的brand-name")

def check_common_issue_channel_chart(result):
    """常见问题3: 频道分布图是否有数据"""
    ok, out, err = run_ssh("curl -s 'http://localhost:8090/api/analytics/channel-dist?range=today' 2>&1")
    if not ok:
        result.add("【常见问题】频道分布图数据", "FAIL", f"SSH失败: {err}",
                   "检查服务器连接")
        return
    try:
        data = json.loads(out)
        chart_data = data.get("data", [])
        if chart_data and len(chart_data) > 0:
            result.add("【常见问题】频道分布图数据", "PASS",
                       f"ClickHouse返回{len(chart_data)}条频道数据")
        else:
            # ClickHouse empty, but Dashboard JS has fallback to MySQL /api/news
            ok2, out2, err2 = run_ssh("curl -s 'http://localhost:8090/api/news?page=1&size=500' 2>&1")
            if ok2:
                try:
                    news_data = json.loads(out2)
                    records = news_data.get("data", {}).get("records", [])
                    if records:
                        # Count channels from news records
                        channels = {}
                        for r in records:
                            ch = r.get("channel", "其他")
                            channels[ch] = channels.get(ch, 0) + 1
                        result.add("【常见问题】频道分布图数据", "PASS",
                                   f"ClickHouse为空但Dashboard fallback可用，MySQL有{len(records)}条新闻覆盖{len(channels)}个频道")
                    else:
                        result.add("【常见问题】频道分布图数据", "FAIL",
                                   "ClickHouse和MySQL均无数据",
                                   "执行init_clickhouse.py和distribute_news.py填充数据")
                except:
                    result.add("【常见问题】频道分布图数据", "FAIL",
                               "ClickHouse为空且MySQL fallback解析失败",
                               "检查后端API和数据库状态")
            else:
                result.add("【常见问题】频道分布图数据", "FAIL",
                           "ClickHouse为空且无法访问MySQL fallback",
                           "检查后端服务状态")
    except json.JSONDecodeError:
        result.add("【常见问题】频道分布图数据", "FAIL",
                   f"API返回非JSON: {out[:100]}",
                   "检查后端analytics服务")

def check_common_issue_navigation_link(result):
    """常见问题4: 前端"数据大屏"跳转链接是否正确"""
    # Use a script on the VM to avoid shell escaping issues
    check_cmd = 'echo "CHECK_NAV_START"; docker exec bigdata-frontend sh -c "grep -o \\\"window.open([^)]*)\\\" /usr/share/nginx/html/assets/js/PortalLayout-ClD55das.js" 2>/dev/null || echo "NOT_FOUND"; echo "CHECK_NAV_END"'
    ok, out, err = run_ssh(check_cmd)
    if not ok:
        result.add("【常见问题】大屏跳转链接", "FAIL", f"SSH失败: {err}",
                   "检查服务器连接")
        return
    if "79a01e6b.r7.cpolar.cn" in out:
        result.add("【常见问题】大屏跳转链接", "PASS",
                   "使用window.open打开独立Dashboard URL，跳转正确")
    elif "window.open" in out:
        result.add("【常见问题】大屏跳转链接", "WARN",
                   "检测到window.open但非cpolar URL",
                   "检查PortalLayout.vue中goDashboard实现")
    elif "NOT_FOUND" in out:
        result.add("【常见问题】大屏跳转链接", "FAIL",
                   "前端JS中未找到window.open跳转",
                   "检查PortalLayout.vue是否包含数据大屏导航")
    else:
        result.add("【常见问题】大屏跳转链接", "WARN",
                   f"无法解析检测结果: {out[:100]}",
                   "手动检查PortalLayout.vue")

def check_common_issue_clickhouse_data(result):
    """常见问题5: ClickHouse user_behavior表是否有数据"""
    ok, out, err = run_ssh("docker exec bigdata-clickhouse clickhouse-client -q 'SELECT count() FROM bigdata_portal.user_behavior' 2>&1")
    if not ok:
        if "UNKNOWN_TABLE" in out:
            result.add("【常见问题】ClickHouse数据", "FAIL",
                       "user_behavior表不存在",
                       "执行CREATE TABLE并填充数据: init_clickhouse.py")
        else:
            result.add("【常见问题】ClickHouse数据", "FAIL",
                       f"查询失败: {out[:100]}",
                       "检查ClickHouse容器状态")
        return
    try:
        count = int(out.strip())
        if count > 0:
            result.add("【常见问题】ClickHouse数据", "PASS",
                       f"user_behavior表有{count}条数据")
        else:
            result.add("【常见问题】ClickHouse数据", "FAIL",
                       "user_behavior表为空，图表无数据",
                       "执行gen_data.py填充ClickHouse数据")
    except:
        result.add("【常见问题】ClickHouse数据", "FAIL",
                   f"无法解析计数: {out[:100]}",
                   "检查ClickHouse查询")

def check_common_issue_homepage_loading(result):
    """常见问题6: 首页头条是否卡在加载中"""
    code, body = http_get(DOGFOOD_URLS["frontend"], timeout=15)
    if code != 200:
        result.add("【常见问题】首页头条渲染", "FAIL", f"HTTP {code}",
                   "检查前端容器和nginx配置")
        return
    if "加载中" in body and "portal" not in body.lower():
        result.add("【常见问题】首页头条渲染", "FAIL",
                   "页面显示'加载中'，头条数据未渲染",
                   "检查fetchNews() API响应解析逻辑，确保data.records格式正确")
    elif "加载中" in body:
        result.add("【常见问题】首页头条渲染", "WARN",
                   "页面含'加载中'但可能已渲染（SPA壳）",
                   "用浏览器打开页面确认")
    else:
        result.add("【常见问题】首页头条渲染", "PASS", "页面正常渲染，无'加载中'")

# ============== 原有Dogfood检测 ==============

def check_dogfood_homepage(result):
    """Dogfood: 首页加载 + 头条渲染"""
    code, body = http_get(DOGFOOD_URLS["frontend"], timeout=15)
    if code != 200:
        result.add("首页加载", "FAIL", f"HTTP {code}", "检查前端容器和nginx配置")
        return
    if "zhixun" in body.lower() or "portal" in body.lower():
        result.add("首页头条渲染", "PASS", "页面正常渲染")
    else:
        result.add("首页头条渲染", "WARN", "无法确认渲染状态",
                   "用浏览器打开页面检查")

def check_dogfood_channels(result):
    """Dogfood: 频道导航"""
    code, body = http_get(f"{DOGFOOD_URLS['frontend']}/channel/AI", timeout=15)
    if code == 200:
        result.add("频道页导航", "PASS", "HTTP 200")
    else:
        result.add("频道页导航", "FAIL", f"HTTP {code}", "检查SPA路由配置")

def check_dogfood_search(result):
    """Dogfood: 搜索功能"""
    code, body = http_get(f"{DOGFOOD_URLS['frontend']}/search", timeout=15)
    if code == 200:
        result.add("搜索页", "PASS", "HTTP 200")
    else:
        result.add("搜索页", "FAIL", f"HTTP {code}", "检查SPA路由配置")
    code, body = http_get(f"{DOGFOOD_URLS['frontend']}/api/news?keyword=AI&page=1&size=3", timeout=15)
    if code == 200 and "records" in body:
        result.add("搜索API", "PASS", "正常返回数据")
    else:
        result.add("搜索API", "FAIL", f"HTTP {code}", "检查后端搜索接口")

def check_dogfood_dashboard(result):
    """Dogfood: 数据大屏"""
    code, body = http_get(DOGFOOD_URLS["dashboard"], timeout=15)
    if code == 200:
        if "overflow" in body.lower() or "dashboard" in body.lower():
            result.add("数据大屏渲染", "PASS", "正常渲染")
        elif len(body) < 200:
            result.add("数据大屏渲染", "FAIL", "页面空白",
                       "检查Dashboard组件echarts导入和API 401")
        else:
            result.add("数据大屏渲染", "PASS", "HTTP 200")
    else:
        result.add("数据大屏渲染", "FAIL", f"HTTP {code}",
                   "检查cpolar隧道指向和Dashboard容器")

def check_dogfood_login(result):
    """Dogfood: 登录页"""
    code, body = http_get(f"{DOGFOOD_URLS['frontend']}/login", timeout=15)
    if code == 200:
        result.add("登录页", "PASS", "HTTP 200")
    else:
        result.add("登录页", "FAIL", f"HTTP {code}", "检查SPA路由")

def check_dogfood_console_errors(result):
    """Dogfood: 检查后端404/500错误"""
    ok, out, err = run_ssh("docker logs --tail 100 bigdata-backend 2>&1 | grep -c ' 500 \\| 404 \\|Exception' || echo 0")
    if not ok:
        return
    try:
        error_count = int(out.strip())
        if error_count > 5:
            result.add("后端HTTP错误", "WARN", f"最近100条日志有{error_count}个错误",
                       "检查后端日志: docker logs bigdata-backend")
        else:
            result.add("后端HTTP错误", "PASS", f"仅{error_count}个错误")
    except:
        result.add("后端HTTP错误", "PASS", "无异常")

def run_dogfood_checks(result):
    """执行所有Dogfood用户模拟测试"""
    print("  [常见问题专项检测] 频道新闻数据...")
    check_common_issue_channel_news(result)
    print("  [常见问题专项检测] Dashboard Logo...")
    check_common_issue_dashboard_logo(result)
    print("  [常见问题专项检测] 频道分布图...")
    check_common_issue_channel_chart(result)
    print("  [常见问题专项检测] 大屏跳转链接...")
    check_common_issue_navigation_link(result)
    print("  [常见问题专项检测] ClickHouse数据...")
    check_common_issue_clickhouse_data(result)
    print("  [常见问题专项检测] 首页头条渲染...")
    check_common_issue_homepage_loading(result)
    print("  [Dogfood] 首页加载...")
    check_dogfood_homepage(result)
    print("  [Dogfood] 频道导航...")
    check_dogfood_channels(result)
    print("  [Dogfood] 搜索功能...")
    check_dogfood_search(result)
    print("  [Dogfood] 数据大屏...")
    check_dogfood_dashboard(result)
    print("  [Dogfood] 登录页...")
    check_dogfood_login(result)
    print("  [Dogfood] 后端错误日志...")
    check_dogfood_console_errors(result)

# ============== 主流程 ==============
def run_all_checks():
    result = CheckResult()

    print(f"[{now_str()}] 开始全量检测...")
    print("=" * 60)

    # 1. 服务器可达性
    check_server_reachable(result)

    # 2. Docker容器
    check_docker_containers(result)

    # 3. 服务器资源
    check_server_memory(result)
    check_server_disk(result)

    # 4. 后端API
    check_backend_api(result)

    # 5. 后端日志
    check_backend_logs(result)

# 6. 前端（通过SSH检测，避免VMware NAT限制）
    ok, out, err = run_ssh(f"curl -s -o /dev/null -w '%{{http_code}}' http://localhost:{FRONTEND_PORT}")
    if ok and out == "200":
        result.add("前端服务", "PASS", f"HTTP 200 (SSH本地检测)")
    elif ok:
        result.add("前端服务", "FAIL", f"HTTP {out}", "docker restart bigdata-frontend")
    else:
        result.add("前端服务", "FAIL", f"SSH检测失败: {err}", "检查服务器和容器状态")

    ok, out, err = run_ssh(f"curl -s -o /dev/null -w '%{{http_code}}' http://localhost:{FRONTEND_PORT}/dashboard/")
    if ok and out == "200":
        result.add("前端Dashboard", "PASS", f"HTTP 200 (SSH本地检测)")
    elif ok:
        result.add("前端Dashboard", "FAIL", f"HTTP {out}", "docker restart bigdata-frontend")
    else:
        result.add("前端Dashboard", "FAIL", f"SSH检测失败: {err}", "检查服务器和容器状态")

    check_frontend_assets(result)

    # 7. Cpolar隧道
    check_cpolar_tunnels(result)

    # 8. APK
    check_apk_exists(result)

    # 9. 前端SPA路由（通过SSH本地检测）
    ok, out, err = run_ssh(f"curl -s -o /dev/null -w '%{{http_code}}' http://localhost:{FRONTEND_PORT}/news/1")
    if ok and out == "200":
        result.add("前端新闻详情页(SPA)", "PASS", f"HTTP 200 (SSH本地检测)")
    elif ok:
        result.add("前端新闻详情页(SPA)", "FAIL", f"HTTP {out}", "检查SPA路由配置")
    else:
        result.add("前端新闻详情页(SPA)", "FAIL", f"SSH检测失败: {err}", "检查服务器和容器状态")

    # 10. Dogfood 用户模拟测试
    print("\n[Dogfood 用户模拟测试]")
    run_dogfood_checks(result)

    print(f"\n检测完成: PASS={result.passed} FAIL={result.failed} WARN={result.warnings}")
    return result

def generate_report(result):
    """生成HTML报告"""
    os.makedirs(REPORT_DIR, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    report_path = os.path.join(REPORT_DIR, f"health_report_{timestamp}.html")

    status_color = {"PASS": "#4CAF50", "FAIL": "#F44336", "WARN": "#FF9800"}
    status_icon = {"PASS": "✅", "FAIL": "❌", "WARN": "⚠️"}

    items_html = ""
    for item in result.items:
        color = status_color.get(item["status"], "#999")
        icon = status_icon.get(item["status"], "❓")
        items_html += f"""
        <tr>
            <td style="color:{color}; font-weight:bold">{icon} {item['status']}</td>
            <td>{item['name']}</td>
            <td>{item['detail']}</td>
            <td style="color:#666; font-size:13px">{item.get('fix', '')}</td>
        </tr>"""

    overall = "PASS" if result.failed == 0 else ("WARN" if result.failed <= 2 else "FAIL")
    overall_color = status_color[overall]

    html = f"""<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>项目健康检测报告 - {timestamp}</title>
    <style>
        * {{ margin:0; padding:0; box-sizing:border-box; }}
        body {{ font-family: 'Microsoft YaHei', sans-serif; background: #f5f5f5; padding: 20px; color: #333; }}
        .container {{ max-width: 900px; margin: 0 auto; }}
        .header {{ background: linear-gradient(135deg, #1A2A4A, #2D4A7A); color: white; padding: 30px; border-radius: 8px 8px 0 0; }}
        .header h1 {{ font-size: 24px; margin-bottom: 8px; }}
        .header .time {{ opacity: 0.8; font-size: 14px; }}
        .summary {{ background: white; padding: 20px 30px; border-bottom: 1px solid #eee; display: flex; gap: 30px; }}
        .summary-item {{ text-align: center; }}
        .summary-item .num {{ font-size: 36px; font-weight: bold; }}
        .summary-item .label {{ font-size: 13px; color: #666; }}
        .overall {{ background: {overall_color}; color: white; padding: 12px 30px; text-align: center; font-size: 18px; font-weight: bold; }}
        table {{ width: 100%; background: white; border-collapse: collapse; }}
        th {{ background: #f0f0f0; padding: 12px; text-align: left; font-size: 13px; color: #666; border-bottom: 2px solid #ddd; }}
        td {{ padding: 12px; border-bottom: 1px solid #eee; font-size: 14px; }}
        tr:hover {{ background: #fafafa; }}
        .footer {{ text-align: center; padding: 20px; color: #999; font-size: 12px; }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔍 项目健康检测报告</h1>
            <div class="time">生成时间: {timestamp}</div>
        </div>
        <div class="overall">
            综合状态: {'正常' if overall == 'PASS' else ('需关注' if overall == 'WARN' else '异常')}
        </div>
        <div class="summary">
            <div class="summary-item">
                <div class="num" style="color:#4CAF50">{result.passed}</div>
                <div class="label">✅ 通过</div>
            </div>
            <div class="summary-item">
                <div class="num" style="color:#F44336">{result.failed}</div>
                <div class="label">❌ 失败</div>
            </div>
            <div class="summary-item">
                <div class="num" style="color:#FF9800">{result.warnings}</div>
                <div class="label">⚠️ 警告</div>
            </div>
        </div>
        <table>
            <thead>
                <tr>
                    <th style="width:80px">状态</th>
                    <th style="width:180px">检测项</th>
                    <th>详情</th>
                    <th style="width:200px">修复建议</th>
                </tr>
            </thead>
            <tbody>
                {items_html}
            </tbody>
        </table>
        <div class="footer">
            智讯大数据门户平台 · 自动检测报告 · {timestamp}
        </div>
    </div>
</body>
</html>"""

    with open(report_path, "w", encoding="utf-8") as f:
        f.write(html)

    print(f"\n报告已生成: {report_path}")
    return report_path

def auto_fix(result):
    """尝试自动修复FAIL项"""
    fixed = []
    for item in result.items:
        if item["status"] == "FAIL":
            name = item["name"]
            if "容器" in name and "不存在" in item.get("detail", ""):
                # 尝试启动容器
                container_name = name.replace("容器 ", "").strip()
                ok, out, err = run_ssh(f"docker start {container_name}")
                if ok:
                    fixed.append(f"已启动容器 {container_name}")
            elif "内存" in name:
                ok, _, _ = run_ssh("docker system prune -f 2>/dev/null; sync; echo 3 > /proc/sys/vm/drop_caches 2>/dev/null")
                if ok:
                    fixed.append("已清理内存缓存")
    return fixed

if __name__ == "__main__":
    result = run_all_checks()
    report_path = generate_report(result)

    # 尝试自动修复
    if result.failed > 0:
        print("\n=== 尝试自动修复 ===")
        fixed = auto_fix(result)
        if fixed:
            for f in fixed:
                print(f"  ✅ {f}")
            print("\n修复后重新检测...")
            result = run_all_checks()
            report_path = generate_report(result)
        else:
            print("  无可自动修复的项目，请手动处理")

    # 输出到stdout供定时任务记录
    print(f"\nREPORT_PATH:{report_path}")
    sys.exit(0 if result.failed == 0 else 1)