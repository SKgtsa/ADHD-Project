// pages/main-device-date-detail/index.js
import * as echarts from '../../ec-canvas/echarts';
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    target: {
      year: null,
      month: null,
      day: null,
      gold: null,
      concentrationE: null,
      time: null,
      startIndex: null,
      trainingNum: null,
      timeVariance: null
    },
    //骨架屏用的两个参数
    hideLoading: true,
    tempList: [1,1,1,1,1,1,1,1,1,1],
    trainingList: [],
    ecLine: null,
    chartReady: false,
    ecLine: {
      onInit: function (canvas, width, height, dpr) {
        const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        canvas.setChart(lineChart);
        console.log(app)
        lineChart.setOption(getLineOption());

        return lineChart;
      }
    },
  },
  jump(target){
    const data = this.data;
    const index = target.target.dataset.target;
    this.setData({targetTraining: data.trainingList[index]})
    console.log(app.globalData.baseURL)
    console.log(app)
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
          url: '../detail-line-graph/index?mark=' + target.mark + '&year=' + target.year + '&month=' + target.month + '&day=' + target.day + '&gold=' + target.gold 
        })
      },
      fail: (res) => {
        console.log(res)
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
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    if(options){
      this.setData({
        target: {
          year: this.options.year,
          month: this.options.month,
          day: this.options.day,
          gold: this.options.gold,
          concentrationE: this.options.concentrationE,
          time: this.options.time,
          startIndex: this.options.startIndex,
          trainingNum: this.options.trainingNum,
          timeVariance: this.options.timeVariance
        }
      })
      console.log(this.data)
    }
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
    this.setData({hideLoading: false})
    wx.request({
      url: app.globalData.baseURL + '/api/training/findDateTraining',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token'),
        startIndex: this.data.target.startIndex
      },
      success: (res) => {
        console.log("data:")
        console.log(this.data)
        const data = res.data;
        this.setData({trainingList: data.content})
        let graphX = new Array();
        let graphY = new Array();
        for(let i = 0;i < data.content.length;i ++){
          graphX[i] = '第' + data.content[i].mark + '次训练';
          graphY[i] = (this.data.trainingList[i].length/60).toFixed(0);
        }
        app.globalData.detailedGraphX = graphX;
        app.globalData.detailedGraphY = graphY;
        console.log(app.globalData.detailedGraphY);
        wx.setStorageSync('token', data.token)
        this.setData({hideLoading: true,chartReady: true})
      },
      fail: (res) => {
        console.log(res)
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
    })
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
});
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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        return params[0].marker + params[0].name +
           '了' + params[0].data + '分钟'
      },
      textStyle: {
        align: 'left'
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.detailedGraphX,
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
      data: app.globalData.detailedGraphY
    }]
  };
}