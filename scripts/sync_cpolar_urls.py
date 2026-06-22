#!/usr/bin/env python3
"""
cpolar URL 自动同步脚本
从 cpolar 日志中提取当前隧道 URL，更新 frontend 和 dashboard 的 config.js

用法:
  python scripts/sync_cpolar_urls.py --host <VM_HOST> [--deploy]
"""
import re, sys, os, argparse, subprocess

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def get_tunnel_urls(host):
    """从 VM 的 cpolar 日志中提取隧道 URL 映射"""
    cmd = [
        "ssh", "-o", "StrictHostKeyChecking=no",
        f"root@{host}",
        "cat /var/log/cpolar/access.log | grep 'Tunnel established' | tail -6"
    ]
    result = subprocess.run(cmd, capture_output=True, text=True, timeout=15)
    if result.returncode != 0:
        print(f"SSH 失败: {result.stderr}")
        return None

    # 解析日志，提取 URL -> 端口 映射
    # 格式: Tunnel established at http://xxx.r7.cpolar.cn
    # 需要从 cpolar.yml 获取端口映射，或通过 curl 测试
    urls = re.findall(r'https?://([a-f0-9]+\.r7\.cpolar\.\w+)', result.stdout)
    urls = list(set(urls))  # 去重

    if len(urls) < 3:
        print(f"警告: 只找到 {len(urls)} 个隧道 URL，期望 3 个")
        print(f"找到: {urls}")

    return urls


def classify_urls(host, urls):
    """通过 curl 确定每个 URL 对应哪个服务"""
    mapping = {}
    for url in urls:
        full_url = f"https://{url}"
        try:
            cmd = ["ssh", "-o", "StrictHostKeyChecking=no", f"root@{host}",
                   f"curl -s -k '{full_url}/' 2>&1 | grep -o '<title>[^<]*</title>' | head -1"]
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
            title = result.stdout.strip()
            if "大数据门户平台" in title or "智讯" in title:
                mapping["frontend"] = full_url
            elif "数据大屏" in title:
                mapping["dashboard"] = full_url
            else:
                # API 返回 JSON 或 404
                cmd2 = ["ssh", "-o", "StrictHostKeyChecking=no", f"root@{host}",
                        f"curl -s -k '{full_url}/api/news/channels' 2>&1 | head -1"]
                result2 = subprocess.run(cmd2, capture_output=True, text=True, timeout=10)
                if result2.stdout.strip().startswith('{') or result2.stdout.strip().startswith('['):
                    mapping["backend"] = full_url
        except Exception as e:
            print(f"  检查 {url} 失败: {e}")
    return mapping


def update_config_files(mapping, deploy=False, host=None):
    """更新 config.js 文件"""
    if "frontend" in mapping:
        frontend_config = os.path.join(BASE_DIR, "frontend", "public", "config.js")
        content = f"""// 数据大屏地址配置
// 浏览器访问门户首页时，"数据大屏"按钮将跳转至此地址
// 可通过 cpolar 隧道或内网地址访问 Dashboard
window.DASHBOARD_URL = '{mapping["dashboard"]}';
"""
        with open(frontend_config, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"已更新: {frontend_config} -> DASHBOARD_URL={mapping['dashboard']}")

    if "dashboard" in mapping:
        dashboard_config = os.path.join(BASE_DIR, "dashboard", "public", "config.js")
        content = f"""// Dashboard 主页 URL 配置
// 浏览器访问 dashboard 时，"回到主页"按钮将跳转至此地址
// 可通过 cpolar 前端隧道或内网地址访问门户首页
window.HOME_URL = '{mapping["frontend"]}/';
"""
        with open(dashboard_config, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"已更新: {dashboard_config} -> HOME_URL={mapping['frontend']}/")

    if deploy and host:
        print("\n部署到 VM...")
        # 部署 frontend config
        subprocess.run(["scp", frontend_config, f"root@{host}:/tmp/front_config.js"],
                       capture_output=True, timeout=10)
        subprocess.run(["ssh", "-o", "StrictHostKeyChecking=no", f"root@{host}",
                        "docker cp /tmp/front_config.js bigdata-frontend:/usr/share/nginx/html/config.js"],
                       capture_output=True, timeout=10)
        print("  frontend config 已部署")

        # 部署 dashboard config
        subprocess.run(["scp", dashboard_config, f"root@{host}:/tmp/dash_config.js"],
                       capture_output=True, timeout=10)
        subprocess.run(["ssh", "-o", "StrictHostKeyChecking=no", f"root@{host}",
                        "docker cp /tmp/dash_config.js bigdata-dashboard:/usr/share/nginx/html/config.js"],
                       capture_output=True, timeout=10)
        print("  dashboard config 已部署")


def main():
    parser = argparse.ArgumentParser(description="同步 cpolar 隧道 URL 到配置文件")
    parser.add_argument("--host", default="192.168.146.128", help="VM 主机地址")
    parser.add_argument("--deploy", action="store_true", help="部署到 VM")
    args = parser.parse_args()

    print("正在获取 cpolar 隧道 URL...")
    urls = get_tunnel_urls(args.host)
    if not urls:
        print("未能获取隧道 URL")
        sys.exit(1)

    print(f"找到 {len(urls)} 个隧道: {urls}")
    print("正在分类 URL...")
    mapping = classify_urls(args.host, urls)

    print(f"\n映射结果:")
    for k, v in mapping.items():
        print(f"  {k}: {v}")

    update_config_files(mapping, deploy=args.deploy, host=args.host)
    print("\n完成!")


if __name__ == "__main__":
    main()