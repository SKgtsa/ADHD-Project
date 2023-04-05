// pages/main-device-date/index.js
//训练详情页 列出训练日列表
import * as echarts from '../../ec-canvas/echarts';
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    //下一页对应数据库表的开始下标
    startIndex: -1,
    //更新获取的页长度
    length: 6,
    hideLoading: true,
    //页面呈现的数据组
    dataList: [

    ],
    targetTraining: null,
    graphReady: false,
    requesting: false,
    colorSet: ['#7ecbff','#ffa447','#ffa6c4','#1eccc3','#ffa4a3'],
    //骨架屏用的两个参数
    showMask: true,
    tempList: [1,1,1,1,1,1,1,1,1,1],
    chartReady: false,
    ecLine: {
      lazyLoad: true
    },
    ecGauge: {
      onInit: function (canvas, width, height, dpr) {
        const gaugeChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        canvas.setChart(gaugeChart);
        gaugeChart.setOption(getGaugeOption());

        return gaugeChart;
      }
    },
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
    trainingList: [],
  },
  init_chart: function(){
    var that = this;
    that.selectComponent('#mychart-dom-line').init((canvas, width, height, dpr) => {
      const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
      });
      canvas.setChart(lineChart);
      lineChart.setOption(getLineOption());
      return lineChart;
  });
  },
  jump(target){
    var that = this;
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
        that.data.targetTraining.graph = data.graph;
        that.data.concentration = data.concentration;
        //保存token 保留登陆状态
        wx.setStorageSync('token', res.data.token)
        that.data.average = 0;
        const target = that.data.targetTraining;
        for(let i = 0;i < target.graph.length;i ++){
          that.data.average += target.graph[i];
        }
        that.data.average = (that.data.average/ target.graph.length).toFixed(0);
        app.globalData.detailedGraphY = target.graph;
        app.globalData.gaugeData = that.data.average;
        //图像的x轴数据
        let graphX = new Array(target.graph.length);
        //时 分 秒数据 统计并写入x轴
        let h = 0;
        let m = 0;
        let s = 0;
        const passage = data.sec/target.graph.length;
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
        that.setData({
          targetTraining: {
            mark: target.mark,
            year: target.year,
            month: target.month,
            day: target.day,
            gold: target.gold,
            average: target.average,
            trainingMessage: "做的不错"
          }
        })
        console.log(app.globalData.detailedGraphX)
        console.log(app.globalData.detailedGraphY)
        that.init_chart();
      },
      fail: (res) => {
        console.log(res)
        app.globalData.login = false;
        wx.showToast({
          title: '发生错误',
          content: '请联系技术人员',
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
  refresh(){
    if(this.data.startIndex == -2){

    }else{
      this.setData({requesting: true})
      console.log("startIndex: " + this.data.startIndex)
      this.setData({showMask: true,hideLoading: false})
      console.log("获取日列表：发送请求至findDateList")
      console.log("startIndex: " + this.data.startIndex)
      console.log("length" + this.data.length)
      console.log("*******************")
      wx.request({
        url: app.globalData.baseURL + '/api/training/findDateList',
        method: 'POST',
        data: {
          token: wx.getStorageSync('token'),
          startIndex: this.data.startIndex,
          length: this.data.length
        },
        success: (res) => {
          console.log(res);
          const data = res.data;
          if(this.data.startIndex == -1){
            this.setData({dataList: []})
          }
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
          wx.setStorageSync('token', data.token)
          if(data.message <= this.data.length){
            let temp = this.data.dataList;
            for(let i = 0;i < data.content.length && data.content[i] != null;i ++){
              temp.push(data.content[i])
            }
            this.setData({dataList: temp})
          }else{
            this.setData({dataList: this.data.dataList.concat(data.content)})
          }
          this.setData({showMask: false})
          this.setData({startIndex: data.message == 0? -2:data.message,requesting: false})
          console.log(this.data.dataList);
          this.setData({hideLoading: true})
        },
        fail: (res) => {
          app.globalData.login = false;
          wx.showToast({
            title: '发生错误',
            content: '请联系技术人员',
            icon: 'error'
          })
          setTimeout(() => {
            wx.switchTab({
              url: '../main-personal/index',
            })
          },500)
        }
      })
    }
  },
  scrollToLower:function(){
    this.onUpdate();
  },
  onUpdate: function(){
    if(!this.data.requesting){
      this.refresh();
    }
  },
  scrollToUpper:function(){
    this.onRefresh();
  },
  onRefresh:function(){
    if(!this.data.requesting){
      this.setData({startIndex: -1, dataList: []})
      this.refresh();
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
    if(!app.globalData.login){
      wx.switchTab({
        url: '../main-personal/index',
      })
    }else{
      this.refresh();
    }
    this.setData({graphReady: true})
  },

  clear(){
    this.setData({startIndex: -1});
    this.refresh();
  },

  append(){
    if(this.data.startIndex == 0){
      wx.showToast({
        title: '沒有更多内容了',
        icon: 'error'
      })
    }else{
      this.refresh();
    }
  },
  jumpToDate: function(target){
    const index = target.target.dataset.target;
    this.setData({
      target: this.data.dataList[index]
    })
    this.setData({hideLoading: false})
    console.log({
      token: wx.getStorageSync('token'),
      id: this.data.target.id
    })
    wx.request({
      url: app.globalData.baseURL + '/api/training/findDateTraining',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token'),
        id: this.data.target.id
      },
      success: (res) => {
        console.log("data:")
        console.log(this.data)
        const data = res.data;
        this.setData({trainingList: data.content})
        wx.setStorageSync('token', data.token)
        this.setData({hideLoading: true,chartReady: true,showMask: false})
      },
      fail: (res) => {
        console.log(res)
        app.globalData.login = false;
        wx.showToast({
          title: '发生错误',
          content: '请联系技术人员',
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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        return params[0].data + "%";
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.detailedGraphX,
      // data: ['A','B','C']
      // show: false
    },
    yAxis: {
      name: '%',
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      },
      formatter: (params) => {
        return params[0].data + '%';
      },
      // show: false
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.detailedGraphY
    }],
    needImage: false,
  };
}
function getGaugeOption() {
  return{
    series: [{
      title: {
        offsetCenter: [0,'70%'],
        show: true,
        color: '#486484',
        fontWeight: 'bold',
        fontSize: 20
      },
      type: 'gauge',
      radius: '90%',
      center: ['50%','55%'],
      detail: {
        formatter: '{value}%',
        textStyle: {
          fontWeight: 'normal',
          color: '#007af2'
        },
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: [[1,'#486484']],
          width:7
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        show: false
      },
      splitLine: {
        show: false
      },
      pointer: {
        width: 15,
        itemStyle: {
          color: '#486484'
        }
      },
      data: [{
        value: app.globalData.gaugeData,
        name: '平均专注率',
        textStyle: {
          color: '#FFF'
        }
      }]

    }]};
}