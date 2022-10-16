
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
  data: {

  },
  /**
   * 生命周期函数--监听页面加载
   */
  onShow(options) {

  },
  onReady() {
  }
});
