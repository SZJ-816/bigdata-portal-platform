import preferences from '@ohos.data.preferences'
import { Context } from '@ohos.abilityAccessCtrl'

const PREFERENCES_NAME = 'bigdata_portal_prefs'

export class StorageUtil {
  private static instance: StorageUtil | null = null
  private prefs: preferences.Preferences | null = null

  private constructor() {}

  static async getInstance(context: Context): Promise<StorageUtil> {
    if (!StorageUtil.instance) {
      StorageUtil.instance = new StorageUtil()
      StorageUtil.instance.prefs = await preferences.getPreferences(context, PREFERENCES_NAME)
    }
    return StorageUtil.instance
  }

  async getString(key: string, defaultValue: string = ''): Promise<string> {
    if (!this.prefs) return defaultValue
    return await this.prefs.get(key, defaultValue) as string
  }

  async setString(key: string, value: string): Promise<void> {
    if (!this.prefs) return
    await this.prefs.put(key, value)
    await this.prefs.flush()
  }

  async getNumber(key: string, defaultValue: number = 0): Promise<number> {
    if (!this.prefs) return defaultValue
    return await this.prefs.get(key, defaultValue) as number
  }

  async setNumber(key: string, value: number): Promise<void> {
    if (!this.prefs) return
    await this.prefs.put(key, value)
    await this.prefs.flush()
  }

  async getBoolean(key: string, defaultValue: boolean = false): Promise<boolean> {
    if (!this.prefs) return defaultValue
    return await this.prefs.get(key, defaultValue) as boolean
  }

  async setBoolean(key: string, value: boolean): Promise<void> {
    if (!this.prefs) return
    await this.prefs.put(key, value)
    await this.prefs.flush()
  }

  async remove(key: string): Promise<void> {
    if (!this.prefs) return
    await this.prefs.delete(key)
    await this.prefs.flush()
  }

  async clear(): Promise<void> {
    if (!this.prefs) return
    await this.prefs.clear()
    await this.prefs.flush()
  }

  async getToken(): Promise<string> {
    return await this.getString('auth_token', '')
  }

  async setToken(token: string): Promise<void> {
    await this.setString('auth_token', token)
  }

  async removeToken(): Promise<void> {
    await this.remove('auth_token')
  }

  async getUserInfo(): Promise<string> {
    return await this.getString('user_info', '')
  }

  async setUserInfo(info: string): Promise<void> {
    await this.setString('user_info', info)
  }

  async getBrowseHistory(): Promise<string> {
    return await this.getString('browse_history', '[]')
  }

  async addBrowseHistory(newsId: string, title: string, coverUrl: string): Promise<void> {
    const historyStr = await this.getBrowseHistory()
    let history: Array<{ newsId: string, title: string, coverUrl: string, time: number }> = []
    try {
      history = JSON.parse(historyStr)
    } catch (e) {
      history = []
    }
    history = history.filter(item => item.newsId !== newsId)
    history.unshift({ newsId, title, coverUrl, time: Date.now() })
    if (history.length > 50) {
      history = history.slice(0, 50)
    }
    await this.setString('browse_history', JSON.stringify(history))
  }

  async isFavorite(newsId: string): Promise<boolean> {
    const favStr = await this.getString('favorites_cache', '[]')
    let favorites: string[] = []
    try {
      favorites = JSON.parse(favStr)
    } catch (e) {
      favorites = []
    }
    return favorites.includes(newsId)
  }

  async toggleFavorite(newsId: string): Promise<boolean> {
    const favStr = await this.getString('favorites_cache', '[]')
    let favorites: string[] = []
    try {
      favorites = JSON.parse(favStr)
    } catch (e) {
      favorites = []
    }
    const index = favorites.indexOf(newsId)
    if (index >= 0) {
      favorites.splice(index, 1)
      await this.setString('favorites_cache', JSON.stringify(favorites))
      return false
    } else {
      favorites.push(newsId)
      await this.setString('favorites_cache', JSON.stringify(favorites))
      return true
    }
  }
}
