export class Constants {
  static readonly BASE_URL: string = 'https://16709c24.r7.cpolar.cn'
  static readonly PAGE_SIZE: number = 10
  static readonly TOKEN_KEY: string = 'auth_token'
  static readonly USER_INFO_KEY: string = 'user_info'
  static readonly HISTORY_KEY: string = 'browse_history'
  static readonly FAVORITES_KEY: string = 'favorites_cache'

  static readonly CHANNELS: ChannelItem[] = [
    { id: 'all', name: '推荐' },
    { id: '互联网', name: '互联网' },
    { id: 'AI', name: 'AI' },
    { id: '创业', name: '创业' },
    { id: '数码', name: '数码' },
    { id: '硬件', name: '硬件' },
    { id: '汽车科技', name: '汽车科技' },
    { id: '云计算', name: '云计算' },
    { id: '大数据', name: '大数据' },
    { id: '安全', name: '安全' },
    { id: '区块链', name: '区块链' }
  ]
}

export interface ChannelItem {
  id: string
  name: string
}
