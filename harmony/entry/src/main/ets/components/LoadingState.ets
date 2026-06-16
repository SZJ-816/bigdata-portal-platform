@Component
export struct LoadingState {
  @Prop state: 'loading' | 'empty' | 'error' = 'loading'
  @Prop emptyText: string = '暂无数据'
  @Prop errorText: string = '加载失败'
  onRetry?: () => void

  build() {
    Column() {
      if (this.state === 'loading') {
        LoadingProgress()
          .width(40)
          .height(40)
          .color('#6750A4')
        Text('加载中...')
          .fontSize(14)
          .fontColor('#79747E')
          .margin({ top: 12 })
      } else if (this.state === 'empty') {
        Text('📭')
          .fontSize(48)
        Text(this.emptyText)
          .fontSize(14)
          .fontColor('#79747E')
          .margin({ top: 8 })
      } else if (this.state === 'error') {
        Text('⚠️')
          .fontSize(48)
        Text(this.errorText)
          .fontSize(14)
          .fontColor('#79747E')
          .margin({ top: 8 })
        Button('重新加载')
          .fontSize(14)
          .backgroundColor('#6750A4')
          .fontColor(Color.White)
          .borderRadius(20)
          .height(36)
          .margin({ top: 16 })
          .onClick(() => {
            if (this.onRetry) {
              this.onRetry()
            }
          })
      }
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
}
