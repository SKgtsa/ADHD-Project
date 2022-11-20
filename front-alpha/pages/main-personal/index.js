// pages/main-personal/index.js

const app = getApp();


Page({
  data: {
    code: '',
    userInfo: {avatarUrl: '../../img/default-avatar.jpg', nickName: '请登录'},
    gold: null,
    hour: null,
    login: false,
    hideLoading: true,
  },
  // 事件处理函数
  bindViewTap() {
    wx.navigateTo({
      url: '../test/test',
    })
  },
  onLoad() {
    
  },
  submitLogin() {
    console.log('intoProcessA')
    // 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认，开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
    //应尽可能减少调用次数，频率上限较低
    wx.getUserProfile({
      desc: '展示用户信息', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res) => {
        //微信获取数据成功后的回调函数
        console.log('intoProcessB')
        console.log(res)
        app.globalData.userInfo = res.userInfo
        this.setData({
          userInfo: res.userInfo,
          hideLoading: false
        })
        const signature = res.signature;
        const rawData = res.rawData;
        wx.login({
          success:(res)=>{
            this.setData({
              code : res.code
            })
            const code = res.code;
            wx.request({
              url: app.globalData.baseURL +  `/api/user/login`,
              method :'POST',
              data:{
                code:code,
                signature:signature,
                rawData:rawData
              },
              success:(res)=>{
                console.log('IntoProcessC')
                console.log(JSON.stringify(res));
                const data = JSON.parse(JSON.stringify(res)).data;

                console.log(data.token);
                if(data.success){
                  wx.setStorageSync("token",data.token)
                  console.log('本地存储token变更为' + data.token)
                  this.setData({hideLoading: true})
                  app.globalData.login = true;
                  // app.setLogin(true)
                  wx.showToast({
                    title: '登录成功',
                    duration: 2000,
                    icon: 'success',
                    mask: true,
                    success: function() {
                      setTimeout(function() {
                        wx.switchTab({
                          url: '../main-main/index',
                        })
                      }, 1900)
                    }
                  })
                }else{
                  this.setData({hideLoading: true})
                  wx.showToast({
                    title: '登录失败',
                    duration: 2000,
                    icon: 'error',
                    mask: true
                  })
                }
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
          }
        })
      }
    })
  },
  onShow(){
    const login = app.globalData.login;
    this.setData({login: login})
    if(login){
      this.setData({userInfo: app.globalData.userInfo,hideLoading: false})
      wx.request({
        url:  app.globalData.baseURL + '/api/user/findUserInfo',
        method: 'POST',
        data: {token: wx.getStorageSync('token')},
        success: (res) => {
          console.log(res);
          const data = res.data;
          wx.setStorageSync('token', data.token);
          this.setData({gold: data.content.gold,hour: data.content.hour,hideLoading: true})
          console.log('本地存储token变更为' + data.token)
        },
        fail: (res) => {
          app.globalData.login = false;
          wx.showToast({
            title: '登录过期',
            icon: 'error'
          })
          this.setData({hideLoading: true})
        }
      })
    }else{
      wx.showModal({
        title: '请先登录',
        content: '为了更好为您提供服务，我们需要您登录微信账号',
        showCancel: false,
        success :(res) => {
          if(res.confirm){
            this.submitLogin();
          }
        }
      })  
    }
  }
})
