const PptxGenJS = require("pptxgenjs");

const pptx = new PptxGenJS();
pptx.layout = "LAYOUT_WIDE";
pptx.author = "智讯团队";
pptx.title = "智讯 - 大数据实时科技资讯聚合平台";

// 主题色
const COLORS = {
  primary: "1A2A4A",    // 深藏蓝（网站抬头色）
  secondary: "2D4A7A",  // 中蓝
  accent: "4FACFE",     // 亮蓝
  accent2: "00F2FE",    // 青蓝
  white: "FFFFFF",
  light: "F0F4F8",
  gray: "64748B",
  dark: "0F172A",
  red: "C41230",
  green: "10B981",
  orange: "F59E0B",
  purple: "7C3AED",
};

// 字体
const FONT_TITLE = "微软雅黑";
const FONT_BODY = "微软雅黑";
const FONT_CODE = "Consolas";

// 辅助函数
function addBg(slide, color = COLORS.primary) {
  slide.addShape(pptx.ShapeType.rect, {
    x: 0, y: 0, w: "100%", h: "100%",
    fill: { color },
  });
}

function addAccentBar(slide, y = 0, h = 0.06) {
  slide.addShape(pptx.ShapeType.rect, {
    x: 0, y, w: "100%", h,
    fill: { color: COLORS.accent },
  });
}

function addSideBar(slide) {
  slide.addShape(pptx.ShapeType.rect, {
    x: 0, y: 0, w: 0.5, h: "100%",
    fill: { color: COLORS.primary },
  });
}

// ========== Slide 1: 封面 ==========
const slide1 = pptx.addSlide();
addBg(slide1, COLORS.primary);

// 顶部装饰线
slide1.addShape(pptx.ShapeType.rect, {
  x: 0, y: 0, w: "100%", h: 0.04,
  fill: { color: COLORS.accent },
});

// 闪电图标区域
slide1.addShape(pptx.ShapeType.rect, {
  x: 4.5, y: 1.2, w: 5, h: 0.04,
  fill: { color: COLORS.accent },
  line: { color: COLORS.accent, width: 0 },
});

// 主标题
slide1.addText("智  讯", {
  x: 1, y: 1.5, w: 12, h: 1.8,
  fontSize: 60, fontFace: FONT_TITLE,
  color: COLORS.white,
  bold: true,
  align: "center",
});

// 英文副标题
slide1.addText("Zhixun Big Data Portal Platform", {
  x: 1, y: 3.2, w: 12, h: 0.8,
  fontSize: 22, fontFace: FONT_BODY,
  color: COLORS.accent,
  align: "center",
});

// 分隔线
slide1.addShape(pptx.ShapeType.rect, {
  x: 5, y: 4.2, w: 4, h: 0.03,
  fill: { color: COLORS.accent },
});

// 描述
slide1.addText("大数据实时科技资讯聚合平台", {
  x: 1, y: 4.5, w: 12, h: 0.8,
  fontSize: 24, fontFace: FONT_BODY,
  color: COLORS.white,
  align: "center",
});

// 底部信息
slide1.addText("项目设计报告  |  2026年6月", {
  x: 1, y: 6.5, w: 12, h: 0.5,
  fontSize: 14, fontFace: FONT_BODY,
  color: COLORS.gray,
  align: "center",
});

// 底部装饰线
slide1.addShape(pptx.ShapeType.rect, {
  x: 0, y: 7.46, w: "100%", h: 0.04,
  fill: { color: COLORS.accent },
});

// ========== Slide 2: 目录 ==========
const slide2 = pptx.addSlide();
addSideBar(slide2);

slide2.addText("目  录", {
  x: 1, y: 0.4, w: 5, h: 1,
  fontSize: 36, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});

slide2.addShape(pptx.ShapeType.rect, {
  x: 1, y: 1.3, w: 2, h: 0.04,
  fill: { color: COLORS.accent },
});

