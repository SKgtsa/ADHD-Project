// pages/main-device-setting/index.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    wifiName: null,
    wifiPass: null,
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
    this.data.wifiName = wx.getStorageSync('wifiName');
    this.data.wifiPass = wx.getStorageSync('wifiPass');
    if(!(this.data.wifiName && this.data.wifiPass)){
      wx.showToast({
        title: '请设置wifi',
        duration: 2000,
        icon: 'error',
        mask: true
      })
    }
  },
  onNameChange(e){
    this.data.wifiName = e.detail.value;
  },
  onPassChange(e){
    this.data.wifiPass = e.detail.value;
  },
  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },
  save(){
    wx.setStorageSync('wifiName', this.data.wifiName);
    wx.setStorageSync('wifiPass', this.data.wifiPass);
    wx.showToast({
      title: '保存成功',
      duration: 2000,
      mask: true
    })
    setTimeout(() => {
      wx.switchTab({
        url: '../main-device/index',
      })
    },1200)
    
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