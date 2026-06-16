export interface News {
  id: string
  title: string
  summary: string
  content: string
  coverUrl: string
  channel: string
  author: string
  publishTime: string
  viewCount: number
  likeCount: number
  commentCount: number
  isFavorite: boolean
  tags: string[]
}

export interface Comment {
  id: string
  newsId: string
  userId: string
  username: string
  avatar: string
  content: string
  createTime: string
  likeCount: number
}

export interface User {
  id: string
  username: string
  email: string
  avatar: string
  nickname: string
  createTime: string
}

export interface ApiResponse<T> {
  success: boolean
  data: T
  error: string
  message: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

export interface LoginResult {
  token: string
  user: User
}

export interface FavoriteItem {
  id: string
  newsId: string
  newsTitle: string
  newsCoverImage: string
  newsSummary: string
  newsChannel: string
  createTime: string
}

export interface HistoryItem {
  id: string
  newsId: string
  newsTitle: string
  newsCoverImage: string
  newsSummary: string
  newsChannel: string
  viewedAt: string
}

export interface HotSummary {
  summary: string
  topics: string[]
  newsIds: number[]
}
