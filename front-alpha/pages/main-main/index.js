import * as echarts from '../../ec-canvas/echarts';

const app = getApp();

Page({
  onShareAppMessage: function (res) {
    return {
      title: '童智训练',
      path: '/pages/main-personal/index',
      success: function () { },
      fail: function () { }
    }
  },
  openCheckInPage: function(){
    this.setData({showCheckInPage: true})
  },


  advice : function (){
    console.log('intoProcess')
    this.setData({showAdvicePage: true})
    console.log(this.data.showAdvicePage)
  },
  onChange(e){
    this.data.suggestion = e.detail.value;
  },

  // adviceSubmit : function (){
  //   console.log('submit')
  //   console.log(this.data.suggestion)
  //   this.setData({hideLoading: true})
  //   wx.request({
  //     url: app.globalData.baseURL + `/api/suggestion/suggest`,
  //     method :'POST',
  //     data:{
  //       content:this.data.suggestion,
  //       token: wx.getStorageSync('token')
  //     },
  //     success:(res)=>{
  //       console.log('IntoProcessC')
  //       console.log(JSON.stringify(res));
  //       const data = JSON.parse(JSON.stringify(res)).data;
  //       this.setData({hideLoading: true})
  //       if(data.token == null){
  //         app.globalData.login = false;
  //         wx.showToast({
  //           title: '登录过期',
  //           icon: 'error'
  //         })
  //         setTimeout(() => {
  //           wx.switchTab({
  //             url: '../main-personal/index',
  //           })
  //         },500)
  //       }
  //       wx.setStorageSync("token",data.token)
  //       console.log('本地存储token变更为' + data.token)
  //       console.log(data.token);
  //       if(data.success){
  //         wx.showToast({
  //           title: '发送成功',
  //           duration: 2000,
  //           icon: 'success',
  //           mask: true
  //         })
  //       }else{
  //         wx.showToast({
  //           title: '请先登录',
  //           duration: 2000,
  //           icon: 'error',
  //           mask: true
  //         })
  //         setTimeout(() => {
  //           wx.switchTab({
  //             url: '../main-personal/index',
  //           })
  //         },500)
  //       }

  //     },
  //     fail: (res) => {
  //       app.globalData.login = false;
  //       wx.showToast({
  //         title: '发生错误',
  //         content: '请联系技术人员',
  //         icon: 'error'
  //       })
  //       setTimeout(() => {
  //         wx.switchTab({
  //           url: '../main-personal/index',
  //         })
  //       },500)
  //     }
  //   })
  // },
  quickGuide: function() {
    wx.navigateTo({
      url: '../main-community/index',
    })    
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
    wx.navigateTo({
      url: '../main-device-date/index',
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
    suggestion: '',
    hideLoading: true,
    chartReady: false,
    ecGauge: {
      onInit: function (canvas, width, height, dpr) {
        const gaugeChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        canvas.setChart(gaugeChart);
        gaugeChart.setOption(getGaugeOption());

        return gaugeChart;
      }
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onShow(options) {
    if(!app.globalData.login){
      wx.switchTab({
        url: '../main-personal/index',
      })
    }else{
      this.setData({ hideLoading: false,})
      wx.request({
        url: app.globalData.baseURL + '/api/user/findMainInfo',
        method: 'POST',
        data: {
          token: wx.getStorageSync('token')
        },
        success: (res) => {
          if(res.data.success){
            console.log('执行请求并成功')
            console.log(res)
            this.setData(res.data)
            if(this.data.dayOfWeek == 0)
              this.setData({dayOfWeek: 7})
            wx.setStorageSync('token', res.data.token)
            console.log('本地存储token变更为' + res.data.token)
            let checkInDay = 0;
            for(let i = 0;i < 7;i ++){
              if(this.data.checkInArray[i]){
                checkInDay ++;
              }
            }
            this.setData({checkInDay: checkInDay});
            if(res.data.average == -101){
              this.setData({average: '未训练'})
            }else{
              this.setData({average: res.data.average + '%'})
            }
            if(res.data.improvementLastTime == -101){
              this.setData({improvementLastTime: '未训练'})
            }else if(res.data.improvementLastTime <= 0){
              this.setData({improvementLastTime: '0%'})
            }else{
              this.setData({average: res.data.improvementLastTime + '%'})
            }
            if(res.data.improvementLastWeek == -101){
              this.setData({improvementLastWeek: '未训练'})
            }else if(res.data.improvementLastWeek <= 0){
              this.setData({improvementLastWeek: '0%'})
            }else{
              this.setData({average: res.data.improvementLastWeek + '%'})
            }
            this.setData({hideLoading: true,chartReady: true})
          }else{
            console.log('执行请求并失败')
            console.log(res)
            app.globalData.login = false;
            this.setData({hideLoading: true})
            wx.switchTab({
              url: '../main-personal/index',
            })
          }
          
        },
        fail: (res) => {
          wx.showToast({
            title: '发生错误',
            content: '请联系技术人员',
            icon: 'error',
            duration: 900
          })
          console.log('执行请求并失败')
          console.log(res)
          app.globalData.login = false;
          this.setData({hideLoading: true})
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
      })
    }
    this.setData({userInfo: app.globalData.userInfo})
  },
  onReady() {
  }
});