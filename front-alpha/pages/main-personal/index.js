// pages/main-personal/index.js

const app = getApp();


Page({
  data: {
    code: '',
    userInfo: {
      avatarURL: app.globalData.userInfo.imageURL, 
      nickName: app.globalData.userInfo.nickName
    },
    gold: null,
    hour: null,
    login: false,
    hideLoading: true,
    baseURL: app.globalData.baseURL,
    showChangeNickName: false
  },
  // 事件处理函数
  bindViewTap() {
    wx.navigateTo({
      url: '../test/test',
    })
  },
  onLoad() {
    console.log("main-personal的onLoad")
    console.log("globalData")
    console.log(app.globalData)
    console.log("data")
    console.log(this.data)
  },
  //点击头像 若已登录则弹出窗口 查看大图或更换头像
  //若未登录则登录
  clickAvatar() {
    console.log('intoProcessA')
    if(app.globalData.login){
      //更换头像
      wx.navigateTo({
        url: '../personal-avatar/index',
      })
    }else{
      this.login();
    }
  },
  changeNickName(){
    this.setData({showChangeNickName: true})
  },
  onInputChange(e){
    this.data.userInfo.nickName = e.detail.value;
  },
  nickNameSubmit(){
    console.log(this.data.userInfo)
    if(this.data.userInfo.nickName == ''){
      wx.showToast({
        title: '昵称不能为空',
        icon: 'error',
        duration: 500
      })
    }else{
      wx.request({
        url: app.globalData.baseURL + '/api/user/updateNickName',
        method: 'POST',
        data: {
          token: wx.getStorageSync('token'),
          nickName: this.data.userInfo.nickName
        },
        success: (res) => {
          console.log(res)
          wx.setStorageSync('token', res.data.token)
          app.globalData.userInfo.nickName = this.data.userInfo.nickName;
          app.globalData.login = false;
          app.globalData.infoChanged = true;
          this.setData({showChangeNickName: false})
          wx.reLaunch({
            url: '../main-personal/index',
          })
        }
      })
    }
  },
  onShow(){
    console.log("main-personal的onShow")
    console.log("globalData")
    console.log(app.globalData)
    console.log("data")
    console.log(this.data)
    const login = app.globalData.login;
    this.setData({login: login})
    console.log('infoChanged' + app.globalData.infoChanged)
    if(login){
      this.setData({userInfo: app.globalData.userInfo,hideLoading: false})
      console.log("globalData")
      console.log(app.globalData)
      console.log("data")
      console.log(this.data)
      wx.request({
        url:  app.globalData.baseURL + '/api/user/findUserInfo',
        method: 'POST',
        data: {token: wx.getStorageSync('token')},
        success: (res) => {
          if(!res.data.success){
            app.globalData.login = false;
            wx.showToast({
              title: '登录过期',
              icon: 'error'
            })
            this.setData({hideLoading: true})
          }else{
            console.log('登录开始 收到正常回调')
            console.log(res);
            const data = res.data;
            wx.setStorageSync('token', data.token);
            this.setData({gold: data.content.gold,minute: data.content.minute,hideLoading: true})
            console.log('本地存储token变更为' + data.token)
            console.log('*******登陆结束********')
          }
        },
        fail: (res) => {
          app.globalData.login = false;
          wx.showToast({
            title: '发生错误',
            content: '请联系技术人员',
            icon: 'error',
            duration: 900
          })
          this.setData({hideLoading: true})
        }
      })
    }else{
      this.login()
    }
  },
  login(){
    console.log("开始登录")
    wx.login({
      success:(res)=>{
        const code = res.code;
        wx.request({
          url: app.globalData.baseURL +  `/api/user/login`,
          method :'POST',
          data:{
            code:code
          },
          success:(res)=>{
            console.log("**************************************************************************************")
            console.log("*****登录请求发送成功*****")
            console.log(JSON.stringify(res));
            console.log("globalData")
            console.log(app.globalData)
            console.log("data")
            console.log(this.data)
            const data = JSON.parse(JSON.stringify(res)).data;
            if(data.success){
              console.log("*****登录成功*****")
              wx.setStorageSync("token",data.token);
              wx.setStorageSync('id', data.content.wxOpenId);
              console.log('本地存储token变更为' + data.token)
              data.content.imageURL = app.globalData.baseURL + data.content.imageURL;
              app.globalData.login = true;
              app.globalData.userInfo = data.content
              this.setData({userInfo:data.content})
              console.log("globalData")
              console.log(app.globalData)
              console.log("data")
              console.log(this.data)
              if(app.globalData.infoChanged){
                app.globalData.infoChanged = false;
                this.onShow();
              }else{
                wx.showToast({
                  title: '登录成功',
                  duration: 1000,
                  icon: 'success',
                  mask: true,
                })
                if(res.data.needRegister){
                  setTimeout(function() {
                    wx.navigateTo({
                      url: '../register-avatar/index',
                    })
                  }, 900)
                }else{
                  setTimeout(function() {
                    wx.switchTab({
                      url: '../main-main/index',
                    })
                  }, 900)
                }
              }
            }else{
              console.log("*****登录失败*****")
              wx.showToast({
                title: '登录失败',
                duration: 2000,
                icon: 'error',
                mask: true
              })
            }
          },
          fail: (res) => {
            console.log("*****登录请求发送失败*****")
            console.log(res)
            app.globalData.login = false;
            wx.showToast({
              title: '发生错误',
              content: '请联系技术人员',
              icon: 'error',
              duration: 900
            })
            setTimeout(() => {
              wx.switchTab({
                url: '../main-personal/index',
              })
            },500)
          }
        })
        console.log("**********")
      }
    })
  }
})
