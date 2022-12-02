// pages/main-community/index.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDetailPage: false,
    suggestion: '',
    post: [
      {
        title: '示例文章A',
        content: '正文A\n\t正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A\n\t正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A正文A'
      },{
        title: '示例文章B',
        content: '正文B'
      },{
        title: '示例文章C',
        content: '正文C'
      },{
        title: '示例文章D',
        content: '正文D\n\t正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D\n\t正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D正文D'
      },{
        title: '示例文章E',
        content: '正文E'
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
