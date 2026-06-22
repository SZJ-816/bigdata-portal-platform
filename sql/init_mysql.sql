CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) DEFAULT 'user',
    is_active TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content LONGTEXT,
    channel VARCHAR(50) NOT NULL,
    source VARCHAR(100),
    image_url VARCHAR(500),
    view_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    is_breaking TINYINT DEFAULT 0,
    publish_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    source_url VARCHAR(500),
    INDEX idx_channel (channel),
    INDEX idx_publish_time (publish_time),
    INDEX idx_source_url (source_url(255)),
    INDEX idx_channel_publish (channel, publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT NULL,
    like_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_news_id (news_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_news (user_id, news_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    duration INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    event_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    target_type VARCHAR(50),
    extra VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_type (event_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO news (title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, content) VALUES
('GPT-5正式发布：多模态推理能力实现质的飞跃', 'OpenAI今日正式发布GPT-5模型，在多模态推理、长文本理解和代码生成方面实现重大突破，引发行业广泛关注', 'AI', '科技日报', 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop', 5842, 328, 1, '2026-05-12 09:00:00', '<h2>革命性的多模态推理</h2><p>OpenAI在今日的全球直播发布会上正式推出了GPT-5模型。新模型在多模态推理方面实现了质的飞跃，能够同时处理文本、图像、音频和视频输入，并在跨模态推理任务中展现出前所未有的能力。据OpenAI CEO Sam Altman介绍，GPT-5在复杂推理基准测试中的表现比GPT-4提升了47%。</p><h2>长文本理解突破</h2><p>GPT-5的上下文窗口扩展至200万token，能够完整理解并分析超长文档。在法律合同审查、学术论文分析等专业场景中，GPT-5的准确率达到了专家水平的92%。这一突破使得AI在专业领域的应用前景更加广阔。</p><h2>代码生成能力升级</h2><p>在代码生成方面，GPT-5不仅能够生成更高质量的代码，还能理解整个代码库的架构，进行跨文件的代码重构和优化。在SWE-bench测试中，GPT-5的通过率从GPT-4的33%提升至61%，接近高级开发者的水平。</p><h2>行业影响与展望</h2><p>业内专家普遍认为，GPT-5的发布将加速AI在各行业的落地应用。从医疗诊断到金融分析，从教育辅导到科研辅助，多模态AI正在重新定义人机协作的边界。不过，也有学者呼吁关注AI安全与伦理问题，确保技术进步惠及全社会。</p>'),
('谷歌DeepMind发布AlphaFold 4：蛋白质结构预测进入新纪元', 'DeepMind最新发布的AlphaFold 4将蛋白质结构预测精度提升至原子级别，有望加速新药研发进程', 'AI', 'Nature科技', 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800&h=400&fit=crop', 3215, 186, 0, '2026-05-11 14:30:00', '<h2>原子级精度预测</h2><p>谷歌DeepMind团队发布了AlphaFold 4，将蛋白质结构预测的精度提升到了原子级别。新模型不仅能预测蛋白质的静态结构，还能模拟蛋白质的动态折叠过程，这对理解蛋白质功能和药物设计具有重大意义。</p><h2>加速新药研发</h2><p>AlphaFold 4的发布受到制药行业的极大关注。传统新药研发周期长达10-15年，而AlphaFold 4有望将靶点发现和先导化合物优化阶段缩短60%以上。多家跨国药企已宣布与DeepMind建立合作关系。</p><h2>开源与可及性</h2><p>DeepMind宣布AlphaFold 4的核心模型将继续开源，并通过Google Cloud提供算力支持。这一举措将使全球科研人员，尤其是发展中国家的研究者，能够平等地获取最前沿的蛋白质结构预测工具。</p>'),
('Apache Spark 4.0正式发布：性能提升3倍', 'Apache Spark 4.0带来了全新的Catalyst优化器、原生GPU支持和改进的流处理引擎，整体性能较3.x版本提升3倍', '大数据', 'InfoQ', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&h=400&fit=crop', 2876, 142, 0, '2026-05-10 10:00:00', '<h2>全新Catalyst优化器</h2><p>Apache Spark 4.0最核心的升级是全新的Catalyst查询优化器。新优化器引入了基于代价的自适应执行框架，能够在运行时动态调整执行计划，大幅减少数据倾斜和shuffle开销。在TPC-DS基准测试中，查询性能平均提升2.8倍。</p><h2>原生GPU加速</h2><p>Spark 4.0首次实现了原生GPU加速支持，通过RAPIDS加速库无缝集成NVIDIA GPU。在机器学习训练和大规模数据处理场景中，GPU加速可带来5-10倍的性能提升，使Spark成为真正的异构计算平台。</p><h2>流处理引擎重构</h2><p>Structured Streaming引擎在4.0版本中进行了全面重构，支持Exactly-Once语义的端到端保证，新增了滑动窗口和会话窗口的原生支持，延迟降低至100毫秒以内，满足更多实时业务场景需求。</p>'),
('Flink 2.0：流批一体架构的终极形态', 'Apache Flink 2.0正式发布，实现了真正的流批一体架构，统一了流处理和批处理的编程模型', '大数据', '开源中国', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&h=400&fit=crop', 2341, 98, 0, '2026-05-09 16:00:00', '<h2>流批一体架构</h2><p>Apache Flink 2.0实现了社区多年追求的流批一体愿景。新版本通过统一的Table API和SQL接口，开发者无需区分流处理和批处理场景，同一套代码即可同时满足实时和离线数据处理需求。</p><h2>状态管理革新</h2><p>Flink 2.0引入了全新的RocksDB状态后端，支持增量Checkpoint和局部恢复，状态恢复时间从分钟级降至秒级。同时新增了状态TTL和状态迁移API，极大简化了状态管理的复杂度。</p><h2>生态整合增强</h2><p>新版本深度整合了Apache Iceberg和Hudi等数据湖组件，支持ACID事务和时间旅行查询。与Kafka、Pulsar的集成也进行了优化，端到端延迟降低40%以上。</p>'),
('AWS发布Graviton5芯片：云原生算力再升级', '亚马逊AWS发布第五代自研Arm芯片Graviton5，性能较上代提升50%，能效比提升60%', '云计算', 'AWS Blog', 'https://images.unsplash.com/photo-1544197150-b99a3bb10b7e?w=800&h=400&fit=crop', 1987, 76, 0, '2026-05-08 11:00:00', '<h2>Graviton5架构解析</h2><p>AWS Graviton5采用4nm工艺制程，搭载128个Neoverse V3核心，主频提升至3.2GHz。新芯片在整数运算、浮点运算和加密性能方面均有显著提升，特别优化了AI推理和数据库工作负载。</p><h2>能效比突破</h2><p>Graviton5在提供更高性能的同时，能效比较Graviton4提升了60%。AWS表示，基于Graviton5的EC2实例在运行典型云原生工作负载时，碳排放量可减少40%以上，助力企业实现绿色计算目标。</p><h2>实例类型与定价</h2><p>AWS同步推出了基于Graviton5的M7g、C7g和R7g实例系列，起步价格与Graviton4实例持平。这意味着客户无需增加预算即可获得50%的性能提升，进一步巩固了Arm架构在云计算领域的性价比优势。</p>'),
('阿里云发布通义千问3.0大模型及PAI 4.0平台', '阿里云在年度峰会上发布通义千问3.0大模型和PAI 4.0机器学习平台，全面升级AI基础设施', '云计算', '36氪', 'https://images.unsplash.com/photo-1544197150-b99a3bb10b7e?w=800&h=400&fit=crop', 2543, 134, 0, '2026-05-07 09:30:00', '<h2>通义千问3.0能力跃升</h2><p>阿里云发布的通义千问3.0在中文理解、代码生成和多轮对话方面实现了显著提升。新模型支持128K上下文窗口，在C-Eval和CMMLU等中文基准测试中刷新纪录，同时在数学推理和代码生成方面接近GPT-4水平。</p><h2>PAI 4.0平台升级</h2><p>PAI 4.0机器学习平台引入了AutoML 2.0能力，支持从特征工程到模型部署的全流程自动化。新增的分布式训练框架可线性扩展至万卡规模，训练效率提升3倍，使企业能够更高效地训练和部署大模型。</p><h2>行业解决方案</h2><p>阿里云同步推出了金融、医疗、制造等8个行业大模型解决方案。这些方案基于通义千问3.0底座，融合行业知识和数据，开箱即用，帮助企业快速构建AI应用。</p>'),
('字节跳动豆包AI月活突破2亿', '字节跳动旗下AI助手豆包月活跃用户突破2亿，成为全球增长最快的AI应用之一', '互联网', '晚点LatePost', 'https://images.unsplash.com/photo-1488229297570-58520851e68c?w=800&h=400&fit=crop', 4123, 267, 1, '2026-05-12 08:00:00', '<h2>用户增长神话</h2><p>字节跳动旗下AI助手豆包宣布月活跃用户突破2亿大关，距离1亿用户仅过去4个月。这一增长速度远超ChatGPT同期表现，成为中国乃至全球增长最快的AI消费级应用。豆包的成功得益于抖音生态的流量加持和精准的产品定位。</p><h2>产品差异化策略</h2><p>豆包的快速增长离不开其差异化策略。与通用AI助手不同，豆包深度整合了短视频创作、社交互动和电商导购场景，用户可以直接用AI生成短视频脚本、设计社交内容、获取购物推荐，形成了独特的"AI+内容"生态闭环。</p><h2>商业化探索</h2><p>在商业化方面，豆包已推出Pro会员和企业版。Pro会员定价29元/月，提供高级创作工具和优先推理服务；企业版则提供API接口和定制化模型训练服务。据内部人士透露，豆包的月营收已突破1亿元。</p><h2>行业竞争加剧</h2><p>豆包的崛起引发了AI应用市场的新一轮竞争。百度文心一言、腾讯混元助手、月之暗面Kimi等产品纷纷加大投入，AI应用赛道正从技术竞赛转向生态和用户体验的全方位比拼。</p>'),
('微信小程序日活突破6亿，生态GMV超5万亿', '腾讯公布微信生态最新数据：小程序日活突破6亿，生态GMV超过5万亿元', '互联网', '腾讯科技', 'https://images.unsplash.com/photo-1488229297570-58520851e68c?w=800&h=400&fit=crop', 1876, 145, 0, '2026-05-06 15:00:00', '<h2>小程序生态持续繁荣</h2><p>腾讯在微信公开课上公布了最新生态数据：微信小程序日活跃用户突破6亿，较去年同期增长25%。小程序生态年GMV超过5万亿元，覆盖零售、餐饮、出行、政务等200多个行业细分领域。</p><h2>AI能力全面开放</h2><p>微信宣布向小程序开发者全面开放AI能力，包括智能客服、内容生成、图像识别等。开发者可通过微信云开发一键接入大模型能力，无需自建AI基础设施，大幅降低了AI应用的开发门槛。</p><h2>商业化新举措</h2><p>微信推出了小程序广告组件3.0和视频号直播带货新工具，帮助开发者实现更高效的流量变现。同时，微信搜一搜的AI搜索功能也进行了升级，支持自然语言理解和多轮对话式搜索。</p>'),
('英伟达Blackwell Ultra GPU量产：AI训练成本再降50%', '英伟达Blackwell Ultra GPU正式量产，单卡算力较H100提升4倍，AI训练成本降低50%', '硬件', 'AnandTech', 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop', 3456, 198, 0, '2026-05-05 10:00:00', '<h2>Blackwell Ultra架构</h2><p>英伟达Blackwell Ultra GPU采用台积电4NP工艺，集成2080亿个晶体管，搭载192GB HBM3e显存，带宽达8TB/s。双Die设计通过10TB/s的NV-H互连连接，实现了前所未有的单卡算力密度。</p><h2>AI训练性能飞跃</h2><p>在AI训练方面，Blackwell Ultra的FP16算力达到5 PFLOPS，较H100提升4倍。配合第五代NVLink和Quantum-X800 InfiniBand，万卡集群的线性扩展效率超过90%，使训练万亿参数模型成为可能。</p><h2>成本与能效优势</h2><p>得益于架构优化和先进工艺，Blackwell Ultra在提供4倍性能的同时，每FLOP成本降低50%，每FLOP功耗降低40%。英伟达CEO黄仁勋表示，Blackwell Ultra将推动AI民主化，让更多组织能够负担大模型训练。</p>'),
('苹果M5芯片曝光：3nm工艺+16核GPU', '供应链消息透露苹果M5芯片将采用台积电3nm工艺，CPU和GPU核心数大幅增加', '硬件', 'MacRumors', 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&h=400&fit=crop', 1654, 89, 0, '2026-05-04 18:00:00', '<h2>M5芯片规格曝光</h2><p>据供应链可靠消息，苹果M5芯片将采用台积电第二代3nm工艺（N3E），集成12核CPU（8性能核+4能效核）和16核GPU。神经引擎核心数增至20个，AI推理性能较M4提升70%。</p><h2>端侧AI能力强化</h2><p>M5芯片的神经引擎专为端侧大模型推理优化，支持运行参数量不超过70亿的本地模型。苹果计划在M5 Mac上推出本地AI助手，可在完全离线状态下完成文本生成、图像编辑和代码辅助等任务。</p><h2>产品线规划</h2><p>M5芯片将首先搭载于MacBook Pro和Mac Studio，随后推出M5 Pro、M5 Max和M5 Ultra版本。M5 Ultra将采用四Die设计，GPU核心数高达64核，面向专业工作站市场。</p>'),
('AI独角兽月之暗面完成10亿美元融资', '月之暗面（Moonshot AI）完成10亿美元新一轮融资，估值达330亿美元', '创业', '36氪', 'https://images.unsplash.com/photo-1573164713714-d95e436ab8d9?w=800&h=400&fit=crop', 2890, 234, 0, '2026-05-03 12:00:00', '<h2>融资详情</h2><p>AI独角兽月之暗面宣布完成10亿美元新一轮融资，由阿里巴巴领投，腾讯、红杉中国等跟投。本轮融资后，月之暗面估值达到330亿美元，成为中国估值最高的AI初创公司。</p><h2>Kimi产品矩阵扩展</h2><p>月之暗面同步发布了Kimi企业版和Kimi开发者平台。Kimi企业版提供私有化部署和行业定制服务，已与50多家大型企业达成合作。Kimi开发者平台则开放了API和SDK，支持第三方开发者基于Kimi模型构建应用。</p><h2>技术路线与愿景</h2><p>月之暗面创始人杨植麟表示，公司将继续深耕长上下文和深度推理方向，计划在年内推出支持1000万token上下文的新模型。他强调，真正的AI突破不在于参数规模，而在于对复杂任务的深度理解和推理能力。</p>'),
('前字节AI Lab团队创办深度推理公司，获5亿天使轮', '前字节跳动AI Lab核心团队离职创办深度推理AI公司DeepLogic，获5亿元天使轮融资', '创业', '钛媒体', 'https://images.unsplash.com/photo-1573164713714-d95e436ab8d9?w=800&h=400&fit=crop', 1234, 67, 0, '2026-05-02 09:00:00', '<h2>团队背景</h2><p>DeepLogic由前字节跳动AI Lab主任研究员张明博士领衔创办，核心团队来自字节、百度和清华，在深度推理和知识图谱领域拥有丰富的研究和工程经验。团队曾在顶级会议发表论文50余篇。</p><h2>技术方向</h2><p>DeepLogic专注于深度推理AI，致力于解决大模型在复杂逻辑推理、数学证明和科学发现方面的瓶颈。公司自主研发的ReasonNet架构，通过引入显式推理链和验证机制，在数学推理任务上的准确率较传统方法提升35%。</p><h2>融资与规划</h2><p>本轮融资由高瓴创投领投，源码资本和真格基金跟投。DeepLogic计划用这笔资金建设推理算力集群，招聘顶尖研究人才，并在年内推出首款面向科研和金融领域的深度推理产品。</p>'),
('ClickHouse Cloud正式支持中国区部署', 'ClickHouse宣布其云服务ClickHouse Cloud正式支持中国区部署，提供本地化数据存储和合规保障', '大数据', 'DBEngines', 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&h=400&fit=crop', 1567, 78, 0, '2026-05-01 14:00:00', '<h2>中国区部署详情</h2><p>ClickHouse Cloud中国区部署基于阿里云基础设施，数据完全存储在中国境内，满足数据本地化合规要求。服务提供与全球版一致的功能，包括自动扩缩容、读写分离和跨区域复制等企业级特性。</p><h2>性能优化</h2><p>针对中国用户的使用场景，ClickHouse Cloud中国版进行了多项优化，包括与阿里云OSS的深度集成、针对时序数据的查询加速，以及中文全文检索支持。在标准基准测试中，查询性能较自建集群提升30%。</p><h2>定价与生态</h2><p>ClickHouse Cloud中国区提供入门版、企业版和旗舰版三个层级，入门版起步价为每月299元人民币。同时，ClickHouse与阿里云DataWorks、Flink等大数据组件实现了深度集成，方便企业构建端到端的数据分析管道。</p>'),
('微软Azure AI推出企业级AI Agent平台', '微软Azure AI推出全新的AI Agent平台，支持企业快速构建和部署自主AI代理', '云计算', 'The Verge', 'https://images.unsplash.com/photo-1544197150-b99a3bb10b7e?w=800&h=400&fit=crop', 2109, 112, 0, '2026-04-30 11:00:00', '<h2>AI Agent平台架构</h2><p>微软Azure AI Agent平台基于GPT-5和Phi-4模型构建，提供从Agent设计、训练、部署到监控的全生命周期管理。平台支持多Agent协作、工具调用、记忆管理和安全沙箱等核心能力，使企业能够快速构建复杂的AI自动化工作流。</p><h2>企业级安全与合规</h2><p>平台内置了企业级安全框架，包括数据隔离、访问控制、审计日志和内容安全过滤。所有Agent交互均通过微软Purview合规平台监控，确保AI行为符合企业策略和监管要求。已通过SOC 2、HIPAA等认证。</p><h2>合作伙伴生态</h2><p>微软宣布与SAP、Salesforce、ServiceNow等企业软件厂商建立Agent生态合作，提供预构建的集成Agent模板。企业用户可从Agent市场直接部署CRM自动化、IT运维、财务审批等场景化Agent，大幅缩短实施周期。</p>');
