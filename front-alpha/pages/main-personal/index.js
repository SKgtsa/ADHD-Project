// pages/main-personal/index.js
Page({
  data: {
    code: '',
    motto: 'Hello World',
    userInfo: {avatarUrl: '../../img/default-avatar.png', nickName: '请登录'},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    canIUseGetUserProfile: false,
    canIUseOpenData: wx.canIUse('open-data.type.userAvatarUrl') && wx.canIUse('open-data.type.userNickName') // 如需尝试获取用户信息可改为false
  },
  // 事件处理函数
  bindViewTap() {
    wx.navigateTo({
      url: '../test/test',
    })
  },
  onLoad() {
    // @ts-ignore
    if (wx.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true
      })
    }
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


        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
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
              url: `http://localhost:5174/api/user/login`,
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
                wx.setStorageSync("token",data.token)
                console.log(data.token);
                if(data.success){
                  if(data.needRegister){
                    wx.switchTab({
                      url: '../register/register'
                    })
                  }else{
                    wx.switchTab({
                      url: '../main/main',
                    })
                  }
                }else{

                }

              }
            })
          }
        })
      }
    })


  }
})
