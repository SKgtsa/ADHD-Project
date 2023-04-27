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
    anonymous: false,
    newPost: {
      heading: '',
      content: '',
      anonymous: false,
      trainingId: null,
      token: '',
    },
    uploadCount: 0,
    imageList: [],
    cId: '',
    imageNeedDelete: false,
    hideLoading: true
  },
  deleteThisImage: function(index){
    let tempList = this.data.imageList;
    tempList.splice(index.currentTarget.dataset.index, 1);
    this.setData({imageList: tempList});
  },
  chooseImage: function(){
    var that = this;
    wx.chooseMedia({
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success(res) {
        let tempList = that.data.imageList;
        for(let i = 0;i < res.tempFiles.length;i ++){
          tempList.push(res.tempFiles[i].tempFilePath);
        }
        that.setData({imageList: tempList});
      }
    })
  },
  submit: function(){
    var that = this;
    if(that.data.newPost.heading == '' || that.data.newPost.content == ''){
      wx.showToast({
        title: '标题与正文不能为空',
        icon: 'error'
      })
      return;
    }
    that.setData({hideLoading: false})
    that.data.newPost.token = wx.getStorageSync('token');
    wx.request({
      method: 'POST',
      url: app.globalData.baseURL + '/api/forum/savePost',
      data: that.data.newPost,
      success: (res) => {
        that.setData({
          cId: res.data.content,
          uploadCount: 0,
          imageNeedDelete: false
        })
        that.uploadNextImage(res.data.token);
      },
      fail: (res) => {
        console.log(res);
        that.setData({hideLoading: true})
        wx.showToast({
          title: '发生内部错误 请告知管理员',
          icon: 'success',
          duration: 1000
        })
      }
    })
  },
  uploadNextImage: function(token){
    console.log('uploadNextImage');
    console.log(token)
    var that = this;
    if(that.data.uploadCount < that.data.imageList.length){
      console.log('upload')
      wx.uploadFile({
        method: 'POST',
        filePath: that.data.imageList[that.data.uploadCount],
        name: 'file',
        url: app.globalData.baseURL + '/api/forum/saveForumImage',
        formData: {
          token: token,
          cId: that.data.cId,
          needDelete: that.data.imageNeedDelete + ""
        },
        success: (res) => {
          that.data.uploadCount ++;
          that.uploadNextImage(JSON.parse(res.data).token);
        },
        fail: (res) => {
          wx.setStorageSync('token', token)
          that.setData({hideLoading: true})
          wx.showToast({
            title: '发生内部错误 请告知管理员',
            icon: 'success',
            duration: 1000
          })
        }
      })

    }else{
      //上传完成
      console.log('Finished')
      wx.setStorageSync('token', token);
      that.setData({
        hideLoading: true,
        showWritePage: false
      })
      that.search()
      wx.showToast({
        title: '上传完成',
        icon: 'success'
      })
    }
  },
  switchAnonymousChange: function(e){
    this.setData({anonymous: e.detail.value})
    this.data.anonymous = e.detail.value;
    this.data.newPost.anonymous = e.detail.value;
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
    console.log(that.data)
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
          that.data.getData.pageNum ++;
          console.log('UpdatePageNum: ' + that.data.getData.pageNum);
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
      post: [],
      last: false
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
