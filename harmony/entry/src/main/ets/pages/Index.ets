import router from '@ohos.router'
import { ApiService } from '../model/ApiService'
import { Constants } from '../common/Constants'
import { News, ApiResponse, PageResult } from '../model/Models'
import { NewsCard } from '../components/NewsCard'
import { ChannelTabs } from '../components/ChannelTabs'
import { LoadingState } from '../components/LoadingState'

@Entry
@Component
struct Index {
  @State currentIndex: number = 0
  @State currentChannel: string = 'all'
  @State newsList: News[] = []
  @State page: number = 1
  @State hasMore: boolean = true
  @State loadState: 'loading' | 'empty' | 'error' | 'success' = 'loading'
  @State isRefreshing: boolean = false

  aboutToAppear() {
    this.loadNewsList(true)
  }

  async loadNewsList(refresh: boolean = false) {
    if (refresh) {
      this.page = 1
      this.hasMore = true
      this.loadState = 'loading'
    }

    try {
      const result = await ApiService.getNewsList(this.currentChannel, this.page)
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
    this.isRefreshing = false
  }

  loadMore() {
    if (this.hasMore && this.loadState === 'success') {
      this.page++
      this.loadNewsList(false)
    }
  }

  onChannelChange(channel: string) {
    this.currentChannel = channel
    this.loadNewsList(true)
  }

  @Builder
  HomeTab() {
    Column() {
      ChannelTabs({
        currentChannel: $currentChannel,
        onChannelChange: (channel: string) => {
          this.onChannelChange(channel)
        }
      })

      if (this.loadState === 'loading' || this.loadState === 'empty' || this.loadState === 'error') {
        LoadingState({
          state: this.loadState,
          emptyText: '暂无新闻',
          errorText: '新闻加载失败',
          onRetry: () => {
            this.loadNewsList(true)
          }
        })
      } else {
        Refresh({ refreshing: $$isRefreshing }) {
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
        }
        .onRefreshing(() => {
          this.loadNewsList(true)
        })
      }
    }
    .width('100%')
    .height('100%')
  }

  @Builder
  SearchTab() {
    Column() {
      Row() {
        Search({ placeholder: '搜索科技资讯' })
          .width('100%')
          .height(44)
          .backgroundColor('#F4EFF4')
          .borderRadius(22)
          .searchButton('搜索')
          .onSubmit((value: string) => {
            router.pushUrl({
              url: 'pages/Search',
              params: { keyword: value }
            })
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 8, bottom: 8 })

      Column() {
        Text('热门搜索')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .fontColor('#1C1B1F')
          .width('100%')
          .margin({ bottom: 12 })

        Flex({ wrap: FlexWrap.Wrap }) {
          ForEach(['人工智能', 'ChatGPT', '量子计算', '区块链', '云计算', '网络安全', '大数据', '物联网'], (tag: string) => {
            Text(tag)
              .fontSize(13)
              .fontColor('#6750A4')
              .backgroundColor('#EADDFF')
              .borderRadius(16)
              .padding({ left: 14, right: 14, top: 6, bottom: 6 })
              .margin({ right: 8, bottom: 8 })
              .onClick(() => {
                router.pushUrl({
                  url: 'pages/Search',
                  params: { keyword: tag }
                })
              })
          }, (tag: string) => tag)
        }
      }
      .width('100%')
      .padding(16)
      .alignItems(HorizontalAlign.Start)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFBFE')
  }

  @Builder
  ProfileTab() {
    Column() {
      Row() {
        Image($r('app.media.app_icon'))
          .width(64)
          .height(64)
          .borderRadius(32)

        Column() {
          Text('点击登录')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor('#1C1B1F')
          Text('登录后享受更多功能')
            .fontSize(13)
            .fontColor('#79747E')
            .margin({ top: 4 })
        }
        .alignItems(HorizontalAlign.Start)
        .margin({ left: 16 })
        .layoutWeight(1)
      }
      .width('100%')
      .padding(20)
      .backgroundColor(Color.White)
      .borderRadius(12)
      .margin({ top: 16, left: 16, right: 16 })
      .onClick(() => {
        router.pushUrl({ url: 'pages/Login' })
      })

      Column() {
        Row() {
          Text('我的收藏')
            .fontSize(15)
            .fontColor('#1C1B1F')
            .layoutWeight(1)
          Text('>')
            .fontSize(15)
            .fontColor('#79747E')
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 14, bottom: 14 })
        .onClick(() => {
          router.pushUrl({ url: 'pages/Profile' })
        })

        Divider().margin({ left: 16 })

        Row() {
          Text('浏览历史')
            .fontSize(15)
            .fontColor('#1C1B1F')
            .layoutWeight(1)
          Text('>')
            .fontSize(15)
            .fontColor('#79747E')
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 14, bottom: 14 })
        .onClick(() => {
          router.pushUrl({ url: 'pages/Profile' })
        })

        Divider().margin({ left: 16 })

        Row() {
          Text('设置')
            .fontSize(15)
            .fontColor('#1C1B1F')
            .layoutWeight(1)
          Text('>')
            .fontSize(15)
            .fontColor('#79747E')
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 14, bottom: 14 })
      }
      .backgroundColor(Color.White)
      .borderRadius(12)
      .margin({ top: 12, left: 16, right: 16 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F4EFF4')
  }

  build() {
    Column() {
      Tabs({ index: $$currentIndex }) {
        TabContent() {
          this.HomeTab()
        }
        .tabBar('首页')

        TabContent() {
          this.SearchTab()
        }
        .tabBar('搜索')

        TabContent() {
          this.ProfileTab()
        }
        .tabBar('我的')
      }
      .width('100%')
      .layoutWeight(1)
      .barMode(BarMode.Fixed)
      .onChange((index: number) => {
        this.currentIndex = index
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F4EFF4')
  }
}
