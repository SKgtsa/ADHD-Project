
const app = getApp();


Page({
  onShareAppMessage: function (res) {
    return {
      title: 'ECharts 可以在微信小程序中使用啦！',
      path: '/pages/index/index',
      success: function () { },
      fail: function () { }
    }
  },
  openCheckInPage: function(){
    this.setData({showCheckInPage: true})
  },
  startCheckIn: function(){
    wx.request({
      url: 'http://localhost:5174/api/checkIn/saveCheckIn',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token')
      },
      success:res =>{
        const data = JSON.parse(JSON.stringify(res)).data;
        console.log(data);
        this.setData({
          message: data.message ? data.message: null ,
          checkInHistory: data.content ? data.message: null, 
          checkInSuccess: data.success ? true: false
        })
      }
    })
  },
  finishCheckIn: function(){

  },

  advice : function (){
    console.log('intoProcess')
    this.setData({showAdvicePage: true})
    console.log(this.data.showAdvicePage)
  },
  onChange(e){
    this.data.suggestion = e.detail.value;
  },

  adviceSubmit : function (){
    console.log('submit')
    console.log(this.data.suggestion)
    wx.request({
      url: `http://localhost:5174/api/suggestion/suggest`,
      method :'POST',
      data:{
        content:this.data.suggestion,
        token: wx.getStorageSync('token')
      },
      success:(res)=>{
        console.log('IntoProcessC')
        console.log(JSON.stringify(res));
        const data = JSON.parse(JSON.stringify(res)).data;
        wx.setStorageSync("token",data.token)
        console.log(data.token);
        if(data.success){
          wx.showToast({
            title: '发送成功',
            duration: 2000,
            icon: 'success',
            mask: true
          })
        }else{
          wx.showToast({
            title: '请先登录',
            duration: 2000,
            icon: 'error',
            mask: true
          })
          wx.switchTab({
            url: '../main-personal/index'
          })
        }

      }
    })
  },
  quickGuide: function() {
    console.log('会进入一个页面，显示一些说明')
  },
  toDataSync: function() {
    //实现按按钮跳转页面并开始同步
    wx.reLaunch({
      url: '../main-device/index?showBlueToothPage=true',
    })
  },
  toDeviceDetail: function() {
    //实现按按钮跳转到设备页并再次跳转前往详情页
    //(跳转同时调用设备页的函数，涉及一个跨页面的函数调用)
    wx.reLaunch({
      url: '../main-device/index?showDetail=' + true,
    })
  },

  toCheckInPage : function() {
    wx.switchTab({
      url: '../main-checkIn/index'
    })
  },


  data: {
    checkInSuccess: false,
    message: "",
    checkInHistory: "0",
    showCheckInPage: false,
    showAdvicePage: false,
    suggestion: ''
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onShow(options) {
    if(!app.globalData.login){
      wx.switchTab({
        url: '../main-personal/index',
      })
    }
  },
  onReady() {
  }
});
