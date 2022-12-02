const app = getApp();

function login() {
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
          console.log("*****登录请求发送成功*****")
          console.log(JSON.stringify(res));
          const data = JSON.parse(JSON.stringify(res)).data;
          if(data.success){
            console.log("*****登录成功*****")
            wx.setStorageSync("token",data.token)
            console.log('本地存储token变更为' + data.token)
            this.setData({hideLoading: true})
            app.globalData.login = true;
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
            console.log("*****登录失败*****")
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
module.exports = {
  login: login
}