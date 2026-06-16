import { Constants, ChannelItem } from '../common/Constants'

@Component
export struct ChannelTabs {
  @Link currentChannel: string
  private channels: ChannelItem[] = Constants.CHANNELS
  private scroller: Scroller = new Scroller()
  onChannelChange?: (channel: string) => void

  build() {
    Scroll(this.scroller) {
      Row() {
        ForEach(this.channels, (channel: ChannelItem) => {
          Text(channel.name)
            .fontSize(14)
            .fontColor(this.currentChannel === channel.id ? '#6750A4' : '#49454F')
            .fontWeight(this.currentChannel === channel.id ? FontWeight.Bold : FontWeight.Normal)
            .padding({ left: 12, right: 12, top: 8, bottom: 8 })
            .borderRadius(20)
            .backgroundColor(this.currentChannel === channel.id ? '#EADDFF' : Color.Transparent)
            .onClick(() => {
              this.currentChannel = channel.id
              if (this.onChannelChange) {
                this.onChannelChange(channel.id)
              }
            })
        }, (channel: ChannelItem) => channel.id)
      }
    }
    .scrollable(ScrollDirection.Horizontal)
    .scrollBar(BarState.Off)
    .width('100%')
    .height(48)
    .padding({ left: 16, right: 16 })
  }
}
