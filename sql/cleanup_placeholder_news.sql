-- 清理占位测试内容

-- 删除云计算频道的占位内容
DELETE FROM news WHERE title IN (
    '云原生技术2026年发展',
    '多云架构最佳实践',
    'Serverless架构深度解析',
    '云成本优化策略',
    '云网络架构设计最佳实践',
    '容器安全最佳实践',
    '云存储技术选型指南'
);

-- 删除大数据频道的占位内容
DELETE FROM news WHERE title IN (
    '2026年大数据发展趋势分析',
    '实时流数据处理技术综述',
    '数据湖与数据仓库架构对比',
    '数据安全与隐私保护实践'
);

-- 删除创业频道的占位内容
DELETE FROM news WHERE title IN (
    '2026年创业成功案例分析',
    'AI时代的创业机遇',
    '融资攻略：如何获得种子轮投资'
);

-- 删除硬件频道的占位内容
DELETE FROM news WHERE title IN (
    '新型AI芯片发布，性能提升5倍',
    '2026年消费电子新品汇总',
    '边缘计算设备发展迅速'
);

-- 删除软件频道的占位内容
DELETE FROM news WHERE title IN (
    '新一代开发工具提升效率50%',
    '低代码平台2026年发展趋势',
    '开源软件最新动态'
);

-- 删除source_url为example.com的占位内容
DELETE FROM news WHERE source_url LIKE 'https://example.com%';

-- 验证删除结果
SELECT channel, COUNT(*) as count FROM news GROUP BY channel ORDER BY channel;
