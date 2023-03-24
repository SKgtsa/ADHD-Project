// pages/main-community/index.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showWritePage: false,
    suggestion: '',
    post: [],
    postPresent: {
      title: '',
      content: ''
    },
    keyword: '',
    getData: {
      token: '',
      pageNum: 0,
      size: 8,
      identity: ''
    },
    requesting: false,
    first: true,
    last: false,
    withGraph: false,
    anonymous: false,
    graphSelected: false,
    newPost: {
      heading: '',
      content: '',
      files: [],
      withGraph: false,
      anonymous: false,
      trainingId: null,
      token: '',
    }
  },
  switchAnonymousChange: function(e){
    this.setData({anonymous: e.detail.value})
    this.data.anonymous = e.detail.value;
  },
  switchGraphChange: function(e){
    this.setData({withGraph: e.detail.value})
    this.data.withGraph = e.detail.value;
  },
  showWrite: function(){
    console.log('click')
    this.setData({showWritePage: true})
  },
  onTextChange: function(e){
    this.data.newPost.content = e.detail.value;
  },
  requestFail(){
    wx.showToast({
      title: '发生内部错误 请告知管理员',
      icon: 'success',
      duration: 1000
    })
    setTimeout(() => {
      wx.reLaunch({
        url: '../main-personal/index',
      })
    },1000)
  },
  refreshList(){
    var that = this;
    that.data.getData.token = wx.getStorageSync('token');
    if(that.data.requesting || that.data.last)
      return;
    that.data.requesting = true;
    wx.request({
      url: app.globalData.baseURL + '/api/forum/getPost',
      method: 'POST',
      data: that.data.getData,
      success: (res) => {
        console.log(res)
        wx.setStorageSync('token', res.data.token);
        if(res.data.success){
          let postList = that.data.post;
          for(let i = 0;i < res.data.content.content.length;i ++)
            postList.push(res.data.content.content[i]);
          that.setData({
            post: postList,
            first: res.data.content.first,
            last: res.data.content.last
          });
        }
        that.data.requesting = false;
      },
      fail: (res) => {
        that.requestFail();
        that.data.requesting = false;
      }
    })
  },
  search(){
    var that = this;
    that.setData({
      getData: {
        token: '',
        pageNum: 0,
        size: that.data.getData.size,
        identity: that.data.keyword
      },
      post: []
    })
    that.refreshList();
  },
  onSearchChange(e){
    this.setData({keyword: e.detail.value});
  },
  onHeadingChange(e){
    this.data.newPost.heading = e.detail.value;
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
