// pages/detail-line-graph/index.js
import * as echarts from '../../ec-canvas/echarts';
const app = getApp();

Page({
  backButton: function() {
    wx.reLaunch({
      url: '../main-main/index',
    })
  },

  /**
   * 页面的初始数据
   */
  data: {
    targetTraining: {
      mark: null,
      year: null,
      month: null,
      day: null,
      gold: null,
      graph: [],
      trainingMessage: "做的不错"
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
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(){
    if(this.options){
      this.setData({
        targetTraining: {
          mark: this.options.mark,
          year: this.options.year,
          month: this.options.month,
          day: this.options.day,
          gold: this.options.gold,
          average: this.options.average,
          trainingMessage: "做的不错"

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
      if(!app.globalData.login){
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
      data: app.globalData.detailedGraphX,
      // show: false
    },
    yAxis: {
      x: 'center',
      type: 'value',
      // splitLine: {
      //   lineStyle: {
      //     type: 'dashed'
      //   }
      // }
      // show: false
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.detailedGraphY,
      color: [
        '#1885f3'
      ]
    }]
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