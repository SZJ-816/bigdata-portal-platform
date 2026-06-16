const now = Date.now()
const minute = 60 * 1000
const hour = 60 * minute

export const channels = [
  { name: 'AI', label: '人工智能' },
  { name: '大数据', label: '大数据' },
  { name: '云计算', label: '云计算' },
  { name: '互联网', label: '互联网' },
  { name: '硬件', label: '硬件' },
  { name: '创业', label: '创业' },
]

const imageIds = [
  '1677442136019-21780ecad995',
  '1518770660439-4636190af475',
  '1558494949-ef010cbdcc31',
  '1544197150-b99a3bb10b7e',
  '1488229297570-58520851e68c',
  '1593642632559-0c6d3fc62b89',
  '1573164713714-d95e436ab8d9',
  '1550751827-4bd946c0558c',
  '1620712943543-bcc4688e7485',
  '1535378917042-10a22a9598a5',
  '1589829545856-d10d557cf95f',
  '1488229297570-58520851e68c',
]

const getImage = (index) => {
  const id = imageIds[index % imageIds.length]
  return `https://imagecdn.oss-cn-beijing.aliyuncs.com/tech-news/${id}?w=800&h=400&fit=crop`
}

export const newsList = [
  {
    id: 1,
    title: 'OpenAI发布GPT-5模型：多模态推理能力实现质的飞跃',
    summary: 'OpenAI今日正式发布GPT-5模型，在多模态推理、长文本理解和代码生成方面实现重大突破，基准测试成绩较GPT-4提升47%。',
    content: `<p>OpenAI于今日正式发布了备受期待的GPT-5模型，这一发布标志着大语言模型技术进入全新阶段。据OpenAI官方介绍，GPT-5在多模态推理、长文本理解和代码生成等多个核心能力上实现了重大突破。</p><p>在基准测试中，GPT-5的综合得分较前代GPT-4提升了47%，其中数学推理能力提升最为显著，达到62%。模型参数规模虽未公开，但据业内分析人士估计，GPT-5的训练数据量和计算资源投入均远超前代。</p><p>值得注意的是，GPT-5首次引入了"持续学习"机制，能够在推理过程中动态更新知识库，这意味着模型不再受限于训练数据的截止日期。OpenAI CEO表示，这一技术突破将彻底改变AI与人类协作的方式。</p><p>业内专家普遍认为，GPT-5的发布将加速AI在医疗、金融、教育等领域的深度应用，同时也引发了关于AI安全和伦理问题的新一轮讨论。</p>`,
    channel: 'AI',
    source: '科技日报',
    imageUrl: 'https://picsum.photos/seed/tech1/800/400',
    viewCount: 28456,
    commentCount: 342,
    publishTime: now - 15 * minute,
    isBreaking: true,
  },
  {
    id: 2,
    title: '英伟达市值突破4万亿美元 AI芯片需求持续井喷',
    summary: '受全球AI基础设施建设浪潮推动，英伟达股价再创历史新高，市值首次突破4万亿美元大关，成为全球最有价值的芯片公司。',
    content: `<p>在全球AI基础设施建设浪潮的持续推动下，英伟达（NVIDIA）股价于本周三再创历史新高，市值首次突破4万亿美元大关。这一里程碑式的成就使英伟达稳居全球最有价值的半导体公司宝座。</p><p>分析人士指出，数据中心对高性能GPU的需求仍在持续增长，尤其是随着大模型训练和推理需求的爆发式增长，英伟达的H100和Blackwell架构芯片供不应求。公司最新财报显示，数据中心业务营收同比增长超过200%。</p><p>英伟达CEO黄仁勋在投资者会议上表示，AI计算的需求才刚刚开始释放，未来十年将看到AI从云端向边缘端全面渗透。他预测，到2030年，全球AI基础设施投资将超过2万亿美元。</p><p>不过也有分析师提醒，英伟达面临来自AMD、英特尔以及自研芯片的科技巨头的日益激烈竞争，市场格局可能在未来几年发生显著变化。</p>`,
    channel: '硬件',
    source: '财经时报',
    imageUrl: 'https://picsum.photos/seed/tech2/800/400',
    viewCount: 21340,
    commentCount: 256,
    publishTime: now - 32 * minute,
    isBreaking: true,
  },
  {
    id: 3,
    title: 'Apache Flink 2.0正式发布 流计算架构全面升级',
    summary: 'Apache Flink社区宣布2.0版本正式发布，新版本在状态管理、批流一体和云原生部署方面进行了全面架构升级。',
    content: `<p>Apache Flink社区正式宣布2.0版本发布，这是该项目自2016年1.0版本以来最重要的架构升级。新版本在状态管理、批流一体和云原生部署方面进行了全面革新。</p><p>Flink 2.0引入了全新的状态后端"StateForge"，支持TB级状态数据的毫秒级检查点，较原有RocksDB后端性能提升5倍以上。同时，批流一体的SQL引擎经过重写，在批处理场景下的性能已接近专业OLAP引擎水平。</p><p>在云原生方面，Flink 2.0原生支持Kubernetes部署，实现了自动弹性扩缩容和资源隔离。社区还推出了Flink Operator，简化了在K8s上的运维管理流程。</p><p>阿里巴巴、字节跳动等国内大厂已率先在生产环境中部署Flink 2.0，反馈积极。社区预计，这一版本将进一步巩固Flink在实时计算领域的领导地位。</p>`,
    channel: '大数据',
    source: '开源中国',
    imageUrl: 'https://picsum.photos/seed/tech3/800/400',
    viewCount: 15678,
    commentCount: 189,
    publishTime: now - 1 * hour,
    isBreaking: false,
  },
  {
    id: 4,
    title: '阿里云发布通义千问3.0 企业级AI应用落地加速',
    summary: '阿里云在年度峰会上发布通义千问3.0，新增企业知识库、多Agent协作和私有化部署能力，推动AI在行业场景的深度落地。',
    content: `<p>阿里云在2026年度AI峰会上正式发布通义千问3.0版本，重点面向企业级AI应用场景，新增了企业知识库、多Agent协作和私有化部署三大核心能力。</p><p>通义千问3.0的企业知识库功能支持企业将内部文档、数据库和业务系统数据快速接入大模型，实现基于企业私有知识的智能问答和决策辅助。多Agent协作框架则允许用户构建复杂的AI工作流，多个专业Agent协同完成业务任务。</p><p>在私有化部署方面，阿里云推出了"通义一体机"解决方案，企业可在本地机房部署完整的通义千问能力，满足数据安全和合规要求。首批客户包括多家国有银行和大型制造企业。</p><p>阿里云智能总裁表示，通义千问3.0的目标是让每一家企业都能拥有自己的AI大脑，真正实现AI从"能用"到"好用"的跨越。</p>`,
    channel: '云计算',
    source: '36氪',
    imageUrl: 'https://picsum.photos/seed/tech4/800/400',
    viewCount: 18920,
    commentCount: 234,
    publishTime: now - 2 * hour,
    isBreaking: false,
  },
  {
    id: 5,
    title: '字节跳动豆包大模型日均调用量突破100亿次',
    summary: '字节跳动宣布旗下豆包大模型日均API调用量突破100亿次，成为国内调用量最大的大模型服务之一。',
    content: `<p>字节跳动在今日举办的AI开放日上宣布，旗下豆包大模型的日均API调用量已突破100亿次，成为国内调用量最大的大模型服务之一。这一数字较半年前增长了3倍。</p><p>豆包大模型的快速增长主要得益于字节系产品的全面AI化改造。抖音、飞书、剪映等核心产品均已深度集成豆包能力，从内容推荐、智能创作到办公协作，AI已渗透到用户日常使用的各个环节。</p><p>此外，字节跳动还面向企业客户推出了豆包企业版，提供定制化模型训练和行业解决方案。目前已服务超过5万家企业客户，覆盖金融、零售、教育等行业。</p><p>字节跳动AI实验室负责人表示，下一步将重点推进多模态理解和生成能力的突破，计划在年内推出支持实时视频理解的下一代模型。</p>`,
    channel: '互联网',
    source: '界面新闻',
    imageUrl: 'https://picsum.photos/seed/tech5/800/400',
    viewCount: 16789,
    commentCount: 198,
    publishTime: now - 3 * hour,
    isBreaking: false,
  },
  {
    id: 6,
    title: '苹果M5芯片曝光：3nm工艺AI算力翻倍',
    summary: '供应链消息显示苹果M5芯片已进入量产阶段，采用台积电3nm工艺，神经网络引擎算力较M4提升超过100%。',
    content: `<p>据供应链最新消息，苹果下一代自研芯片M5已进入量产阶段，预计将搭载于今年秋季发布的新款MacBook Pro和iPad Pro产品线。</p><p>M5芯片将继续采用台积电3nm工艺制程，但在架构设计上进行了重大调整。最显著的变化是神经网络引擎（NPU）的算力较M4提升超过100%，达到每秒40万亿次运算。这一提升将为设备端AI推理提供强大支撑。</p><p>CPU方面，M5将采用全新的高性能核心设计，单核性能提升约25%，多核性能提升约35%。GPU核心数量也有所增加，光追性能大幅改善。统一内存架构支持最高192GB容量。</p><p>业内分析认为，M5芯片的AI算力跃升是苹果"Apple Intelligence"战略的关键一步，将使更多AI功能能够在设备本地完成，减少对云端计算的依赖。</p>`,
    channel: '硬件',
    source: '电子工程专辑',
    imageUrl: 'https://picsum.photos/seed/tech6/800/400',
    viewCount: 23456,
    commentCount: 312,
    publishTime: now - 4 * hour,
    isBreaking: false,
  },
  {
    id: 7,
    title: 'AI编程助手市场爆发 GitHub Copilot营收突破10亿美元',
    summary: 'GitHub宣布Copilot年化营收突破10亿美元，AI编程工具赛道竞争白热化，Cursor、Codeium等新兴玩家快速崛起。',
    content: `<p>GitHub在年度开发者大会上宣布，旗下AI编程助手Copilot的年化营收已突破10亿美元，付费订阅用户超过180万，成为增长最快的企业SaaS产品之一。</p><p>Copilot的成功也催生了一个蓬勃的AI编程工具市场。Cursor凭借深度代码理解和多文件编辑能力快速获得开发者青睐，估值已达25亿美元。Codeium则以免费策略和本地部署能力切入市场，企业客户增长迅猛。</p><p>国内市场同样热闹，字节跳动的Trae、阿里的通义灵码、百度的Comate等产品纷纷入局。据不完全统计，全球AI编程工具市场规模已超过50亿美元，预计到2028年将增长至200亿美元。</p><p>不过，AI编程工具的快速发展也引发了关于代码质量、安全性和开发者技能退化的担忧。多位技术领袖呼吁行业建立AI辅助编程的最佳实践和伦理准则。</p>`,
    channel: 'AI',
    source: 'InfoQ',
    imageUrl: 'https://picsum.photos/seed/tech7/800/400',
    viewCount: 14567,
    commentCount: 178,
    publishTime: now - 5 * hour,
    isBreaking: false,
  },
  {
    id: 8,
    title: 'ClickHouse Cloud全球扩张 亚太数据中心正式启用',
    summary: 'ClickHouse宣布亚太区域三个数据中心正式启用，分别为东京、新加坡和孟买，加速服务亚洲市场企业客户。',
    content: `<p>开源实时分析数据库ClickHouse宣布其云服务ClickHouse Cloud的亚太区域三个数据中心正式启用，分别位于东京、新加坡和孟买。</p><p>这一扩张标志着ClickHouse Cloud已覆盖全球主要区域。亚太数据中心的启用将显著降低亚洲用户的查询延迟，从之前的200-300毫秒降低至20毫秒以内，满足实时分析场景的严苛要求。</p><p>ClickHouse CEO表示，亚太地区尤其是中国市场对实时数据分析的需求增长迅猛，金融风控、广告投放、物联网监控等场景对毫秒级查询响应有着刚性需求。</p><p>目前，ClickHouse Cloud已服务超过3000家企业客户，包括多家中国互联网头部企业。公司计划在2027年进一步扩展至中东和非洲市场。</p>`,
    channel: '大数据',
    source: 'DBEngines',
    imageUrl: 'https://picsum.photos/seed/tech8/800/400',
    viewCount: 8934,
    commentCount: 98,
    publishTime: now - 6 * hour,
    isBreaking: false,
  },
  {
    id: 9,
    title: '腾讯云发布新一代云原生数据库TDSQL-C 极致弹性架构',
    summary: '腾讯云发布新一代云原生数据库TDSQL-C，采用计算存储分离架构，支持秒级弹性扩缩容和Serverless按需计费模式。',
    content: `<p>腾讯云在数据库技术峰会上发布了新一代云原生数据库TDSQL-C，该产品采用计算存储分离的极致弹性架构，旨在满足AI时代数据库工作负载的动态变化需求。</p><p>TDSQL-C的核心创新在于其"无边界"弹性能力。计算节点可在3秒内完成扩缩容，存储层基于分布式共享存储架构，容量可自动扩展至PB级别。Serverless模式下，用户只需为实际使用的计算资源付费，空闲时成本可降至零。</p><p>在兼容性方面，TDSQL-C完整兼容MySQL 8.0和PostgreSQL 16协议，支持无缝迁移。同时集成了AI驱动的智能索引推荐和查询优化功能，可自动识别慢查询并提供优化建议。</p><p>腾讯云数据库负责人表示，TDSQL-C已在微信支付、王者荣耀等核心业务中完成验证，峰值QPS超过千万级，可用性达到99.999%。</p>`,
    channel: '云计算',
    source: '腾讯科技',
    imageUrl: 'https://picsum.photos/seed/tech9/800/400',
    viewCount: 11234,
    commentCount: 145,
    publishTime: now - 7 * hour,
    isBreaking: false,
  },
  {
    id: 10,
    title: '美团即时零售业务日订单量突破3000万 AI调度系统立功',
    summary: '美团宣布即时零售业务日订单量突破3000万单，背后AI智能调度系统每秒处理超过50万次路径计算。',
    content: `<p>美团宣布其即时零售业务日订单量正式突破3000万单，创下行业新纪录。公司表示，这一里程碑的实现离不开其AI智能调度系统的持续进化。</p><p>美团调度系统"超脑"目前已升级至第四代，每秒可处理超过50万次路径计算，平均配送时长缩短至28分钟。系统综合考量骑手位置、订单密度、天气状况、交通状况等数百个维度，实现全局最优调度。</p><p>在AI预测方面，超脑系统能够提前30分钟预测各区域的订单需求，准确率达到92%以上。基于预测结果，系统会提前进行骑手预调度，有效降低高峰期配送延迟率。</p><p>美团技术团队透露，下一代调度系统将引入大模型能力，实现更精细的个性化配送服务和异常情况智能处理。</p>`,
    channel: '互联网',
    source: '晚点LatePost',
    imageUrl: 'https://picsum.photos/seed/tech10/800/400',
    viewCount: 13456,
    commentCount: 167,
    publishTime: now - 8 * hour,
    isBreaking: false,
  },
  {
    id: 11,
    title: 'AI芯片初创公司"芯擎智能"完成10亿元B轮融资',
    summary: '国产AI芯片初创企业芯擎智能完成10亿元B轮融资，由深创投领投，将用于下一代推理芯片的研发和量产。',
    content: `<p>国产AI芯片初创企业芯擎智能宣布完成10亿元人民币B轮融资，本轮融资由深创投领投，国投创合、中金资本跟投，老股东红杉中国继续加注。</p><p>芯擎智能成立于2023年，专注于大模型推理芯片的设计与开发。公司首款产品"星云-1"推理芯片已于去年底流片成功，在INT8精度下算力达到400 TOPS，功耗仅75W，性能功耗比达到国际领先水平。</p><p>本轮融资将主要用于下一代"星云-2"芯片的研发和量产。据透露，星云-2将采用7nm工艺，支持MoE架构模型的高效推理，预计2027年初量产交付。</p><p>芯擎智能CEO表示，随着大模型从训练走向推理，推理芯片市场将迎来爆发式增长。公司目标是在2028年前成为国内领先的AI推理芯片供应商。</p>`,
    channel: '创业',
    source: '投资界',
    imageUrl: 'https://picsum.photos/seed/tech11/800/400',
    viewCount: 9876,
    commentCount: 123,
    publishTime: now - 10 * hour,
    isBreaking: false,
  },
  {
    id: 12,
    title: '台积电2nm工艺良率突破80% 量产在即',
    summary: '台积电2nm（N2）工艺良率已突破80%关键门槛，预计2027年上半年进入量产，苹果、英伟达为首批客户。',
    content: `<p>据半导体行业最新消息，台积电2nm（N2）工艺的良率已突破80%的关键门槛，标志着这一先进制程已具备量产条件。台积电计划在2027年上半年正式启动2nm量产。</p><p>2nm工艺将首次采用全环绕栅极（GAA）晶体管架构，取代沿用多年的FinFET架构。据台积电公布的数据，N2工艺在相同功耗下性能提升15%，在相同性能下功耗降低30%，晶体管密度提升约20%。</p><p>苹果和英伟达已确认为2nm工艺的首批客户。苹果预计将在A21和M6芯片中率先采用2nm工艺，英伟达则计划用于下一代GPU架构。</p><p>台积电已在新竹和高雄建设2nm晶圆厂，总投资超过400亿美元。业界预计2nm芯片的晶圆价格将超过3万美元，较3nm上涨约50%。</p>`,
    channel: '硬件',
    source: '半导体行业观察',
    imageUrl: 'https://picsum.photos/seed/tech12/800/400',
    viewCount: 19876,
    commentCount: 234,
    publishTime: now - 12 * hour,
    isBreaking: false,
  },
  {
    id: 13,
    title: 'Kafka 4.0发布：彻底告别ZooKeeper KRaft模式成为默认',
    summary: 'Apache Kafka 4.0正式发布，KRaft模式成为默认且唯一的元数据管理方式，ZooKeeper依赖被完全移除。',
    content: `<p>Apache Kafka社区宣布4.0版本正式发布，这是Kafka发展历程中的又一个重要里程碑。最显著的变化是KRaft模式成为默认且唯一的元数据管理方式，对ZooKeeper的依赖被完全移除。</p><p>KRaft（Kafka Raft）模式自3.3版本引入以来，经过多个版本的迭代和验证，已在生产环境中证明了其稳定性和可靠性。Kafka 4.0中，KRaft模式成为唯一选择，这意味着运维人员不再需要维护独立的ZooKeeper集群，大幅简化了部署和运维复杂度。</p><p>除了移除ZooKeeper，Kafka 4.0还带来了多项性能优化。分区领导选举速度提升10倍，控制器故障恢复时间从分钟级降至秒级。新增的分层存储（Tiered Storage）功能支持将冷数据自动迁移至对象存储，显著降低存储成本。</p><p>社区预计，Kafka 4.0将加速企业在云原生环境中的流数据平台建设，已有包括LinkedIn、Confluent等在内的多家企业完成升级验证。</p>`,
    channel: '大数据',
    source: 'Apache官方',
    imageUrl: 'https://picsum.photos/seed/tech13/800/400',
    viewCount: 12345,
    commentCount: 156,
    publishTime: now - 14 * hour,
    isBreaking: false,
  },
  {
    id: 14,
    title: '小红书电商GMV突破5000亿 AI内容理解驱动精准推荐',
    summary: '小红书2025年电商GMV突破5000亿元，AI内容理解技术在商品推荐和搜索中发挥了关键作用。',
    content: `<p>据知情人士透露，小红书2025年全年电商GMV已突破5000亿元人民币，同比增长超过80%，成为增长最快的电商平台之一。</p><p>小红书电商的快速增长离不开其AI内容理解技术的深度应用。通过多模态大模型对笔记内容进行深度理解，小红书能够实现"所见即所得"的商品推荐——用户看到一篇穿搭笔记，系统即可精准识别笔记中的每件单品并推荐购买链接。</p><p>在搜索方面，小红书的AI搜索理解能力也大幅提升。用户可以用自然语言描述需求，如"适合海边度假的连衣裙"，系统会综合理解语义、场景和风格，返回最匹配的结果。</p><p>小红书技术负责人表示，公司将继续加大AI投入，重点突破视频内容理解和实时个性化推荐能力，进一步提升用户购物体验和转化效率。</p>`,
    channel: '互联网',
    source: '36氪',
    imageUrl: 'https://picsum.photos/seed/tech14/800/400',
    viewCount: 16789,
    commentCount: 201,
    publishTime: now - 16 * hour,
    isBreaking: false,
  },
  {
    id: 15,
    title: 'AI法律科技赛道升温 "法智"完成5亿元C轮融资',
    summary: 'AI法律科技公司"法智"完成5亿元C轮融资，其AI律师助手已服务超过10万法律从业者，覆盖合同审查、案件分析等场景。',
    content: `<p>AI法律科技公司"法智"宣布完成5亿元人民币C轮融资，本轮融资由IDG资本领投，源码资本、高瓴创投跟投。公司估值达到50亿元。</p><p>法智成立于2022年，是国内领先的AI法律科技企业。其核心产品"法智助手"基于自研法律大模型，已服务超过10万名法律从业者，覆盖合同审查、案件分析、法律咨询、文书生成等多个场景。</p><p>据公司披露，法智助手在合同审查场景中的准确率达到97%，效率较人工审查提升20倍。在案件分析方面，系统能够自动提取案件关键事实、匹配相关法条和判例，辅助律师制定诉讼策略。</p><p>法智CEO表示，本轮融资将用于法律大模型的持续训练和垂直场景的深度拓展，计划在2027年前将服务范围扩展至企业法务和个人法律咨询领域。</p>`,
    channel: '创业',
    source: '创业邦',
    imageUrl: 'https://picsum.photos/seed/tech15/800/400',
    viewCount: 7654,
    commentCount: 89,
    publishTime: now - 18 * hour,
    isBreaking: false,
  },
  {
    id: 16,
    title: '华为云盘古大模型5.0发布 深耕行业大模型路线',
    summary: '华为云在开发者大会上发布盘古大模型5.0，坚持"不做通用聊天，深耕行业"路线，已落地矿山、气象、金融等10余个行业。',
    content: `<p>华为云在2026 HDC开发者大会上正式发布盘古大模型5.0。与业界追求通用能力的趋势不同，华为始终坚持"不做通用聊天，深耕行业"的战略路线。</p><p>盘古5.0在行业深度上实现了新的突破。在矿山领域，盘古矿山大模型已能自主完成采掘设备的智能调度和安全隐患的实时识别，帮助某大型煤矿将生产效率提升30%，安全事故率下降60%。</p><p>在气象领域，盘古气象大模型的全球天气预报精度首次超越传统数值预报方法，预测速度提升10000倍。在金融领域，盘古金融大模型已在多家国有银行部署，用于智能风控和合规审查。</p><p>华为云CEO表示，盘古5.0已覆盖矿山、气象、金融、医药、制造、政务等10余个行业，服务超过500家大型企业客户。未来将继续加大行业数据的积累和模型能力的深耕。</p>`,
    channel: '云计算',
    source: '华为官方',
    imageUrl: 'https://picsum.photos/seed/tech16/800/400',
    viewCount: 15678,
    commentCount: 189,
    publishTime: now - 20 * hour,
    isBreaking: false,
  },
  {
    id: 17,
    title: '全球首条AI全自动芯片设计流片成功 从RTL到GDS仅需72小时',
    summary: '一家硅谷初创公司宣布全球首条AI全自动芯片设计流程流片成功，从RTL代码到GDS交付仅需72小时，传统流程需6-9个月。',
    content: `<p>硅谷芯片设计自动化初创公司ChipGenius宣布，全球首条AI全自动芯片设计流程已成功流片。该流程从RTL代码输入到GDS交付仅需72小时，而传统芯片设计流程通常需要6-9个月。</p><p>ChipGenius的AI芯片设计平台集成了大语言模型、强化学习和图神经网络等多种AI技术，能够自动完成逻辑综合、物理设计、时序优化、功耗分析等全流程任务。平台已通过台积电3nm工艺的流片验证。</p><p>此次流片的测试芯片是一颗RISC-V处理器，包含超过10亿个晶体管。流片后的测试结果显示，芯片功能和性能均达到设计预期，良率在正常范围内。</p><p>这一突破在半导体行业引发巨大震动。多位芯片设计专家表示，AI全自动芯片设计如果成熟，将彻底改变芯片行业的游戏规则，大幅降低芯片设计的门槛和成本。</p>`,
    channel: 'AI',
    source: 'IEEE Spectrum',
    imageUrl: 'https://picsum.photos/seed/tech17/800/400',
    viewCount: 21345,
    commentCount: 278,
    publishTime: now - 22 * hour,
    isBreaking: false,
  },
  {
    id: 18,
    title: 'Snowflake与Databricks合并谈判曝光 大数据领域或将诞生超级巨头',
    summary: '据华尔街日报报道，Snowflake与Databricks正在进行合并谈判，交易金额可能超过800亿美元，将重塑大数据行业格局。',
    content: `<p>据《华尔街日报》独家报道，云数据仓库巨头Snowflake与数据湖仓平台Databricks正在就合并事宜进行深入谈判。若交易达成，合并金额可能超过800亿美元，将成为软件行业历史上最大的并购交易之一。</p><p>两家公司在大数据领域各有优势。Snowflake在云数据仓库和SQL分析方面占据领先地位，拥有超过10000家企业客户；Databricks则在数据工程和AI/ML平台方面实力强劲，Spark和Delta Lake拥有庞大的开发者生态。</p><p>分析人士认为，两者的合并将创造一个覆盖数据存储、处理、分析和AI全链路的超级平台，对传统数据库厂商和云厂商的数据服务构成重大威胁。</p><p>不过，这笔交易面临严格的反垄断审查。多位法律专家表示，合并后的公司在大数据分析市场的份额可能触发监管机构的深度调查。</p>`,
    channel: '大数据',
    source: '华尔街日报',
    imageUrl: 'https://picsum.photos/seed/tech18/800/400',
    viewCount: 25678,
    commentCount: 345,
    publishTime: now - 24 * hour,
    isBreaking: false,
  },
]

export const hotSearchWords = ['GPT-5', '英伟达', 'Flink 2.0', '通义千问', 'M5芯片', 'Kafka 4.0', 'AI编程', '2nm工艺', '盘古5.0', '芯擎智能']

export function formatRelativeTime(timestamp) {
  const diff = Date.now() - timestamp
  const mins = Math.floor(diff / minute)
  const hours = Math.floor(diff / hour)
  const days = Math.floor(diff / (24 * hour))
  if (mins < 1) return '刚刚'
  if (mins < 60) return `${mins}分钟前`
  if (hours < 24) return `${hours}小时前`
  return `${days}天前`
}

export function getNewsById(id) {
  return newsList.find(n => n.id === Number(id))
}

export function getNewsByChannel(channel) {
  return newsList.filter(n => n.channel === channel)
}

export function getHotNews(limit = 10) {
  return [...newsList].sort((a, b) => b.viewCount - a.viewCount).slice(0, limit)
}

export function searchNews(keyword) {
  const kw = keyword.toLowerCase()
  return newsList.filter(n =>
    n.title.toLowerCase().includes(kw) ||
    n.summary.toLowerCase().includes(kw) ||
    n.channel.toLowerCase().includes(kw)
  )
}
