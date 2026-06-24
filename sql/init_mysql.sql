-- ============================================================
--  智讯科技资讯聚合平台 - MySQL 数据库初始化脚本
--  编码：UTF-8
--  说明：MyBatis-Plus 自动映射表名（驼峰 → 下划线）
-- ============================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
--  系统用户表 sys_user
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(加密)',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    gender TINYINT DEFAULT 0 COMMENT '性别(0未知 1男 2女)',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1停用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入测试用户（密码: admin123，MD5加密: 0192023a7bbd73250516f069df18b500）
INSERT INTO sys_user (username, password, nickname, email, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '平台管理员', 'admin@zhixun.com', 0),
('test', '098f6bcd4621d373cade4e832627b4f6', '测试用户', 'test@zhixun.com', 0);

-- ------------------------------------------------------------
--  角色表 sys_role
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(64) NOT NULL COMMENT '角色键',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1停用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO sys_role (role_name, role_key, sort, status) VALUES
('超级管理员', 'admin', 1, 0),
('普通用户', 'user', 2, 0);

-- ------------------------------------------------------------
--  CMS频道表 cms_channel
-- ------------------------------------------------------------
DROP TABLE IF EXISTS cms_channel;
CREATE TABLE cms_channel (
    channel_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '频道ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父频道ID(0为顶级)',
    channel_name VARCHAR(64) NOT NULL COMMENT '频道名称',
    channel_key VARCHAR(64) DEFAULT NULL COMMENT '频道标识',
    icon VARCHAR(255) DEFAULT NULL COMMENT '频道图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1停用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (channel_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CMS频道表';

-- 插入 10 大核心频道
INSERT INTO cms_channel (channel_name, channel_key, sort, status, icon, remark) VALUES
('人工智能', 'ai', 1, 0, '🤖', 'AI技术前沿动态'),
('大数据', 'bigdata', 2, 0, '📊', '大数据技术与应用'),
('云计算', 'cloud', 3, 0, '☁️', '云计算平台与服务'),
('互联网', 'internet', 4, 0, '🌐', '互联网行业资讯'),
('硬件', 'hardware', 5, 0, '💻', '硬件设备与芯片'),
('创业', 'startup', 6, 0, '🚀', '创业投资动态'),
('安全', 'security', 7, 0, '🔒', '网络安全与防护'),
('区块链', 'blockchain', 8, 0, '⛓️', '区块链与Web3'),
('数码', 'digital', 9, 0, '📱', '数码产品评测'),
('汽车科技', 'auto', 10, 0, '🚗', '智能汽车与自动驾驶');

-- ------------------------------------------------------------
--  CMS文章表 cms_article
-- ------------------------------------------------------------
DROP TABLE IF EXISTS cms_article;
CREATE TABLE cms_article (
    article_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    channel_id BIGINT DEFAULT NULL COMMENT '频道ID',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    summary VARCHAR(512) DEFAULT NULL COMMENT '文章摘要',
    content LONGTEXT DEFAULT NULL COMMENT '文章内容',
    cover_image VARCHAR(512) DEFAULT NULL COMMENT '封面图',
    source VARCHAR(128) DEFAULT NULL COMMENT '来源',
    source_url VARCHAR(512) DEFAULT NULL COMMENT '原文链接',
    author VARCHAR(128) DEFAULT NULL COMMENT '作者',
    tags VARCHAR(255) DEFAULT NULL COMMENT '标签(逗号分隔)',
    view_count BIGINT DEFAULT 0 COMMENT '浏览量',
    like_count BIGINT DEFAULT 0 COMMENT '点赞数',
    comment_count BIGINT DEFAULT 0 COMMENT '评论数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶(0否 1是)',
    status TINYINT DEFAULT 1 COMMENT '状态(0草稿 1已发布 2已下架)',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (article_id),
    KEY idx_channel_id (channel_id),
    KEY idx_status (status),
    KEY idx_view_count (view_count),
    FULLTEXT KEY idx_title_content (title, summary, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CMS文章表';

-- 插入示例文章（每个频道 3 篇，共 30 篇）
INSERT INTO cms_article (channel_id, title, summary, content, cover_image, source, author, tags, view_count, like_count, comment_count, is_top, status, publish_time) VALUES
-- 人工智能 (channel_id = 1)
(1, '大模型技术路线全景解析：从 Transformer 到 GPT 再到 Agent',
 '详细梳理大模型的技术演进路径，包括架构创新、训练优化、对齐方法等核心技术环节。',
 '<p>自 2017 年 Transformer 架构提出以来，大模型技术经历了快速演进...</p>',
 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800',
 '36氪', '智讯编辑', '大模型,Transformer,Agent', 2458, 156, 32, 1, 1, NOW()),
(1, 'NVIDIA H200 发布：显存突破141GB，AI训练性能再上台阶',
 'NVIDIA 发布最新 H200 加速卡，配备 141GB HBM3e 显存，推理性能相比 H100 提升 110%。',
 '<p>NVIDIA 正式发布 H200 高性能计算加速卡...</p>',
 'https://images.unsplash.com/photo-1686191128892-f2b9a9a4d0d4?w=800',
 'IT之家', '智讯编辑', 'NVIDIA,GPU,AI芯片', 3892, 245, 67, 0, 1, NOW()),
(1, '多模态大模型的未来：从文本生成到世界模型',
 '多模态能力已成为大模型的核心竞争力，世界模型将是下一个突破点。',
 '<p>多模态大模型正在从文本+图像向视频、3D、音频等更丰富的模态拓展...</p>',
 'https://images.unsplash.com/photo-1721322800607-8c38375fef34?w=800',
 '36氪', '智讯编辑', '多模态,世界模型,AI', 1823, 98, 19, 0, 1, NOW()),

-- 大数据 (channel_id = 2)
(2, 'ClickHouse vs Doris vs StarRocks：OLAP 引擎横向对比',
 '从存储引擎、查询性能、运维复杂度三个维度，对比主流开源 OLAP 方案。',
 '<p>实时数仓市场正在快速变化，三大开源方案各有优势...</p>',
 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800',
 'InfoQ', '智讯编辑', 'ClickHouse,Doris,OLAP', 2145, 132, 28, 0, 1, NOW()),
(2, '数据湖 3.0 时代：湖仓一体架构的实践与反思',
 '湖仓一体已成为大数据平台的主流架构，但实际落地仍面临挑战。',
 '<p>从 Hadoop 到 Iceberg/Hudi/Delta，再到湖仓一体...</p>',
 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800',
 'InfoQ', '智讯编辑', '数据湖,湖仓一体,Iceberg', 1678, 89, 15, 0, 1, NOW()),
(2, '实时计算引擎演进：Flink、Spark Streaming、Kafka Streams',
 '流式计算引擎技术对比与选型建议。',
 '<p>实时计算已成为大数据平台的标配能力...</p>',
 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
 'IT之家', '智讯编辑', 'Flink,Spark,Kafka', 1923, 112, 22, 0, 1, NOW()),

-- 云计算 (channel_id = 3)
(3, 'Kubernetes 1.30 新特性解读：Pod 调度新算法',
 'Kubernetes 1.30 引入全新的 Pod 调度算法，集群资源利用率提升显著。',
 '<p>新版 Kubernetes 在调度层面做了重大改进...</p>',
 'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=800',
 'InfoQ', '智讯编辑', 'Kubernetes,容器,云原生', 2345, 167, 35, 0, 1, NOW()),
(3, 'Serverless 冷启动：从毫秒到微秒的技术跨越',
 '各大云厂商在 Serverless 冷启动优化上持续投入，启动时间已进入亚毫秒级。',
 '<p>冷启动曾是 Serverless 的最大痛点...</p>',
 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
 '36氪', '智讯编辑', 'Serverless,冷启动,云计算', 1567, 78, 12, 0, 1, NOW()),
(3, '多云战略下的统一管理平台建设',
 '企业如何应对多云环境下的资源管理、成本核算、安全合规挑战。',
 '<p>多云已成为企业上云的主流选择...</p>',
 'https://images.unsplash.com/photo-1650830291281-9cb69be9fd97?w=800',
 'InfoQ', '智讯编辑', '多云,云管理,成本优化', 1234, 65, 10, 0, 1, NOW()),

-- 互联网 (channel_id = 4)
(4, '字节跳动海外扩张：TikTok 之后的下一个增长曲线',
 '字节跳动在 TikTok 成功基础上，继续拓展海外业务线，寻找下一个增长引擎。',
 '<p>字节跳动正在加速海外业务布局...</p>',
 'https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=800',
 '36氪', '智讯编辑', '字节跳动,TikTok,出海', 3567, 289, 56, 1, 1, NOW()),
(4, '反垄断监管下的互联网平台治理新范式',
 '全球范围内的互联网反垄断浪潮正在重塑平台经济规则。',
 '<p>反垄断监管进入常态化阶段...</p>',
 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
 'IT之家', '智讯编辑', '反垄断,平台经济,监管', 1892, 123, 24, 0, 1, NOW()),
(4, 'AIGC 重塑内容创作生态',
 'AI 生成内容正在从工具层面深度改变内容创作的生产关系。',
 '<p>从写作到绘画，从视频到代码...</p>',
 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800',
 '36氪', '智讯编辑', 'AIGC,内容创作,生产力', 2789, 198, 42, 0, 1, NOW()),

-- 硬件 (channel_id = 5)
(5, '国产 GPU 芯片路线图：从训练到推理的全面突破',
 '国产 GPU 厂商在 AI 训练和推理两个赛道加速追赶国际巨头。',
 '<p>国产 GPU 正在经历从能用到好用的阶段...</p>',
 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800',
 '36氪', '智讯编辑', 'GPU,国产芯片,AI', 4523, 345, 78, 1, 1, NOW()),
(5, 'RISC-V 架构生态走向成熟：从 MCU 到高性能计算',
 'RISC-V 开源指令集架构在各领域快速落地，生态日趋完善。',
 '<p>RISC-V 正从边缘 MCU 向高性能计算进军...</p>',
 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800',
 'InfoQ', '智讯编辑', 'RISC-V,芯片,开源', 2134, 156, 28, 0, 1, NOW()),
(5, 'DDR5 内存普及加速，AI 训练场景需求爆发',
 'DDR5 内存市场在 AI 浪潮推动下迎来新一轮增长周期。',
 '<p>内存带宽是 AI 训练的关键瓶颈之一...</p>',
 'https://images.unsplash.com/photo-1591405351990-492700289245?w=800',
 'IT之家', '智讯编辑', 'DDR5,内存,AI训练', 1756, 98, 18, 0, 1, NOW()),

-- 创业 (channel_id = 6)
(6, '2025 年值得关注的十大 AI 创业赛道',
 '从底层算力到上层应用，AI 创业正呈现结构性机会。',
 '<p>AI 创业进入新阶段，基础设施和垂直应用两端机会凸显...</p>',
 'https://images.unsplash.com/photo-1556155092-490a1ba16284?w=800',
 '36氪', '智讯编辑', '创业,AI,投资', 3245, 234, 52, 0, 1, NOW()),
(6, '企业服务市场的下一个十年：从 SaaS 到 AI-Native',
 '企业服务正在经历从 SaaS 向 AI-Native 的范式跃迁。',
 '<p>AI-Native 企业服务正在改写 SaaS 的游戏规则...</p>',
 'https://images.unsplash.com/photo-1552581234-26169f7dace?w=800',
 '36氪', '智讯编辑', '企业服务,SaaS,AI', 1876, 123, 25, 0, 1, NOW()),
(6, '开源商业化的成功路径：从 MongoDB 到 Elastic',
 '开源项目如何构建可持续的商业模式？',
 '<p>开源商业化走过了漫长的探索期...</p>',
 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800',
 'InfoQ', '智讯编辑', '开源,商业模式', 1432, 87, 16, 0, 1, NOW()),

-- 安全 (channel_id = 7)
(7, '零信任架构实战：从概念到落地的完整路径',
 '零信任已成为企业安全架构的核心原则，但落地仍需系统方法论。',
 '<p>零信任的核心是永不信任，始终验证...</p>',
 'https://images.unsplash.com/photo-1639762681057-408e52192e55?w=800',
 'InfoQ', '智讯编辑', '零信任,安全,企业安全', 1967, 134, 28, 0, 1, NOW()),
(7, 'AI 时代的新安全挑战：Prompt 注入与模型投毒',
 '大模型的广泛应用带来了全新的安全攻击面。',
 '<p>随着大模型接入越来越多的系统...</p>',
 'https://images.unsplash.com/photo-1639762681485-04469fdb4f3d?w=800',
 '36氪', '智讯编辑', 'AI安全,大模型,Prompt注入', 2345, 167, 38, 1, 1, NOW()),
(7, '供应链攻击防护：从 Log4j 到 XZ Utils 的警示',
 '软件供应链安全已成为企业不可忽视的基础风险。',
 '<p>最近几年的安全事件不断提醒我们...</p>',
 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800',
 'IT之家', '智讯编辑', '供应链,安全,漏洞', 1567, 89, 15, 0, 1, NOW()),

-- 区块链 (channel_id = 8)
(8, 'Web3 与 AI 的交叉：从 Token 到 Agent 经济',
 '区块链和 AI 正在 Agent 经济的语境下找到新的结合点。',
 '<p>Token 曾是 Web3 的核心叙事...</p>',
 'https://images.unsplash.com/photo-1639762681057-408e52192e55?w=800',
 '36氪', '智讯编辑', 'Web3,区块链,Agent', 1234, 78, 12, 0, 1, NOW()),
(8, '以太坊路线图更新：Pectra、Danksharding 与协议层演进',
 '以太坊协议层在合并之后继续快速演进，路线图更加清晰。',
 '<p>以太坊合并之后，协议层进入新的发展阶段...</p>',
 'https://images.unsplash.com/photo-1620321024358-8b3788c2bf5a?w=800',
 '36氪', '智讯编辑', '以太坊,区块链,DeFi', 987, 56, 9, 0, 1, NOW()),
(8, '央行数字货币跨境支付试点新进展',
 '多国央行数字货币在跨境支付场景下的合作试点取得实质性进展。',
 '<p>数字人民币的跨境试点正在扩大...</p>',
 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800',
 '36氪', '智讯编辑', 'CBDC,数字人民币,跨境支付', 1543, 98, 18, 0, 1, NOW()),

-- 数码 (channel_id = 9)
(9, 'iPhone 17 Pro 深度评测：AI 能力是最大亮点',
 'iPhone 17 Pro 在 AI 计算能力、相机系统、续航方面实现全面升级。',
 '<p>Apple Intelligence 是 iPhone 17 系列的最大卖点...</p>',
 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800',
 'IT之家', '智讯编辑', 'iPhone,苹果,评测', 4567, 367, 89, 1, 1, NOW()),
(9, '折叠屏手机市场观察：价格下探与形态创新',
 '折叠屏手机价格持续下探，形态创新成为差异化竞争的关键。',
 '<p>折叠屏正在从旗舰走向主流市场...</p>',
 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800',
 'IT之家', '智讯编辑', '折叠屏,手机,数码', 2134, 145, 32, 0, 1, NOW()),
(9, '智能手表进入健康监测新阶段：从心率到血糖',
 '智能手表的健康监测能力持续增强，无创血糖监测成为下一个热点。',
 '<p>智能手表已成为个人健康数据的重要入口...</p>',
 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800',
 'IT之家', '智讯编辑', '智能手表,健康,可穿戴', 1756, 112, 22, 0, 1, NOW()),

-- 汽车科技 (channel_id = 10)
(10, '特斯拉 FSD 中国落地：自动驾驶的真实体验与局限',
 '特斯拉 FSD（完全自动驾驶）正式在中国落地，实际道路体验如何？',
 '<p>经过长期等待，FSD 终于在中国开启测试...</p>',
 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800',
 '36氪', '智讯编辑', '特斯拉,FSD,自动驾驶', 3892, 278, 65, 1, 1, NOW()),
(10, '华为智选车模式复盘：从零部件供应商到整车方案',
 '华为通过智选模式深度参与造车，为行业带来新的合作范式。',
 '<p>华为不造车，但华为的车业务越来越深入...</p>',
 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800',
 '36氪', '智讯编辑', '华为,智能汽车,智选', 2745, 189, 42, 0, 1, NOW()),
(10, '固态电池量产倒计时：从实验室到商业化',
 '全固态电池的商业化进程正在加速，预计 2027 年前后实现量产。',
 '<p>固态电池被认为是下一代电动汽车的能量来源...</p>',
 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800',
 'IT之家', '智讯编辑', '固态电池,新能源,电池技术', 1987, 134, 28, 0, 1, NOW());

-- ------------------------------------------------------------
--  用户行为表 user_behavior（MySQL 版本，用于小数据量存储）
--  大数据量请使用 ClickHouse 中的 behavior_events 表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS user_behavior;
CREATE TABLE user_behavior (
    behavior_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '行为ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    article_id BIGINT DEFAULT NULL COMMENT '文章ID',
    behavior_type VARCHAR(32) DEFAULT NULL COMMENT '行为类型(VIEW浏览 LIKE点赞 SHARE分享 FAVORITE收藏)',
    duration INT DEFAULT NULL COMMENT '停留时长(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (behavior_id),
    KEY idx_user_id (user_id),
    KEY idx_article_id (article_id),
    KEY idx_behavior_type (behavior_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- 插入示例行为数据（模拟用户浏览、点赞等）
INSERT INTO user_behavior (user_id, article_id, behavior_type, duration, create_time) VALUES
(1, 1, 'VIEW', 125, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 1, 'VIEW', 89, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 2, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 2, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(1, 4, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(2, 4, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(1, 7, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(2, 10, 'VIEW', 145, DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(1, 13, 'VIEW', 112, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
(2, 16, 'VIEW', 98, DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(1, 19, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 11 HOUR)),
(2, 22, 'VIEW', 167, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(1, 25, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 13 HOUR)),
(2, 28, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 14 HOUR)),
(1, 30, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
(2, 3, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(1, 6, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(2, 9, 'COMMENT', NULL, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(1, 12, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(2, 15, 'VIEW', 123, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(1, 18, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 21 HOUR)),
(2, 21, 'VIEW', 167, DATE_SUB(NOW(), INTERVAL 22 HOUR)),
(1, 24, 'VIEW', 145, DATE_SUB(NOW(), INTERVAL 23 HOUR)),
(2, 27, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 26 HOUR)),
(2, 8, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 28 HOUR)),
(1, 11, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 30 HOUR)),
(2, 14, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 32 HOUR)),
(1, 17, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 34 HOUR)),
(2, 20, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 36 HOUR)),
(1, 23, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(2, 26, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(1, 29, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 42 HOUR)),
(2, 30, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 44 HOUR)),
(1, 1, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 46 HOUR)),
(2, 3, 'COMMENT', NULL, DATE_SUB(NOW(), INTERVAL 48 HOUR)),
(1, 7, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 50 HOUR)),
(2, 10, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 52 HOUR)),
(1, 13, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(2, 16, 'VIEW', 145, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(1, 19, 'VIEW', 167, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(2, 22, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(1, 25, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 62 HOUR)),
(2, 28, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 64 HOUR)),
(1, 4, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 66 HOUR)),
(2, 6, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 68 HOUR)),
(1, 9, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 70 HOUR)),
(2, 12, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 72 HOUR)),
(1, 15, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 74 HOUR)),
(2, 18, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 76 HOUR)),
(1, 21, 'COMMENT', NULL, DATE_SUB(NOW(), INTERVAL 78 HOUR)),
(2, 24, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 80 HOUR)),
(1, 27, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 82 HOUR)),
(2, 30, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 84 HOUR)),
(1, 2, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 86 HOUR)),
(2, 5, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 88 HOUR)),
(1, 8, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 90 HOUR)),
(2, 11, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 92 HOUR)),
(1, 14, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 94 HOUR)),
(2, 17, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 96 HOUR)),
(1, 20, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 98 HOUR)),
(2, 23, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 100 HOUR)),
(1, 26, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 102 HOUR)),
(2, 29, 'COMMENT', NULL, DATE_SUB(NOW(), INTERVAL 104 HOUR)),
(1, 3, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 106 HOUR)),
(2, 6, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 108 HOUR)),
(1, 9, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 110 HOUR)),
(2, 12, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 112 HOUR)),
(1, 15, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 114 HOUR)),
(2, 18, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 116 HOUR)),
(1, 21, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 118 HOUR)),
(2, 24, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 120 HOUR)),
(1, 27, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 122 HOUR)),
(2, 30, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 124 HOUR)),
(1, 2, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 126 HOUR)),
(2, 5, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 128 HOUR)),
(1, 8, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 130 HOUR)),
(2, 11, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 132 HOUR)),
(1, 14, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 134 HOUR)),
(2, 17, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 136 HOUR)),
(1, 20, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 138 HOUR)),
(2, 23, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 140 HOUR)),
(1, 26, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 142 HOUR)),
(2, 29, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 144 HOUR)),
(1, 1, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 146 HOUR)),
(2, 4, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 148 HOUR)),
(1, 7, 'COMMENT', NULL, DATE_SUB(NOW(), INTERVAL 150 HOUR)),
(2, 10, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 152 HOUR)),
(1, 13, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 154 HOUR)),
(2, 16, 'FAVORITE', NULL, DATE_SUB(NOW(), INTERVAL 156 HOUR)),
(1, 19, 'VIEW', 203, DATE_SUB(NOW(), INTERVAL 158 HOUR)),
(2, 22, 'VIEW', 134, DATE_SUB(NOW(), INTERVAL 160 HOUR)),
(1, 25, 'VIEW', 189, DATE_SUB(NOW(), INTERVAL 162 HOUR)),
(2, 28, 'LIKE', NULL, DATE_SUB(NOW(), INTERVAL 164 HOUR)),
(1, 30, 'SHARE', NULL, DATE_SUB(NOW(), INTERVAL 166 HOUR)),
(2, 2, 'VIEW', 156, DATE_SUB(NOW(), INTERVAL 168 HOUR)),
(1, 5, 'VIEW', 178, DATE_SUB(NOW(), INTERVAL 170 HOUR)),
(2, 8, 'VIEW', 201, DATE_SUB(NOW(), INTERVAL 172 HOUR));

