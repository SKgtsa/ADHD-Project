// pages/personal-avatar/index.js
const app = getApp();
Page({

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
        wx.request({
          url: app.globalData.baseURL + '/api/user/updateAvatar',
          method: 'POST',
          data: {
            token: wx.getStorageSync('token'),
            avatarURL: this.data.avatarURL
          },
          success: (res) => {
            wx.setStorageSync('token', res.data.token)
            console.log('头像修改完毕，将infoChanged修改为')
            app.globalData.infoChanged = true;
            app.globalData.login = false;
            console.log(app.globalData.infoChanged)
            wx.showToast({
              title: '修改成功',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.reLaunch({
                url: '../main-personal/index',
              })
            },900)
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