import router from '@ohos.router'
import http from '@ohos.net.http'
import util from '@ohos.util'
import { ApiService } from '../model/ApiService'
import { Constants } from '../common/Constants'
import { News, ApiResponse, PageResult } from '../model/Models'
import { NewsCard } from '../components/NewsCard'
import { LoadingState } from '../components/LoadingState'

@Entry
@Component
struct Search {
  @State keyword: string = ''
  @State searchMode: 'normal' | 'ai' = 'normal'
  @State newsList: News[] = []
  @State page: number = 1
  @State hasMore: boolean = true
  @State loadState: 'idle' | 'loading' | 'empty' | 'error' | 'success' = 'idle'
  @State aiAnswer: string = ''
  @State aiStreamingText: string = ''
  @State isAiSearching: boolean = false
  private sseRequest: http.HttpRequest | null = null

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>
    if (params && params.keyword) {
      this.keyword = params.keyword
      this.doSearch()
    }
  }

  aboutToDisappear() {
    this.destroySseRequest()
  }

  destroySseRequest() {
    if (this.sseRequest) {
      this.sseRequest.off('dataReceive')
      this.sseRequest.off('dataEnd')
      this.sseRequest.destroy()
      this.sseRequest = null
    }
  }

  async doSearch() {
    if (!this.keyword.trim()) return

    if (this.searchMode === 'normal') {
      this.doNormalSearch(true)
    } else {
      this.doAiSearch()
    }
  }

  async doNormalSearch(refresh: boolean = false) {
    if (refresh) {
      this.page = 1
      this.hasMore = true
    }
    this.loadState = 'loading'

    try {
      const result = await ApiService.searchNews(this.keyword, this.page)
      if (result.success) {
        const list = result.data.list || []
        if (refresh) {
          this.newsList = list
        } else {
          this.newsList = [...this.newsList, ...list]
        }
        this.hasMore = list.length >= Constants.PAGE_SIZE
        this.loadState = this.newsList.length > 0 ? 'success' : 'empty'
      } else {
        this.loadState = 'error'
      }
    } catch (e) {
      this.loadState = 'error'
    }
  }

  doAiSearch() {
    this.destroySseRequest()
    this.aiStreamingText = ''
    this.aiAnswer = ''
    this.isAiSearching = true
    this.loadState = 'loading'

    this.sseRequest = http.createHttp()

    let fullUrl = `${Constants.BASE_URL}/api/ai/search/stream?keyword=${encodeURIComponent(this.keyword)}`

    let accumulated = ''

    this.sseRequest.on('dataReceive', (data: ArrayBuffer) => {
      const decoder = new util.TextDecoder('utf-8')
      const chunk = decoder.decodeToString(new Uint8Array(data))
      const lines = chunk.split('\n')
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.substring(5).trim()
          if (content === '[DONE]') {
            this.aiAnswer = accumulated
            this.isAiSearching = false
            this.loadState = 'success'
            this.destroySseRequest()
            return
          }
          accumulated += content
          this.aiStreamingText = accumulated
        }
      }
    })

    this.sseRequest.on('dataEnd', () => {
      this.aiAnswer = accumulated
      this.isAiSearching = false
      this.loadState = accumulated.length > 0 ? 'success' : 'empty'
    })

    this.sseRequest.requestInStream(fullUrl, {
      method: http.RequestMethod.GET,
      header: { 'Accept': 'text/event-stream' },
      connectTimeout: 60000,
      readTimeout: 60000
    }).catch((err: Error) => {
      this.isAiSearching = false
      this.loadState = 'error'
    })
  }

  loadMore() {
    if (this.hasMore && this.loadState === 'success' && this.searchMode === 'normal') {
      this.page++
      this.doNormalSearch(false)
    }
  }

  build() {
    Column() {
      Row() {
        Text('<')
          .fontSize(22)
          .fontColor('#1C1B1F')
          .onClick(() => {
            router.back()
          })

        Search({ placeholder: '搜索科技资讯', text: $$keyword })
          .layoutWeight(1)
          .height(40)
          .margin({ left: 8 })
          .searchButton('搜索')
          .onSubmit((value: string) => {
            this.keyword = value
            this.doSearch()
          })
      }
      .width('100%')
      .height(56)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)
      .alignItems(VerticalAlign.Center)

      Row() {
        Text('普通搜索')
          .fontSize(14)
          .fontColor(this.searchMode === 'normal' ? '#6750A4' : '#49454F')
          .fontWeight(this.searchMode === 'normal' ? FontWeight.Bold : FontWeight.Normal)
          .padding({ left: 16, right: 16, top: 8, bottom: 8 })
          .borderRadius(20)
          .backgroundColor(this.searchMode === 'normal' ? '#EADDFF' : Color.Transparent)
          .onClick(() => {
            this.searchMode = 'normal'
          })

        Text('AI 智能搜索')
          .fontSize(14)
          .fontColor(this.searchMode === 'ai' ? '#6750A4' : '#49454F')
          .fontWeight(this.searchMode === 'ai' ? FontWeight.Bold : FontWeight.Normal)
          .padding({ left: 16, right: 16, top: 8, bottom: 8 })
          .borderRadius(20)
          .backgroundColor(this.searchMode === 'ai' ? '#EADDFF' : Color.Transparent)
          .margin({ left: 8 })
          .onClick(() => {
            this.searchMode = 'ai'
          })
      }
      .width('100%')
      .padding({ left: 16, top: 8, bottom: 8 })

      if (this.loadState === 'loading' && this.searchMode === 'normal') {
        LoadingState({ state: 'loading' })
      } else if (this.loadState === 'error') {
        LoadingState({
          state: 'error',
          errorText: '搜索失败',
          onRetry: () => {
            this.doSearch()
          }
        })
      } else if (this.loadState === 'empty') {
        LoadingState({ state: 'empty', emptyText: '未找到相关结果' })
      } else if (this.searchMode === 'ai') {
        Scroll() {
          Column() {
            if (this.isAiSearching || this.aiStreamingText || this.aiAnswer) {
              Row() {
                Text('AI 回答')
                  .fontSize(14)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#6750A4')
                if (this.isAiSearching) {
                  LoadingProgress()
                    .width(16)
                    .height(16)
                    .color('#6750A4')
                    .margin({ left: 8 })
                }
              }
              .margin({ bottom: 12 })

              Text(this.isAiSearching ? this.aiStreamingText : this.aiAnswer)
                .fontSize(15)
                .fontColor('#1C1B1F')
                .lineHeight(24)
                .width('100%')
            } else {
              Column() {
                Text('🤖')
                  .fontSize(48)
                Text('输入关键词，AI 为你智能解读')
                  .fontSize(14)
                  .fontColor('#79747E')
                  .margin({ top: 8 })
              }
              .width('100%')
              .justifyContent(FlexAlign.Center)
              .alignItems(HorizontalAlign.Center)
              .margin({ top: 100 })
            }
          }
          .padding(16)
          .width('100%')
        }
        .layoutWeight(1)
      } else if (this.searchMode === 'normal' && this.newsList.length > 0) {
        List() {
          ForEach(this.newsList, (news: News) => {
            ListItem() {
              NewsCard({
                news: news,
                onItemClick: (item: News) => {
                  router.pushUrl({
                    url: 'pages/NewsDetail',
                    params: { id: item.id }
                  })
                }
              })
            }
            .margin({ bottom: 8 })
          }, (news: News) => news.id)

          if (this.hasMore) {
            ListItem() {
              Row() {
                LoadingProgress()
                  .width(24)
                  .height(24)
                  .color('#6750A4')
                Text('加载更多...')
                  .fontSize(13)
                  .fontColor('#79747E')
                  .margin({ left: 8 })
              }
              .justifyContent(FlexAlign.Center)
              .width('100%')
              .padding(16)
            }
          }
        }
        .width('100%')
        .layoutWeight(1)
        .padding({ left: 16, right: 16, top: 8 })
        .onReachEnd(() => {
          this.loadMore()
        })
      } else {
        Column() {
          Text('🔍')
            .fontSize(48)
          Text('输入关键词开始搜索')
            .fontSize(14)
            .fontColor('#79747E')
            .margin({ top: 8 })
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFBFE')
  }
}