-- ------------------------------------------------------------
--  社区评论表 community_comment
-- ------------------------------------------------------------
DROP TABLE IF EXISTS community_comment;
CREATE TABLE community_comment (
    comment_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    article_id BIGINT DEFAULT NULL COMMENT '文章ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级评论)',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1隐藏)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (comment_id),
    KEY idx_article_id (article_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论表';

-- 插入示例评论
INSERT INTO community_comment (article_id, user_id, parent_id, content, like_count, status) VALUES
(1, 2, 0, '非常精彩的技术梳理，期待更多关于 Agent 的深度内容！', 23, 0),
(1, 1, 0, 'Transformer 架构确实是近年来最重要的突破之一。', 15, 0),
(2, 2, 0, 'NVIDIA 的技术积累太可怕了，期待国产 GPU 早日追平。', 45, 0),
(4, 1, 0, '字节的海外战略执行力确实很强。', 18, 0),
(10, 2, 0, 'FSD 在中国的道路场景下表现如何？期待实测。', 32, 0),
(25, 1, 0, 'iPhone 17 Pro 的 AI 能力确实是最大的升级点。', 28, 0),
(11, 2, 0, 'GPU 是 AI 时代的基础算力，国产替代非常关键。', 56, 0),
(13, 1, 0, 'RISC-V 的生态发展速度超出预期。', 12, 0),
(19, 2, 0, 'AI 安全确实是一个容易被忽视但非常重要的领域。', 21, 0),
(30, 1, 0, '固态电池一旦量产，新能源汽车会迎来真正的爆发。', 38, 0);

-- ------------------------------------------------------------
--  用户收藏表 user_favorite
-- ------------------------------------------------------------
DROP TABLE IF EXISTS user_favorite;
CREATE TABLE user_favorite (
    favorite_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    article_id BIGINT DEFAULT NULL COMMENT '文章ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_article (user_id, article_id),
    KEY idx_user_id (user_id),
    KEY idx_article_id (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

INSERT INTO user_favorite (user_id, article_id) VALUES
(1, 1), (1, 2), (1, 4), (1, 10), (1, 19), (1, 25), (1, 30),
(2, 2), (2, 5), (2, 11), (2, 13), (2, 22), (2, 28), (2, 30);

-- ------------------------------------------------------------
--  系统配置表 sys_config
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    config_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(128) NOT NULL COMMENT '配置键',
    config_value VARCHAR(512) DEFAULT NULL COMMENT '配置值',
    config_name VARCHAR(128) DEFAULT NULL COMMENT '配置名称',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (config_id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

INSERT INTO sys_config (config_key, config_value, config_name, remark) VALUES
('site.name', '智讯科技资讯聚合平台', '站点名称', NULL),
('site.description', '基于 Vue3 + Spring Boot + Kafka + ClickHouse 的大数据资讯门户', '站点描述', NULL),
('ai.enabled', 'true', '是否启用 AI 功能', NULL),
('ai.nvidia.model', 'qwen/qwen3.5-122b-a10b', 'AI 模型名称', NULL);

-- ------------------------------------------------------------
--  系统菜单表 sys_menu
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    menu_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    path VARCHAR(128) DEFAULT NULL COMMENT '路由路径',
    icon VARCHAR(64) DEFAULT NULL COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1停用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

INSERT INTO sys_menu (parent_id, menu_name, path, icon, sort, status) VALUES
(0, '内容管理', '/cms', '📝', 1, 0),
(0, '用户管理', '/system', '👥', 2, 0),
(0, '数据统计', '/statistics', '📊', 3, 0),
(0, 'AI 智能', '/ai', '🤖', 4, 0);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
--  数据库初始化完成！
--  建议后续操作：
--    1. docker exec 到 MySQL 容器执行 SHOW TABLES 确认
--    2. docker exec 到 ClickHouse 容器执行建表 SQL
--    3. 启动后端服务后验证 API 是否正常
-- ============================================================
