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
    graphReady: false,
    requesting: false,
    colorSet: ['#7ecbff','#ffa447','#ffa6c4','#1eccc3','#ffa4a3'],
    //骨架屏用的两个参数
    showMask: true,
    tempList: [1,1,1,1,1,1,1,1,1,1],
    chartReady: false,
    ecLine: {
      onInit: function (canvas, width, height, dpr) {
        const lineChart = echarts.init(canvas, null, {
          width: width,
          height: height,
          devicePixelRatio: dpr // new
        });
        console.log("width: " + width);
        console.log("height: " + height);
        canvas.setChart(lineChart);
        lineChart.setOption(getLineOption());

        return lineChart;
      }
    },
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
      // setTimeout(function() {
      //   wx.hideNavigationBarLoading();
      //   wx.stopPullDownRefresh();
      //   this.setData({requesting: false})
      // },2000)
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
  jump: function(target){
    const data = this.data;
    const index = target.target.dataset.target;
    wx.navigateTo({
      url: '../main-device-date-detail/index?year=' + data.dataList[index].year + '&month=' + data.dataList[index].month + '&day=' + data.dataList[index].day + '&gold=' + data.dataList[index].gold + '&concentrationE=' + data.dataList[index].concentrationE + '&time=' + data.dataList[index].time + '&startIndex=' + data.dataList[index].startIndex + '&trainingNum=' + data.dataList[index].trainingNum + '&timeVariance' + data.dataList[index].timeVariance + '&id=' + data.dataList[index].id + '&dayOfTheWeek=' + data.dataList[index].dayOfTheWeek + '&weekOfTheYear=' + data.dataList[index].weekOfTheYear + '&imageName=' + data.dataList[index].imageName
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
        let time = params[0].data;
        let s = time%60;
        time = (time - s)/60;
        let m = time%60;
        time = (time - m)/60;
        let h = time;
        let result = '';
        if(h != 0)
          result += h + '时'
        if(m != 0)
          result += m + '分'
        if(s != 0)
          result += s + '秒'
        return result;
      },
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: app.globalData.sevenDayGraphX,
      // data: ['A','B','C']
      // show: false
    },
    yAxis: {
      name: '秒',
      x: 'center',
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      },
      formatter: (params) => {
        console.log(params)
        let time = params[0].data;
        let s = time%60;
        time = (time - s)/60;
        let m = time%60;
        time = (time - m)/60;
        let h = time;
        let result = '';
        if(h != 0)
          result += h + '时'
        if(m != 0)
          result += m + '分'
        if(s != 0)
          result += s + '秒'
        return result;
      },
      // show: false
    },
    series: [{
      type: 'line',
      smooth: true,
      data: app.globalData.sevenDayGraphY
    }],
    needImage: false,
  };
}