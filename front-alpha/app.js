//app.js
App({
  onLaunch: function () {
   
    //获取系统信息
    this.globalData.sysinfo = wx.getSystemInfoSync()
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || [];
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    wx.setStorageSync('login',false)
    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  globalData: {
    baseURL: 'https://chenanbella.cn',
    // baseURL: 'http://localhost:5174',
    detailedGraphX: null,
    detailedGraphY: null,
    userInfo: null,
    sysinfo: null,
    login: false,
    userInfo: {avatarUrl: '../../img/default-avatar.png', nickName: '请登录'},
    //蓝牙部分 全局变量
    deviceId: '',
    serviceId: '',
    syncResult: ''

  },
  getLogin: function() {
    return this.globalData.login
  },
  setLogin: function(login) {
    this.globalData.setLogin(login);
  },
  getModel: function () { //获取手机型号
    return this.globalData.sysinfo["model"]
  },
  getVersion: function () { //获取微信版本号
      return this.globalData.sysinfo["version"]
  },
  getSystem: function () { //获取操作系统版本
      return this.globalData.sysinfo["system"]
  },
  getPlatform: function () { //获取客户端平台
      return this.globalData.sysinfo["platform"]
  },
  getSDKVersion: function () { //获取客户端基础库版本
      return this.globalData.sysinfo["SDKVersion"]
  },
  
})
