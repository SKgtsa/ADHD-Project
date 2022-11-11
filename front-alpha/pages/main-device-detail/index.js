// pages/main-device-detail/index.js
import * as echarts from '../../ec-canvas/echarts';

const app = getApp();

Page({
  
  /**
   * 页面的初始数据
   */
  data: {
    pageSize: 5,
    pageNum: 1,
    maxPage: 1,
    targetTraining: {
      id: '',
      mark: 0,
      year: 0,
      month: 0,
      day: 0,
      weekOfTheYear: 0,
      dayOfTheWeek: 0,
      gold: 0,
      graph : [10,20],
      concentration: null
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
    list: [],
    //list中对象
    // {
    //   id: null,
    //   mark: null,
    //   year: 2022,
    //   month: 6,
    //   day: 10,
    //   weekOfTheYear: null,
    //   dayOfTheWeek: null,
    //   gold: null,
    //   message: null
    // }
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
//刷新列表
  refreshList(){
    console.log(this.data.pageSize)
    wx.request({
      url: app.globalData.baseURL + '/api/training/find',
      method :'POST',
      data: {token: wx.getStorageSync('token'),pageNum: this.data.pageNum, size:this.data.pageSize},
      success: (res) => {
        const data = res.data;
        if(data.token == null){
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
        console.log(res);
        this.setData({list: res.data.content, maxPage: res.data.message});
        wx.setStorageSync('token', res.data.token)
      },
      fail: (res) => {
        if(res.data.token == null){
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
      }
    })
  },
  jump: function(target){
    const data = this.data;
    const index = target.target.dataset.target;
    this.setData({targetTraining: data.list[index]})
    console.log(this.data)
    wx.request({
      url: app.globalData.baseURL + '/api/training/findGraph',
      method :'POST',
      data: {
        token: wx.getStorageSync('token'),
        id: this.data.targetTraining.id
      },
      success: (res) => {
        const data = res.data;
        if(data.token == null){
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
        console.log(res)
        //将后端传来的图像数据存入data的targetGraph中
        //起到将之前获得的简略数据与本次的详细图像数据整合的作用
        this.data.targetTraining.graph = data.graph;
        this.data.concentration = data.concentration;
        //保存token 保留登陆状态
        wx.setStorageSync('token', res.data.token)
        const target = this.data.targetTraining;
        app.globalData.detailedGraphY = target.graph;
        //图像的x轴数据
        let graphX = new Array(target.graph.length);
        //时 分 秒数据 统计并写入x轴
        let h = 0;
        let m = 0;
        let s = 0;
        const passage = data.sec/target.graph.length;
        console.log("passage: " + passage)
        console.log("sec: " + target.sec)
        console.log("length: " + target.graph.length)
        for(let i = 0;i < graphX.length;i ++){
          graphX[i] = '';
          if(h != 0)
            graphX[i] += h.toFixed(0) + '时';
          if(m != 0)
            graphX[i] += m.toFixed(0) + '分';
          graphX[i] += s.toFixed(0) + '秒';
          s += passage;
          //进位
          if(s >= 60){
            m += s / 60;
            s %= 60;
          }
          if(m >= 60){
            h += m / 60;
            m %= 60;
          }
        }
        //设置x轴数据
        app.globalData.detailedGraphX = graphX;
        console.log(app.globalData.detailedGraphX);
        wx.navigateTo({
          url: '../detail-line-graph/index?expired=' + this.data.expired + '&mark=' + target.mark + '&year=' + target.year + '&month=' + target.month + '&day=' + target.day + '&gold=' + target.gold 
        })
      },
      fail: (res) => {
        if(res.data.token == null){
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
      }
    })
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