const tocItems = [
  { num: "01", title: "项目概述", desc: "背景、目标、技术选型" },
  { num: "02", title: "系统架构设计", desc: "总体架构、模块划分、数据流" },
  { num: "03", title: "数据库设计", desc: "MySQL、ClickHouse、Kudu" },
  { num: "04", title: "功能模块设计", desc: "用户端、后台管理、大数据、移动端" },
  { num: "05", title: "核心代码实现", desc: "后端、前端、大数据" },
  { num: "06", title: "系统测试", desc: "功能、性能、兼容性" },
  { num: "07", title: "总结与展望", desc: "项目成果、未来规划" },
];

tocItems.forEach((item, i) => {
  const y = 1.8 + i * 0.75;
  slide2.addText(item.num, {
    x: 1.2, y, w: 0.8, h: 0.6,
    fontSize: 22, fontFace: FONT_TITLE,
    color: COLORS.accent, bold: true,
  });
  slide2.addText(item.title, {
    x: 2.2, y, w: 3, h: 0.35,
    fontSize: 18, fontFace: FONT_TITLE,
    color: COLORS.dark, bold: true,
  });
  slide2.addText(item.desc, {
    x: 2.2, y: y + 0.3, w: 5, h: 0.3,
    fontSize: 12, fontFace: FONT_BODY,
    color: COLORS.gray,
  });
});

// ========== Slide 3: 项目背景 ==========
const slide3 = pptx.addSlide();
addSideBar(slide3);

