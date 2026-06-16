@Component
export struct NewsCard {
  @Prop news: News
  onItemClick?: (news: News) => void

  build() {
    Row() {
      Column() {
        Text(this.news.title)
          .fontSize(16)
          .fontColor('#1C1B1F')
          .fontWeight(FontWeight.Medium)
          .maxLines(2)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .width('100%')

        Text(this.news.summary)
          .fontSize(13)
          .fontColor('#49454F')
          .margin({ top: 6 })
          .maxLines(2)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .width('100%')

        Row() {
          Text(this.news.author)
            .fontSize(11)
            .fontColor('#79747E')

          Text(this.news.publishTime)
            .fontSize(11)
            .fontColor('#79747E')
            .margin({ left: 12 })

          Row() {
            Text(`${this.news.viewCount}`)
              .fontSize(11)
              .fontColor('#79747E')
          }.margin({ left: 12 })

          Row() {
            Text(`${this.news.commentCount}`)
              .fontSize(11)
              .fontColor('#79747E')
          }.margin({ left: 12 })
        }
        .margin({ top: 8 })
        .width('100%')
      }
      .layoutWeight(1)
      .alignItems(HorizontalAlign.Start)

      if (this.news.coverUrl) {
        Image(this.news.coverUrl)
          .width(100)
          .height(72)
          .borderRadius(8)
          .objectFit(ImageFit.Cover)
          .margin({ left: 12 })
          .alt($r('app.media.app_icon'))
      }
    }
    .width('100%')
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .onClick(() => {
      if (this.onItemClick) {
        this.onItemClick(this.news)
      }
    })
  }
}
