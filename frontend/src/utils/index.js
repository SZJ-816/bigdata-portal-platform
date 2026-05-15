import DOMPurify from 'dompurify'

export function cleanText(text) {
  if (!text) return ''
  let cleaned = text
  cleaned = cleaned.replace(/<[^>]*>/g, '')
  cleaned = cleaned.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&nbsp;/g, ' ').replace(/&quot;/g, '"').replace(/&#39;/g, "'")
  cleaned = cleaned.replace(/[\uFFFD\u00EF\u00BF\u00BD]/g, '')
  return cleaned.trim()
}

export function cleanHtmlContent(html) {
  if (!html) return ''
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'br', 'hr', 'blockquote', 'pre', 'code', 'strong', 'em', 'b', 'i', 'u', 's', 'a', 'ul', 'ol', 'li', 'img', 'table', 'thead', 'tbody', 'tr', 'th', 'td', 'figure', 'figcaption', 'span', 'div'],
    ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'id', 'target', 'rel', 'colspan', 'rowspan', 'style', 'data-channel'],
    ALLOW_DATA_ATTR: false,
    ADD_ATTR: ['target']
  })
}

export function formatViewCount(count) {
  if (!count) return 0
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count
}

const CHANNEL_ICON_MAP = {
  'ai': '/images/channel-ai.svg',
  '人工智能': '/images/channel-ai.svg',
  'bigdata': '/images/channel-bigdata.svg',
  '大数据': '/images/channel-bigdata.svg',
  'cloud': '/images/channel-cloud.svg',
  '云计算': '/images/channel-cloud.svg',
  'internet': '/images/channel-internet.svg',
  '互联网': '/images/channel-internet.svg',
  'hardware': '/images/channel-hardware.svg',
  '硬件': '/images/channel-hardware.svg',
  'startup': '/images/channel-startup.svg',
  '创业': '/images/channel-startup.svg',
}

export function getChannelIcon(channel) {
  if (!channel) return '/images/channel-internet.svg'
  const lower = channel.toLowerCase().trim()
  for (const [key, icon] of Object.entries(CHANNEL_ICON_MAP)) {
    if (lower.includes(key.toLowerCase()) || key.toLowerCase().includes(lower)) {
      return icon
    }
  }
  return '/images/channel-internet.svg'
}

export const CHANNEL_LABEL_MAP = {
  'AI': '人工智能',
  '大数据': '大数据',
  '云计算': '云计算',
  '互联网': '互联网',
  '硬件': '硬件',
  '创业': '创业',
  '人工智能': '人工智能'
}

export function renderMarkdown(text) {
  if (!text) return ''
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code>$2</code></pre>')
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>')
  html = html.replace(/\n\n/g, '</p><p>')
  html = html.replace(/\n/g, '<br>')
  html = '<p>' + html + '</p>'
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'pre', 'code', 'a', 'ul', 'ol', 'li'],
    ALLOWED_ATTR: ['href', 'target', 'rel']
  })
}

export const channels = [
  { name: 'AI', label: '人工智能' },
  { name: '大数据', label: '大数据' },
  { name: '云计算', label: '云计算' },
  { name: '互联网', label: '互联网' },
  { name: '硬件', label: '硬件' },
  { name: '创业', label: '创业' }
]

export const hotSearchWords = ['GPT-5', '大模型', 'AI Agent', '云计算', '5G-A', '自动驾驶']

export function formatRelativeTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  const now = Date.now()
  const diff = now - date.getTime()
  const seconds = Math.floor(diff / 1000)
  if (seconds < 60) return '刚刚'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  const months = Math.floor(days / 30)
  if (months < 12) return `${months}个月前`
  return `${Math.floor(months / 12)}年前`
}
