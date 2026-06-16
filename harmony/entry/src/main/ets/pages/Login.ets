import router from '@ohos.router'
import { ApiService } from '../model/ApiService'
import { StorageUtil } from '../common/StorageUtil'
import { LoginResult, ApiResponse } from '../model/Models'
import promptAction from '@ohos.promptAction'

@Entry
@Component
struct Login {
  @State isLogin: boolean = true
  @State username: string = ''
  @State password: string = ''
  @State email: string = ''
  @State code: string = ''
  @State codeCountdown: number = 0
  @State canSendCode: boolean = true
  @State isLoading: boolean = false

  async handleLogin() {
    if (!this.username.trim()) {
      promptAction.showToast({ message: '请输入用户名', duration: 1500 })
      return
    }
    if (!this.password.trim()) {
      promptAction.showToast({ message: '请输入密码', duration: 1500 })
      return
    }

    this.isLoading = true
    try {
      const result = await ApiService.login(this.username, this.password)
      if (result.success) {
        await this.saveLoginInfo(result.data)
        promptAction.showToast({ message: '登录成功', duration: 1500 })
        router.back()
      } else {
        promptAction.showToast({ message: result.error || result.message || '登录失败', duration: 1500 })
      }
    } catch (e) {
      promptAction.showToast({ message: '登录失败，请检查网络', duration: 1500 })
    }
    this.isLoading = false
  }

  async handleSendCode() {
    if (!this.email.trim()) {
      promptAction.showToast({ message: '请输入邮箱', duration: 1500 })
      return
    }

    this.canSendCode = false
    this.codeCountdown = 60

    try {
      const result = await ApiService.sendCode(this.email)
      if (!result.success) {
        promptAction.showToast({ message: result.error || '发送失败', duration: 1500 })
        this.canSendCode = true
        this.codeCountdown = 0
      }
    } catch (e) {
      promptAction.showToast({ message: '发送失败', duration: 1500 })
      this.canSendCode = true
      this.codeCountdown = 0
    }

    const timer = setInterval(() => {
      this.codeCountdown--
      if (this.codeCountdown <= 0) {
        clearInterval(timer)
        this.canSendCode = true
      }
    }, 1000)
  }

  async handleRegister() {
    if (!this.username.trim()) {
      promptAction.showToast({ message: '请输入用户名', duration: 1500 })
      return
    }
    if (!this.password.trim()) {
      promptAction.showToast({ message: '请输入密码', duration: 1500 })
      return
    }
    if (!this.email.trim()) {
      promptAction.showToast({ message: '请输入邮箱', duration: 1500 })
      return
    }
    if (!this.code.trim()) {
      promptAction.showToast({ message: '请输入验证码', duration: 1500 })
      return
    }

    this.isLoading = true
    try {
      const result = await ApiService.register(this.username, this.password, this.email, this.code)
      if (result.success) {
        await this.saveLoginInfo(result.data)
        promptAction.showToast({ message: '注册成功', duration: 1500 })
        router.back()
      } else {
        promptAction.showToast({ message: result.error || result.message || '注册失败', duration: 1500 })
      }
    } catch (e) {
      promptAction.showToast({ message: '注册失败，请检查网络', duration: 1500 })
    }
    this.isLoading = false
  }

  async saveLoginInfo(data: LoginResult) {
    try {
      const context = getContext(this)
      const storageUtil = await StorageUtil.getInstance(context)
      await storageUtil.setToken(data.token)
      await storageUtil.setUserInfo(JSON.stringify(data.user))
    } catch (e) {
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
      }
      .width('100%')
      .height(56)
      .padding({ left: 16 })
      .alignItems(VerticalAlign.Center)

      Column() {
        Text(this.isLogin ? '欢迎登录' : '注册账号')
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
          .fontColor('#1C1B1F')
          .margin({ top: 40 })

        Text(this.isLogin ? '登录后享受更多功能' : '创建账号，开始探索')
          .fontSize(14)
          .fontColor('#79747E')
          .margin({ top: 8 })

        Column() {
          TextInput({ placeholder: '用户名', text: $$username })
            .width('100%')
            .height(48)
            .borderRadius(24)
            .backgroundColor('#F4EFF4')
            .padding({ left: 20, right: 20 })
            .margin({ top: 32 })

          TextInput({ placeholder: '密码', text: $$password })
            .width('100%')
            .height(48)
            .borderRadius(24)
            .backgroundColor('#F4EFF4')
            .padding({ left: 20, right: 20 })
            .type(InputType.Password)
            .margin({ top: 16 })

          if (!this.isLogin) {
            TextInput({ placeholder: '邮箱', text: $$email })
              .width('100%')
              .height(48)
              .borderRadius(24)
              .backgroundColor('#F4EFF4')
              .padding({ left: 20, right: 20 })
              .type(InputType.Email)
              .margin({ top: 16 })

            Row() {
              TextInput({ placeholder: '验证码', text: $$code })
                .layoutWeight(1)
                .height(48)
                .borderRadius(24)
                .backgroundColor('#F4EFF4')
                .padding({ left: 20, right: 20 })
                .type(InputType.Number)

              Button(this.codeCountdown > 0 ? `${this.codeCountdown}s` : '获取验证码')
                .height(48)
                .fontSize(13)
                .backgroundColor(this.canSendCode ? '#6750A4' : '#B3261E')
                .fontColor(Color.White)
                .borderRadius(24)
                .margin({ left: 8 })
                .enabled(this.canSendCode)
                .onClick(() => {
                  this.handleSendCode()
                })
            }
            .width('100%')
            .margin({ top: 16 })
          }

          Button(this.isLogin ? '登 录' : '注 册')
            .width('100%')
            .height(48)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .backgroundColor('#6750A4')
            .fontColor(Color.White)
            .borderRadius(24)
            .margin({ top: 32 })
            .enabled(!this.isLoading)
            .onClick(() => {
              if (this.isLogin) {
                this.handleLogin()
              } else {
                this.handleRegister()
              }
            })

          Row() {
            Text(this.isLogin ? '还没有账号？' : '已有账号？')
              .fontSize(14)
              .fontColor('#79747E')

            Text(this.isLogin ? '立即注册' : '去登录')
              .fontSize(14)
              .fontColor('#6750A4')
              .fontWeight(FontWeight.Medium)
              .margin({ left: 4 })
              .onClick(() => {
                this.isLogin = !this.isLogin
              })
          }
          .margin({ top: 20 })
        }
        .width('100%')
      }
      .padding({ left: 32, right: 32 })
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#FFFBFE')
  }
}
