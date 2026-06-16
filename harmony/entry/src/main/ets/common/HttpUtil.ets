import http from '@ohos.net.http'
import util from '@ohos.util'
import { Constants } from './Constants'

export interface RequestOptions {
  method: http.RequestMethod
  url: string
  data?: Object | string
  header?: Object
  extraData?: string
}

export class HttpUtil {
  static request<T>(options: RequestOptions): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      const httpRequest = http.createHttp()

      const fullUrl = options.url.startsWith('http') ? options.url : Constants.BASE_URL + options.url

      const header: Object = Object.assign({
        'Content-Type': 'application/json'
      }, options.header || {})

      const connectOptions: http.HttpRequestOptions = {
        method: options.method,
        header: header,
        extraData: options.data,
        expectDataType: http.HttpDataType.STRING,
        connectTimeout: 15000,
        readTimeout: 15000
      }

      httpRequest.request(fullUrl, connectOptions, (err, data) => {
        if (!err) {
          if (data.responseCode === 200 || data.responseCode === 201) {
            try {
              const result = JSON.parse(data.result as string) as T
              resolve(result)
            } catch (e) {
              reject(new Error('数据解析失败'))
            }
          } else {
            reject(new Error(`请求失败，状态码: ${data.responseCode}`))
          }
        } else {
          reject(new Error(`网络请求错误: ${err.message}`))
        }
        httpRequest.destroy()
      })
    })
  }

  static get<T>(url: string, params?: Record<string, string>, header?: Object): Promise<T> {
    let fullUrl = url
    if (params) {
      const queryParts: string[] = []
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined && params[key] !== null) {
          queryParts.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        }
      })
      if (queryParts.length > 0) {
        fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryParts.join('&')
      }
    }
    return HttpUtil.request<T>({
      method: http.RequestMethod.GET,
      url: fullUrl,
      header: header
    })
  }

  static post<T>(url: string, data?: Object, header?: Object): Promise<T> {
    return HttpUtil.request<T>({
      method: http.RequestMethod.POST,
      url: url,
      data: data ? JSON.stringify(data) : undefined,
      header: header
    })
  }

  static put<T>(url: string, data?: Object, header?: Object): Promise<T> {
    return HttpUtil.request<T>({
      method: http.RequestMethod.PUT,
      url: url,
      data: data ? JSON.stringify(data) : undefined,
      header: header
    })
  }

  static delete<T>(url: string, header?: Object): Promise<T> {
    return HttpUtil.request<T>({
      method: http.RequestMethod.DELETE,
      url: url,
      header: header
    })
  }

  static createSseRequest(url: string, params?: Record<string, string>): http.HttpRequest {
    const httpRequest = http.createHttp()

    let fullUrl = url.startsWith('http') ? url : Constants.BASE_URL + url
    if (params) {
      const queryParts: string[] = []
      Object.keys(params).forEach(key => {
        if (params[key] !== undefined && params[key] !== null) {
          queryParts.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        }
      })
      if (queryParts.length > 0) {
        fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryParts.join('&')
      }
    }

    return httpRequest
  }

  static startSseRequest(
    httpRequest: http.HttpRequest,
    url: string,
    token?: string,
    onData?: (chunk: string) => void,
    onComplete?: (fullText: string) => void,
    onError?: (err: Error) => void
  ): void {
    let fullUrl = url.startsWith('http') ? url : Constants.BASE_URL + url

    let accumulated = ''

    const header: Record<string, string> = {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    }
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    httpRequest.on('dataReceive', (data: ArrayBuffer) => {
      const decoder = new util.TextDecoder('utf-8')
      const chunk = decoder.decodeToString(new Uint8Array(data))
      const lines = chunk.split('\n')
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.substring(5).trim()
          if (content === '[DONE]') {
            if (onComplete) {
              onComplete(accumulated)
            }
            httpRequest.destroy()
            return
          }
          accumulated += content
          if (onData) {
            onData(content)
          }
        }
      }
    })

    httpRequest.on('dataEnd', () => {
      if (onComplete) {
        onComplete(accumulated)
      }
    })

    httpRequest.requestInStream(fullUrl, {
      method: http.RequestMethod.GET,
      header: header,
      connectTimeout: 60000,
      readTimeout: 60000
    }).catch((err: Error) => {
      if (onError) {
        onError(err)
      }
    })
  }
}
