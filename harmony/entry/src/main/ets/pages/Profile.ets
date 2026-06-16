import router from '@ohos.router'
import { ApiService } from '../model/ApiService'
import { StorageUtil } from '../common/StorageUtil'
import { User, FavoriteItem, HistoryItem, ApiResponse, PageResult, News } from '../model/Models'
import { LoadingState } from '../components/LoadingState'
import promptAction from '@ohos.promptAction'

@Entry
@Component
struct Profile {
  @State user: User | null = null
  @State isLogged: boolean = false
  @State currentTab: number = 0
  @State favorites: FavoriteItem[] = []
  @State historyList: HistoryItem[] = []
  @State loadState: 'loading' | 'empty' | 'error' | 'success' = 'loading'
  private storageUtil: StorageUtil | null = null

  async aboutToAppear() {
    try {
      const context = getContext(this)
      this.storageUtil = await StorageUtil.getInstance(context)
      await this.checkLogin()
    } catch (e) {
    }
  }

  async checkLogin() {
    if (!this.storageUtil) return

    const token = await this.storageUtil.getToken()
    if (token) {
      this.isLogged = true
      await this.loadUserInfo(token)
      await this.loadFavorites(token)
      await this.loadHistory()
    } else {
      this.isLogged = false
      this.loadState = 'empty'
    }
  }

  async loadUserInfo(token: string) {
    try {
      const result = await ApiService.getUserProfile(token)
      if (result.success) {
        this.user = result.data
      }
    } catch (e) {
    }
  }

  async loadFavorites(token: string) {
    try {
      const result = await ApiService.getFavorites(token)
      if (result.success) {
        this.favorites = result.data || []
      }
    } catch (e) {
    }
  }

  async loadHistory() {
    if (!this.storageUtil) return
    try {
      const token = await this.storageUtil.getToken()
      if (token) {
        const result = await ApiService.getHistory(token)
        if (result.success) {
          this.historyList = result.data || []
        }
      }
      this.loadState = 'success'
    } catch (e) {
      this.loadState = 'empty'
    }
  }

  async handleLogout() {
    if (!this.storageUtil) return
    await this.storageUtil.removeToken()
    await this.storageUtil.remove('user_info')
    this.isLogged = false
    this.user = null
    this.favorites = []
    this.historyList = []
    promptAction.showToast({ message: '已退出登录', duration: 1500 })
  }

  @Builder
  FavoritesList() {
    if (this.favorites.length === 0) {
      LoadingState({ state: 'empty', emptyText: '暂无收藏' })
    } else {
      List() {
        ForEach(this.favorites, (item: FavoriteItem) => {
          ListItem() {
            Row() {
              if (item.newsCoverImage) {
                Image(item.newsCoverImage)
                  .width(80)
                  .height(60)
                  .borderRadius(8)
                  .objectFit(ImageFit.Cover)
                  .alt($r('app.media.app_icon'))
              }

              Column() {
                Text(item.newsTitle)
                  .fontSize(14)
                  .fontColor('#1C1B1F')
                  .maxLines(2)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .width('100%')

                Text(item.createTime)
                  .fontSize(11)
                  .fontColor('#79747E')
                  .margin({ top: 6 })
              }
              .layoutWeight(1)
              .margin({ left: 12 })
              .alignItems(HorizontalAlign.Start)
            }
            .width('100%')
            .padding(12)
            .backgroundColor(Color.White)
            .borderRadius(12)
            .onClick(() => {
              router.pushUrl({
                url: 'pages/NewsDetail',
                params: { id: item.newsId }
              })
            })
          }
          .margin({ bottom: 8 })
        }, (item: FavoriteItem) => item.id)
      }
      .width('100%')
      .layoutWeight(1)
      .padding({ left: 16, right: 16, top: 8 })
    }
  }

  @Builder
  HistoryList() {
    if (this.historyList.length === 0) {
      LoadingState({ state: 'empty', emptyText: '暂无浏览历史' })
    } else {
      List() {
        ForEach(this.historyList, (item: HistoryItem) => {
          ListItem() {
            Row() {
              if (item.newsCoverImage) {
                Image(item.newsCoverImage)
                  .width(80)
                  .height(60)
                  .borderRadius(8)
                  .objectFit(ImageFit.Cover)
                  .alt($r('app.media.app_icon'))
              }

              Column() {
                Text(item.newsTitle)
                  .fontSize(14)
                  .fontColor('#1C1B1F')
                  .maxLines(2)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .width('100%')

                Text(item.viewedAt)
                  .fontSize(11)
                  .fontColor('#79747E')
                  .margin({ top: 6 })
              }
              .layoutWeight(1)
              .margin({ left: 12 })
              .alignItems(HorizontalAlign.Start)
            }
            .width('100%')
            .padding(12)
            .backgroundColor(Color.White)
            .borderRadius(12)
            .onClick(() => {
              router.pushUrl({
                url: 'pages/NewsDetail',
                params: { id: item.newsId }
              })
            })
          }
          .margin({ bottom: 8 })
        }, (item: HistoryItem) => item.newsId)
      }
      .width('100%')
      .layoutWeight(1)
      .padding({ left: 16, right: 16, top: 8 })
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

        Text('个人中心')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#1C1B1F')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Text('  ')
          .fontSize(22)
      }
      .width('100%')
      .height(56)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)
      .alignItems(VerticalAlign.Center)

      if (!this.isLogged) {
        Column() {
          Text('🔒')
            .fontSize(48)
          Text('请先登录')
            .fontSize(16)
            .fontColor('#49454F')
            .margin({ top: 12 })

          Button('去登录')
            .fontSize(14)
            .backgroundColor('#6750A4')
            .fontColor(Color.White)
            .borderRadius(20)
            .height(40)
            .margin({ top: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/Login' })
            })
        }
        .width('100%')
        .layoutWeight(1)
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else {
        Column() {
          Row() {
            Image(this.user?.avatar || $r('app.media.app_icon'))
              .width(64)
              .height(64)
              .borderRadius(32)

            Column() {
              Text(this.user?.nickname || this.user?.username || '用户')
                .fontSize(18)
                .fontWeight(FontWeight.Medium)
                .fontColor('#1C1B1F')
              Text(this.user?.email || '')
                .fontSize(13)
                .fontColor('#79747E')
                .margin({ top: 4 })
            }
            .alignItems(HorizontalAlign.Start)
            .margin({ left: 16 })
            .layoutWeight(1)

            Button('退出')
              .fontSize(13)
              .backgroundColor('#FFFBFE')
              .fontColor('#B3261E')
              .borderRadius(16)
              .height(32)
              .border({ width: 1, color: '#B3261E' })
              .onClick(() => {
                this.handleLogout()
              })
          }
          .width('100%')
          .padding(20)
          .backgroundColor(Color.White)
          .borderRadius(12)
          .margin({ top: 12, left: 16, right: 16 })

          Tabs({ index: $$currentTab }) {
            TabContent() {
              this.FavoritesList()
            }
            .tabBar('我的收藏')

            TabContent() {
              this.HistoryList()
            }
            .tabBar('浏览历史')
          }
          .width('100%')
          .layoutWeight(1)
          .margin({ top: 12 })
          .barMode(BarMode.Fixed)
        }
        .width('100%')
        .layoutWeight(1)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F4EFF4')
  }
}
