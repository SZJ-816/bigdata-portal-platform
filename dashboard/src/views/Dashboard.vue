<template>
  <div class="dashboard">
    <header class="top-bar">
      <div class="bar-inner">
        <div class="brand" @click="goHome" style="cursor:pointer">
          <img src="/logo.jpg" alt="智讯" class="brand-icon" />
          <span class="brand-name">智讯</span>
          <span class="brand-sub">科技新闻数据大屏</span>
        </div>
        <nav class="main-nav">
          <button :class="['nav-link', { active: activeTab === 'overview' }]" @click="switchTab('overview')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="7" height="7" rx="1.5"/>
              <rect x="14" y="3" width="7" height="7" rx="1.5"/>
              <rect x="3" y="14" width="7" height="7" rx="1.5"/>
              <rect x="14" y="14" width="7" height="7" rx="1.5"/>
            </svg>数据总览
          </button>
          <button :class="['nav-link', { active: activeTab === 'news' }]" @click="switchTab('news')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <line x1="10" y1="9" x2="8" y2="9"/>
            </svg>新闻浏览
          </button>
          <button :class="['nav-link', { active: activeTab === 'behavior' }]" @click="switchTab('behavior')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="7" r="4"/>
              <path d="M5.5 21a6.5 6.5 0 0 1 13 0"/>
              <polyline points="17 11 19 14 22 12"/>
            </svg>实时行为
          </button>
          <span class="nav-separator"></span>
          <button :class="['nav-link', 'nav-link-admin', { active: activeTab === 'newsManage' }]" @click="switchTab('newsManage')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>新闻管理
          </button>
          <button :class="['nav-link', 'nav-link-admin', { active: activeTab === 'userManage' }]" @click="switchTab('userManage')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>用户管理
          </button>
          <button :class="['nav-link', 'nav-link-admin', { active: activeTab === 'commentManage' }]" @click="switchTab('commentManage')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>评论管理
          </button>
          <button :class="['nav-link', 'nav-link-admin', { active: activeTab === 'aiConfig' }]" @click="switchTab('aiConfig')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a4 4 0 0 0-4 4v2H6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V10a2 2 0 0 0-2-2h-2V6a4 4 0 0 0-4-4z"/>
              <circle cx="12" cy="14" r="2"/>
              <path d="M12 16v2"/>
            </svg>AI配置
          </button>
        </nav>
        <div class="bar-right">
          <a href="javascript:void(0)" @click.prevent="goHome" class="home-btn">
            <svg class="home-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>回到主页
          </a>
          <span class="live-tag"><i class="dot"></i>LIVE</span>
          <span class="clock">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <main class="page-body">
      <div v-if="activeTab === 'overview'" class="overview-page">
        <section class="kpi-banner">
          <div class="kpi-item" v-for="stat in statsData" :key="stat.label" :style="{ '--kpi-color': stat.color }">
            <div class="kpi-num" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="kpi-label">{{ stat.label }}</div>
          </div>
        </section>

        <div class="time-range-bar">
          <button :class="['range-btn', { active: timeRange === 'today' }]" @click="timeRange = 'today'">今日</button>
          <button :class="['range-btn', { active: timeRange === 'week' }]" @click="timeRange = 'week'">近7天</button>
          <button :class="['range-btn', { active: timeRange === 'month' }]" @click="timeRange = 'month'">近30天</button>
        </div>
        <div class="overview-grid">
          <section class="panel panel-trend">
            <div class="panel-head">
              <h2>今日阅读趋势</h2>
              <span class="panel-tag">实时</span>
            </div>
            <div ref="trendChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-hot">
            <div class="panel-head">
              <h2>热门新闻 TOP10</h2>
              <span class="panel-tag">热度</span>
            </div>
            <div ref="hotNewsChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-channel">
            <div class="panel-head">
              <h2>频道分布</h2>
            </div>
            <div ref="channelChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-funnel">
            <div class="panel-head">
              <h2>行为漏斗</h2>
            </div>
            <div ref="funnelChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-event">
            <div class="panel-head">
              <h2>行为类型分布</h2>
            </div>
            <div ref="regionChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-source">
            <div class="panel-head">
              <h2>新闻来源分布</h2>
              <span class="panel-tag">来源</span>
            </div>
            <div ref="sourceChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-active-hour">
            <div class="panel-head">
              <h2>用户活跃时段</h2>
              <span class="panel-tag">24h</span>
            </div>
            <div ref="activeHourChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-publish-trend">
            <div class="panel-head">
              <h2>新闻发布趋势</h2>
              <span class="panel-tag">趋势</span>
            </div>
            <div ref="publishTrendChartRef" class="panel-chart"></div>
          </section>
        </div>
      </div>

      <div v-if="activeTab === 'news'" class="manage-page">
        <div class="search-bar">
          <input v-model="newsKeyword" class="search-input" placeholder="搜索新闻标题或摘要..." @keyup.enter="loadNews(1)" />
          <select v-model="newsChannel" class="search-select" @change="loadNews(1)">
            <option value="">全部频道</option>
            <option v-for="ch in channels" :key="ch" :value="ch">{{ ch }}</option>
          </select>
          <button class="btn-primary" @click="loadNews(1)">搜索</button>
          <span class="result-count">共 {{ newsTotal }} 条新闻</span>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-title">标题</th>
                <th class="col-ch">频道</th>
                <th class="col-src">来源</th>
                <th class="col-num">浏览</th>
                <th class="col-num">评论</th>
                <th class="col-time">发布时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in newsList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-title">{{ item.title }}</td>
                <td class="col-ch"><span class="ch-tag">{{ item.channel }}</span></td>
                <td class="col-src">{{ item.source }}</td>
                <td class="col-num">{{ item.viewCount }}</td>
                <td class="col-num">{{ item.commentCount }}</td>
                <td class="col-time">{{ formatDate(item.publishTime) }}</td>
              </tr>
              <tr v-if="newsList.length === 0"><td colspan="7" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button :disabled="newsPage <= 1" @click="loadNews(newsPage - 1)">‹ 上一页</button>
          <span>{{ newsPage }} / {{ newsTotalPages || 1 }} 页 · 共 {{ newsTotal }} 条</span>
          <button :disabled="newsPage >= newsTotalPages" @click="loadNews(newsPage + 1)">下一页 ›</button>
        </div>
      </div>

      <!-- 实时行为页面 -->
      <div v-if="activeTab === 'behavior'" class="behavior-page">
        <div class="behavior-layout">
          <!-- 左侧：实时统计 + 行为事件流 -->
          <div class="behavior-left">
            <div class="behavior-stats">
              <div class="behavior-stat-card" v-for="s in behaviorStats" :key="s.label">
                <div class="behavior-stat-value" :style="{ color: s.color }">{{ s.value }}</div>
                <div class="behavior-stat-label">{{ s.label }}</div>
              </div>
            </div>
            <div class="behavior-event-panel">
              <div class="behavior-event-head">
                <h2>实时行为事件流</h2>
                <span class="panel-tag">自动刷新</span>
              </div>
              <div class="behavior-event-list">
                <div v-for="(evt, i) in behaviorEvents" :key="i" class="behavior-event-item">
                  <span class="behavior-event-time">{{ evt.time }}</span>
                  <span class="behavior-event-user">用户{{ evt.userId }}</span>
                  <span class="behavior-event-action">进行了</span>
                  <span :class="['behavior-event-type', evt.typeClass]">{{ evt.action }}</span>
                </div>
                <div v-if="behaviorEvents.length === 0" class="behavior-event-empty">等待行为数据...</div>
              </div>
            </div>
          </div>
          <!-- 右侧：分布图 + 趋势图 -->
          <div class="behavior-right">
            <section class="panel panel-behavior-dist">
              <div class="panel-head">
                <h2>行为类型分布</h2>
                <span class="panel-tag">实时</span>
              </div>
              <div ref="behaviorDistChartRef" class="panel-chart"></div>
            </section>
            <section class="panel panel-behavior-trend">
              <div class="panel-head">
                <h2>行为趋势</h2>
                <span class="panel-tag">趋势</span>
              </div>
              <div ref="behaviorTrendChartRef" class="panel-chart"></div>
            </section>
          </div>
        </div>
      </div>

      <!-- 新闻管理页面 -->
      <div v-if="activeTab === 'newsManage'" class="manage-page">
        <div class="admin-page-header">
          <h2 class="admin-page-title">新闻管理</h2>
          <div class="admin-user-info" v-if="authApi.isLoggedIn()">
            <span class="admin-user-name">{{ (authApi.getAdminUser() || {}).nickname || (authApi.getAdminUser() || {}).username || '管理员' }}</span>
            <button class="btn-logout" @click="handleLogout">退出登录</button>
          </div>
        </div>
        <div class="search-bar">
          <input v-model="adminNewsKeyword" class="search-input" placeholder="搜索新闻标题..." @keyup.enter="loadAdminNews(1)" />
          <select v-model="adminNewsChannel" class="search-select" @change="loadAdminNews(1)">
            <option value="">全部频道</option>
            <option v-for="ch in adminNewsChannels" :key="ch" :value="ch">{{ ch }}</option>
          </select>
          <button class="btn-primary" @click="loadAdminNews(1)">搜索</button>
          <span class="result-count">共 {{ adminNewsTotal }} 条新闻</span>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-title">标题</th>
                <th class="col-ch">频道</th>
                <th class="col-src">来源</th>
                <th class="col-num">浏览</th>
                <th class="col-num">评论</th>
                <th class="col-status">状态</th>
                <th class="col-time">发布时间</th>
                <th class="col-action">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in adminNewsList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-title">{{ item.title }}</td>
                <td class="col-ch"><span class="ch-tag">{{ item.channel }}</span></td>
                <td class="col-src">{{ item.source }}</td>
                <td class="col-num">{{ item.viewCount || 0 }}</td>
                <td class="col-num">{{ item.commentCount || 0 }}</td>
                <td class="col-status"><span :class="['status-tag', 'status-' + (item.status ?? 1)]">{{ statusText(item.status) }}</span></td>
                <td class="col-time">{{ formatDate(item.publishTime) }}</td>
                <td class="col-action">
                  <button class="btn-table btn-edit" @click="openNewsEdit(item)">编辑</button>
                  <button class="btn-table btn-delete" @click="deleteNews(item.id)">删除</button>
                </td>
              </tr>
              <tr v-if="adminNewsList.length === 0"><td colspan="9" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button :disabled="adminNewsPage <= 1" @click="loadAdminNews(adminNewsPage - 1)">‹ 上一页</button>
          <span>{{ adminNewsPage }} / {{ adminNewsTotalPages || 1 }} 页 · 共 {{ adminNewsTotal }} 条</span>
          <button :disabled="adminNewsPage >= adminNewsTotalPages" @click="loadAdminNews(adminNewsPage + 1)">下一页 ›</button>
        </div>
      </div>

      <!-- 用户管理页面 -->
      <div v-if="activeTab === 'userManage'" class="manage-page">
        <div class="admin-page-header">
          <h2 class="admin-page-title">用户管理</h2>
          <div class="admin-user-info" v-if="authApi.isLoggedIn()">
            <span class="admin-user-name">{{ (authApi.getAdminUser() || {}).nickname || (authApi.getAdminUser() || {}).username || '管理员' }}</span>
            <button class="btn-logout" @click="handleLogout">退出登录</button>
          </div>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-username">用户名</th>
                <th class="col-nickname">昵称</th>
                <th class="col-email">邮箱</th>
                <th class="col-phone">手机</th>
                <th class="col-status">状态</th>
                <th class="col-time">创建时间</th>
                <th class="col-action">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in userList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-username">{{ item.username }}</td>
                <td class="col-nickname">{{ item.nickname || '-' }}</td>
                <td class="col-email">{{ item.email || '-' }}</td>
                <td class="col-phone">{{ item.phone || '-' }}</td>
                <td class="col-status"><span :class="['status-tag', item.status === 1 ? 'status-1' : 'status-0']">{{ item.status === 1 ? '启用' : '停用' }}</span></td>
                <td class="col-time">{{ formatDate(item.createTime) }}</td>
                <td class="col-action">
                  <button class="btn-table btn-toggle" @click="toggleUserStatus(item)">{{ item.status === 1 ? '停用' : '启用' }}</button>
                  <button class="btn-table btn-delete" @click="deleteUser(item.id)">删除</button>
                </td>
              </tr>
              <tr v-if="userList.length === 0"><td colspan="8" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 评论管理页面 -->
      <div v-if="activeTab === 'commentManage'" class="manage-page">
        <div class="admin-page-header">
          <h2 class="admin-page-title">评论管理</h2>
          <div class="admin-user-info" v-if="authApi.isLoggedIn()">
            <span class="admin-user-name">{{ (authApi.getAdminUser() || {}).nickname || (authApi.getAdminUser() || {}).username || '管理员' }}</span>
            <button class="btn-logout" @click="handleLogout">退出登录</button>
          </div>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-article-id">文章ID</th>
                <th class="col-user-id">用户ID</th>
                <th class="col-content">内容</th>
                <th class="col-num">点赞</th>
                <th class="col-status">状态</th>
                <th class="col-time">创建时间</th>
                <th class="col-action">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in commentList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-article-id">{{ item.articleId || item.newsId || '-' }}</td>
                <td class="col-user-id">{{ item.userId || '-' }}</td>
                <td class="col-content">{{ item.content }}</td>
                <td class="col-num">{{ item.likeCount || 0 }}</td>
                <td class="col-status"><span :class="['status-tag', item.status === 1 ? 'status-1' : 'status-0']">{{ item.status === 1 ? '显示' : '隐藏' }}</span></td>
                <td class="col-time">{{ formatDate(item.createTime) }}</td>
                <td class="col-action">
                  <button class="btn-table btn-toggle" @click="toggleCommentStatus(item)">{{ item.status === 1 ? '隐藏' : '显示' }}</button>
                  <button class="btn-table btn-delete" @click="deleteComment(item.id)">删除</button>
                </td>
              </tr>
              <tr v-if="commentList.length === 0"><td colspan="8" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- AI模型配置页面 -->
      <div v-if="activeTab === 'aiConfig'" class="manage-page">
        <div class="admin-page-header">
          <h2 class="admin-page-title">AI模型配置</h2>
          <div class="admin-user-info" v-if="authApi.isLoggedIn()">
            <button class="btn-primary" @click="openAiConfigAdd" style="margin-right:12px">新增配置</button>
            <span class="admin-user-name">{{ (authApi.getAdminUser() || {}).nickname || (authApi.getAdminUser() || {}).username || '管理员' }}</span>
            <button class="btn-logout" @click="handleLogout">退出登录</button>
          </div>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-title">名称</th>
                <th class="col-ch">提供商</th>
                <th class="col-src">模型</th>
                <th class="col-num">温度</th>
                <th class="col-num">MaxTokens</th>
                <th class="col-status">状态</th>
                <th class="col-status">默认</th>
                <th class="col-action">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in aiConfigList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-title">{{ item.name }}</td>
                <td class="col-ch"><span class="ch-tag">{{ providerText(item.provider) }}</span></td>
                <td class="col-src" style="font-size:11px">{{ item.modelName }}</td>
                <td class="col-num">{{ item.temperature }}</td>
                <td class="col-num">{{ item.maxTokens }}</td>
                <td class="col-status"><span :class="['status-tag', item.status === 1 ? 'status-1' : 'status-0']">{{ item.status === 1 ? '启用' : '停用' }}</span></td>
                <td class="col-status"><span :class="['status-tag', item.isDefault === 1 ? 'status-1' : 'status-0']">{{ item.isDefault === 1 ? '默认' : '-' }}</span></td>
                <td class="col-action">
                  <button v-if="item.isDefault !== 1" class="btn-table btn-toggle" @click="setDefaultAiConfig(item.id)">设为默认</button>
                  <button class="btn-table btn-edit" @click="openAiConfigEdit(item)">编辑</button>
                  <button class="btn-table btn-delete" @click="deleteAiConfig(item.id)">删除</button>
                </td>
              </tr>
              <tr v-if="aiConfigList.length === 0"><td colspan="9" class="empty-row">暂无配置</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>

    <!-- 登录弹窗 -->
    <div v-if="loginDialogVisible" class="modal-overlay" @click.self="loginDialogVisible = false">
      <div class="modal-box">
        <div class="modal-header">
          <h3>管理员登录</h3>
          <button class="modal-close" @click="loginDialogVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input v-model="loginForm.username" class="form-input" type="text" placeholder="请输入用户名" @keyup.enter="handleLogin" />
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <input v-model="loginForm.password" class="form-input" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
          </div>
          <p v-if="loginError" class="form-error">{{ loginError }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="loginDialogVisible = false">取消</button>
          <button class="btn-primary" @click="handleLogin" :disabled="loginLoading">{{ loginLoading ? '登录中...' : '登录' }}</button>
        </div>
      </div>
    </div>

    <!-- 新闻编辑弹窗 -->
    <div v-if="newsEditDialogVisible" class="modal-overlay" @click.self="newsEditDialogVisible = false">
      <div class="modal-box modal-box-lg">
        <div class="modal-header">
          <h3>编辑新闻</h3>
          <button class="modal-close" @click="newsEditDialogVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">封面图片</label>
            <div class="image-upload-area">
              <div v-if="newsEditForm.coverImage" class="image-preview">
                <img :src="newsEditForm.coverImage" alt="封面" class="preview-img" />
                <button class="image-remove" @click="newsEditForm.coverImage = ''">&times;</button>
              </div>
              <div v-else class="image-upload-placeholder" @click="$refs.imageInput.click()">
                <svg viewBox="0 0 24 24" width="32" height="32" fill="none" stroke="#94A3B8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2"/>
                  <circle cx="8.5" cy="8.5" r="1.5"/>
                  <path d="M21 15l-5-5L5 21"/>
                </svg>
                <span class="upload-text">{{ imageUploading ? '上传中...' : '点击上传图片' }}</span>
              </div>
              <input ref="imageInput" type="file" accept="image/*" style="display:none" @change="handleImageUpload" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">标题</label>
            <input v-model="newsEditForm.title" class="form-input" type="text" placeholder="请输入标题" />
          </div>
          <div class="form-group">
            <label class="form-label">摘要</label>
            <textarea v-model="newsEditForm.summary" class="form-textarea" placeholder="请输入摘要" rows="3"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">来源</label>
              <input v-model="newsEditForm.source" class="form-input" type="text" placeholder="请输入来源" />
            </div>
            <div class="form-group form-group-half">
              <label class="form-label">频道</label>
              <select v-model="newsEditForm.channel" class="form-select">
                <option value="">请选择频道</option>
                <option v-for="ch in adminNewsChannels" :key="ch" :value="ch">{{ ch }}</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">状态</label>
            <select v-model="newsEditForm.status" class="form-select">
              <option :value="0">草稿</option>
              <option :value="1">已发布</option>
              <option :value="2">已下架</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="newsEditDialogVisible = false">取消</button>
          <button class="btn-primary" @click="saveNewsEdit" :disabled="newsEditLoading">{{ newsEditLoading ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>

    <!-- 确认删除弹窗 -->
    <div v-if="confirmDialogVisible" class="modal-overlay" @click.self="confirmDialogVisible = false">
      <div class="modal-box modal-box-sm">
        <div class="modal-header">
          <h3>确认操作</h3>
          <button class="modal-close" @click="confirmDialogVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <p class="confirm-text">{{ confirmMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="confirmDialogVisible = false">取消</button>
          <button class="btn-primary btn-danger" @click="executeConfirm">确认</button>
        </div>
      </div>
    </div>

    <!-- AI配置编辑弹窗 -->
    <div v-if="aiConfigEditDialogVisible" class="modal-overlay" @click.self="aiConfigEditDialogVisible = false">
      <div class="modal-box modal-box-lg">
        <div class="modal-header">
          <h3>{{ aiConfigEditForm.id ? '编辑AI配置' : '新增AI配置' }}</h3>
          <button class="modal-close" @click="aiConfigEditDialogVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">配置名称</label>
            <input v-model="aiConfigEditForm.name" class="form-input" type="text" placeholder="如：NVIDIA Qwen3.5" />
          </div>
          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">提供商</label>
              <select v-model="aiConfigEditForm.provider" class="form-select">
                <option value="nvidia">NVIDIA</option>
                <option value="openai">OpenAI</option>
                <option value="other">其他</option>
              </select>
            </div>
            <div class="form-group form-group-half">
              <label class="form-label">模型名称</label>
              <input v-model="aiConfigEditForm.modelName" class="form-input" type="text" placeholder="如：qwen/qwen3.5-122b-a10b" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">API地址</label>
            <input v-model="aiConfigEditForm.apiUrl" class="form-input" type="text" placeholder="如：https://integrate.api.nvidia.com/v1/chat/completions" />
          </div>
          <div class="form-group">
            <label class="form-label">API密钥</label>
            <input v-model="aiConfigEditForm.apiKey" class="form-input" type="password" placeholder="请输入API Key" />
          </div>
          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">Temperature</label>
              <input v-model.number="aiConfigEditForm.temperature" class="form-input" type="number" step="0.1" min="0" max="2" />
            </div>
            <div class="form-group form-group-half">
              <label class="form-label">Max Tokens</label>
              <input v-model.number="aiConfigEditForm.maxTokens" class="form-input" type="number" min="1" max="128000" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">Top P</label>
              <input v-model.number="aiConfigEditForm.topP" class="form-input" type="number" step="0.05" min="0" max="1" />
            </div>
            <div class="form-group form-group-half">
              <label class="form-label">状态</label>
              <select v-model="aiConfigEditForm.status" class="form-select">
                <option :value="1">启用</option>
                <option :value="0">停用</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="aiConfigEditDialogVisible = false">取消</button>
          <button class="btn-primary" @click="saveAiConfig" :disabled="aiConfigEditLoading">{{ aiConfigEditLoading ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>

    <div v-if="toast.show" :class="['toast', toast.type]">
      {{ toast.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { analyticsApi, newsApi, userApi, commentApi, authApi, imageApi, aiConfigApi } from '../api/index.js'

const emit = defineEmits([])

const activeTab = ref('overview')
const trendChartRef = ref(null)
const hotNewsChartRef = ref(null)
const channelChartRef = ref(null)
const funnelChartRef = ref(null)
const regionChartRef = ref(null)
const sourceChartRef = ref(null)
const activeHourChartRef = ref(null)
const publishTrendChartRef = ref(null)
const behaviorDistChartRef = ref(null)
const behaviorTrendChartRef = ref(null)
let trendChart = null, hotNewsChart = null, channelChart = null, funnelChart = null, regionChart = null, sourceChart = null, activeHourChart = null, publishTrendChart = null, behaviorDistChart = null, behaviorTrendChart = null
const currentTime = ref('')

const statsData = ref([
  { label: '今日UV', value: '-', icon: '', color: '#2F6BFF' },
  { label: '今日PV', value: '-', icon: '', color: '#7C3AED' },
  { label: '实时在线', value: '-', icon: '', color: '#10B981' },
  { label: '平均阅读时长', value: '-', icon: '', color: '#F59E0B' },
  { label: '总用户数', value: '-', icon: '', color: '#EC4899' },
  { label: '总新闻数', value: '-', icon: '', color: '#06B6D4' },
  { label: '今日新增', value: '-', icon: '', color: '#8B5CF6' },
  { label: '总浏览量', value: '-', icon: '', color: '#F97316' }
])

const newsList = ref([])
const newsPage = ref(1)
const newsTotal = ref(0)
const newsTotalPages = computed(() => Math.max(1, Math.ceil(newsTotal.value / 20)))
const newsKeyword = ref('')
const newsChannel = ref('')
const channels = ref([])
const timeRange = ref('today')

// 实时行为相关变量
const behaviorEvents = ref([])
const behaviorStats = ref([
  { label: '今日UV', value: '-', color: '#2F6BFF' },
  { label: '今日PV', value: '-', color: '#7C3AED' },
  { label: '在线用户', value: '-', color: '#10B981' },
  { label: '平均时长', value: '-', color: '#F59E0B' }
])
let behaviorInterval = null

// 事件类型映射
const eventTypeMap = {
  page_view: { action: '浏览页面', typeClass: 'type-view' },
  news_click: { action: '点击新闻', typeClass: 'type-click' },
  news_read: { action: '阅读新闻', typeClass: 'type-read' },
  search: { action: '搜索', typeClass: 'type-search' },
  ai_search: { action: 'AI搜索', typeClass: 'type-ai' },
  click: { action: '点击', typeClass: 'type-click' },
  comment: { action: '评论', typeClass: 'type-comment' },
  share: { action: '分享', typeClass: 'type-share' },
  favorite: { action: '收藏', typeClass: 'type-fav' }
}

// ======================== 管理后台相关变量 ========================
const adminTabs = ['newsManage', 'userManage', 'commentManage', 'aiConfig']

// 登录相关
const loginDialogVisible = ref(false)
const loginForm = ref({ username: '', password: '' })
const loginError = ref('')
const loginLoading = ref(false)
const pendingTab = ref('')

// 新闻管理相关
const adminNewsList = ref([])
const adminNewsPage = ref(1)
const adminNewsTotal = ref(0)
const adminNewsPageSize = 20
const adminNewsTotalPages = computed(() => Math.max(1, Math.ceil(adminNewsTotal.value / adminNewsPageSize)))
const adminNewsKeyword = ref('')
const adminNewsChannel = ref('')
const adminNewsChannels = ref([])
const newsEditDialogVisible = ref(false)
const newsEditForm = ref({ id: null, title: '', summary: '', source: '', channel: '', status: 1, coverImage: '' })
const newsEditLoading = ref(false)
const imageUploading = ref(false)

// 用户管理相关
const userList = ref([])

// 评论管理相关
const commentList = ref([])

// AI配置相关
const aiConfigList = ref([])
const aiConfigEditDialogVisible = ref(false)
const aiConfigEditForm = ref({ id: null, name: '', provider: 'nvidia', apiUrl: '', apiKey: '', modelName: '', temperature: 0.6, maxTokens: 16384, topP: 0.95, status: 1, isDefault: 0 })
const aiConfigEditLoading = ref(false)

// 确认弹窗相关
const confirmDialogVisible = ref(false)
const confirmMessage = ref('')
let confirmCallback = null

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'news') loadNews(1)
  if (tab === 'behavior') {
    loadBehaviorEvents()
    initBehaviorCharts()
  }
  if (tab === 'newsManage') {
    loadAdminNews(1)
    loadAdminNewsChannels()
  }
  if (tab === 'userManage') loadUsers()
  if (tab === 'commentManage') loadComments()
  if (tab === 'aiConfig') loadAiConfigs()
}

// ======================== 登录逻辑 ========================
const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    loginError.value = '请输入用户名和密码'
    return
  }
  loginLoading.value = true
  loginError.value = ''
  try {
    const res = await authApi.login(loginForm.value.username, loginForm.value.password)
    const data = res.data || res
    if (data && (data.code === 200 || data.token)) {
      const token = data.token || data.data?.token
      const userInfo = data.data || data
      if (token) {
        localStorage.setItem('admin_token', token)
        localStorage.setItem('admin_user', JSON.stringify({ userId: userInfo.userId, username: userInfo.username, nickname: userInfo.nickname }))
        loginDialogVisible.value = false
        showToast('登录成功', 'success')
        if (pendingTab.value) {
          activeTab.value = pendingTab.value
          pendingTab.value = ''
          if (activeTab.value === 'newsManage') { loadAdminNews(1); loadAdminNewsChannels() }
          if (activeTab.value === 'userManage') loadUsers()
          if (activeTab.value === 'commentManage') loadComments()
          if (activeTab.value === 'aiConfig') loadAiConfigs()
        }
      } else {
        loginError.value = data.message || '登录失败，请检查用户名和密码'
      }
    } else {
      loginError.value = data.message || '登录失败，请检查用户名和密码'
    }
  } catch (e) {
    loginError.value = e.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    loginLoading.value = false
  }
}

const handleLogout = () => {
  authApi.logout()
  if (adminTabs.includes(activeTab.value)) {
    activeTab.value = 'overview'
  }
  showToast('已退出登录', 'success')
}

// ======================== 新闻管理逻辑 ========================
const statusText = (status) => {
  if (status === 0) return '草稿'
  if (status === 1) return '已发布'
  if (status === 2) return '已下架'
  return '未知'
}

const loadAdminNews = async (page) => {
  if (page) adminNewsPage.value = page
  try {
    const res = await newsApi.list(adminNewsPage.value, adminNewsPageSize, adminNewsChannel.value || undefined, adminNewsKeyword.value || undefined)
    const pageData = res?.data?.data || res?.data || res || {}
    const records = pageData.records || pageData.data || []
    adminNewsList.value = Array.isArray(records) ? records : []
    adminNewsTotal.value = pageData.total || 0
  } catch (e) {
    console.error('Failed to load admin news:', e)
    adminNewsList.value = []
    adminNewsTotal.value = 0
  }
}

const loadAdminNewsChannels = async () => {
  try {
    const res = await newsApi.channels()
    const d = res?.data?.data || res?.data || res || []
    adminNewsChannels.value = Array.isArray(d) ? d.map(item => item.channel || item.name || item) : Object.keys(d)
  } catch (e) {
    console.error('Failed to load admin news channels:', e)
  }
}

const openNewsEdit = (item) => {
  newsEditForm.value = {
    id: item.id,
    title: item.title || '',
    summary: item.summary || '',
    source: item.source || '',
    channel: item.channel || '',
    status: item.status ?? 1,
    coverImage: item.coverImage || ''
  }
  newsEditDialogVisible.value = true
}

const saveNewsEdit = async () => {
  if (!newsEditForm.value.title) {
    showToast('请填写标题', 'error')
    return
  }
  newsEditLoading.value = true
  try {
    const { id, ...data } = newsEditForm.value
    await newsApi.update(id, data)
    newsEditDialogVisible.value = false
    showToast('保存成功', 'success')
    loadAdminNews(adminNewsPage.value)
  } catch (e) {
    showToast('保存失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    newsEditLoading.value = false
  }
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    showToast('请选择图片文件', 'error')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    showToast('图片大小不能超过10MB', 'error')
    return
  }
  imageUploading.value = true
  try {
    const res = await imageApi.upload(file)
    const url = res?.data || res
    if (url) {
      newsEditForm.value.coverImage = url
      showToast('图片上传成功', 'success')
    } else {
      showToast('上传失败', 'error')
    }
  } catch (e) {
    showToast('上传失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    imageUploading.value = false
    event.target.value = ''
  }
}

const deleteNews = (id) => {
  showConfirm('确定要删除这条新闻吗？删除后不可恢复。', async () => {
    try {
      await newsApi.delete(id)
      showToast('删除成功', 'success')
      loadAdminNews(adminNewsPage.value)
    } catch (e) {
      showToast('删除失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  })
}

// ======================== 用户管理逻辑 ========================
const loadUsers = async () => {
  try {
    const res = await userApi.list()
    const data = res?.data?.data || res?.data || res || []
    userList.value = Array.isArray(data) ? data : (data.records || [])
  } catch (e) {
    console.error('Failed to load users:', e)
    userList.value = []
  }
}

const toggleUserStatus = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1
  const label = newStatus === 1 ? '启用' : '停用'
  showConfirm(`确定要${label}用户 "${item.username}" 吗？`, async () => {
    try {
      await userApi.update(item.id, { status: newStatus })
      showToast(`${label}成功`, 'success')
      loadUsers()
    } catch (e) {
      showToast(`操作失败: ${e.response?.data?.message || e.message}`, 'error')
    }
  })
}

const deleteUser = (id) => {
  showConfirm('确定要删除该用户吗？删除后不可恢复。', async () => {
    try {
      await userApi.delete(id)
      showToast('删除成功', 'success')
      loadUsers()
    } catch (e) {
      showToast('删除失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  })
}

// ======================== 评论管理逻辑 ========================
const loadComments = async () => {
  try {
    const res = await commentApi.list()
    const data = res?.data?.data || res?.data || res || []
    commentList.value = Array.isArray(data) ? data : (data.records || [])
  } catch (e) {
    console.error('Failed to load comments:', e)
    commentList.value = []
  }
}

const toggleCommentStatus = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1
  const label = newStatus === 1 ? '显示' : '隐藏'
  showConfirm(`确定要${label}该评论吗？`, async () => {
    try {
      await commentApi.update(item.id, { status: newStatus })
      showToast(`${label}成功`, 'success')
      loadComments()
    } catch (e) {
      showToast(`操作失败: ${e.response?.data?.message || e.message}`, 'error')
    }
  })
}

const deleteComment = (id) => {
  showConfirm('确定要删除该评论吗？删除后不可恢复。', async () => {
    try {
      await commentApi.delete(id)
      showToast('删除成功', 'success')
      loadComments()
    } catch (e) {
      showToast('删除失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  })
}

// ======================== AI配置管理逻辑 ========================
const loadAiConfigs = async () => {
  try {
    const res = await aiConfigApi.list()
    const data = res?.data?.data || res?.data || res || []
    aiConfigList.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Failed to load AI configs:', e)
    aiConfigList.value = []
  }
}

const openAiConfigAdd = () => {
  aiConfigEditForm.value = { id: null, name: '', provider: 'nvidia', apiUrl: '', apiKey: '', modelName: '', temperature: 0.6, maxTokens: 16384, topP: 0.95, status: 1, isDefault: 0 }
  aiConfigEditDialogVisible.value = true
}

const openAiConfigEdit = (item) => {
  aiConfigEditForm.value = { ...item }
  aiConfigEditDialogVisible.value = true
}

const saveAiConfig = async () => {
  if (!aiConfigEditForm.value.name || !aiConfigEditForm.value.apiUrl || !aiConfigEditForm.value.modelName) {
    showToast('请填写必要字段', 'error')
    return
  }
  aiConfigEditLoading.value = true
  try {
    if (aiConfigEditForm.value.id) {
      await aiConfigApi.update(aiConfigEditForm.value.id, aiConfigEditForm.value)
    } else {
      await aiConfigApi.add(aiConfigEditForm.value)
    }
    aiConfigEditDialogVisible.value = false
    showToast('保存成功', 'success')
    loadAiConfigs()
  } catch (e) {
    showToast('保存失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    aiConfigEditLoading.value = false
  }
}

const deleteAiConfig = (id) => {
  showConfirm('确定要删除该模型配置吗？', async () => {
    try {
      await aiConfigApi.delete(id)
      showToast('删除成功', 'success')
      loadAiConfigs()
    } catch (e) {
      showToast('删除失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  })
}

const setDefaultAiConfig = (id) => {
  showConfirm('确定要设为默认模型吗？', async () => {
    try {
      await aiConfigApi.setDefault(id)
      showToast('设置成功', 'success')
      loadAiConfigs()
    } catch (e) {
      showToast('设置失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  })
}

const providerText = (p) => {
  const map = { nvidia: 'NVIDIA', openai: 'OpenAI', other: '其他' }
  return map[p] || p
}

// ======================== 确认弹窗 ========================
const showConfirm = (message, callback) => {
  confirmMessage.value = message
  confirmCallback = callback
  confirmDialogVisible.value = true
}

const executeConfirm = () => {
  confirmDialogVisible.value = false
  if (confirmCallback) {
    confirmCallback()
    confirmCallback = null
  }
}

const toast = ref({ show: false, message: '', type: 'success' })
const showToast = (msg, type = 'success') => { toast.value = { show: true, message: msg, type }; setTimeout(() => { toast.value.show = false }, 3000) }

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const formatDuration = (s) => {
  if (!s && s !== 0) return '0s'
  s = Math.round(s)
  if (s <= 0) return '0s'
  return s >= 60 ? `${Math.floor(s / 60)}m${s % 60}s` : `${s}s`
}

const formatDate = (d) => {
  if (!d) return '-'
  return new Date(d).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

let realtimeLoading = false
const updateRealtimeStats = async () => {
  if (realtimeLoading) return
  realtimeLoading = true
  try {
    const res = await analyticsApi.getRealtimeStats(timeRange.value)
    const d = (res.data || res) || {}
    const prefix = timeRange.value === 'today' ? '今日' : timeRange.value === 'week' ? '近7天' : '近30天'
    statsData.value = [
      { label: prefix + 'UV', value: formatNumber(d.todayUsers || d.totalUsers), color: '#2F6BFF' },
      { label: prefix + 'PV', value: formatNumber(d.totalViews), color: '#7C3AED' },
      { label: '总行为数', value: formatNumber(d.totalBehaviors), color: '#10B981' },
      { label: '总用户数', value: formatNumber(d.totalUsers), color: '#F59E0B' },
      { label: '总新闻数', value: formatNumber(d.totalNews), color: '#EC4899' },
      { label: '今日新增', value: formatNumber(d.todayNews), color: '#06B6D4' },
      { label: '阅读记录', value: formatNumber(d.totalReadHistory), color: '#8B5CF6' },
      { label: '总浏览量', value: formatNumber(d.totalViews), color: '#F97316' }
    ]
  } catch (e) { console.error(e) }
  finally { realtimeLoading = false }
}

const chartFontFamily = "'Noto Sans SC', 'Microsoft YaHei', 'PingFang SC', 'Hiragino Sans GB', 'WenQuanYi Micro Hei', sans-serif"

const chartTheme = {
  textColor: '#64748B',
  splitColor: '#F1F5F9',
  tipBg: '#1E293B',
  tipText: '#F8FAFC',
  fontFamily: chartFontFamily
}

const initTrendChart = async () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)
  try {
    const res = await analyticsApi.getTrend(timeRange.value)
    const data = res.data || res
    if (!Array.isArray(data) || data.length === 0) {
      trendChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      legend: { data: ['UV', 'PV'], textStyle: { color: chartTheme.textColor, fontSize: 11, fontFamily: chartTheme.fontFamily }, top: 0, right: 0 },
      grid: { left: 40, right: 16, bottom: 24, top: 32 },
      xAxis: { type: 'category', data: data.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      series: [
        { name: 'UV', type: 'line', smooth: true, data: data.map(d => d.uv), symbol: 'none', lineStyle: { color: '#2F6BFF', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,107,255,0.18)' }, { offset: 1, color: 'rgba(47,107,255,0)' }]) } },
        { name: 'PV', type: 'line', smooth: true, data: data.map(d => d.pv), symbol: 'none', lineStyle: { color: '#7C3AED', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(124,58,237,0.18)' }, { offset: 1, color: 'rgba(124,58,237,0)' }]) } }
      ]
    })
  } catch (e) { console.error(e) }
}

const initHotNewsChart = async () => {
  if (!hotNewsChartRef.value) return
  if (!hotNewsChart) hotNewsChart = echarts.init(hotNewsChartRef.value)
  try {
    // 从新闻列表API获取真实数据，按view_count排序
    const res = await fetch('/api/news?page=1&size=10').then(r => r.json())
    const pageData = res.data || res
    const records = pageData?.data || pageData?.records || []
    let data = []
    if (Array.isArray(records) && records.length > 0) {
      // 检查view_count是否都为0
      const hasViews = records.some(r => (r.viewCount || r.view_count || 0) > 0)
      if (hasViews) {
        // 按view_count排序
        const sorted = [...records].sort((a, b) => (b.viewCount || b.view_count || 0) - (a.viewCount || a.view_count || 0))
        data = sorted.slice(0, 10).map(d => ({
          name: d.title || '',
          value: d.viewCount || d.view_count || 0
        }))
      } else {
        // view_count都是0时，按publishTime排序（最新发布）
        const sorted = [...records].sort((a, b) => {
          const ta = new Date(a.publishTime || a.publish_time || 0).getTime()
          const tb = new Date(b.publishTime || b.publish_time || 0).getTime()
          return tb - ta
        })
        data = sorted.slice(0, 10).map(d => ({
          name: d.title || '',
          value: d.viewCount || d.view_count || 0
        }))
      }
    }
    if (data.length === 0) {
      hotNewsChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    hotNewsChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 8, right: 40, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => (d.name || '').substring(0, 10)).reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'bar', data: data.map(d => d.value).reverse(), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2F6BFF' }, { offset: 1, color: '#7C3AED' }]), borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
    })
  } catch (e) { console.error(e) }
}

const initChannelChart = async () => {
  if (!channelChartRef.value) return
  if (channelChart) channelChart.dispose()
  channelChart = echarts.init(channelChartRef.value)
  try {
    const res = await analyticsApi.getNewsChannels()
    const channelData = res.data || res
    let data = []
    if (channelData && typeof channelData === 'object') {
      data = Object.entries(channelData).map(([name, items]) => ({
        name,
        value: Array.isArray(items) ? items.length : (typeof items === 'number' ? items : 0)
      }))
    }
    if (data.length === 0) {
      channelChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
    channelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}条 ({d}%)' },
      legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
      series: [{ type: 'pie', radius: ['38%', '68%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: data.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  } catch (e) { console.error(e) }
}

const initFunnelChart = async () => {
  if (!funnelChartRef.value) return
  if (funnelChart) funnelChart.dispose()
  funnelChart = echarts.init(funnelChartRef.value)
  try {
    const [overviewRes, eventRes] = await Promise.all([
      analyticsApi.getOverview(timeRange.value),
      analyticsApi.getEventDist(timeRange.value)
    ])
    const overview = (overviewRes.data || overviewRes) || {}
    const eventData = eventRes.data || eventRes

    const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'search': '搜索', 'ai_search': 'AI搜索', 'news_comment': '评论', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
    let data = []
    if (Array.isArray(eventData) && eventData.length > 0) {
      data = eventData.map(d => ({
        name: typeMap[d.event_type] || d.event_type || '其他',
        value: d.cnt || d.value || 0
      })).filter(d => d.value > 0).sort((a, b) => b.value - a.value)
    }

    if (data.length < 3) {
      data = [
        { name: '浏览', value: overview.totalViews || 0 },
        { name: '行为', value: overview.totalBehaviors || 0 },
        { name: '阅读记录', value: overview.totalReadHistory || 0 },
        { name: '用户', value: overview.totalUsers || 0 }
      ].filter(d => d.value > 0)
    }

    if (data.length === 0) {
      funnelChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899']
    funnelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: (p) => `${p.name}: ${formatNumber(p.value)}` },
      series: [{ type: 'funnel', left: '10%', top: 10, bottom: 10, width: '65%', min: 0, max: 100, minSize: '10%', maxSize: '100%', sort: 'descending', gap: 4, label: { show: true, position: 'inside', color: '#fff', fontSize: 11, fontWeight: 500, fontFamily: chartTheme.fontFamily, formatter: (p) => `${p.name}\n${formatNumber(p.value)}` }, labelLine: { show: false }, itemStyle: { borderColor: '#fff', borderWidth: 1 }, data: data.map((item, i) => ({ name: item.name, value: item.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i % colors.length] || colors[0] }, { offset: 1, color: (colors[i % colors.length] || colors[0]) + '88' }]) } })) }]
    })
  } catch (e) { console.error(e) }
}

const initRegionChart = async () => {
  if (!regionChartRef.value) return
  if (regionChart) regionChart.dispose()
  regionChart = echarts.init(regionChartRef.value)
  try {
    const res = await analyticsApi.getEventDist(timeRange.value)
    let data = res.data || res
    if (!Array.isArray(data)) data = []
    const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'news_comment': '新闻评论', 'search': '搜索', 'ai_search': 'AI搜索', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
    const mappedData = data.map(d => ({ name: typeMap[d.event_type] || d.event_type || d.name || '其他', value: d.cnt || d.value || 0 })).filter(d => d.value > 0)
    if (mappedData.length === 0) {
      regionChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
    regionChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}次 ({d}%)' },
      legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
      series: [{ type: 'pie', radius: ['35%', '65%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: mappedData.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  } catch (e) { console.error(e) }
}

const initSourceChart = async () => {
  if (!sourceChartRef.value) return
  if (sourceChart) sourceChart.dispose()
  sourceChart = echarts.init(sourceChartRef.value)
  try {
    const res = await analyticsApi.getNewsList(1, 500)
    const json = res.data || res
    const records = json?.data || json?.records || json?.data?.records || []
    const sourceMap = {}
    records.forEach(item => {
      const src = item.source || '未知来源'
      sourceMap[src] = (sourceMap[src] || 0) + 1
    })
    let data = Object.entries(sourceMap).map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value).slice(0, 10)
    if (data.length === 0) {
      sourceChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6', '#F97316', '#14B8A6']
    sourceChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 8, right: 40, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => d.name).reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'bar', data: data.map((d, i) => ({ value: d.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i % colors.length] }, { offset: 1, color: (colors[i % colors.length]) + '88' }]), borderRadius: [0, 4, 4, 0] } })).reverse(), barWidth: 14 }]
    })
  } catch (e) { console.error(e) }
}

const initActiveHourChart = async () => {
  if (!activeHourChartRef.value) return
  if (activeHourChart) activeHourChart.dispose()
  activeHourChart = echarts.init(activeHourChartRef.value)
  try {
    let hourData = []
    try {
      const res = await analyticsApi.getTrend('today')
      const data = res.data || res
      if (Array.isArray(data) && data.length > 0) {
        hourData = data.map(d => ({ hour: d.hour, value: (d.pv || 0) + (d.uv || 0) }))
      }
    } catch (e) { /* ignore */ }
    if (hourData.length === 0) {
      activeHourChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const maxVal = Math.max(...hourData.map(d => d.value))
    activeHourChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: (p) => `${p[0].name}<br/>活跃度: ${p[0].value}` },
      grid: { left: 40, right: 16, bottom: 24, top: 16 },
      xAxis: { type: 'category', data: hourData.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 9, fontFamily: chartTheme.fontFamily, interval: 2 }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { show: false }, splitLine: { show: false } },
      series: [{
        type: 'bar',
        data: hourData.map(d => ({
          value: d.value,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: d.value > maxVal * 0.7 ? '#2F6BFF' : d.value > maxVal * 0.4 ? '#60A5FA' : '#BFDBFE' },
              { offset: 1, color: d.value > maxVal * 0.7 ? 'rgba(47,107,255,0.3)' : d.value > maxVal * 0.4 ? 'rgba(96,165,250,0.2)' : 'rgba(191,219,254,0.1)' }
            ]),
            borderRadius: [3, 3, 0, 0]
          }
        })),
        barWidth: '60%'
      }]
    })
  } catch (e) { console.error(e) }
}

const initPublishTrendChart = async () => {
  if (!publishTrendChartRef.value) return
  if (publishTrendChart) publishTrendChart.dispose()
  publishTrendChart = echarts.init(publishTrendChartRef.value)
  try {
    const res = await analyticsApi.getNewsList(1, 500)
    const json = res.data || res
    const records = json?.data || json?.records || json?.data?.records || []
    const dayMap = {}
    const now = new Date()
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      const key = `${d.getMonth() + 1}/${d.getDate()}`
      dayMap[key] = 0
    }
    records.forEach(item => {
      if (item.publishTime) {
        const d = new Date(item.publishTime)
        const key = `${d.getMonth() + 1}/${d.getDate()}`
        if (key in dayMap) dayMap[key] = (dayMap[key] || 0) + 1
      }
    })
    const dates = Object.keys(dayMap)
    const values = Object.values(dayMap)
    publishTrendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 40, right: 16, bottom: 24, top: 16 },
      xAxis: { type: 'category', data: dates, axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      series: [{
        type: 'line', smooth: true, data: values, symbol: 'circle', symbolSize: 6,
        lineStyle: { color: '#10B981', width: 2.5 },
        itemStyle: { color: '#10B981', borderColor: '#fff', borderWidth: 2 },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(16,185,129,0.2)' }, { offset: 1, color: 'rgba(16,185,129,0)' }]) }
      }]
    })
  } catch (e) { console.error(e) }
}

// 实时行为：加载行为事件
const loadBehaviorEvents = async () => {
  try {
    const [realtimeRes, trendRes] = await Promise.all([
      analyticsApi.getRealtimeStats('today'),
      analyticsApi.getTrend('today')
    ])
    const d = (realtimeRes.data || realtimeRes) || {}
    // 更新统计卡片
    behaviorStats.value = [
      { label: '今日UV', value: formatNumber(d.todayUsers || d.totalUsers), color: '#2F6BFF' },
      { label: '今日PV', value: formatNumber(d.totalViews), color: '#7C3AED' },
      { label: '在线用户', value: formatNumber(d.totalUsers), color: '#10B981' },
      { label: '平均时长', value: formatDuration(d.avgDuration || d.avgReadTime), color: '#F59E0B' }
    ]
    // 从 ClickHouse 查询真实行为事件
    try {
      const sql = encodeURIComponent('SELECT event_time, user_id, event_type, target_id, target_type FROM bigdata_portal.behavior_events ORDER BY event_time DESC LIMIT 30 FORMAT JSON')
      const chRes = await fetch(`/ch-query/?query=${sql}`)
      if (chRes.ok) {
        const chData = await chRes.json()
        const rows = chData.data || []
        const newEvents = rows.map(row => {
          const mapped = eventTypeMap[row.event_type] || { action: row.event_type, typeClass: 'type-other' }
          const time = new Date(row.event_time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
          return {
            time,
            userId: String(row.user_id).slice(-6),
            action: mapped.action,
            typeClass: mapped.typeClass,
            target: row.target_type || ''
          }
        })
        behaviorEvents.value = newEvents
      }
    } catch (e) {
      // ClickHouse不可用时，从MySQL查询
      try {
        const mysqlRes = await fetch('/api/analytics/realtime?range=today')
        const mysqlData = await mysqlRes.json()
        // 仅显示统计信息，不生成模拟事件
        if (behaviorEvents.value.length === 0) {
          behaviorEvents.value = [{ time: '--:--:--', userId: '-', action: '暂无行为数据', typeClass: 'type-other', target: '' }]
        }
      } catch (e2) { /* ignore */ }
    }
  } catch (e) { console.error(e) }
}

// 实时行为：初始化图表
const initBehaviorCharts = async () => {
  await nextTick()
  // 行为类型分布饼图
  if (behaviorDistChartRef.value) {
    if (behaviorDistChart) behaviorDistChart.dispose()
    behaviorDistChart = echarts.init(behaviorDistChartRef.value)
    try {
      const res = await analyticsApi.getEventDist('today')
      let data = res.data || res
      if (!Array.isArray(data)) data = []
      const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'news_comment': '新闻评论', 'search': '搜索', 'ai_search': 'AI搜索', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
      const mappedData = data.map(d => ({ name: typeMap[d.event_type] || d.event_type || '其他', value: d.cnt || d.value || 0 })).filter(d => d.value > 0)
      if (mappedData.length === 0) {
        behaviorDistChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
        })
      } else {
        const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
        behaviorDistChart.setOption({
          backgroundColor: 'transparent',
          tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}次 ({d}%)' },
          legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
          series: [{ type: 'pie', radius: ['35%', '65%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: mappedData.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
        })
      }
    } catch (e) { console.error(e) }
  }
  // 行为趋势折线图
  if (behaviorTrendChartRef.value) {
    if (behaviorTrendChart) behaviorTrendChart.dispose()
    behaviorTrendChart = echarts.init(behaviorTrendChartRef.value)
    try {
      const res = await analyticsApi.getTrend('today')
      const data = res.data || res
      if (!Array.isArray(data) || data.length === 0) {
        behaviorTrendChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
        })
      } else {
        behaviorTrendChart.setOption({
          backgroundColor: 'transparent',
          tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
          legend: { data: ['UV', 'PV'], textStyle: { color: chartTheme.textColor, fontSize: 11, fontFamily: chartTheme.fontFamily }, top: 0, right: 0 },
          grid: { left: 40, right: 16, bottom: 24, top: 32 },
          xAxis: { type: 'category', data: data.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
          yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
          series: [
            { name: 'UV', type: 'line', smooth: true, data: data.map(d => d.uv), symbol: 'none', lineStyle: { color: '#2F6BFF', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,107,255,0.18)' }, { offset: 1, color: 'rgba(47,107,255,0)' }]) } },
            { name: 'PV', type: 'line', smooth: true, data: data.map(d => d.pv), symbol: 'none', lineStyle: { color: '#7C3AED', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(124,58,237,0.18)' }, { offset: 1, color: 'rgba(124,58,237,0)' }]) } }
          ]
        })
      }
    } catch (e) { console.error(e) }
  }
}

const handleResize = () => { trendChart?.resize(); hotNewsChart?.resize(); channelChart?.resize(); funnelChart?.resize(); regionChart?.resize(); sourceChart?.resize(); activeHourChart?.resize(); publishTrendChart?.resize(); behaviorDistChart?.resize(); behaviorTrendChart?.resize() }

const loadNews = async (page) => {
  if (page) newsPage.value = page
  try {
    const params = new URLSearchParams({ page: newsPage.value, size: 20 })
    if (newsChannel.value) params.set('channel', newsChannel.value)
    if (newsKeyword.value) params.set('keyword', newsKeyword.value)
    const res = await fetch(`/api/news?${params}`).then(r => r.json())
    // 兼容多种返回格式: {success:true,data:{records:[]}} / {code:200,data:{records:[]}} / {records:[]}
    const pageData = res?.data?.data || res?.data || res || {}
    const records = pageData.records || pageData.data || []
    newsList.value = Array.isArray(records) ? records : []
    newsTotal.value = pageData.total || 0
  } catch (e) {
    console.error('Failed to load news:', e)
    newsList.value = []
    newsTotal.value = 0
  }
}
const loadChannels = async () => {
  try {
    const res = await fetch('/api/news/channels').then(r => r.json())
    // 兼容 {data:[]} / {code:200,data:[]} / {data:{data:[]}} 等格式
    const d = res?.data?.data || res?.data || res || []
    channels.value = Array.isArray(d) ? d.map(item => item.channel || item.name || item) : Object.keys(d)
  } catch (e) { console.error('Failed to load channels:', e) }
}

const loadOverviewData = async () => {
  await updateRealtimeStats()
  await initCharts()
}

let statsInterval = null, timeInterval = null, hotNewsInterval = null
const initCharts = async () => { await nextTick(); await initTrendChart(); await initHotNewsChart(); await initChannelChart(); await initFunnelChart(); await initRegionChart(); await initSourceChart(); await initActiveHourChart(); await initPublishTrendChart() }
watch(activeTab, async (val) => {
  if (val === 'overview') { await nextTick(); initCharts() }
  if (val === 'behavior') {
    loadBehaviorEvents()
    initBehaviorCharts()
  }
})
watch(timeRange, () => {
  loadOverviewData()
})
onMounted(() => {
  updateTime(); timeInterval = setInterval(updateTime, 1000); updateRealtimeStats(); statsInterval = setInterval(updateRealtimeStats, 10000); initCharts(); loadChannels(); hotNewsInterval = setInterval(() => { initHotNewsChart(); initTrendChart() }, 30000);
  // 实时行为定时刷新
  behaviorInterval = setInterval(() => {
    if (activeTab.value === 'behavior') loadBehaviorEvents()
  }, 5000)
  window.addEventListener('resize', handleResize)
})
onUnmounted(() => {
  if (statsInterval) clearInterval(statsInterval); if (timeInterval) clearInterval(timeInterval); if (hotNewsInterval) clearInterval(hotNewsInterval); if (behaviorInterval) clearInterval(behaviorInterval)
  trendChart?.dispose(); hotNewsChart?.dispose(); channelChart?.dispose(); funnelChart?.dispose(); regionChart?.dispose(); sourceChart?.dispose(); activeHourChart?.dispose(); publishTrendChart?.dispose(); behaviorDistChart?.dispose(); behaviorTrendChart?.dispose()
  window.removeEventListener('resize', handleResize)
})

const goHome = () => {
  const homeUrl = window.__SERVER_URL__ || window.HOME_URL || '/'
  window.location.href = homeUrl
}
</script>

<style scoped>
@import url('https://fonts.googleapis.cn/css2?family=DM+Sans:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;600;700&display=swap');

.dashboard {
  width: 100vw;
  height: 100vh;
  background: #F8FAFC;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'DM Sans', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1E293B;
}

.top-bar {
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  flex-shrink: 0;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  position: relative;
  z-index: 10;
}

.bar-inner {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 28px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.brand-icon {
  width: 34px;
  height: 34px;
}

.brand-name {
  font-size: 20px;
  font-weight: 800;
  color: #F8FAFC;
  letter-spacing: 1px;
}

.brand-sub {
  font-size: 11px;
  color: #94A3B8;
  background: rgba(255,255,255,0.08);
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: 500;
}

.main-nav {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-link {
  padding: 8px 18px;
  border: none;
  background: transparent;
  color: #94A3B8;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover { background: rgba(255,255,255,0.08); color: #E2E8F0; }
.nav-link.active { background: rgba(47,107,255,0.2); color: #60A5FA; font-weight: 600; }
.nav-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.home-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.home-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 6px;
  color: #94A3B8;
  font-size: 12px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}
.home-btn:hover {
  background: rgba(255,255,255,0.12);
  color: #fff;
  border-color: rgba(255,255,255,0.25);
}

.live-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 700;
  color: #EF4444;
  letter-spacing: 0.5px;
}

.dot {
  width: 7px;
  height: 7px;
  background: #EF4444;
  border-radius: 50%;
  animation: blink 1.5s ease-in-out infinite;
}

@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0.3; } }

.clock {
  font-size: 12px;
  color: #94A3B8;
  font-family: 'DM Sans', monospace;
  font-weight: 500;
  background: rgba(255,255,255,0.06);
  padding: 4px 12px;
  border-radius: 6px;
}

.page-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.page-body::-webkit-scrollbar { width: 6px; }
.page-body::-webkit-scrollbar-track { background: transparent; }
.page-body::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 3px; }

.overview-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.kpi-banner {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
}

.kpi-item {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px 14px;
  text-align: center;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.kpi-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); transform: translateY(-1px); }

.kpi-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--kpi-color, #2F6BFF);
  border-radius: 12px 12px 0 0;
}

.kpi-num {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.kpi-label {
  font-size: 11.5px;
  color: #94A3B8;
  margin-top: 6px;
  font-weight: 500;
}

.time-range-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}
.range-btn {
  padding: 6px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
  background: #fff;
  color: #64748B;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.range-btn:hover {
  border-color: #2F6BFF;
  color: #2F6BFF;
}
.range-btn.active {
  background: #2F6BFF;
  color: #fff;
  border-color: #2F6BFF;
}
.overview-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 16px;
}

.panel {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
}

.panel:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.panel-trend, .panel-hot { height: 280px; }
.panel-channel { height: 280px; }
.panel-funnel { height: 280px; }
.panel-event { height: 280px; }
.panel-source { height: 280px; }
.panel-active-hour { height: 280px; }
.panel-publish-trend { height: 280px; }

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.panel-head h2 {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  margin: 0;
}

.panel-tag {
  font-size: 10px;
  font-weight: 600;
  color: #2F6BFF;
  background: #EFF6FF;
  padding: 3px 10px;
  border-radius: 10px;
  letter-spacing: 0.3px;
}

.panel-chart { flex: 1; min-height: 0; }

.manage-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 900px;
  margin: 20px auto;
}
.stat-card {
  background: rgba(30,41,59,0.6);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #F1F5F9;
  font-family: 'DM Sans', sans-serif;
}
.stat-label {
  font-size: 13px;
  color: #94A3B8;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  max-width: 380px;
  padding: 9px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  background: #fff;
  color: #1E293B;
  transition: border-color 0.2s;
}

.search-input:focus { border-color: #2F6BFF; box-shadow: 0 0 0 3px rgba(47,107,255,0.1); }
.search-input::placeholder { color: #94A3B8; }

.search-select {
  padding: 9px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  background: #fff;
  color: #1E293B;
  outline: none;
  cursor: pointer;
}

.search-select option { background: #fff; }

.btn-primary {
  padding: 9px 22px;
  background: #2F6BFF;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover { background: #1E5AE0; box-shadow: 0 2px 8px rgba(47,107,255,0.3); }

.btn-ghost {
  padding: 9px 22px;
  background: transparent;
  color: #64748B;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.btn-ghost:hover { border-color: #CBD5E1; background: #F8FAFC; }

.result-count { font-size: 13px; color: #64748B; font-weight: 500; }

.table-card {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  overflow: hidden;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.table-card::-webkit-scrollbar { width: 6px; }
.table-card::-webkit-scrollbar-track { background: transparent; }
.table-card::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 3px; }

.news-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.news-table th {
  background: #F8FAFC;
  color: #64748B;
  padding: 11px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 11.5px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #E2E8F0;
  position: sticky;
  top: 0;
  z-index: 1;
}

.news-table td {
  padding: 12px 16px;
  color: #334155;
  border-bottom: 1px solid #F1F5F9;
}

.news-table tr:hover td { background: #FAFBFE; }

.col-id { width: 55px; }
.col-title { max-width: 340px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.col-ch { width: 80px; }
.col-src { width: 100px; }
.col-num { width: 65px; text-align: right; }
.col-time { width: 120px; }

.ch-tag {
  display: inline-block;
  padding: 2px 10px;
  background: #EFF6FF;
  border-radius: 10px;
  color: #2F6BFF;
  font-size: 11px;
  font-weight: 600;
}

.toast {
  position: fixed;
  top: 64px;
  left: 50%;
  transform: translateX(-50%);
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  z-index: 9999;
  pointer-events: none;
  animation: fadeInDown 0.2s ease;
}
.toast.success { background: #10B981; color: #fff; }
.toast.error { background: #EF4444; color: #fff; }
@keyframes fadeInDown { from { opacity: 0; transform: translateX(-50%) translateY(-8px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }

.empty-row { text-align: center; color: #94A3B8; padding: 60px 0 !important; font-size: 14px; }

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 14px 0;
  font-size: 13px;
  color: #64748B;
  flex-wrap: wrap;
}

.pager button {
  padding: 7px 18px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  color: #334155;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}

.pager button:hover:not(:disabled) { border-color: #2F6BFF; color: #2F6BFF; }
.pager button:disabled { opacity: 0.4; cursor: not-allowed; }

/* 实时行为页面样式 */
.behavior-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.behavior-layout {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.behavior-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.behavior-right {
  width: 420px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.behavior-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.behavior-stat-card {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 16px 14px;
  text-align: center;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.behavior-stat-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); transform: translateY(-1px); }

.behavior-stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--kpi-color, #2F6BFF);
  border-radius: 12px 12px 0 0;
}

.behavior-stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.behavior-stat-label {
  font-size: 11px;
  color: #94A3B8;
  margin-top: 6px;
  font-weight: 500;
}

.behavior-event-panel {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.behavior-event-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.behavior-event-head h2 {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  margin: 0;
}

.behavior-event-list {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.behavior-event-list::-webkit-scrollbar { width: 4px; }
.behavior-event-list::-webkit-scrollbar-track { background: transparent; }
.behavior-event-list::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 2px; }

.behavior-event-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid #F1F5F9;
  font-size: 13px;
  color: #475569;
  transition: background 0.15s;
}

.behavior-event-item:hover { background: #FAFBFE; }
.behavior-event-item:last-child { border-bottom: none; }

.behavior-event-time {
  font-family: 'DM Sans', monospace;
  font-size: 11px;
  color: #94A3B8;
  flex-shrink: 0;
  min-width: 70px;
}

.behavior-event-user {
  font-weight: 600;
  color: #334155;
  flex-shrink: 0;
}

.behavior-event-action {
  color: #94A3B8;
  flex-shrink: 0;
}

.behavior-event-type {
  font-weight: 600;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
}

.type-view { color: #2F6BFF; background: #EFF6FF; }
.type-click { color: #7C3AED; background: #F5F3FF; }
.type-read { color: #10B981; background: #ECFDF5; }
.type-search { color: #F59E0B; background: #FFFBEB; }
.type-ai { color: #8B5CF6; background: #F5F3FF; }
.type-comment { color: #EC4899; background: #FDF2F8; }
.type-share { color: #06B6D4; background: #ECFEFF; }
.type-fav { color: #EF4444; background: #FEF2F2; }

.behavior-event-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94A3B8;
  font-size: 14px;
  min-height: 200px;
}

.panel-behavior-dist { height: 280px; }
.panel-behavior-trend { height: 280px; }

@media (max-width: 1200px) {
  .kpi-banner { grid-template-columns: repeat(4, 1fr); }
  .bar-inner { padding: 0 16px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .behavior-layout { flex-direction: column; }
  .behavior-right { width: 100%; flex-direction: row; }
  .behavior-right .panel { flex: 1; }
}

@media (max-width: 900px) {
  .kpi-banner { grid-template-columns: repeat(2, 1fr); }
  .overview-grid { grid-template-columns: 1fr; }
  .bar-inner { padding: 0 12px; gap: 16px; }
  .brand-sub { display: none; }
  .live-tag { display: none; }
  .behavior-stats { grid-template-columns: repeat(2, 1fr); }
  .behavior-layout { flex-direction: column; }
  .behavior-right { width: 100%; flex-direction: column; }
}

@media (max-width: 600px) {
  .top-bar { height: auto; min-height: 48px; padding: 6px 0; }
  .bar-inner { flex-wrap: wrap; padding: 0 10px; gap: 8px; }
  .brand { order: 1; gap: 6px; }
  .brand-icon { width: 26px; height: 26px; }
  .brand-name { font-size: 15px; }
  .bar-right { order: 2; margin-left: auto; gap: 8px; }
  .main-nav { order: 3; width: 100%; justify-content: space-between; gap: 2px; }
  .nav-link { padding: 6px 10px; font-size: 11px; gap: 4px; }
  .nav-icon { width: 14px; height: 14px; }
  .home-btn { padding: 4px 8px; font-size: 11px; }
  .home-icon { width: 14px; height: 14px; }
  .clock { font-size: 10px; padding: 2px 8px; }
  .overview-page { padding: 12px 8px; gap: 12px; }
  .manage-page { padding: 12px 8px; }
  .behavior-page { padding: 12px 8px; }
  .kpi-banner { grid-template-columns: repeat(2, 1fr); gap: 8px; }
  .kpi-item { padding: 10px 8px; }
  .kpi-num { font-size: 18px; }
  .kpi-label { font-size: 10px; margin-top: 4px; }
  .time-range-bar { gap: 6px; margin-bottom: 12px; }
  .range-btn { padding: 5px 12px; font-size: 12px; }
  .overview-grid { grid-template-columns: 1fr; gap: 10px; }
  .panel { padding: 12px; }
  .panel-trend, .panel-hot { height: 220px; }
  .panel-channel, .panel-funnel, .panel-event { height: 220px; }
  .panel-source, .panel-active-hour, .panel-publish-trend { height: 220px; }
  .panel-head h2 { font-size: 13px; }
  .search-bar { flex-direction: column; align-items: stretch; gap: 8px; }
  .search-input { max-width: 100%; padding: 8px 12px; }
  .search-select { width: 100%; padding: 8px 12px; }
  .news-table th, .news-table td { padding: 8px 6px; font-size: 11px; }
  .col-title { max-width: 120px; }
  .col-src, .col-time { display: none; }
  .pager { gap: 10px; font-size: 12px; }
  .pager button { padding: 6px 14px; font-size: 12px; }
  .field-row { flex-direction: column; gap: 0; }
  .behavior-stats { grid-template-columns: repeat(2, 1fr); }
  .behavior-stat-card { padding: 10px 8px; }
  .behavior-stat-value { font-size: 16px; }
  .behavior-stat-label { font-size: 10px; }
  .panel-behavior-dist, .panel-behavior-trend { height: 220px; }
}

@media (max-width: 400px) {
  .bar-inner { padding: 0 6px; gap: 4px; }
  .brand-name { font-size: 13px; }
  .nav-link { padding: 4px 8px; font-size: 10px; }
  .kpi-num { font-size: 16px; }
  .kpi-label { font-size: 9px; }
  .overview-page { padding: 8px 6px; gap: 8px; }
  .panel-trend, .panel-hot { height: 180px; }
  .panel-channel, .panel-funnel, .panel-event { height: 180px; }
  .panel-source, .panel-active-hour, .panel-publish-trend { height: 180px; }
}

/* ======================== 管理后台新增样式 ======================== */

/* 导航分隔符 */
.nav-separator {
  width: 1px;
  height: 20px;
  background: rgba(255,255,255,0.15);
  margin: 0 6px;
  flex-shrink: 0;
  align-self: center;
}

/* 管理标签样式 */
.nav-link-admin {
  color: #7C8DB5;
}
.nav-link-admin:hover {
  background: rgba(245,158,11,0.12);
  color: #FBBF24;
}
.nav-link-admin.active {
  background: rgba(245,158,11,0.2);
  color: #FBBF24;
  font-weight: 600;
}

/* 管理页面头部 */
.admin-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-page-title {
  font-size: 18px;
  font-weight: 700;
  color: #0F172A;
  margin: 0;
}

.admin-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-user-name {
  font-size: 13px;
  color: #64748B;
  font-weight: 500;
}

.btn-logout {
  padding: 5px 14px;
  background: transparent;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
  color: #94A3B8;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-logout:hover {
  border-color: #EF4444;
  color: #EF4444;
  background: #FEF2F2;
}

/* 状态标签 */
.col-status { width: 70px; }
.col-action { width: 130px; }
.col-username { width: 100px; }
.col-nickname { width: 100px; }
.col-email { width: 160px; }
.col-phone { width: 120px; }
.col-article-id { width: 70px; }
.col-user-id { width: 70px; }
.col-content { max-width: 260px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.status-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}
.status-0 { color: #94A3B8; background: #F1F5F9; }
.status-1 { color: #10B981; background: #ECFDF5; }
.status-2 { color: #EF4444; background: #FEF2F2; }

/* 表格操作按钮 */
.btn-table {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
  margin-right: 4px;
}
.btn-table:last-child { margin-right: 0; }

.btn-edit {
  background: #EFF6FF;
  color: #2F6BFF;
}
.btn-edit:hover {
  background: #DBEAFE;
}

.btn-delete {
  background: #FEF2F2;
  color: #EF4444;
}
.btn-delete:hover {
  background: #FEE2E2;
}

.btn-toggle {
  background: #FFFBEB;
  color: #F59E0B;
}
.btn-toggle:hover {
  background: #FEF3C7;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15,23,42,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.15s ease;
}

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-box {
  background: #fff;
  border-radius: 16px;
  width: 400px;
  max-width: 90vw;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
  animation: slideUp 0.2s ease;
}

.modal-box-lg {
  width: 560px;
}

.modal-box-sm {
  width: 360px;
}

@keyframes slideUp { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;
}

.modal-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #F1F5F9;
  border-radius: 8px;
  font-size: 18px;
  color: #64748B;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}
.modal-close:hover {
  background: #E2E8F0;
  color: #334155;
}

.modal-body {
  padding: 20px 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 24px 20px;
}

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}
.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 9px 14px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  background: #fff;
  color: #1E293B;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-input:focus {
  border-color: #2F6BFF;
  box-shadow: 0 0 0 3px rgba(47,107,255,0.1);
}
.form-input::placeholder { color: #94A3B8; }

.form-textarea {
  width: 100%;
  padding: 9px 14px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  background: #fff;
  color: #1E293B;
  transition: border-color 0.2s;
  resize: vertical;
  box-sizing: border-box;
  font-family: inherit;
}
.form-textarea:focus {
  border-color: #2F6BFF;
  box-shadow: 0 0 0 3px rgba(47,107,255,0.1);
}
.form-textarea::placeholder { color: #94A3B8; }

.form-select {
  width: 100%;
  padding: 9px 14px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  background: #fff;
  color: #1E293B;
  outline: none;
  cursor: pointer;
  box-sizing: border-box;
}

.form-row {
  display: flex;
  gap: 12px;
}
.form-group-half {
  flex: 1;
}

.form-error {
  color: #EF4444;
  font-size: 12px;
  margin: 0;
  margin-top: 4px;
}

.confirm-text {
  font-size: 14px;
  color: #334155;
  margin: 0;
  line-height: 1.6;
}

.btn-danger {
  background: #EF4444;
}
.btn-danger:hover {
  background: #DC2626;
  box-shadow: 0 2px 8px rgba(239,68,68,0.3);
}

/* 图片上传样式 */
.image-upload-area {
  width: 100%;
}
.image-preview {
  position: relative;
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
}
.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.image-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(0,0,0,0.6);
  color: #fff;
  border: none;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
.image-remove:hover {
  background: rgba(239,68,68,0.8);
}
.image-upload-placeholder {
  width: 200px;
  height: 120px;
  border: 2px dashed #E2E8F0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.image-upload-placeholder:hover {
  border-color: #2F6BFF;
  background: #F8FAFF;
}
.upload-text {
  font-size: 12px;
  color: #94A3B8;
}
</style>
