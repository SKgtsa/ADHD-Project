// pages/main-community/index.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDetail: false,
    showWritePage: false,
    postOnShow: null,
    onShowIndex: 0,
    suggestion: '',
    post: [],
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
  openDetail: function(index){
    console.log(this.data)
    this.data.onShowIndex = index.target.dataset.index;
    this.setData({
      showDetail: true,
      postOnShow: this.data.post[index.target.dataset.index]
    });
    console.log(this.data);
  },
  closeDetail: function(){
    this.setData({
      showDetail: false,
      postOnShow: null
    });
  },
  submit: function(){
    var that = this;
    if((that.data.newPost.heading == '' && !that.data.showDetail ) || that.data.newPost.content == ''){
      wx.showToast({
        title: '标题与正文不能为空',
        icon: 'error'
      })
      return;
    }
    that.setData({hideLoading: false})
    that.data.newPost.token = wx.getStorageSync('token');
    if(!that.data.showDetail){
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
          that.requestFail();
        }
      })
    }else{
      wx.request({
        url: app.globalData.baseURL + '/api/forum/saveComment',
        method: 'POST',
        data: {
          pid: that.data.postOnShow.id,
          cid: null,
          content: that.data.newPost.content,
          token: that.data.newPost.token,
          anonymous: that.data.newPost.anonymous
        },
        success: (res) => {
          that.setData({
            cId: res.data.content,
            uploadCount: 0,
            imageNeedDelete: false,
            postOnShow: that.data.post[that.data.onShowIndex]
          })
          that.uploadNextImage(res.data.token);
        },
        fail: (res) => {
          that.requestFail();
        }
      })
    }
  },
  uploadNextImage: function(token){
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
      icon: 'error',
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
          for(let i = 0;i < res.data.content.content.length;i ++){
            let temp = res.data.content.content[i];
            console.log(res.data.content)
            console.log(temp);
            for(let j = 0;j < temp.commentList.length; j ++){
              temp.commentList[j].images = temp.commentList[j].images.substring(1, temp.commentList[j].images.length - 1).split(',');
              for(let k = 0;k < temp.commentList[j].images.length;k ++){
                temp.commentList[j].images[k] = temp.commentList[j].images[k].substring(temp.commentList[j].images[k].indexOf('/'));
              }
            }
            postList.push(temp);
          }
          that.setData({
            post: postList,
            first: res.data.content.first,
            last: res.data.content.last,
            postOnShow: postList[that.data.onShowIndex]
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
  deleteComment(e){
    const index = e.currentTarget.dataset.index;
    console.log(index)
    console.log(e)
    const that = this;
    wx.showModal({
      title: '提示',
      content: '确认删除' + (index === 0? '帖子':'评论'),
      complete: (res) => {
        if (res.confirm) {
          wx.request({
            url: app.globalData.baseURL + '/api/forum/deleteComment',
            method: 'POST',
            data: {
              token: wx.getStorageSync('token'),
              id: that.data.postOnShow.commentList[index].id
            },
            success: (res) => {
              console.log(res)
              wx.setStorageSync('token', res.data.token);
              if(res.data.success){
                if(index === 0){
                  let tempPost = that.data.post;
                  tempPost.splice(that.data.onShowIndex, 1);
                  that.setData({
                    post: tempPost
                  })
                  that.closeDetail();
                }else{
                  let tempPostOnShow = that.data.postOnShow;
                  tempPostOnShow.commentList.splice(index, 1);
                  that.setData({
                    postOnShow: tempPostOnShow
                  })
                }
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                })
              }else{
                wx.showToast({
                  title: '删除失败',
                  icon: 'error'
                })
              }
            },
            fail: (res) => {
              console.log(res)
              // that.requestFail();
            }
          })
        }
      }
    })
  },
  clickImage(e){
    const that = this;
    const indexImage = e.currentTarget.dataset.indeximage;
    const indexComment = e.currentTarget.dataset.indexcomment;
    that.imagePreview(that.data.postOnShow.commentList[indexComment].images, indexImage);
  },
  imagePreview(urls, currentIndex){
    console.log(currentIndex)
    let newURLs = [];
    for(let i = 0;i < urls.length;i ++){
      newURLs[i] = app.globalData.baseURL + urls[i];
    }
    wx.previewImage({
      current: newURLs[currentIndex],
      urls: newURLs
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
    this.search();
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
