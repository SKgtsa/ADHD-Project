// pages/main-community/index.js

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDetailPage: false,
    suggestion: '',
    post: [
      {
        title: 'A',
        content: 'This is the content of A'
      },{
        title: 'B',
        content: 'This is the content of B'
      },{
        title: 'C',
        content: 'This is the content of C'
      },{
        title: 'D',
        content: 'This is the content of D'
      },{
        title: 'E',
        content: 'This is the content of E'
      }
    ],
    postPresent: {
      title: '',
      content: ''
    }
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
