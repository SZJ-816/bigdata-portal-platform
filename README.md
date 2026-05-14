# 科技资讯聚合平台

基于 Vue 3 + Spring Boot 的大数据新闻门户系统，支持RSS订阅抓取、AI智能摘要、用户行为分析和邮箱注册验证。

## 功能特性

### 资讯聚合
- 多源RSS订阅自动抓取（36氪、虎嗅、IT之家、澎湃等）
- 关键词智能频道分类（AI、大数据、云计算、互联网、硬件、创业）
- 全站搜索和分类筛选
- 图片懒加载优化

### AI 能力
- 新闻摘要自动生成
- 中英文翻译
- 图片AI生成（Midjourney API）
- 相似新闻推荐

### 用户系统
- JWT 认证登录
- 邮箱验证码注册
- 收藏夹和阅读历史
- 用户行为追踪（点击、收藏、搜索）

### 大数据分析
- 实时用户行为分析（Flink）
- 数据仓库（ClickHouse）
- Kafka 消息队列
- 可视化数据看板

## 技术栈

### 前端
- **Vue 3** + Composition API
- **Vite** 构建工具
- **Axios** HTTP 客户端
- **Vue Router** 路由管理
- 响应式设计，支持移动端

### 后端
- **Spring Boot 2.7** 框架
- **MyBatis** ORM 映射
- **MySQL 8.0** 关系数据库
- **Rome** RSS 解析库
- **JWT** 用户认证
- **Spring Security** 密码加密

### 大数据组件
- **Apache Kafka** 消息队列
- **Apache Flink** 流处理
- **ClickHouse** 分析型数据库
- **Zookeeper** 服务协调

### 部署运维
- **Docker** 容器化
- **Docker Compose** 服务编排
- **Nginx** 反向代理
- **cpolar** 内网穿透

## 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                      用户访问                           │
│                   cpolar 穿透                          │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                     Nginx                                │
│              反向代理 + 静态资源                         │
└──────┬────────────────────────────┬────────────────────┘
       │                            │
┌──────▼──────┐          ┌────────▼────────┐
│   Frontend   │          │    Backend      │
│    Vue 3     │          │  Spring Boot    │
│   静态网站    │          │   API 服务      │
└──────────────┘          └────┬───────────┘
                                │
         ┌──────────────────────┼──────────────────────┐
         │                      │                      │
┌────────▼────────┐  ┌──────────▼────────┐  ┌─────────▼─────────┐
│     MySQL       │  │      Kafka        │  │    ClickHouse    │
│   用户/新闻     │  │   行为消息队列     │  │   行为数据仓库    │
└─────────────────┘  └─────────┬──────────┘  └──────────────────┘
                                │
                    ┌───────────▼──────────┐
                    │       Flink          │
                    │    实时流处理        │
                    └──────────────────────┘
```

## 快速部署

### 环境要求
- Docker & Docker Compose
- 4GB+ 内存
- 20GB+ 磁盘空间

### 部署步骤

1. **克隆项目**
```bash
git clone https://github.com/SZJ-816/bigdata-portal-platform.git
cd bigdata-portal-platform
```

2. **配置环境变量（可选）**
编辑 `docker-compose.yml` 中的环境变量：
```yaml
environment:
  - MAIL_HOST=smtp.qq.com
  - MAIL_PORT=587
  - MAIL_USERNAME=your-email@qq.com
  - MAIL_PASSWORD=your-auth-code
```

3. **启动服务**
```bash
docker compose up -d
```

4. **访问应用**
- 前端地址：`http://localhost`
- 后端API：`http://localhost:8090`
- 数据看板：`http://localhost:3000`

### 默认账号
- 用户名：`admin`
- 密码：`123456`

## 目录结构

```
bigdata-portal-platform/
├── frontend/                 # Vue 3 前端项目
│   ├── src/
│   │   ├── api/             # API 接口封装
│   │   ├── components/      # 公共组件
│   │   ├── composables/     # 组合式函数
│   │   ├── directives/       # 自定义指令
│   │   ├── mock/            # 模拟数据
│   │   ├── router/           # 路由配置
│   │   ├── styles/           # 全局样式
│   │   └── views/            # 页面视图
│   └── Dockerfile
├── backend/                  # Spring Boot 后端项目
│   └── src/main/java/
│       └── com/bigdata/portal/
│           ├── config/        # 配置类
│           ├── controller/     # 控制器
│           ├── entity/         # 实体类
│           ├── kafka/          # Kafka 生产者
│           ├── mapper/         # MyBatis Mapper
│           ├── service/        # 业务服务
│           └── util/           # 工具类
├── sql/                      # 数据库脚本
├── docker-compose.yml         # Docker 编排配置
└── README.md
```

## API 接口

### 新闻接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/news` | 获取新闻列表 |
| GET | `/api/news/{id}` | 获取新闻详情 |
| GET | `/api/news/hot` | 获取热门新闻 |

### 用户接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/users/login` | 用户登录 |
| POST | `/api/users/register` | 用户注册 |
| POST | `/api/users/send-code` | 发送邮箱验证码 |
| GET | `/api/users/favorites` | 获取收藏列表 |

### AI 接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/ai/summary` | 生成新闻摘要 |
| POST | `/api/ai/translate` | 翻译文本 |
| POST | `/api/ai/chat` | AI 对话 |

## 开发指南

### 前端开发
```bash
cd frontend
npm install
npm run dev
```

### 后端开发
```bash
cd backend
mvn spring-boot:run
```

### 运行测试
```bash
# 前端测试
npm run test

# 后端测试
mvn test
```

## 配置说明

### RSS 订阅源
在 `RssService.java` 中配置 RSS 订阅源：
```java
private static final Map<String, String> RSS_SOURCES = Map.of(
    "36氪", "https://36kr.com/feed",
    "虎嗅", "https://www.huxiu.com/rss/",
    "IT之家", "https://www.ithome.com/site/feed"
);
```

### 频道关键词
在 `NewsService.java` 中配置频道分类关键词：
```java
private static final Map<String, List<String>> CHANNEL_KEYWORDS = Map.of(
    "AI", List.of("人工智能", "AI", "大模型", "GPT", "ChatGPT", "机器学习"),
    "大数据", List.of("大数据", "Hadoop", "Spark", "数据湖", "数据分析")
);
```

### AI 服务
支持配置不同的 AI 服务：
```yaml
ai:
  nvidia:
    base-url: https://integrate.api.nvidia.com/v1
    api-key: your-nvidia-api-key
    model: meta/llama-3.1-8b-instruct
```

## 项目截图

| 首页 | 频道分类 | 新闻详情 |
|:---:|:---:|:---:|
| ![首页](public/screenshot-home.png) | ![频道](public/screenshot-channel.png) | ![详情](public/screenshot-detail.png) |

## License

MIT License

## 联系方式

- GitHub: [SZJ-816](https://github.com/SZJ-816)
- Email: 18233296406@163.com
