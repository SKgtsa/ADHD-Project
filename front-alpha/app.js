//app.js
const app = getApp()

App({
  onLaunch: function () {
    //获取系统信息
    this.globalData.sysinfo = wx.getSystemInfoSync()
    console.log('系统信息：')
    console.log(this.globalData.sysinfo)
    this.globalData.infoChanged = false;
  },
  globalData: {
    baseURL: 'https://chenanbella.cn',
    // baseURL: 'http://localhost:5174',
    // baseURL: 'http://clankalliance.com',
    detailedGraphX: null,
    detailedGraphY: null,
    sevenDayGraphX: null,
    sevenDayGraphY: null,
    gaugeData: null,
    userInfo: null,
    sysinfo: null,
    login: false,
    userInfo: {avatarURL: '../../img/default-avatar.png', nickName: '请登录'},
    //蓝牙部分 全局变量
    deviceId: '',
    serviceId: '',
    syncResult: '',
    infoChanged: null
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
