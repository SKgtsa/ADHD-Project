// pages/main-device-binding/index.js

const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: '',

  },

  onIdChange(e){
    this.data.id = e.detail.value;
  },

  binding(){
    const that = this;
    wx.request({
      url: app.globalData.baseURL + '/api/cart/bind',
      method: 'POST',
      data: {
        id: that.data.id,
        token: wx.getStorageSync('token')
      },
      success: (res) => {
        console.log(res);
        const dataResponse = res.data;
        if(dataResponse.success){
          wx.setStorageSync('token', dataResponse.token);
          wx.showToast({
            title: dataResponse.message,
            icon: 'success',
            duration: 1000
          })
        }else{
          wx.showToast({
            title: dataResponse.message,
            icon: 'error',
            duration: 1000
          })
        }
      },
      fail: (res) => {
        console.log(res);
        wx.showToast({
          title: '发生内部错误 请告知管理员',
          icon: 'error',
          duration: 1000
        })
        setTimeout(() => {
          wx.reLaunch({
            url: '../main-personal/index',
          })
        },1000)
      }
    })
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