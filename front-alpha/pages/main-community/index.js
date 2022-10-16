// pages/main-community/index.js
Page({

  advice : function (){
    console.log('intoProcess')
    this.setData({showAdvicePage: true})
    console.log(this.data.showAdvicePage)
  },
  onChange(e){
    this.data.suggestion = e.detail.value;
  },

  adviceSubmit : function (){
    console.log('submit')
    console.log(this.data.suggestion)
    wx.request({
      url: `http://localhost:5174/api/suggestion/suggest`,
      method :'POST',
      data:{
        content:this.data.suggestion,
        token: wx.getStorageSync('token')
      },
      success:(res)=>{
        console.log('IntoProcessC')
        console.log(JSON.stringify(res));
        const data = JSON.parse(JSON.stringify(res)).data;
        wx.setStorageSync("token",data.token)
        console.log(data.token);
        if(data.success){
          wx.showToast({
            title: '发送成功',
            duration: 2000,
            icon: 'success',
            mask: true
          })
        }else{
          wx.showToast({
            title: '请先登录',
            duration: 2000,
            icon: 'error',
            mask: true
          })
          wx.switchTab({
            url: '../main-personal/index'
          })
        }

      }
    })
  },

  /**
   * 页面的初始数据
   */
  data: {
    showAdvicePage: false,
    suggestion: ''
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
