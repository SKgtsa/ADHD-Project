// pages/main-device-detail/index.js
Page({
  
  /**
   * 页面的初始数据
   */
  data: {
    pageSize: 6,
    pageNum: 1,
    maxPage: 1,
    showInfo: false,
    targetTraining: {
      id: '',
      startTime: 'time1',
      endTime: '',
      concentrationRate: 0.1
    },
    list: [{
      id: '',
      startTime: 'time1',
      endTime: 'time1E',
      concentrationRate: 0.1
    },{
      id: '',
      startTime: 'time2',
      endTime: 'time2E',
      concentrationRate: 0.1
    },{
      id: '',
      startTime: 'time3',
      endTime: 'time3E',
      concentrationRate: 0.1
    },{
      id: '',
      startTime: 'time4',
      endTime: 'time4E',
      concentrationRate: 0.1
    },{
      id: '',
      startTime: 'time5',
      endTime: 'time5E',
      concentrationRate: 0.1
    },{
      id: '',
      startTime: 'time6',
      endTime: 'time6E',
      concentrationRate: 0.1
    },],

  },
  jump: function(target){
    const data = this.data.list[target];
    this.setData({targetTraining: data})
    this.setData({showInfo: true})
    console.log('startJump')
    // wx.reLaunch({
    //   url: '../main-device-detail-info/index?startTime=' + data.startTime + '&endTime=' + data.endTime + '&concentrationRate' + data.concentrationRate
    // })
  },
  nextPage: function(){
    if(this.data.pageNum == this.data.maxPage){
      wx.showToast({
        title: '没有下一页了',
        icon: 'error'
      })
    }else{
      this.setData({pageNum: this.data.pageNum + 1})
      this.refreshList();
    }
  },
  lastPage: function(){
    if(this.data.pageNum == 1){
      wx.showToast({
        title: '没有上一页了',
        icon: 'error'
      })
    }else{
      this.setData({pageNum: this.data.pageNum - 1})
      this.refreshList();
    }
  },
  refreshList(){
    wx.request({
      url: 'http://localhost:5174/api/training/find',
      method :'POST',
      data: {token: wx.getStorageSync('token'),pageNum: this.data.pageNum - 1, size:this.data.pageSize},
      success: (res) => {
        console.log(res);
        this.setData({list: res.data.content, maxPage: res.data.message});
        console.log(this.data.maxPage);
        console.log(res.message);
        wx.setStorageSync('token', res.data.token)
      }
    })
  },
  but0(){this.jump(0)},
  but1(){this.jump(1)},
  but2(){this.jump(2)},
  but3(){this.jump(3)},
  but4(){this.jump(4)},
  but5(){this.jump(5)},
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    this.refreshList();
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