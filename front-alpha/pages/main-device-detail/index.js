// pages/main-device-detail/index.js
import * as echarts from '../../ec-canvas/echarts';

const app = getApp();

Page({
  
  /**
   * 页面的初始数据
   */
  data: {
    expired: false,
    pageSize: 5,
    pageNum: 1,
    maxPage: 1,
    showInfo: false,
    targetTraining: {
      id: '',
      mark: 0,
      year: 0,
      month: 0,
      day: 0,
      weekOfTheYear: 0,
      dayOfTheWeek: 0,
      gold: 0,
      graph : [10,20]
    },
    ecLine: {
      onInit: function (canvas, width, height, dpr) {
        const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        canvas.setChart(lineChart);
        lineChart.setOption(getLineOption());
        return lineChart;
      }
    },
    list: [{
        id: null,
        mark: null,
        year: 2022,
        month: 6,
        day: 10,
        weekOfTheYear: null,
        dayOfTheWeek: null,
        gold: null,
        message: null
      },{
        id: null,
        mark: null,
        year: 2022,
        month: 6,
        day: 10,
        weekOfTheYear: null,
        dayOfTheWeek: null,
        gold: null,
        message: null
      },{
        id: null,
        mark: null,
        year: 2022,
        month: 6,
        day: 10,
        weekOfTheYear: null,
        dayOfTheWeek: null,
        gold: null,
        message: null
      }
    ],
  },
  inTime(){
    this.setData({expired: false, pageNum: 1});
    this.refreshList();
  },
  expired(){
    this.setData({expired: true, pageNum: 1});
    this.refreshList();
  },
  nextPage: function(){
    if(this.data.expired){
      if(this.data.pageNumE == this.data.maxPageE){
        wx.showToast({
          title: '没有下一页了',
          icon: 'error'
        })
      }else{
        this.setData({pageNumE: this.data.pageNumE + 1})
        this.refreshList();
      }
    }else{
      if(this.data.pageNumN == this.data.maxPageN){
      wx.showToast({
        title: '没有下一页了',
        icon: 'error'
      })
      }else{
        this.setData({pageNumN: this.data.pageNumN + 1})
        this.refreshList();
      }
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
    if(this.data.expired){
      wx.request({
        url: app.globalData.baseURL + '/api/training/findExpired',
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
    }else{
      wx.request({
        url: app.globalData.baseURL + '/api/training/findNormal',
        method :'POST',
        data: {token: wx.getStorageSync('token'),pageNum: this.data.pageNum - 1, size:this.data.pageSize},
        success: (res) => {
          console.log(res);
          this.setData({list: res.data.content, maxPage: res.data.message});
          wx.setStorageSync('token', res.data.token)
        }
      })
    }
  },
  jump: function(target){
    const data = this.data;
    const index = target.target.dataset.target;
    this.setData({targetTraining: data.list[index]})
    console.log(this.data.targetTraining)
    this.setData({showInfo: true})
    console.log('startJump')
    if(this.data.expired){
      wx.request({
        url: app.globalData.baseURL + '/api/training/findExpiredGraph',
        method :'POST',
        data: {
          token: wx.getStorageSync('token'),
          id: this.data.targetTraining.id
        },
        success: (res) => {
          const data = res.data;
          this.data.targetTraining.graph = data.content;

          const target = this.data.targetTraining;
          app.globalData.graph = target.graph;
          wx.setStorageSync('token', res.data.token)
          wx.reLaunch({
            url: '../detail-line-graph/index?expired=' + this.data.expired + '&mark=' + target.mark + '&year=' + target.year + '&month=' + target.month + '&day=' + target.day + '&gold=' + target.gold 
          })
        }
      })
    }else{
      console.log(this.data)
      wx.request({
        url: app.globalData.baseURL + '/api/training/findNormalGraph',
        method :'POST',
        data: {
          token: wx.getStorageSync('token'),
          id: this.data.targetTraining.id
        },
        success: (res) => {
          const data = res.data;
          this.data.targetTraining.graph = data.content;
          wx.setStorageSync('token', res.data.token)

          const target = this.data.targetTraining;
          app.globalData.detailedGraphY = target.graph;

          // const graphX = new String[target.graph.length];
          let graphX = new Array(target.graph.length);
          let h = 0;
          let m = 0;
          let s = 0;
          for(let i = 0;i < graphX.length;i ++){
            graphX[i] = h + ':' + m + ':' + s;
            s ++;
            if(s == 60){
              s = 0;
              m ++;
            }
            if(m == 60){
              m = 0;
              h ++;
            }
          }

          app.globalData.detailedGraphX = graphX;
          console.log(app.globalData.detailedGraphX);
          wx.reLaunch({
            url: '../detail-line-graph/index?expired=' + this.data.expired + '&mark=' + target.mark + '&year=' + target.year + '&month=' + target.month + '&day=' + target.day + '&gold=' + target.gold 
          })
        }
      })
      
    }
    
    // wx.reLaunch({
    //   url: '../main-device-detail-info/index?startTime=' + data.startTime + '&endTime=' + data.endTime + '&concentrationRate' + data.concentrationRate
    // })
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


function getLineOption() {
  return {
    grid: {
      containLabel: true,
      left: 0,
      right: 0,
      bottom: 0,
      top: 38
    },
    tooltip: {
      show: true,
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: ['x.1', 'x.2', 'x.3', 'x.4', 'x.5', 'x.6', 'x.7'],
      // show: false
    },
    yAxis: {
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
      // show: false
    },
    series: [{
      type: 'line',
      smooth: true,
      data: [20,30,40,50,60,50,10]
    }]
  };
}