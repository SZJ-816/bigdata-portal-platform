import router from '@ohos.router'
import { ApiService } from '../model/ApiService'
import { News, Comment, ApiResponse } from '../model/Models'
import { StorageUtil } from '../common/StorageUtil'
import { LoadingState } from '../components/LoadingState'
import promptAction from '@ohos.promptAction'

@Entry
@Component
struct NewsDetail {
  @State news: News | null = null
  @State comments: Comment[] = []
  @State commentPage: number = 1
  @State hasMoreComments: boolean = true
  @State loadState: 'loading' | 'empty' | 'error' | 'success' = 'loading'
  @State commentText: string = ''
  @State isFavorited: boolean = false
  @State isSubmitting: boolean = false
  private newsId: string = ''
  private storageUtil: StorageUtil | null = null

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>
    if (params && params.id) {
      this.newsId = params.id
    }
    this.loadNewsDetail()
    this.loadComments(true)
    this.checkFavorite()
  }

  async loadNewsDetail() {
    this.loadState = 'loading'
    try {
      const result = await ApiService.getNewsDetail(this.newsId)
      if (result.success) {
        this.news = result.data
        this.loadState = 'success'
        this.saveBrowseHistory()
      } else {
        this.loadState = 'error'
      }
    } catch (e) {
      this.loadState = 'error'
    }
  }

  async loadComments(refresh: boolean = false) {
    if (refresh) {
      this.commentPage = 1
      this.hasMoreComments = true
    }

    try {
      const result = await ApiService.getComments(this.newsId)
      if (result.success) {
        this.comments = result.data || []
      }
    } catch (e) {
    }
  }

  async checkFavorite() {
    try {
      const context = getContext(this)
      this.storageUtil = await StorageUtil.getInstance(context)
      this.isFavorited = await this.storageUtil.isFavorite(this.newsId)
    } catch (e) {
    }
  }

  async saveBrowseHistory() {
    if (this.news && this.storageUtil) {
      await this.storageUtil.addBrowseHistory(
        this.news.id,
        this.news.title,
        this.news.coverUrl
      )
    }
  }

  async toggleFavorite() {
    if (!this.storageUtil) return

    try {
      const token = await this.storageUtil.getToken()
      if (token) {
        if (this.isFavorited) {
          await ApiService.removeFavorite(this.newsId, token)
        } else {
          await ApiService.addFavorite(this.newsId, token)
        }
      }

      const isNowFavorite = await this.storageUtil.toggleFavorite(this.newsId)
      this.isFavorited = isNowFavorite
      promptAction.showToast({
        message: isNowFavorite ? '已收藏' : '已取消收藏',
        duration: 1500
      })
    } catch (e) {
      promptAction.showToast({ message: '操作失败', duration: 1500 })
    }
  }

  async submitComment() {
    if (!this.commentText.trim()) {
      promptAction.showToast({ message: '请输入评论内容', duration: 1500 })
      return
    }

    if (!this.storageUtil) return

    const token = await this.storageUtil.getToken()
    if (!token) {
      promptAction.showToast({ message: '请先登录', duration: 1500 })
      router.pushUrl({ url: 'pages/Login' })
      return
    }

    this.isSubmitting = true
    try {
      const result = await ApiService.postComment(this.newsId, this.commentText, token)
      if (result.success) {
        this.commentText = ''
        promptAction.showToast({ message: '评论成功', duration: 1500 })
        this.loadComments(true)
      } else {
        promptAction.showToast({ message: result.error || result.message || '评论失败', duration: 1500 })
      }
    } catch (e) {
      promptAction.showToast({ message: '评论失败', duration: 1500 })
    }
    this.isSubmitting = false
  }

  @Builder
  CommentItem(comment: Comment) {
    Row() {
      Image(comment.avatar || $r('app.media.app_icon'))
        .width(36)
        .height(36)
        .borderRadius(18)

      Column() {
        Text(comment.username)
          .fontSize(13)
          .fontColor('#49454F')
          .fontWeight(FontWeight.Medium)

        Text(comment.content)
          .fontSize(14)
          .fontColor('#1C1B1F')
          .margin({ top: 4 })
          .width('100%')

        Row() {
          Text(comment.createTime)
            .fontSize(11)
            .fontColor('#79747E')

          Text(`赞 ${comment.likeCount}`)
            .fontSize(11)
            .fontColor('#79747E')
            .margin({ left: 16 })
        }
        .margin({ top: 4 })
      }
      .layoutWeight(1)
      .alignItems(HorizontalAlign.Start)
      .margin({ left: 10 })
    }
    .width('100%')
    .padding({ top: 12, bottom: 12 })
    .alignItems(VerticalAlign.Top)
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

        Text('文章详情')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#1C1B1F')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Text(this.isFavorited ? '★' : '☆')
          .fontSize(22)
          .fontColor(this.isFavorited ? '#6750A4' : '#79747E')
          .onClick(() => {
            this.toggleFavorite()
          })
      }
      .width('100%')
      .height(56)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)
      .alignItems(VerticalAlign.Center)

      if (this.loadState !== 'success') {
        LoadingState({
          state: this.loadState,
          errorText: '文章加载失败',
          onRetry: () => {
            this.loadNewsDetail()
          }
        })
      } else if (this.news) {
        Scroll() {
          Column() {
            Text(this.news.title)
              .fontSize(20)
              .fontWeight(FontWeight.Bold)
              .fontColor('#1C1B1F')
              .width('100%')

            Row() {
              Text(this.news.author)
                .fontSize(12)
                .fontColor('#79747E')
              Text(this.news.publishTime)
                .fontSize(12)
                .fontColor('#79747E')
                .margin({ left: 12 })
              Text(`${this.news.viewCount} 阅读`)
                .fontSize(12)
                .fontColor('#79747E')
                .margin({ left: 12 })
            }
            .margin({ top: 10 })
            .width('100%')

            if (this.news.coverUrl) {
              Image(this.news.coverUrl)
                .width('100%')
                .height(200)
                .borderRadius(8)
                .objectFit(ImageFit.Cover)
                .margin({ top: 16 })
                .alt($r('app.media.app_icon'))
            }

            Text(this.news.content)
              .fontSize(15)
              .fontColor('#1C1B1F')
              .lineHeight(26)
              .margin({ top: 16 })
              .width('100%')

            Divider()
              .margin({ top: 20, bottom: 16 })

            Text(`评论 (${this.comments.length})`)
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .fontColor('#1C1B1F')
              .width('100%')

            if (this.comments.length === 0) {
              Column() {
                Text('暂无评论，快来抢沙发~')
                  .fontSize(14)
                  .fontColor('#79747E')
              }
              .width('100%')
              .padding({ top: 24, bottom: 24 })
              .alignItems(HorizontalAlign.Center)
            } else {
              ForEach(this.comments, (comment: Comment) => {
                this.CommentItem(comment)
              }, (comment: Comment) => comment.id)

              if (this.hasMoreComments) {
                Text('加载更多评论')
                  .fontSize(13)
                  .fontColor('#6750A4')
                  .margin({ top: 8, bottom: 8 })
                  .onClick(() => {
                    this.commentPage++
                    this.loadComments(false)
                  })
              }
            }
          }
          .padding(16)
        }
        .layoutWeight(1)

        Row() {
          TextInput({ placeholder: '写评论...', text: $$commentText })
            .layoutWeight(1)
            .height(40)
            .borderRadius(20)
            .backgroundColor('#F4EFF4')
            .type(InputType.Normal)

          Button('发送')
            .height(36)
            .fontSize(14)
            .backgroundColor('#6750A4')
            .fontColor(Color.White)
            .borderRadius(18)
            .margin({ left: 8 })
            .enabled(!this.isSubmitting)
            .onClick(() => {
              this.submitComment()
            })
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 8, bottom: 8 })
        .backgroundColor(Color.White)
        .alignItems(VerticalAlign.Center)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFBFE')
  }
}
