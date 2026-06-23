// 数据大屏地址配置
// 浏览器访问门户首页时，"数据大屏"按钮将跳转至此地址
// 可通过 cpolar 隧道或内网地址访问 Dashboard
window.DASHBOARD_URL = 'https://ff5241f.r7.cpolar.cn/bigscreen/';

// API 服务器默认地址（浏览器访问时使用）
// App 端会通过 WebViewBridge 注入覆盖此值
window.__API_BASE_URL__ = '/api';