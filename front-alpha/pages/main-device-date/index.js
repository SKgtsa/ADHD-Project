// pages/main-device-date/index.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    //下一页对应数据库表的开始下标
    startIndex: -1,
    //更新获取的页长度
    length: 6,
    //页面呈现的数据组
    dateData: [

    ]
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
    }else{
      this.refresh();
    }
  },

  refresh(){
    wx.request({
      url: app.globalData.baseURL + '/api/training/findDateList',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token'),
        startIndex: this.data.startIndex,
        length: this.data.length
      },
      success: (res) => {
        console.log(res);
        const data = res.data;
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
        wx.setStorageSync('token', data.token)
        this.setData({dataList: data.content})
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