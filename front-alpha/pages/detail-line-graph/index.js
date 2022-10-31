// pages/detail-line-graph/index.js
import * as echarts from '../../ec-canvas/echarts';
const app = getApp();

Page({
  backButton: function() {
    wx.reLaunch({
      url: '../main-main/index',
    })
  },

  /**
   * 页面的初始数据
   */
  data: {
    expired: null,
    targetTraining: {
      mark: null,
      year: null,
      month: null,
      day: null,
      gold: null,
      graph: [],
      trainingMessage: "做的不错"
    },
    ecLine: {
      onInit: function (canvas, width, height, dpr) {
        const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        canvas.setChart(lineChart);
        lineChart.setOption(getLineOption());

        return lineChart;
      }
    },
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(){
    if(this.options){
      this.setData({
        expired: this.options.expired,
        targetTraining: {
          mark: this.options.mark,
          year: this.options.year,
          month: this.options.month,
          day: this.options.day,
          gold: this.options.gold
        }
      })
      console.log(this.data)
    }
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

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

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
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.detailedGraphX,
      // show: false
    },
    yAxis: {
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
      // show: false
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.detailedGraphY
    }]
  };
}