slide3.addText("01  项目概述", {
  x: 1, y: 0.4, w: 5, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide3, 1.15, 0.04);

slide3.addText("项目背景", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const bgPoints = [
  "信息爆炸时代，科技资讯分散在各大网站，用户需要频繁切换多个平台获取信息",
  "传统资讯平台缺乏智能化处理，无法实现个性化推荐和智能摘要",
  "大数据技术日趋成熟，为实时采集、智能分析提供了技术基础",
  "移动端需求增长，需要多端适配（Web、Android、HarmonyOS）",
];

bgPoints.forEach((text, i) => {
  const y = 2.3 + i * 0.85;
  slide3.addShape(pptx.ShapeType.ellipse, {
    x: 1.2, y: y + 0.08, w: 0.25, h: 0.25,
    fill: { color: COLORS.accent },
  });
  slide3.addText(text, {
    x: 1.7, y, w: 11, h: 0.7,
    fontSize: 14, fontFace: FONT_BODY,
    color: COLORS.dark,
    valign: "top",
  });
});

// ========== Slide 4: 项目目标 ==========
const slide4 = pptx.addSlide();
addSideBar(slide4);

slide4.addText("01  项目概述", {
  x: 1, y: 0.4, w: 5, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide4, 1.15, 0.04);

slide4.addText("项目目标", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const goals = [
  { icon: "📡", title: "多源聚合", desc: "整合10+科技频道资讯\nRSS实时采集" },
  { icon: "🧠", title: "AI赋能", desc: "NVIDIA NIM API\n智能摘要生成" },
  { icon: "📊", title: "大数据分析", desc: "Kafka+Flink实时计算\nClickHouse即席查询" },
  { icon: "📱", title: "多端适配", desc: "Web + Android\n+ HarmonyOS" },
];

goals.forEach((g, i) => {
  const x = 1 + i * 3.2;
  slide4.addShape(pptx.ShapeType.roundRect, {
    x, y: 2.3, w: 2.8, h: 3.5,
    fill: { color: COLORS.light },
    rectRadius: 0.15,
  });
  slide4.addText(g.icon, {
    x, y: 2.6, w: 2.8, h: 1,
    fontSize: 36, align: "center",
  });
  slide4.addText(g.title, {
    x, y: 3.5, w: 2.8, h: 0.6,
    fontSize: 18, fontFace: FONT_TITLE,
    color: COLORS.primary, bold: true,
    align: "center",
  });
  slide4.addText(g.desc, {
    x: x + 0.2, y: 4.2, w: 2.4, h: 1.2,
    fontSize: 12, fontFace: FONT_BODY,
    color: COLORS.gray,
    align: "center",
    lineSpacingMultiple: 1.5,
  });
});

// ========== Slide 5: 技术选型 ==========
const slide5 = pptx.addSlide();
addSideBar(slide5);

slide5.addText("01  项目概述", {
  x: 1, y: 0.4, w: 5, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide5, 1.15, 0.04);

slide5.addText("技术选型", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const techRows = [
  ["展示层", "Vue3 + Vite / Element Plus / Kotlin / ArkTS", "多端适配"],
  ["服务层", "Spring Boot 2.7 + MyBatis + JWT", "多模块Maven架构"],
  ["数据层", "MySQL 8.0 / ClickHouse / Kudu", "业务+分析双存储"],
  ["消息层", "Kafka + Zookeeper", "行为数据流"],
  ["计算层", "Flink", "实时流式处理"],
  ["AI层", "NVIDIA NIM API", "智能摘要生成"],
  ["部署层", "Docker Compose + Nginx", "一键容器化部署"],
];

const tableRows = [
  [
    { text: "层次", options: { bold: true, color: COLORS.white, fill: { color: COLORS.primary }, fontSize: 12, fontFace: FONT_TITLE } },
    { text: "技术栈", options: { bold: true, color: COLORS.white, fill: { color: COLORS.primary }, fontSize: 12, fontFace: FONT_TITLE } },
    { text: "说明", options: { bold: true, color: COLORS.white, fill: { color: COLORS.primary }, fontSize: 12, fontFace: FONT_TITLE } },
  ],
  ...techRows.map((row, i) =>
    row.map((cell, j) => ({
      text: cell,
      options: {
        fontSize: 11, fontFace: FONT_BODY,
        color: COLORS.dark,
        fill: { color: i % 2 === 0 ? COLORS.light : COLORS.white },
      },
    }))
  ),
];

slide5.addTable(tableRows, {
  x: 1, y: 2.2, w: 12,
  colW: [1.8, 6.5, 3.7],
  border: { pt: 0.5, color: "CBD5E1" },
  rowH: 0.55,
});

// ========== Slide 6: 系统架构 ==========
const slide6 = pptx.addSlide();
addSideBar(slide6);

slide6.addText("02  系统架构设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide6, 1.15, 0.04);

slide6.addText("总体架构", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

// 四层架构
const layers = [
  { name: "展示层", items: "Vue3前端  |  Android  |  HarmonyOS  |  Admin后台", color: COLORS.accent, y: 2.2 },
  { name: "服务层", items: "Spring Boot  |  MyBatis  |  JWT  |  AOP  |  多模块Maven", color: COLORS.secondary, y: 3.3 },
  { name: "数据层", items: "MySQL  |  ClickHouse  |  Kudu  |  Redis缓存", color: COLORS.primary, y: 4.4 },
  { name: "基础设施", items: "Kafka  |  Flink  |  Zookeeper  |  Docker  |  Nginx", color: COLORS.dark, y: 5.5 },
];

layers.forEach((layer) => {
  slide6.addShape(pptx.ShapeType.roundRect, {
    x: 1, y: layer.y, w: 12, h: 0.9,
    fill: { color: layer.color },
    rectRadius: 0.1,
  });
  slide6.addText(layer.name, {
    x: 1.3, y: layer.y, w: 2, h: 0.9,
    fontSize: 16, fontFace: FONT_TITLE,
    color: COLORS.white, bold: true,
    valign: "middle",
  });
  slide6.addText(layer.items, {
    x: 3.5, y: layer.y, w: 9, h: 0.9,
    fontSize: 13, fontFace: FONT_BODY,
    color: COLORS.white,
    valign: "middle",
  });
});

// 箭头连接
[3.1, 4.2, 5.3].forEach(arrowY => {
  slide6.addShape(pptx.ShapeType.downArrow, {
    x: 6.8, y: arrowY, w: 0.4, h: 0.2,
    fill: { color: COLORS.accent },
  });
});

// ========== Slide 7: 后端模块架构 ==========
const slide7 = pptx.addSlide();
addSideBar(slide7);

slide7.addText("02  系统架构设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide7, 1.15, 0.04);

slide7.addText("后端多模块架构", {
  x: 1, y: 1.5, w: 6, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const modules = [
  { name: "common", desc: "公共工具\n异常/响应体", color: COLORS.accent },
  { name: "framework", desc: "框架层\nJWT/Security/AOP", color: COLORS.secondary },
  { name: "system", desc: "系统管理\n用户/角色/菜单", color: COLORS.primary },
  { name: "cms", desc: "内容运营\n文章/频道", color: COLORS.purple },
  { name: "community", desc: "用户社区\n评论/收藏", color: COLORS.green },
  { name: "ai", desc: "AI智能\nNVIDIA API", color: COLORS.orange },
  { name: "statistics", desc: "数据统计\nClickHouse", color: COLORS.red },
  { name: "application", desc: "启动模块\n配置入口", color: COLORS.dark },
];

modules.forEach((m, i) => {
  const col = i % 4;
  const row = Math.floor(i / 4);
  const x = 1 + col * 3.1;
  const y = 2.3 + row * 2.6;

  slide7.addShape(pptx.ShapeType.roundRect, {
    x, y, w: 2.8, h: 2.2,
    fill: { color: m.color },
    rectRadius: 0.12,
  });
  slide7.addText(m.name, {
    x, y: y + 0.3, w: 2.8, h: 0.6,
    fontSize: 16, fontFace: FONT_CODE,
    color: COLORS.white, bold: true,
    align: "center",
  });
  slide7.addText(m.desc, {
    x, y: y + 1, w: 2.8, h: 1,
    fontSize: 11, fontFace: FONT_BODY,
    color: COLORS.white,
    align: "center",
    lineSpacingMultiple: 1.4,
  });
});

// ========== Slide 8: 数据流架构 ==========
const slide8 = pptx.addSlide();
addSideBar(slide8);

slide8.addText("02  系统架构设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide8, 1.15, 0.04);

slide8.addText("数据流架构", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const flowSteps = [
  { label: "RSS采集", sub: "定时抓取\n多源资讯", color: COLORS.accent },
  { label: "MySQL", sub: "业务数据\n结构化存储", color: COLORS.secondary },
  { label: "Kafka", sub: "消息队列\n行为数据流", color: COLORS.primary },
  { label: "Flink", sub: "实时计算\n流式处理", color: COLORS.purple },
  { label: "ClickHouse", sub: "实时分析\n即席查询", color: COLORS.green },
  { label: "可视化", sub: "统计报表\n数据大屏", color: COLORS.orange },
];

flowSteps.forEach((step, i) => {
  const x = 0.8 + i * 2.1;
  slide8.addShape(pptx.ShapeType.roundRect, {
    x, y: 2.8, w: 1.8, h: 2.5,
    fill: { color: step.color },
    rectRadius: 0.1,
  });
  slide8.addText(step.label, {
    x, y: 3, w: 1.8, h: 0.6,
    fontSize: 14, fontFace: FONT_TITLE,
    color: COLORS.white, bold: true,
    align: "center",
  });
  slide8.addText(step.sub, {
    x, y: 3.7, w: 1.8, h: 1.2,
    fontSize: 10, fontFace: FONT_BODY,
    color: COLORS.white,
    align: "center",
    lineSpacingMultiple: 1.4,
  });

  // 箭头
  if (i < flowSteps.length - 1) {
    slide8.addShape(pptx.ShapeType.rightArrow, {
      x: x + 1.85, y: 3.8, w: 0.25, h: 0.3,
      fill: { color: COLORS.accent },
    });
  }
});

// ========== Slide 9: 数据库设计 ==========
const slide9 = pptx.addSlide();
addSideBar(slide9);

slide9.addText("03  数据库设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide9, 1.15, 0.04);

// MySQL
slide9.addShape(pptx.ShapeType.roundRect, {
  x: 1, y: 1.5, w: 4.5, h: 5.5,
  fill: { color: COLORS.light },
  rectRadius: 0.1,
});
slide9.addText("MySQL 8.0", {
  x: 1, y: 1.6, w: 4.5, h: 0.6,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
  align: "center",
});

const mysqlTables = ["sys_user 用户表", "sys_role 角色表", "sys_menu 菜单表", "news 新闻表", "comment 评论表", "user_favorite 收藏表", "user_behavior 行为表"];
mysqlTables.forEach((t, i) => {
  slide9.addText(t, {
    x: 1.3, y: 2.3 + i * 0.6, w: 4, h: 0.5,
    fontSize: 12, fontFace: FONT_CODE,
    color: COLORS.dark,
  });
});

// ClickHouse
slide9.addShape(pptx.ShapeType.roundRect, {
  x: 5.8, y: 1.5, w: 3.5, h: 3,
  fill: { color: COLORS.light },
  rectRadius: 0.1,
});
slide9.addText("ClickHouse", {
  x: 5.8, y: 1.6, w: 3.5, h: 0.6,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.green, bold: true,
  align: "center",
});
slide9.addText("user_behavior_log\n行为日志表\n\npv_uv_daily\nPV/UV统计表", {
  x: 6, y: 2.3, w: 3, h: 2,
  fontSize: 12, fontFace: FONT_CODE,
  color: COLORS.dark,
  lineSpacingMultiple: 1.3,
});

// Kudu
slide9.addShape(pptx.ShapeType.roundRect, {
  x: 5.8, y: 4.8, w: 3.5, h: 2.2,
  fill: { color: COLORS.light },
  rectRadius: 0.1,
});
slide9.addText("Kudu", {
  x: 5.8, y: 4.9, w: 3.5, h: 0.6,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.purple, bold: true,
  align: "center",
});
slide9.addText("news_archive\n新闻归档表", {
  x: 6, y: 5.6, w: 3, h: 1,
  fontSize: 12, fontFace: FONT_CODE,
  color: COLORS.dark,
});

// ========== Slide 10: 用户端功能 ==========
const slide10 = pptx.addSlide();
addSideBar(slide10);

slide10.addText("04  功能模块设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide10, 1.15, 0.04);

slide10.addText("用户端功能", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const userFeatures = [
  { title: "首页", desc: "频道导航\n新闻列表\n热门推荐", color: COLORS.accent },
  { title: "频道页", desc: "10大科技频道\n分类浏览\nAI/大数据/云计算...", color: COLORS.secondary },
  { title: "新闻详情", desc: "内容展示\nAI摘要\n相关推荐\n评论区", color: COLORS.primary },
  { title: "搜索", desc: "全文搜索\n关键词高亮\n历史记录", color: COLORS.purple },
  { title: "个人中心", desc: "登录注册\n收藏管理\n浏览历史", color: COLORS.green },
];

userFeatures.forEach((f, i) => {
  const x = 1 + i * 2.5;
  slide10.addShape(pptx.ShapeType.roundRect, {
    x, y: 2.3, w: 2.2, h: 4.2,
    fill: { color: f.color },
    rectRadius: 0.1,
  });
  slide10.addText(f.title, {
    x, y: 2.5, w: 2.2, h: 0.7,
    fontSize: 18, fontFace: FONT_TITLE,
    color: COLORS.white, bold: true,
    align: "center",
  });
  slide10.addShape(pptx.ShapeType.rect, {
    x: x + 0.3, y: 3.2, w: 1.6, h: 0.03,
    fill: { color: COLORS.white },
  });
  slide10.addText(f.desc, {
    x: x + 0.2, y: 3.4, w: 1.8, h: 2.8,
    fontSize: 12, fontFace: FONT_BODY,
    color: COLORS.white,
    align: "center",
    lineSpacingMultiple: 1.6,
  });
});

// ========== Slide 11: 后台管理功能 ==========
const slide11 = pptx.addSlide();
addSideBar(slide11);

slide11.addText("04  功能模块设计", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide11, 1.15, 0.04);

slide11.addText("后台管理功能", {
  x: 1, y: 1.5, w: 5, h: 0.6,
  fontSize: 22, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

const adminFeatures = [
  { title: "仪表盘", items: ["用户统计", "PV/UV趋势", "文章统计", "AI调用次数"], color: COLORS.accent },
  { title: "系统管理", items: ["用户管理", "角色管理", "菜单权限", "字典配置"], color: COLORS.secondary },
  { title: "内容运营", items: ["文章管理", "频道管理", "内容审核", "RSS源配置"], color: COLORS.primary },
  { title: "AI服务", items: ["模型配置", "API密钥管理", "摘要生成测试", "调用统计"], color: COLORS.purple },
  { title: "数据统计", items: ["行为分析", "热门文章", "频道分布", "实时监控"], color: COLORS.green },
];

adminFeatures.forEach((f, i) => {
  const x = 1 + i * 2.5;
  slide11.addShape(pptx.ShapeType.roundRect, {
    x, y: 2.2, w: 2.2, h: 4.8,
    fill: { color: COLORS.light },
    rectRadius: 0.1,
  });
  slide11.addShape(pptx.ShapeType.roundRect, {
    x, y: 2.2, w: 2.2, h: 0.8,
    fill: { color: f.color },
    rectRadius: 0.1,
  });
  // 覆盖底部圆角
  slide11.addShape(pptx.ShapeType.rect, {
    x, y: 2.8, w: 2.2, h: 0.2,
    fill: { color: f.color },
  });
  slide11.addText(f.title, {
    x, y: 2.2, w: 2.2, h: 0.8,
    fontSize: 15, fontFace: FONT_TITLE,
    color: COLORS.white, bold: true,
    align: "center", valign: "middle",
  });
  f.items.forEach((item, j) => {
    slide11.addText("• " + item, {
      x: x + 0.2, y: 3.2 + j * 0.55, w: 1.8, h: 0.45,
      fontSize: 11, fontFace: FONT_BODY,
      color: COLORS.dark,
    });
  });
});

// ========== Slide 12: 核心代码 - 后端 ==========
const slide12 = pptx.addSlide();
addSideBar(slide12);

slide12.addText("05  核心代码实现", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide12, 1.15, 0.04);

slide12.addText("统一响应体 R<T>", {
  x: 1, y: 1.5, w: 5, h: 0.5,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

slide12.addText(
  "public class R<T> {\n" +
  "    private int code;\n" +
  "    private String msg;\n" +
  "    private T data;\n\n" +
  "    public static <T> R<T> ok(T data) {\n" +
  "        R<T> r = new R<>();\n" +
  "        r.code = 200;\n" +
  "        r.msg = \"success\";\n" +
  "        r.data = data;\n" +
  "        return r;\n" +
  "    }\n\n" +
  "    public static <T> R<T> fail(String msg) {\n" +
  "        R<T> r = new R<>();\n" +
  "        r.code = 500;\n" +
  "        r.msg = msg;\n" +
  "        return r;\n" +
  "    }\n" +
  "}", {
  x: 1, y: 2.1, w: 7, h: 4.5,
  fontSize: 11, fontFace: FONT_CODE,
  color: COLORS.dark,
  fill: { color: COLORS.light },
  valign: "top",
  lineSpacingMultiple: 1.3,
});

slide12.addText("JWT认证过滤器", {
  x: 8.5, y: 1.5, w: 5, h: 0.5,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

slide12.addText(
  "public class JwtAuthenticationFilter\n" +
  "    extends OncePerRequestFilter {\n\n" +
  "  @Override\n" +
  "  protected void doFilterInternal(\n" +
  "    HttpServletRequest request,\n" +
  "    HttpServletResponse response,\n" +
  "    FilterChain chain) {\n\n" +
  "    String token = request\n" +
  "      .getHeader(\"Authorization\");\n" +
  "    if (token != null\n" +
  "        && token.startsWith(\"Bearer \")) {\n" +
  "      // 验证JWT并设置认证信息\n" +
  "      Claims claims = jwtUtil\n" +
  "        .parseToken(token.substring(7));\n" +
  "      // 设置SecurityContext\n" +
  "    }\n" +
  "    chain.doFilter(request, response);\n" +
  "  }\n" +
  "}", {
  x: 8.5, y: 2.1, w: 5, h: 4.5,
  fontSize: 11, fontFace: FONT_CODE,
  color: COLORS.dark,
  fill: { color: COLORS.light },
  valign: "top",
  lineSpacingMultiple: 1.3,
});

// ========== Slide 13: 核心代码 - 大数据 ==========
const slide13 = pptx.addSlide();
addSideBar(slide13);

slide13.addText("05  核心代码实现", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide13, 1.15, 0.04);

slide13.addText("Kafka 行为数据生产者", {
  x: 1, y: 1.5, w: 6, h: 0.5,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

slide13.addText(
  "@Service\n" +
  "public class BehaviorProducer {\n" +
  "  @Autowired\n" +
  "  private KafkaTemplate<String, String> kafka;\n\n" +
  "  public void sendBehavior(BehaviorEvent event) {\n" +
  "    String json = JSON.toJSONString(event);\n" +
  "    kafka.send(\"user-behavior\",\n" +
  "      event.getUserId().toString(), json);\n" +
  "  }\n" +
  "}", {
  x: 1, y: 2.1, w: 6.5, h: 3.5,
  fontSize: 11, fontFace: FONT_CODE,
  color: COLORS.dark,
  fill: { color: COLORS.light },
  valign: "top",
  lineSpacingMultiple: 1.3,
});

slide13.addText("Flink 流式处理", {
  x: 8, y: 1.5, w: 6, h: 0.5,
  fontSize: 18, fontFace: FONT_TITLE,
  color: COLORS.dark, bold: true,
});

slide13.addText(
  "public class UserBehaviorAnalysis {\n" +
  "  public static void main(String[] args) {\n" +
  "    StreamExecutionEnvironment env =\n" +
  "      StreamExecutionEnvironment\n" +
  "        .getExecutionEnvironment();\n\n" +
  "    DataStream<String> stream = env\n" +
  "      .addSource(new FlinkKafkaConsumer<>(\n" +
  "        \"user-behavior\",\n" +
  "        new SimpleStringSchema(),\n" +
  "        kafkaProps));\n\n" +
  "    // 实时统计PV/UV\n" +
  "    stream.keyBy(event -> event.channel)\n" +
  "      .window(TumblingProcessingTimeWindows\n" +
  "        .of(Time.minutes(5)))\n" +
  "      .aggregate(new PvUvAggregator());\n" +
  "  }\n" +
  "}", {
  x: 8, y: 2.1, w: 5.5, h: 4.5,
  fontSize: 10, fontFace: FONT_CODE,
  color: COLORS.dark,
  fill: { color: COLORS.light },
  valign: "top",
  lineSpacingMultiple: 1.3,
});

// ========== Slide 14: 系统测试 ==========
const slide14 = pptx.addSlide();
addSideBar(slide14);

slide14.addText("06  系统测试", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide14, 1.15, 0.04);

const testCategories = [
  {
    title: "功能测试",
    color: COLORS.accent,
    items: ["用户注册/登录", "新闻浏览与搜索", "后台管理CRUD", "AI摘要生成", "评论与收藏"],
  },
  {
    title: "性能测试",
    color: COLORS.secondary,
    items: ["接口响应 < 200ms", "并发 500 QPS", "ClickHouse查询 < 100ms", "Kafka吞吐量 10K/s"],
  },
  {
    title: "兼容性测试",
    color: COLORS.green,
    items: ["Chrome/Firefox/Safari/Edge", "移动端响应式适配", "Android 8.0+", "HarmonyOS 4.0+"],
  },
];

testCategories.forEach((cat, i) => {
  const x = 1 + i * 4.2;
  slide14.addShape(pptx.ShapeType.roundRect, {
    x, y: 1.8, w: 3.8, h: 5,
    fill: { color: COLORS.light },
    rectRadius: 0.1,
  });
  slide14.addShape(pptx.ShapeType.roundRect, {
    x, y: 1.8, w: 3.8, h: 0.8,
    fill: { color: cat.color },
    rectRadius: 0.1,
  });
  slide14.addShape(pptx.ShapeType.rect, {
    x, y: 2.4, w: 3.8, h: 0.2,
    fill: { color: cat.color },
  });
  slide14.addText(cat.title, {
    x, y: 1.8, w: 3.8, h: 0.8,
    fontSize: 18, fontFace: FONT_TITLE,
    color: COLORS.white, bold: true,
    align: "center", valign: "middle",
  });
  cat.items.forEach((item, j) => {
    slide14.addText("✓  " + item, {
      x: x + 0.3, y: 2.9 + j * 0.65, w: 3.2, h: 0.5,
      fontSize: 13, fontFace: FONT_BODY,
      color: COLORS.dark,
    });
  });
});

// ========== Slide 15: 总结与展望 ==========
const slide15 = pptx.addSlide();
addSideBar(slide15);

slide15.addText("07  总结与展望", {
  x: 1, y: 0.4, w: 6, h: 0.8,
  fontSize: 32, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
});
addAccentBar(slide15, 1.15, 0.04);

// 项目总结
slide15.addShape(pptx.ShapeType.roundRect, {
  x: 1, y: 1.5, w: 6, h: 5.2,
  fill: { color: COLORS.light },
  rectRadius: 0.1,
});
slide15.addText("项目总结", {
  x: 1, y: 1.6, w: 6, h: 0.6,
  fontSize: 20, fontFace: FONT_TITLE,
  color: COLORS.primary, bold: true,
  align: "center",
});

const summaryItems = [
  "完成科技资讯聚合平台全栈开发",
  "实现大数据实时采集、处理、分析闭环",
  "AI赋能内容生产，智能摘要生成",
  "多端适配：Web + Android + HarmonyOS",
  "Docker Compose 一键部署",
];
summaryItems.forEach((item, i) => {
  slide15.addText("✓  " + item, {
    x: 1.3, y: 2.4 + i * 0.7, w: 5.4, h: 0.6,
    fontSize: 13, fontFace: FONT_BODY,
    color: COLORS.dark,
  });
});

// 未来展望
slide15.addShape(pptx.ShapeType.roundRect, {
  x: 7.5, y: 1.5, w: 6, h: 5.2,
  fill: { color: COLORS.primary },
  rectRadius: 0.1,
});
slide15.addText("未来展望", {
  x: 7.5, y: 1.6, w: 6, h: 0.6,
  fontSize: 20, fontFace: FONT_TITLE,
  color: COLORS.white, bold: true,
  align: "center",
});

const futureItems = [
  "个性化推荐算法优化",
  "更多数据源接入（API/爬虫）",
  "实时数据大屏可视化",
  "国际化多语言支持",
];
futureItems.forEach((item, i) => {
  slide15.addText("→  " + item, {
    x: 7.8, y: 2.4 + i * 0.7, w: 5.4, h: 0.6,
    fontSize: 13, fontFace: FONT_BODY,
    color: COLORS.white,
  });
});

// ========== Slide 16: 感谢页 ==========
const slide16 = pptx.addSlide();
addBg(slide16, COLORS.primary);

slide16.addShape(pptx.ShapeType.rect, {
  x: 0, y: 0, w: "100%", h: 0.04,
  fill: { color: COLORS.accent },
});

slide16.addText("感谢聆听", {
  x: 1, y: 2.5, w: 12, h: 1.5,
  fontSize: 48, fontFace: FONT_TITLE,
  color: COLORS.white, bold: true,
  align: "center",
});

slide16.addText("THANK YOU", {
  x: 1, y: 4, w: 12, h: 0.8,
  fontSize: 20, fontFace: FONT_BODY,
  color: COLORS.accent,
  align: "center",
});

slide16.addShape(pptx.ShapeType.rect, {
  x: 5, y: 5, w: 4, h: 0.03,
  fill: { color: COLORS.accent },
});

slide16.addText("智讯 - 大数据实时科技资讯聚合平台", {
  x: 1, y: 5.3, w: 12, h: 0.6,
  fontSize: 14, fontFace: FONT_BODY,
  color: COLORS.gray,
  align: "center",
});

slide16.addShape(pptx.ShapeType.rect, {
  x: 0, y: 7.46, w: "100%", h: 0.04,
  fill: { color: COLORS.accent },
});

// 保存
pptx.writeFile({ fileName: "d:\\workspace\\bigdata-portal-platform\\docs\\project-presentation.pptx" })
  .then(() => console.log("PPT created successfully!"))
  .catch(err => console.error("Error:", err));
