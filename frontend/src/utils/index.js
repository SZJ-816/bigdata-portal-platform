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
  let cleaned = html
  cleaned = cleaned.replace(/[\uFFFD\u00EF\u00BF\u00BD]/g, '')
  cleaned = cleaned.replace(/<script[^>]*>[\s\S]*?<\/script>/gi, '')
  cleaned = cleaned.replace(/<style[^>]*>[\s\S]*?<\/style>/gi, '')
  cleaned = cleaned.replace(/<iframe[^>]*>[\s\S]*?<\/iframe>/gi, '')
  cleaned = cleaned.replace(/<iframe[^>]*\/?>/gi, '')
  cleaned = cleaned.replace(/<object[^>]*>[\s\S]*?<\/object>/gi, '')
  cleaned = cleaned.replace(/<embed[^>]*\/?>/gi, '')
  cleaned = cleaned.replace(/<form[^>]*>[\s\S]*?<\/form>/gi, '')
  cleaned = cleaned.replace(/\son\w+\s*=\s*["'][^"']*["']/gi, '')
  cleaned = cleaned.replace(/\son\w+\s*=\s*[^\s>]*/gi, '')
  cleaned = cleaned.replace(/href\s*=\s*["']javascript:[^"']*["']/gi, 'href="#"')
  cleaned = cleaned.replace(/(<img[^>]+src=["'])(https?:\/\/[^"']+)(["'][^>]*>)/gi, (match, prefix, url, suffix) => {
    return prefix + '/api/image/proxy?url=' + encodeURIComponent(url) + suffix
  })
  return cleaned.trim()
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
  return html
}
