// pages/register-avatar/index.js
const app = getApp();
Page({
  onChooseAvatar(e) {
    let url = e.detail.avatarUrl;
    this.setData({
      avatarURL: url,
      loadingX: true
    })
    console.log("更改后data:")
    console.log(this.data)
    setTimeout(() => {
      this.setData({loadingX: false})
    },1000)
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

        wx.request({
          url: app.globalData.baseURL + '/api/user/updateAvatar',
          method: 'POST',
          data: {
            token: wx.getStorageSync('token'),
            avatarURL: this.data.avatarURL
          },
          success: (res) => {
            this.setData({loadingX: false})
            wx.setStorageSync('token', res.data.token)
            app.globalData.infoChanged = true;
            wx.showToast({
              title: '成功',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.navigateTo({
                url: '../register-name/index'
              })
            },800)
          }
        })
      },
    })
  },
  /**
   * 页面的初始数据
   */
  data: {
    avatarURL: '',
    resultAvatar: app.globalData.userInfo.avatarURL,
    loadingX: false
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