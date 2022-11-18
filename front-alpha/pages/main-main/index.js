import * as echarts from '../../ec-canvas/echarts';

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
    this.setData({hideLoading: true})
    wx.request({
      url: app.globalData.baseURL + `/api/suggestion/suggest`,
      method :'POST',
      data:{
        content:this.data.suggestion,
        token: wx.getStorageSync('token')
      },
      success:(res)=>{
        console.log('IntoProcessC')
        console.log(JSON.stringify(res));
        const data = JSON.parse(JSON.stringify(res)).data;
        this.setData({hideLoading: true})
        if(data.token == null){
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
        wx.setStorageSync("token",data.token)
        console.log('本地存储token变更为' + data.token)
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
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }

      },
      fail: (res) => {
        app.globalData.login = false;
        wx.showToast({
          title: '登录过期',
          icon: 'error'
        })
        setTimeout(() => {
          wx.switchTab({
            url: '../main-personal/index',
          })
        },500)
      }
    })
  },
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
    suggestion: '',
    hideLoading: true,
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
          console.log('执行请求并成功')
          console.log(res)
          this.setData(res.data)
          if(this.data.lastTrainingTime == '今天'){
            this.setData({trainingLetter: '做得不错！'})
          }else{
            this.setData({trainingLetter: '快开始今天的训练吧'})
          }
          this.setData({hideLoading: true})
          if(this.data.lastDateTraining == null){
            app.globalData.gaugeData = 0;
          }else{
            app.globalData.gaugeData = this.data.lastDateTraining.concentrationE;
          }
          wx.setStorageSync('token', res.data.token)
          console.log('本地存储token变更为' + res.data.token)
        },
        fail: (res) => {
          console.log('执行请求并失败')
          console.log(res)
          app.globalData.login = false;
          wx.switchTab({
            url: '../main-personal/index',
          })
        }
      })
    }
    this.setData({userInfo: app.globalData.userInfo})
  },
  onReady() {
  }
});
function getGaugeOption() {
  return{
    series: [{
      title: {
        offsetCenter: [0,'70%'],
        show: true,
        color: '#486484',
        fontWeight: 'bold',
        fontSize: 20
      },
      type: 'gauge',
      radius: '100%',
      center: ['50%','50%'],
      detail: {
        formatter: '{value}%',
        textStyle: {
          fontWeight: 'normal',
          color: '#007af2'
        },
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: [[1,'#486484']],
          width:7
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        show: false
      },
      splitLine: {
        show: false
      },
      pointer: {
        width: 15,
        itemStyle: {
          color: '#486484'
        }
      },
      data: [{
        value: app.globalData.gaugeData,
        name: '平均专注率',
        textStyle: {
          color: '#FFF'
        }
      }]

    }]};
}