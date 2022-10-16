import * as echarts from '../../ec-canvas/echarts';

const app = getApp();

Page({
  onShareAppMessage: function (res) {
    return {
      title: '工程样本',
      path: '/pages/index/index',
      success: function () { },
      fail: function () { }
    }
  },
  startTraining: function (){
    this.setData({showBlueToothPage: true})
  },
  connectionFailed: function (){
    this.setData({showBlueToothPage: false})
  },
  connectionSucceeded: function (){
    this.setData({showBlueToothPage: false})
  },
  data: {
    showBlueToothPage: false,
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
  onReady() {
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onShow(options) {

  },
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
      data: [2, 4, 3, 5, 1, 8, 3]
    }]
  };
}
function getGaugeOption() {
  return{
    series: [{
      title: {
        offsetCenter: [0,'70%'],
        show: true,
        color: '#FFF',
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
          color: '#FFF'
        },
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: [[1,'#FFF']],
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
          color: '#FFF'
        }
      },
      data: [{
        value: 90,
        name: '专注率',
        textStyle: {
          color: '#FFF'
        }
      }]

    }]};
}
