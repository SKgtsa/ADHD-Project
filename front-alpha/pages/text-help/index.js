// pages/text-help/index.js
const app = getApp();
import * as echarts from '../../ec-canvas/echarts';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    changeT : true,
    threshold: 5,
    training: true,
    ecLine: {
      lazyLoad: true
    },
    oneComponent: null,
    lowNum: 0,
    highNum: 0,
    guide: [
      "请尝试专注一下，这样你就能完成任务并感到自豪。",
      "我们可以一起来完成这个任务，你需要专注一下才能做得更好。",
      "请看着我，让我们一起来完成这个任务。",
      "如果你专注完成这个任务，你就可以获得更多的奖励或自由时间。",
      "请注意力集中，让我们完成这个任务并尽快完成，然后我们就可以做更有趣的事情了。",
      "你需要专注才能做得更好，但如果你需要休息一下，也可以和我说一声。",
      "请把注意力集中在这个任务上，完成它可能比你想象的更快，这样我们就可以做更多有趣的事情了。"
    ],
    releasePressure: [
      "我知道这很难，但你已经做得很好了，我们会一直在你身边帮助你的。",
      "不要担心，我们会一起克服这个问题。",
      "我知道你觉得很难，但我相信你可以克服困难。",
      "我知道你有能力克服这个问题，我们会找到正确的方法来帮助你的。",
      "请别灰心，这只是一个小挑战，我们可以一起来解决它。",
      "我们可以一起来解决这个问题，如果我们一起合作，我们肯定会找到解决方案。",
      "我知道你在挑战自己，但这也是你成长的一部分。你在做得很好，继续努力吧！",
      "你正在做得非常好，不要放弃！",
    ],
    encourage: [
      "太棒了，你现在非常专注！",
      "你的专注力真的很出色！",
      "你现在的专注力很棒，继续保持这样的状态。",
      "你的专注力正在让你变得更加出色，很棒！",
      "你的努力和专注是非常值得称赞的。",
    ],
    statusString: '未开始训练',
    commentString: '请启动小车',
    statusNum: 0,
    // {
    //   onInit: function (canvas, width, height, dpr) {
    //     const lineChart = echarts.init(canvas, null, {
    //       width: width,
    //       height: height,
    //       devicePixelRatio: dpr // new
    //     });
    //     canvas.setChart(lineChart);
    //     lineChart.setOption(getLineOption());
    //     return lineChart;
    //   }
    // },
  },
  encourage(){
    var that = this;
    that.setData({
      statusString: "孩子很棒!",
      commentString: that.data.encourage[Math.floor(that.data.encourage.length * Math.random())]
    })
  },
  guideConcentration(){
    var that = this;
    that.setData({
      statusString: "孩子专注力低于阈值",
      commentString: "请引导孩子专注。您可以说: " + that.data.guide[Math.floor(that.data.guide.length * Math.random())]
    })
  },
  releasePressure(){
    var that = this;
    that.setData({
      statusString: "专注力持续低于阈值",
      commentString: "请不要给孩子过多压力。您可以说: " + that.data.releasePressure[Math.floor(that.data.releasePressure.length * Math.random())]
    })
  },
  updateStatus(){
    var that = this;
    if(app.globalData.detailedGraphY[app.globalData.detailedGraphY.length - 1] >= that.data.threshold){
      that.data.lowNum = 0;
      that.data.statusNum ++;
      that.data.highNum ++;
      if(that.data.highNum === 0){
        that.encourage();
      }else if(that.data.highNum === 10){
        that.encourage();
        that.data.highNum = 0;
      }
    }else{
      that.data.highNum = 0;
      that.data.statusNum --;
      that.data.lowNum ++;
      if(that.data.lowNum === 3){
        that.guideConcentration();
      }else if(that.data.lowNum % 10 === 0){
        that.releasePressure();
      }
    }
    
  },
  changeMap(){
    var that = this;
    wx.scanCode({
      success(res){
        var result = res.result;
        try{
          Number(result);
        }catch(e){
          wx.showToast({
            title: '二维码错误',
            content: '请扫描官方地图二维码',
            icon: 'error',
            duration: 900
          })
          return;
        }
        wx.request({
          url: app.globalData.baseURL + '/api/cart/updateMap',
          method: 'POST',
          data: {
            token: wx.getStorageSync('token'),
            map: result
          },
          success: (res) => {
            if(res.data.success){
              wx.setStorageSync('token', res.data.token)
              wx.showToast({
                title: '更换成功',
                duration: 900
              })
            }else{
              wx.showToast({
                title: '发生错误',
                content: '请联系技术人员',
                icon: 'error',
                duration: 900
              })
            }
          },
          fail: (res) => {
            wx.showToast({
              title: '发生错误',
              content: '请联系技术人员',
              icon: 'error',
              duration: 900
            })
          }
        })
      }
    })
  },
  sliderChange(e){
    var that = this;
    that.setData({
      threshold: e.detail.value,
      changeT: true
    })
  },

  init_chart: function(){
    var that = this;
    if(!that.data.training)
      return;
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

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    wx.request({
      url: app.globalData.baseURL + '/api/cart/getThreshold',
      method: 'POST',
      data: {
        token: wx.getStorageSync('token')
      },
      success: (res) => {
        if(res.data.success){
          that.setData({threshold: res.data.content});
          wx.setStorageSync('token', res.data.token)
        }else{

        }
      },
      fail: (res) => {
        wx.showToast({
          title: '发生错误',
          content: '请联系技术人员',
          icon: 'error',
          duration: 900
        })
      }
    })
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
    var that = this;
    app.globalData.detailedGraphY = [];
    app.globalData.detailedGraphX = [];
    app.globalData.timer = setInterval(
      function() {
        if(!that.data.changeT){
          wx.request({
            url: app.globalData.baseURL + '/api/cart/getDot',
            method: 'POST',
            data: {
              token: wx.getStorageSync('token')
            },
            success: (res) => {
              if(res.data.success){
                if(res.data.content === -1){
                  that.setData({training: false});
                  return;
                }
                that.setData({training: true});
                app.globalData.detailedGraphY.push(res.data.content);
                if(app.globalData.detailedGraphX.length !== 60){
                  app.globalData.detailedGraphX.unshift(app.globalData.detailedGraphX.length + '秒');
                }else{
                  app.globalData.detailedGraphY.shift();
                }
                wx.setStorageSync('token', res.data.token);
                that.updateStatus();
              }
              console.log(app.globalData.detailedGraphY)
            },
            fail: (res) => {
              wx.showToast({
                title: '发生错误',
                content: '请联系技术人员',
                icon: 'error',
                duration: 900
              })
            }
          })
        }else{
          that.setData({
            changeT: false
          })
          wx.request({
            url: app.globalData.baseURL + '/api/cart/getDotWithT',
            method: 'POST',
            data: {
              token: wx.getStorageSync('token'),
              threshold: that.data.threshold
            },
            success: (res) => {
              if(res.data.success){
                if(res.data.content === -1){
                  that.setData({training: false});
                  return;
                }
                that.setData({training: true});
                app.globalData.detailedGraphY.push(res.data.content);
                if(app.globalData.detailedGraphX.length !== 60){
                  app.globalData.detailedGraphX.unshift(app.globalData.detailedGraphX.length + '秒');
                }else{
                  app.globalData.detailedGraphY.shift();
                }
                console.log(app.globalData.detailedGraphY)
                wx.setStorageSync('token', res.data.token);
              }
            },
            fail: (res) => {
              wx.showToast({
                title: '发生错误',
                content: '请联系技术人员',
                icon: 'error',
                duration: 900
              })
            }
          })
        }
        that.init_chart();
      }
    ,1000)
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    clearInterval(app.globalData.timer);
    app.globalData.timer = null;
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    clearInterval(app.globalData.timer);
    app.globalData.timer = null;
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
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.detailedGraphX,
      // data: ['A','B','C']
      // show: false
    },
    yAxis: {
      name: '专注度',
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      },
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.detailedGraphY,
      animation : false,
      force: {
        layoutAnimation:false
      }
    }],
  };
}