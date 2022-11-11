import * as echarts from '../../ec-canvas/echarts';

const app = getApp();
Page({
  bleGetDeviceServices(deviceId){
    wx.getBLEDeviceServices({
      deviceId, // 搜索到设备的 deviceId
      success: (res) => {
        console.log(res.services)
        for (let i = 0; i < res.services.length; i++) {
          if (res.services[i].isPrimary) {
            // 可根据具体业务需要，选择一个主服务进行通信
            this.bleGetDeviceCharacteristics(deviceId,res.services[i].uuid)
          }
        }
      }
    })
  },
  bleGetDeviceCharacteristics(deviceId,serviceId){
    wx.getBLEDeviceCharacteristics({
      deviceId, // 搜索到设备的 deviceId
      serviceId, // 上一步中找到的某个服务
      success: (res) => {
        for (let i = 0; i < res.characteristics.length; i++) {
          let item = res.characteristics[i]
          console.log(item)
          if (item.properties.write) { // 该特征值可写
            // 本示例是向蓝牙设备发送一个 0x00 的 16 进制数据
            // 实际使用时，应根据具体设备协议发送数据
            // let buffer = new ArrayBuffer(1)
            // let dataView = new DataView(buffer)
            // dataView.setUint8(0, 0)
            // let senddata = 'FF';
            // let buffer = this.hexString2ArrayBuffer(senddata);
            var buffer = this.stringToBytes("connected")
            this.setData({
              'deviceId':deviceId,
              'serviceId':serviceId,
              'characteristicId':item.uuid
            })
            app.globalData.deviceId = deviceId;
            app.globalData.serviceId = serviceId;
            app.globalData.characteristicId = item.uuid; 
            console.log('发送数据 connected')
            console.log('deviceId: ' + deviceId)
            console.log('serviceId: ' + serviceId)
            console.log('characteristicId: ' + item.uuid);
            console.log(buffer)
            console.log('结束发送')
            wx.writeBLECharacteristicValue({
              deviceId,
              serviceId,
              characteristicId: item.uuid,
              value: buffer,
            })
          }
          if (item.properties.read) { // 改特征值可读
            wx.readBLECharacteristicValue({
              deviceId,
              serviceId,
              characteristicId: item.uuid,
            })
          }
          if (item.properties.notify || item.properties.indicate) {
            // 必须先启用 wx.notifyBLECharacteristicValueChange 才能监听到设备 onBLECharacteristicValueChange 事件
            wx.notifyBLECharacteristicValueChange({
              deviceId,
              serviceId,
              characteristicId: item.uuid,
              state: true,
            })
          }
        }
      }
    })
  },
  stringToBytes(str) {
    var array = new Uint8Array(str.length);
    for (var i = 0, l = str.length; i < l; i++) {
      array[i] = str.charCodeAt(i);
    }
    console.log(array);
    return array.buffer;
  },
  hextoString: function (hex) {
    var arr = hex.split("")
    var out = ""
    for (var i = 0; i < arr.length / 2; i++) {
      var tmp = "0x" + arr[i * 2] + arr[i * 2 + 1]
      var charValue = String.fromCharCode(tmp);
      out += charValue
    }
    return out
  },
  ab2hex(buffer) {
    var hexArr = Array.prototype.map.call(
      new Uint8Array(buffer),
      function (bit) {
        return ('00' + bit.toString(16)).slice(-2)
      }
    )
    return hexArr.join('');
  },

  onShareAppMessage: function (res) {
    return {
      title: '工程样本',
      path: '/pages/index/index',
      success: function () { },
      fail: function () { }
    }
  },
  versionCompare: function (ver1, ver2) { //版本比较
    var version1pre = parseFloat(ver1)
    var version2pre = parseFloat(ver2)
    var version1next = parseInt(ver1.replace(version1pre + ".", ""))
    var version2next = parseInt(ver2.replace(version2pre + ".", ""))
    if (version1pre > version2pre)
        return true
    else if (version1pre < version2pre) 
        return false
    else {
        if (version1next > version2next)
            return true
        else
            return false
    }
  },
  //兼容性检查
  checkBLESupport(){
    let result = false;
    //Android 从微信 6.5.7 开始支持，iOS 从微信 6.5.6 开始支持
    //第一项，如果手机是android系统，需要判断版本信息
    if (app.getPlatform() == "android") {
      console.log('successA')
      if(this.versionCompare("6.5.7", app.getVersion())){
        console.log('微信版本过低')
        wx.showModal({
          title: '提示',
          content: '当前微信版本过低，请更新至最新版本',
          showCancel: false
        });
      }else if(!this.versionCompare("6.0.0", app.getSystem().replace("Android","").replace(" ",""))){
        console.log('微信版本满足')
        // wx.getSetting({
        //   success: (res) => {
        //     console.log('成功获得权限信息')
        //     let statu = res.authSetting;
        //     if(!statu['scope.addPhoneCalendar.userLocation']){
        //       console.log('无定位权限')
        //       wx.showModal({
        //         cancelColor: 'cancelColor',
        //         title: '温馨提示',
        //         content: '请授予位置服务权限，以便搜索设备',
        //         success: (tip) => {
        //           if(tip.confirm){
        //             wx.openSetting({
        //               success: (data) => {
        //                 if(data.authSetting["scope.userLocation"] === true){
        //                   wx.showToast({
        //                     title: '授权成功',
        //                     icon: 'success',
        //                     duration: 1000
        //                   })
        //                   result = true;
        //                 }else{
        //                   wx.showToast({
        //                     title: '授权失败',
        //                     icon: 'error',
        //                     duration: 1000
        //                   })
        //                 }
        //               }
        //             })
        //           }else{
        //             console.log('点击取消')
        //           }
        //         }
        //       })
        //     }else{
        //       //存在权限
        //       console.log('已授予定位权限')
        //       result = true;
        //     }
        //   }
        // })
        result = true;
      }else if(!this.versionCompare(app.getSystem().replace("Android", "").replace(" ", "")), "4.3.0"){
        console.log('手机系统太老')
        wx.showToast({
          title: '温馨提示',
          content: '您的手机系统版本过低，无法操作蓝牙设备',
          icon: 'error',
          duration: 1000
        })
      }else{
        console.log('其他系统，默认支持')
        result = true;
      }
      return result;
    }
    //第二项，如果手机是ios系统，需要判断版本信息
    if (app.getPlatform() == "ios" && versionCompare("6.5.6", app.getVersion())) {
      wx.showModal({
        title: '提示',
        content: '当前微信版本过低，请更新至最新版本',
        showCancel: false
      });
    }
  },
  //蓝牙初始化，开始搜索设备
  bleInit() {
    if(this.checkBLESupport()){
      //初始化开始
      console.log('searchBle')
      // 监听扫描到新设备事件
      wx.onBluetoothDeviceFound((res) => {
        this.setData({deviceFoundStart: true})
        res.devices.forEach((device) => {
          // 这里可以做一些过滤
          console.log('Device Found', device.name)
          if(device.name == "Nero_Car_Service"){
            console.log('found Nero_Car_Service!!')
            // 找到设备开始连接
            this.bleConnection(device.deviceId);
            wx.stopBluetoothDevicesDiscovery()
            this.setData({deviceFoundStart: false, deviceId: device.deviceId})
            app.globalData.deviceId = device.deviceId;
            
          }else{
            console.log("not Nero_Car_Service")
          }
        })
        // 找到要搜索的设备后，及时停止扫描
        // 
      })

      // 初始化蓝牙模块
      wx.openBluetoothAdapter({
        mode: 'central',
        success: (res) => {
          this.setData({blueToothAdapterStart: true})
          // 开始搜索附近的蓝牙外围设备
          wx.startBluetoothDevicesDiscovery({
            allowDuplicatesKey: false,
          })
        },
        fail: (res) => {
          if (res.errCode !== 10001) {
            this.setData({blueToothAdapterStart: false})
            wx.showToast({
              title: '蓝牙错误',
            })
            return;
          }
          this.setData({blueToothAdapterStart: true})
          wx.onBluetoothAdapterStateChange((res) => {
            this.setData({onBlueToothAdapterStateChange: true})
            if (!res.available) return
            // 开始搜寻附近的蓝牙外围设备
            wx.startBluetoothDevicesDiscovery({
              allowDuplicatesKey: false,
            })
          })
        }
      })
      var that = this
      //正在同步数据
      let inProcess = false;
      wx.onBLECharacteristicValueChange((result) => {
        this.setData({onBLECharaValueChange: true})
        console.log('onBLECharacteristicValueChange',result.value)
        let hex = that.ab2hex(result.value)
        const input = that.hextoString(hex);
        console.log('hextoString',input)
        console.log('hex',hex)
        let error = false;
        if(!inProcess){
          //没有在发送 此时检测开始信号
          //截取前三位 作为控制信号
          
          const startMark = input.substring(0,3);
          console.log("StartMark: input.substring(0,3)  " + startMark)
          //头部标记为aaa
          if(startMark == 'aaa'){
            //取控制信号后的整个字符串 拼接进syncResult 进行同步数据的积累
            //整个数据收取完成后整个发给后端处理
            this.data.syncResult = input.substring(3);
            console.log('接收到开始信号 存储开始信号后的数据： ' + this.data.syncResult)
            //设置布尔值inProcess
            //代表已进入数据收取过程
            inProcess = true;
          }
        }else{
          //正在发送
          //截取末三位 作为控制信号 控制数据收取的停止时机
          const endMark = input.substring(input.length - 3);
          console.log('endMark: ' + endMark)
          if(endMark == "ccc" || endMark == "ddd"){
            console.log('接收到停止信号: ' + endMark)
            //接收到数据发送终止符 发送数据 取字符串开头到倒数第四位为数据
            //拼接给syncResult 之后发送给后端
            this.data.syncResult += input.substring(0,input.length - 3)
            if(this.data.syncResult.substring(0,3) == 'aaa'){
              this.setData({syncResult: this.data.syncResult.substring(3)})
            }
            console.log( "同步结果: " + this.data.syncResult)
            let str = '';
            console.log('发送数据')
            wx.request({
              url: 'https://chenanbella.cn/api/training/save',
              method: 'POST',
              data: {
                token: wx.getStorageSync('token'),
                rawData: this.data.syncResult
              },
              success(res){
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
                console.log("发送成功 收到反馈如下")
                console.log(res)
                //保存成功,向设备发送数据
                if(res.data.success){
                  //成功发送并保存
                  str = "ok";
                  var buffer = new Uint8Array(str.length);
                  for (var i = 0, l = str.length; i < l; i++) {
                    buffer[i] = str.charCodeAt(i);
                  }
                  wx.setStorageSync('token', res.data.token)
                }else{
                  //成功发送但没保存 可能出现登录过期 找不到用户
                  str = "stop";
                  var buffer = new Uint8Array(str.length);
                  for (var i = 0, l = str.length; i < l; i++) {
                    buffer[i] = str.charCodeAt(i);
                  }
                  error = true;
                  if(res.data.message == "登录过期"){
                    app.globalData.login = false;
                  }
                }
                console.log("开始向设备发送数据 " + str)
                //如果出现bug 给写特征值的函数加个延时
                setTimeout(() => {
                  wx.writeBLECharacteristicValue({
                    deviceId: app.globalData.deviceId,
                    serviceId: app.globalData.serviceId,
                    characteristicId: app.globalData.characteristicId,
                    value: buffer.buffer,
                  })
                },300)
                
                wx.setStorageSync('token', res.data.token)
              },
              fail(res){
                if(res.data.token == null){
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
                console.log('发送失败 结果如下：')
                console.log(res)
                //发送失败
                const str = "stop";
                var buffer = new Uint8Array(str.length);
                for (var i = 0, l = str.length; i < l; i++) {
                  buffer[i] = str.charCodeAt(i);
                }
                error = true;
                console.log("开始向设备发送数据 " + str)
                //如果出现bug,给写特征值的函数加上延时
                setTimeout(() => {
                  wx.writeBLECharacteristicValue({
                    deviceId: app.globalData.deviceId,
                    serviceId: app.globalData.serviceId,
                    characteristicId: app.globalData.characteristicId,
                    value: buffer.buffer,
                  })
                },300)
              },
            })
            this.data.syncResult = '';
            console.log('清空syncResult: ' + this.data.syncResult);
            if(input.substring(input.length - 3) == 'ddd' || error){
              //本次发送的数据包结尾为ddd 代表所有数据已发送完毕
              //error代表出现了错误
              //发送完毕 卸载蓝牙 延迟一秒，防止有需要蓝牙的异步函数还没有执行完的情况
              console.log('接收到数据为ddd 或者发生了错误 进入蓝牙卸载环节')
              setTimeout(() => {
                this.setData({showBlueToothPage: false})
              } ,1000)
            }
          }else{
            //接收数据 因为设备连接后会首先发来Nero_Car_Service 所以滤过该信号
            if(input != 'Nero_Car_Service'){
              this.data.syncResult += input;
            }
          }
        }
      })
      //初始化结束
    } 
  },
  bleConnection(deviceId){
    wx.createBLEConnection({
      deviceId, // 搜索到设备的 deviceId
      success: () => {
        // 连接成功，获取服务
        
        console.log('连接成功，获取服务')
        this.setData({blueToothConnceted: true, blueToothStatus: '设备已连接', deviceId: deviceId})
        this.bleGetDeviceServices(deviceId)
        wx.showToast({
          title: '连接成功',
          icon: 'success',
          duration: 1500,
          mask: true,
          success: function() {
            setTimeout(function() {
              wx.navigateTo({
                url: '../main-ble-control/index.wxml'
              })
            }, 900)
          }
        })
      },
      fail: (res)=>{
        console.log('连接失败')
        console.log(res)
        this.setData({blueToothConnceted: false, blueToothStatus: '连接失败'})
        setTimeout(() => {
          this.bleInit();
        },1000)
      }
    })
  },
  //蓝牙卸载
  uninstallBle: function(){
    console.log('开始蓝牙卸载')
    if(this.data.blueToothConnceted){
      console.log('断开蓝牙连接')
      wx.closeBLEConnection({
        deviceId: this.data.deviceId,
      })
    }
    if(this.data.deviceFoundStart){
      console.log('停止设备发现')
      wx.stopBluetoothDevicesDiscovery()
      this.setData({deviceFoundStart: false})
    }
    if(this.data.blueToothAdapterStart){
      console.log('关闭蓝牙适配器')
      wx.closeBluetoothAdapter({
        success: () => {
          this.setData({blueToothAdapterStart: false})
        },
        fail: ()=>{
          console.log('无法关闭蓝牙适配器')
        }
      })
    }
    if(this.data.onBlueToothAdapterStateChange){
      console.log('关闭状态改变监视')
      wx.offBluetoothAdapterStateChange()
      this.setData({onBlueToothAdapterStateChange: false})
    }
    if(this.data.onBLECharaValueChange){
      console.log('关闭特征值变化检测')
      wx.offBLECharacteristicValueChange()
      this.setData({onBLECharaValueChange: false})
    }
  },
  //点击开始训练
  startSync: function (){
    this.setData({showBlueToothPage: true})
    console.log('startSync')
  },
  openBLEWindow: function(){

    this.setData({showGauge: true,blueToothStatus: '正在搜索蓝牙设备'})
    this.bleInit()
    console.log('end')
  },
  closeBLEWindow: function(){
    console.log('closeConnection')
    this.uninstallBle();
    this.setData({showGauge: false})
    
  },
  connectionFailed: function (){
    this.setData({showBlueToothPage: false})
  },
  connectionSucceeded: function (){
    this.setData({showBlueToothPage: false})
  },
  data: {
    //蓝牙状态参数
    blueToothStatus: '',
    blueToothConnceted: false,
    //是否显示蓝牙配对页面
    showBlueToothPage: false,
    //是否开始设备搜索
    deviceFoundStart: false,
    blueToothAdapterStart: false,
    onBlueToothAdapterStateChange: false,
    onBLECharaValueChange: false,
    targetTraining: {
      graph: [
        50,60,30,40,90,80,20
      ]
    },
    showGauge: false,
    'deviceId':'',
    'serviceId':'',
    'characteristicId':'',
   
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
  detailedButton: function() {
    wx.navigateTo({
      url: '../main-device-detail/index',
    })
  },
  onReady() {
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onShow() {
    if(!app.globalData.login){
      wx.switchTab({
        url: '../main-personal/index',
      })
    }
    console.log('intoOnShow()')
    wx.request({
      url: app.globalData.baseURL +  '/api/training/findLastSevenDay',
      method: 'POST',
      data: {token: wx.getStorageSync('token')},
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
        console.log(res);
        app.globalData.gaugeData = data.message;
        console.log('gaugeData: ' + app.globalData.gaugeData)
        app.globalData.detailedGraphY = data.content;
        console.log('detailedGraphY' + app.globalData.detailedGraphY);
        app.globalData.detailedGraphX = ['1','2','3','4','5','6','7'];
        wx.setStorageSync('token', data.token)
      },
      fail: (res) => {
        if(res.data.token == null){
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
      }
    })
  },
  onLoad(){
    if(this.options){
      console.log(this.options.showBlueToothPage)
      this.setData({
        showBlueToothPage: this.options.showBlueToothPage,
      })
      if(this.options.showDetail){
        this.detailedButton();
      }
    }
  }
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
      data: app.globalData.detailedGraphX,
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
      data: app.globalData.detailedGraphY
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
        value: app.globalData.gaugeData,
        name: '平均专注率',
        textStyle: {
          color: '#FFF'
        }
      }]

    }]};
}
//数据备份
// {
//   time: 'a',
//   concentrationRate: 50
// },{
//   time: 'a',
//   concentrationRate: 70
// },{
//   time: 'a',
//   concentrationRate: 80
// },{
//   time: 'a',
//   concentrationRate: 10
// },{
//   time: 'a',
//   concentrationRate: 30
// }