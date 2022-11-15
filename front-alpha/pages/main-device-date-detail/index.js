// pages/main-device-date-detail/index.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    target: {
      year: null,
      month: null,
      day: null,
      gold: null,
      concentrationE: null,
      time: null,
      startIndex: null,
      trainingNum: null,
      timeVariance: null
    },
    detailedList: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if(options){
      this.setData({
        target: {
          year: this.options.year,
          month: this.options.month,
          day: this.options.day,
          gold: this.options.gold,
          concentrationE: this.options.concentrationE,
          time: this.options.time,
          startIndex: this.options.startIndex,
          trainingNum: this.options.trainingNum,
          timeVariance: this.options.timeVariance
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
    wx.request({
      url: app.baseURL + '/api/training/findDateTraining',
      method: 'POST',
      success: (res) => {

      },
      fail: (res) => {
        if(res.data.token == null){
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
      }
    })
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