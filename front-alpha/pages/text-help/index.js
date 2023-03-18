// pages/text-help/index.js
const app = getApp();
import * as echarts from '../../ec-canvas/echarts';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    changeT : true,
    threshold: 5,
    training: true,
    ecLine: {
      lazyLoad: true
    },
    oneComponent: null,
    // {
    //   onInit: function (canvas, width, height, dpr) {
    //     const lineChart = echarts.init(canvas, null, {
    //       width: width,
    //       height: height,
    //       devicePixelRatio: dpr // new
    //     });
    //     canvas.setChart(lineChart);
    //     lineChart.setOption(getLineOption());
    //     return lineChart;
    //   }
    // },
  },

  sliderChange(e){
    var that = this;
    that.setData({
      threshold: e.detail.value,
      changeT: true
    })
  },

  init_chart: function(){
    var that = this;
    if(!that.data.training)
      return;
    that.selectComponent('#mychart-dom-line').init((canvas, width, height, dpr) => {
      const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
      });
      canvas.setChart(lineChart);
      lineChart.setOption(getLineOption());
      return lineChart;
  });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    wx.request({
      url: app.globalData.baseURL + '/api/cart/getDot',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token')
      },
      success: (res) => {
        if(res.data.success){
          that.setData({threshold: res.data.content});
          wx.setStorageSync('token', res.data.token)
        }else{

        }
      },
      fail: (res) => {
        wx.showToast({
          title: '发生错误',
          content: '请联系技术人员',
          icon: 'error',
          duration: 900
        })
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    var that = this;
    app.globalData.detailedGraphY = [];
    app.globalData.detailedGraphX = [];
    app.globalData.timer = setInterval(
      function() {
        if(!that.data.changeT){
          wx.request({
            url: app.globalData.baseURL + '/api/cart/getDot',
            method: 'POST',
            data: {
              token: wx.getStorageSync('token')
            },
            success: (res) => {
              if(res.data.success){
                if(res.data.content === -1){
                  that.setData({training: false});
                  return;
                }
                that.setData({training: true});
                app.globalData.detailedGraphY.push(res.data.content);
                if(app.globalData.detailedGraphX.length !== 60){
                  app.globalData.detailedGraphX.unshift(app.globalData.detailedGraphX.length + '秒');
                }else{
                  app.globalData.detailedGraphY.shift();
                }
                wx.setStorageSync('token', res.data.token);
              }
              console.log(app.globalData.detailedGraphY)
            },
            fail: (res) => {
              wx.showToast({
                title: '发生错误',
                content: '请联系技术人员',
                icon: 'error',
                duration: 900
              })
            }
          })
        }else{
          that.setData({
            changeT: false
          })
          wx.request({
            url: app.globalData.baseURL + '/api/cart/getDotWithT',
            method: 'POST',
            data: {
              token: wx.getStorageSync('token'),
              threshold: that.data.threshold
            },
            success: (res) => {
              if(res.data.success){
                if(res.data.content === -1){
                  that.setData({training: false});
                  return;
                }
                that.setData({training: true});
                app.globalData.detailedGraphY.push(res.data.content);
                if(app.globalData.detailedGraphX.length !== 60){
                  app.globalData.detailedGraphX.unshift(app.globalData.detailedGraphX.length + '秒');
                }else{
                  app.globalData.detailedGraphY.shift();
                }
                console.log(app.globalData.detailedGraphY)
                wx.setStorageSync('token', res.data.token);
              }
            },
            fail: (res) => {
              wx.showToast({
                title: '发生错误',
                content: '请联系技术人员',
                icon: 'error',
                duration: 900
              })
            }
          })
        }
        that.init_chart();
      }
    ,1000)
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    clearInterval(app.globalData.timer);
    app.globalData.timer = null;
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})
function getLineOption() {
  return {
    grid: {
      containLabel: true,
      left: 0,
      right: 0,
      bottom: 0,
      top: 38
    },
    tooltip: {
      show: true,
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.detailedGraphX,
      // data: ['A','B','C']
      // show: false
    },
    yAxis: {
      name: '专注度',
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      },
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.detailedGraphY,
      animation : false,
      force: {
        layoutAnimation:false
      }
    }],
  };
}