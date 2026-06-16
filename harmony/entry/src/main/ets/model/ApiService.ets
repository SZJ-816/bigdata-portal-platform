import { HttpUtil } from '../common/HttpUtil'
import { Constants } from '../common/Constants'
import {
  News, Comment, User, ApiResponse, PageResult, LoginResult, FavoriteItem, HistoryItem, HotSummary
} from './Models'

export class ApiService {
  static async getNewsList(channel: string, page: number, size: number = Constants.PAGE_SIZE): Promise<ApiResponse<PageResult<News>>> {
    const params: Record<string, string> = {
      page: page.toString(),
      size: size.toString()
    }
    if (channel !== 'all') {
      params.channel = channel
    }
    return await HttpUtil.get<ApiResponse<PageResult<News>>>('/api/news', params)
  }

  static async getNewsDetail(id: string): Promise<ApiResponse<News>> {
    return await HttpUtil.get<ApiResponse<News>>(`/api/news/${id}`)
  }

  static async getHotNews(): Promise<ApiResponse<News[]>> {
    return await HttpUtil.get<ApiResponse<News[]>>('/api/news/hot')
  }

  static async getHotSummary(): Promise<ApiResponse<HotSummary>> {
    return await HttpUtil.get<ApiResponse<HotSummary>>('/api/ai/hot-summary')
  }

  static async getComments(newsId: string): Promise<ApiResponse<Comment[]>> {
    return await HttpUtil.get<ApiResponse<Comment[]>>(`/api/news/${newsId}/comments`)
  }

  static async postComment(newsId: string, content: string, token: string): Promise<ApiResponse<null>> {
    return await HttpUtil.post<ApiResponse<null>>(`/api/news/${newsId}/comment`,
      { content },
      { 'Authorization': `Bearer ${token}` }
    )
  }

  static async searchNews(keyword: string, page: number, size: number = Constants.PAGE_SIZE): Promise<ApiResponse<PageResult<News>>> {
    return await HttpUtil.get<ApiResponse<PageResult<News>>>('/api/news', {
      keyword,
      page: page.toString(),
      size: size.toString()
    })
  }

  static createAiSearchRequest(keyword: string): { request: Object, fullUrl: string } {
    const fullUrl = `${Constants.BASE_URL}/api/ai/search/stream?keyword=${encodeURIComponent(keyword)}`
    return {
      request: {
        method: http.RequestMethod.GET,
        header: { 'Accept': 'text/event-stream' },
        connectTimeout: 60000,
        readTimeout: 60000
      },
      fullUrl: fullUrl
    }
  }

  static async login(username: string, password: string): Promise<ApiResponse<LoginResult>> {
    return await HttpUtil.post<ApiResponse<LoginResult>>('/api/users/login', {
      username,
      password
    })
  }

  static async sendCode(email: string): Promise<ApiResponse<null>> {
    return await HttpUtil.post<ApiResponse<null>>('/api/users/send-code', {
      email
    })
  }

  static async register(username: string, password: string, email: string, code: string): Promise<ApiResponse<LoginResult>> {
    return await HttpUtil.post<ApiResponse<LoginResult>>('/api/users/register', {
      username,
      password,
      email,
      code
    })
  }

  static async getUserProfile(token: string): Promise<ApiResponse<User>> {
    return await HttpUtil.get<ApiResponse<User>>('/api/users/profile', undefined, {
      'Authorization': `Bearer ${token}`
    })
  }

  static async getFavorites(token: string): Promise<ApiResponse<FavoriteItem[]>> {
    return await HttpUtil.get<ApiResponse<FavoriteItem[]>>('/api/users/favorites', undefined, {
      'Authorization': `Bearer ${token}`
    })
  }

  static async addFavorite(newsId: string, token: string): Promise<ApiResponse<null>> {
    return await HttpUtil.post<ApiResponse<null>>(`/api/users/favorites/${newsId}`, undefined, {
      'Authorization': `Bearer ${token}`
    })
  }

  static async removeFavorite(newsId: string, token: string): Promise<ApiResponse<null>> {
    return await HttpUtil.delete<ApiResponse<null>>(`/api/users/favorites/${newsId}`, {
      'Authorization': `Bearer ${token}`
    })
  }

  static async getHistory(token: string): Promise<ApiResponse<HistoryItem[]>> {
    return await HttpUtil.get<ApiResponse<HistoryItem[]>>('/api/users/history', undefined, {
      'Authorization': `Bearer ${token}`
    })
  }

  static async addHistory(newsId: string, token: string): Promise<ApiResponse<null>> {
    return await HttpUtil.post<ApiResponse<null>>('/api/users/history', {
      newsId: parseInt(newsId),
      duration: 0
    }, {
      'Authorization': `Bearer ${token}`
    })
  }
}

import http from '@ohos.net.http'
