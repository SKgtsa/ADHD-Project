// pages/main-checkIn/index.js

const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    message:'test',
    checkInButton: '签到成功',
    checkInDays: '5',
    list: null,
    dayOfTheWeek: null,
    day: ['一','二','三','四','五','六','七']
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    
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
    if(!app.globalData.login){
      wx.switchTab({
        url: '../main-personal/index',
      })
    }
    wx.request({
      url: app.globalData.baseURL +  '/api/checkIn/saveCheckIn',
      method: 'POST',
      data: {token: wx.getStorageSync('token')},
      success: (res) => {
        const data = res.data;
        console.log(data)
        const dayOfWeek = data.message == 1? 6: data.message - 2;
        console.log(dayOfWeek)
        this.setData({list: data.content, dayOfTheWeek: dayOfWeek})
        wx.setStorageSync('token', data.token)
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