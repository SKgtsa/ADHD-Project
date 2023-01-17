// pages/comment-submit/index.js

const recorder = wx.getRecorderManager();
const app = getApp();

var recordTimeInterval;
var audio;

Page({

  /**
   * 页面的初始数据
   */
  data: {
    text: '',
    recordTime: '0:00',
    recording: false,
    needRecord: true,
    timeShown: '0:00',
    playing: false,
    timeSecond: 0,
    filePath: ''
  },
  onTextChange(e){
    this.setData({
      text: e.detail.value
    })
  },
  tapAudioButton(){
    if(this.data.needRecord && !this.data.recording){
      console.log('开始录制')
      this.startRecording();
    }else if(this.data.recording){
      console.log('结束录制')
      this.endRecording();
    }else if(!this.data.needRecord && !this.data.playing){
      console.log('开始播放')
      this.startPlaying();
    }else if(this.data.playing){
      console.log('结束播放')
      this.endPlaying();
    }
  },
  startRecording(){
    console.log('开始录音')
    var that = this;
    this.setData({
      recording: true
    })
    recordTimeInterval = setInterval(function() {
      console.log('进入recordTimeInterval')
      that.data.timeSecond ++;
      var second = that.data.timeSecond;
      if(second == 600){
        that.endRecording();
        wx.showToast({
          title: '录制语音最长为10分钟',
        })
      }
      var timeFormated = (second - second % 60)/60 + ':' + (second % 60 > 9? second % 60 : ('0' + second % 60));
      that.setData({
        timeShown: timeFormated,
        recordTime: timeFormated
      })
      console.log('循环更新data')
      console.log(that.data)
    },1000)
    const options = {
      duration: 600000,
      sampleRate: 44100,
      numberOfChannels: 1,
      encodeBitRate: 192000,
      format: 'mp3'
    }
    recorder.start(options);
    recorder.onStart((res) => {
      console.log('录制启动 收到回传')
      console.log(res)
    })
  },
  endRecording(){
    console.log('结束录音')
    var that = this;
    clearInterval(recordTimeInterval);
    recorder.stop();
    recorder.onStop((res) => {
      console.log('录音结束 收到回传')
      console.log(res);
      that.setData({
        recording: false,
        needRecord: false,
        filePath: res.tempFilePath
      })
      console.log(that.data)
    })
  },
  startPlaying(){
    this.setData({
      playing: true
    })
    audio = wx.createInnerAudioContext();
    audio.src = this.data.filePath;
    audio.autoplay = true
  },
  endPlaying(){
    this.setData({
      playing: false
    })
    audio.stop()
  },
  deleteRecording(){
    this.setData({
      filePath: '',
      timeSecond: 0,
      timeShown: '0:00',
      recordTime: '0:00',
      needRecord: true
    })
  },
  submit(){
    if(this.data.text == '' && this.data.filePath == ''){
      wx.showToast({
        title: '请选择一种方式记录',
      })
    }else{
      if(this.data.text == ''){
        this.data.text = null;
      }
      if(this.data.filePath == ''){
        wx.request({
          url: app.globalData.baseURL + '/api/training/saveComment',
          formData: {
            'token': wx.getStorageSync('token'),
            'text': this.data.text,
            'audio': null
          },
          success: (res) => {
            const data = res.data;
            console.log('提交成功')
            console.log(res)
            wx.setStorageSync('token', data.token)
            wx.showToast({
              title: '提交成功',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.reLaunch({
                url: '../main-device/index',
              })
            },1000)
          },
          fail: (res) => {
            console.log('提交失败')
            console.log(res)
            wx.showToast({
              title: '发生内部错误 请告知管理员',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.reLaunch({
                url: '../main-personal/index',
              })
            },1000)
          }
        })
      }else{
        wx.uploadFile({
          filePath: this.data.filePath,
          name: 'audio',
          url: app.globalData.baseURL + '/api/training/saveComment',
          formData: {
            'token': wx.getStorageSync('token'),
            'text': this.data.text
          },
          success: (res) => {
            const data = res.data;
            console.log('提交成功')
            console.log(res)
            wx.setStorageSync('token', data.token)
            wx.showToast({
              title: '提交成功',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.reLaunch({
                url: '../main-device/index',
              })
            },1000)
          },
          fail: (res) => {
            console.log('提交失败')
            console.log(res)
            wx.showToast({
              title: '发生内部错误 请告知管理员',
              icon: 'success',
              duration: 1000
            })
            setTimeout(() => {
              wx.reLaunch({
                url: '../main-personal/index',
              })
            },1000)
          }
        })
      }
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