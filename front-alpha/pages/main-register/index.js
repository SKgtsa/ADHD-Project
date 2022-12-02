// pages/main-register/index.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    avatarURL: '',
    resultAvatar: app.globalData.userInfo.avatarURL,
    nickName: '',
  },
  onChooseAvatar(e) {
    let url = e.detail.avatarUrl;
    this.setData({
      avatarURL: url,
    })
    console.log(this.data)
    wx.uploadFile({
      filePath: this.data.avatarURL,
      name: 'file',
      url: app.globalData.baseURL + '/api/upload/avatarUpload',
      formData: {
        'token': wx.getStorageSync('token')
      },
      success : (res) => {
        console.log(res)
        const data = JSON.parse(res.data);
        wx.setStorageSync('token', data.token)
        this.setData({
          avatarURL: data.content,
          resultAvatar: app.globalData.baseURL + data.content
        })
      },
    })
  },

  onInputBlur(options){
    this.setData({nickName: options.detail.value});
  },

  submit(){
    if(this.data.nickName == '' || this.data.avatarURL == app.globalData.avatarURL){
      wx.showToast({
        title: '请填写完整',
        icon: 'error',
        duration: 1000
      })
    }else{
      if(this.data.avatarURL == ''){
        const baseURL = app.globalData.baseURL;
        this.data.avatarURL = this.data.resultAvatar.substring(baseURL.length)
        console.log("截取后" + this.data.avatarURL)
      }
      wx.request({
        url: app.globalData.baseURL + '/api/user/updateInfo',
        method: 'POST',
        data: {
          token: wx.getStorageSync('token'),
          nickName: this.data.nickName,
          avatarURL: this.data.avatarURL
        },
        success: (res) => {
          console.log("修改成功")
          console.log(res)
          wx.setStorageSync('token', res.data.token)
          wx.showToast({
            title: '修改成功',
            icon: 'success',
            duration: 1000
          })
          console.log("a")

          wx.redirectTo({
            url: '../main-personal/index.wxml',
          })
          console.log("b")

        }
      })
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
    this.setData({avatarURL: '',resultAvatar: app.globalData.userInfo.imageURL,nickName: app.globalData.userInfo.nickName})
    console.log(this.data)
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