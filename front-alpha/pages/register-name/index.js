// pages/register-name/index.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    nickName: ''
  },
  onInputChange(e){
    console.log(e)
    this.data.nickName = e.detail.value;
  },
  submit(){
    if(this.data.nickName == ''){
      wx.showToast({
        title: '昵称不能为空',
        icon: 'error',
        duration: 500
      })
    }else{
      wx.request({
        url: app.globalData.baseURL + '/api/user/updateNickName',
        method: 'POST',
        data: {
          token: wx.getStorageSync('token'),
          nickName: this.data.nickName
        },
        success: (res) => {
          console.log(res)
          wx.setStorageSync('token', res.data.token)
          app.globalData.nickName = this.data.nickName;
          app.globalData.login = false;
          app.globalData.infoChanged = true;
          this.setData({showChangeNickName: false})
          wx.reLaunch({
            url: '../main-personal/index',
          })
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