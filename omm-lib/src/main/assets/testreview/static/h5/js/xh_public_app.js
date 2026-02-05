(function (window) {

  window.app = window.app || {};

  //转发接口
  var XH_FORWORD_URL = "http://172.28.0.56:9999/service/requestForward";
  var ssoToken = '';

  /*=====================工具类=====================*/
  app.utils = window.utils || {};
  /**
   * @desc 将json字符串转成json对象
   * @param param {String} JSON字符串
   */
  app.utils.toJSON = function (param) {
      return typeof (param) == "string" ? eval('(' + param + ')') : param;
  }
  /**
   * @desc 判断是否在PC上
   * @return {boolean} 返回结果 ⇒ true | false
   */
  app.utils.isPC = function () {
      var userAgentInfo = navigator.userAgent;
      var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod");
      var flag = true;
      for (var v = 0; v < Agents.length; v++) {
          if (userAgentInfo.indexOf(Agents[v]) > 0) {
              flag = false;
              break;
          }
      }
      return flag;
  }
  /**
   * @desc 判断是否在Cordova上
   * @return {boolean} 返回结果 ⇒ true | false
   */
  app.utils.isCordova = function () {
      return '_cordovaNative' in window;
  }


  //post , get提交时带上手机硬件参数
  var userLoginId;
  var deviceId;
  var meid;
  var imsi;
  var app_device = {};

  function getInfos() {
      app.getLoginInfo(function (result) {
          userLoginId = result.loginId;
          app_device["loginId"] = userLoginId;
      });

      //获取设备号
      app.getDeviceId(function (result) {
          deviceId = result;
          app_device["bingoDeviceId"] = deviceId;
      });

      //获取meid
      app.getMeid(function (result) {
          meid = result;
          app_device["meid"] = meid;
      });

      //获取imsi
      app.getImsi(function (result) {
          imsi = result;
          app_device["imsi"] = imsi;
      });
  }

  //link plugin
  /**
   * @desc 1、获取用户登录信息
   *      不需要传参
   * @output  result:{"loginId":"登陆ID", "user":"用户", "userId":"用户ID", "orgName":"部门"}
   */

  app.getLoginInfo = function (callback, failCb) {
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      }
      Cordova.exec(successCallback, failCallback, "LinkPlugin", "getLoginInfo", []);
  }

  /**
   * @desc 2、重载webView，重新打开页面，目前只适用于首页
   * @output 不需要传参
   */
  app.recreateWebView = function (callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "LinkPlugin", "recreateWebView", []);
  }

  /**
   * @desc 3、获取所有联系人信息
   * @input []
   * @output 联系人信息json结构
   */
  app.getAllContacts = function (callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "LinkPlugin", "getAllContacts", []);
  }

  /**
   * @desc 4、选择本地文件
   * @input []
   * @output  file_path 本地文件路径
              mime_type 文件类型
              base64Data 图片/视频预览图的base64数据，其他类型的文件无数据

   */
  app.chooseFile = function (callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(JSON.parse(result));
      };
      Cordova.exec(successCallback, null, "LinkPlugin", "chooseFile", []);
  }

  /**
   * @desc  5、S3服务器文件上传
   * @param   {   
   *              localFileName:"xxx"   本地文件路径
   *              fileType:1/2/3        文件类型
   *              isCompress:true      是否压缩文件    
   *          }
   * @Cordova_input   [fileName, Type, isCompress]
              String  fileName 文件路径
              Int     Type 文件类型：1：图片，2：视频， 3：其他
              Bool    isCompress 是否压缩文件，只对图片类型文件有效
   *  
   */
  app.uploadFile = function (params, callback) {

      var cordovaParams = [
          params.localFileName,
          params.fileType || 1,
          params.isCompress || true
      ];
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failedCallback = function (result) {
          var err = {
              error_msg: "上传文件失败"
          }
          callback(result, err);
      };
      Cordova.exec(successCallback, failedCallback, "LinkPlugin", "upload", cordovaParams);

  }

  /**
   * @desc 6、 获取sso登陆的token
   *      不需要传参
   * @output token的值
   */
  app.getToken = function (callback, failCb) {
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      }


      Cordova.exec(successCallback, null, "LinkPlugin", "getToken", []);
  }
  window.onload = function () {
      document.addEventListener("deviceready", function () {
          //声明页面事件
          app.getToken(function (result) {
              ssoToken = result;
          })
      }, false);
  }();


  /**
   * @desc 7、根据userId（用户ID）获取用户数据
   *      不需要传参
   * @output 用户的json数据
   */
  app.getUserInfo = function (params, callback, failCb) {
      var corvodaParams = [
          params.userId
      ]
      console.log(params);
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      }
      Cordova.exec(successCallback, failCallback, "LinkPlugin", "getUserInfo", corvodaParams);
  }

  /**
   * @desc 8、根据部门ID获取联系人信息
   * @param { orgName:"xxx" }
   * @Cordova_input [orgName] orgName: 部门ID
   */
  app.getDeptInfoByOrgId = function (params, callback) {
      var params = [
          params.orgName
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      }
      Cordova.exec(successCallback, null, "LinkPlugin", "getDeptInfoByOrgId", params);

  }

  /**
   * @desc 9、选择联系人(多选)
   *      不需要传参
   * @output 返回选择联系人的数据 , 是一个数组
   */
  app.startContactMulitSelector = function (callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "LinkPlugin", "startContactMulitSelector2", []);

  }

  /**
   * @desc 10、根据用户ID跳转到联系人
   * @param { userId:"用户ID" }
   * @Corvoda_input [userId]  String userId用户ID

   */
  app.startUserCard = function (params, callback) {
      var callback = callback || function () {};
      var corvodaParams = [
          params.userId
      ]
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "LinkPlugin", "startUserCard", corvodaParams);

  }

  //Extend App 
  /**
   * @desc 1、获取设备ID
   * @output result : deviceId
   */
  app.getDeviceId = function (callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "getDeviceId", []);

  }

  /**
   * @desc 2、获取手机Meid
   * @output result : MeidID
   */
  app.getMeid = function (callback, failCb) {
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "ExtendApp", "getMeid", []);
  }

  /**
   * @desc 3、.获取手机Imsi
   * @output result : Imsi     * 
   */
  app.getImsi = function (callback, failCb) {
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "ExtendApp", "getImsi", []);
  }

  /**
   * @desc 4、打开显示进度框
   * @input { title:"xxx",message:"xxx" }
   * @Cordova_input  [title, message]
   *          Title:标题
              Message:消息
   * @output 
   */
  app.progressStart = function (params, callback) {
      var params = [
          params.title,
          params.message
      ]
      var successCallback = function (result) {
          console.log(result);

          var callback = callback || function () {};
          callback(result);
      };
      console.log(params);

      Cordova.exec(successCallback, null, "ExtendApp", "progressStart", params);
  }

  /**
   * @desc 5、关闭显示进度框progressStop
   */
  app.progressStop = function (callback) {
      var successCallback = function (result) {
          var callback = callback || function () {};
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "progressStop", []);
  }

  /**
   * @desc 6、退出界面
   */
  app.exit = function (callback) {
      var successCallback = function (result) {
          var callback = callback || function () {};
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "exit", []);
  }

  /**
   * @desc 7、app应用是否存在
   * @input { packname:"aaa" }
   * @covorda_input [packname] Packname: 包名
   * @output result:{"res":true/false}
   */
  app.isExistApp = function (params, callback, failCb) {
      var params = [
          params.packname
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "ExtendApp", "isExistApp", params);
  }

  /**
   * @desc 8、安装应用
   * @input { filename:"aaa" }
   * @covorda_input [filename] filename: 应用名称
   * @output 
   */
  app.install = function (params, callback) {
      var params = [
          params.filename
      ]
      var successCallback = function (result) {
          var callback = callback || function () {};
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "install", params);
  }

  /**
   * @desc 9根据appId获取apk绝对文件的路径
   * @input { appID:"165a1s6d6a5s1d665asd" }
   * @covorda_input [filename] filename: 应用名称
   * @output 
   */

  //打不开也没有错误回调反馈,成功反馈根本不执行
  app.getApkPath = function (params, callback) {
      if (typeof params === 'string') {
          params = JSON.parse(params)
      }
      var cordovaParams = [params.appId || params.appID];
      var successCallback = function (result) {
          var callback = callback || function () {};
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "getApkFileName", cordovaParams);
  }

  /**
   * @desc 10根据包名和组件名启动apk
   * @input { appID:"165a1s6d6a5s1d665asd" }
   * @covorda_input [filename] filename: 应用名称
   * @output 
   */
  app.runApp = function (params, callback) {
      params = params || {};
      var onSuccess = function (result) {
          var callback = callback || function () {};
          callback(result);
      };
      if (params.packName !== undefined && params.packName !== "" && params.component !== undefined && params.component !== "") {
          var cordovaParams = [params.packName, params.component];
          Cordova.exec(onSuccess, null, "ExtendApp", "runComponent", cordovaParams)
      } else if (params.packName !== undefined && params.packName !== "" && (params.component == undefined || params.component == "")) {
          var cordovaParams = [params.packName];
          Cordova.exec(onSuccess, null, "ExtendApp", "runApp", cordovaParams);
      } else {
          console.error("参数有误")
      }
  }




  //BarcodeScanner 系列
  /**
   * @desc 二维码扫描
   */
  app.scan = function (callback) {
      var callback = callback || function () {};
      var scanRes = function (result) {
          callback(result);
      }
      Cordova.exec(scanRes, null, "BarcodeScanner", "scan", []);
  }

  //SzgaPlugin 系列
  /**
   * @desc 1、开始定位获取定位信息
   * @input { isEnable:true }
   * @output 1:success , 0:fail
   */
  app.startLocation = function (params, callback) {
      var callback = callback || function () {};
      var successCallback = function (result) {
          console.log(result);

          callback(result);
      };
      Cordova.exec(successCallback, null, "SzgaPlugin", "startLocation", []);
  }

  /**
   * @desc 2、停止定位
   * @input {
              closeWifi:true,
              isEnable:true
          }
      @output 1:success , 0:fail
   */
  app.stopLocation = function (params, callback, failCb) {
      var corvodaParams = [
          params
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "stopLocation", corvodaParams);
  }
  /**
   * @desc 3、证件检测
   * @input { types:2 } 检测身份证
   * @Cordova_input [type]
   */
  app.startORC = function (params, callback) {
      var corvodaParams = [
          params.types || 2
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      console.log(corvodaParams);
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "startORC", corvodaParams);
  }

  /**
   * @desc 4、跳转到指定activity且等待返回结果
   * @input  { “action”:“com.xhinfo.orc.IdentifyFaceActivity” }  人脸识别
   * @Cordova_input   [{“action”:””}]
   * @output 
   */
  app.startActivityForResult = function (params, callback, failCb) {
      var params = [params];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };


      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "startActivityForResult", params);
  }

  /**
   * @desc 5、获取wifi、蓝牙、定位、nfc状态getStatus    
   * @input  { type:1 } 1,2,3,4
   * @Cordova_input   1:wifi , 2:GPS , 3: 蓝牙 ， 4：NFC
   * @output { isEnable: false }
   */
  app.getStatus = function (params, callback, failCb) {
      var corvodaParams = [
          params.type
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          console.log(result);

          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, null, "SzgaPlugin", "getStatus", []);
      // Cordova.exec(successCallback, failCallback, "SzgaPlugin", "getStatus", corvodaParams);
  }

  /**
   * @desc 6、设置wifi、蓝牙、定位、nfc状态   
   * @input  { 
   *          flag:""  ,  true or false
   *          which:""  ,   1:wifi , 2:GPS , 3: 蓝牙 ， 4：NFC
   *          isEnable:""  true or false
   *          }
   * @Cordova_input   { flag:""  , which:""  , isEnable:"" }
   * @output 
   */
  app.setStatus = function (params, callback, failCb) {
      var corvodaParams = [
          params
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "setStatus", corvodaParams);
  }

  /**
   * @desc 7、新版本获取定位信息接口  
   * @input  
   * @Cordova_input   
   * @output 
   */
  app.getLocationInfo2 = function (callback, failCb) {
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "getLocationInfo2", []);
  }

  /**
   * @desc 8、通用开启服务  
   * @input  {
              "action":" ",   //需要启动的服务对应的action
              "package":" ",  //这个服务对应的包名
              "values":"",    //传递的数据对应的名字
              "names":""      //传递的数据对应的值
          }
   * @Cordova_input {   "action":" ",  "package":" ",  "values":",  "names":"" }
   * @output OK
   */
  app.startService = function (params, callback, failCb) {
      var corvodaParams = [params.action, params.package, params.values, params.names];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          console.log(result);

          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      console.log(corvodaParams);

      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "startService", corvodaParams);
  }

  /**
   * @desc 9、通用停止服务  
   * @input  {
              "action":" ",   //需要停止的服务对应的action
              "package":" "   //这个服务对应的包名
          };
   * @Cordova_input {   "action":" ",  "package":" " }
   * @output OK
   */
  app.stopService = function (params, callback, failCb) {
      var corvodaParams = [params];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "stopService", corvodaParams);
  }

  /**
   * @desc 10、Base64数据转Bitmap 
   * @input  { "ret":" ",   //base64编码的数据
   *           "path":" ",  //保存的文件夹
   *           "finalName":" " } //保存的文件名
   * @Cordova_input {"ret":" ",  "path":" ",  "finalName":" "}
   * @output 
   */
  app.base64ToBitmap = function (params, callback, failCb) {
      var corvodaParams = [params];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "base64ToBitmap", corvodaParams);
  }

  /**
   * @desc 11、bitmapToBase64
   * @input  
   * @Cordova_input {"imagePath":" "}  //图片地址
   * @output Base64编码后的数据
   */
  app.bitmapToBase64 = function (params, callback, failCb) {
      var corvodaParams = [params.imageObjk];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "bitmapToBase64", corvodaParams);
  }

  /**
   * @desc 12、获取海拔信息
   * @input  
   * @Cordova_input 
   * @output 海拔高度实际值
   */
  app.getAltitude = function (callback, failCb) {

      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "getAltitude", []);
  }

  /**
   * @desc 13、通用发送组件信息
   * @input  {
              "action":" ", //需要发送消息的服务对应的action
              "values":" ",  //传递的数据对应的值
              "names":" "  //传递的数据对应的名字
          };
   * @Cordova_input {“action”:””, “values”:””, “names”:””}
   * @output 
   */
  app.startNotifyMessage = function (params, callback, failCb) {
      var corvodaParams = [params];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "SzgaPlugin", "startNotifyMessage", corvodaParams);
  }

  //NativeBroadCast 系列
  /**
   * @desc 1、注册系统消息监听
   * @input  
   * @Cordova_input 
   * @output 
   */
  app.registerReceiver = function (callback, failCb) {

      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (err) {
          failCb(err);
      };
      Cordova.exec(successCallback, failCallback, "NativeBroadCast", "registerReceiver", []);
  }

  //HttpRequest 系列



  /**
   * @desc 1、ajax
   * @input  params.url, 
              params.data, 
              params.method, 
              params.async,
              params.contentType, 
              params.headers, 
              params.timeout ,

              params.success,
              params.error,

              params.isCloud:false,       //
              params.isUsingNativeGetAndPost:true //是否使用安卓原生ajax请求

   * @Cordova_input [params.url, params.data,params.method,params.async,params.contentType,params.headers,params.timeout,params.endpoint]
   * @output 
   */

  app.ajax = function (params) {
      params = params || {};
      params.method = params.method || "GET";
      if (typeof (params.async) == "undefined") {
          params.async = true;
      }
      params.contentType = params.contentType || "";
      params.headers = params.headers || {};
      params.headers.requestType = "app";
      params.headers.accessToken = ssoToken;

      params.timeout = params.timeout || 10000;
      params.success = params.success || function () {};
      params.fail = params.fail || function () {};

      var successCallback = function (result) {

          params.success(app.utils.toJSON(result));
      };
      var failCallback = function (result) {
          params.error(app.utils.toJSON(result));
      }
      var CordovaParams = [
          params.url,
          params.data,
          params.method,
          params.async,
          params.contentType,
          params.headers,
          params.timeout
      ]
      console.log(params);

      $.ajax({
          url: params.url,
          data: params.data,
          headers: params.headers,
          contentType: params.contentType,
          type: params.method,
          success: successCallback,
          error: failCallback
      });



      // Cordova.exec(successCallback, failCallback, "HttpRequest", "ajax", CordovaParams);
  }

  /**
   * @desc 2、POST，对ajax进行转发操作
   */
  app.ajaxPost = function (params) {
      if (typeof (userLoginId) == "undefined" || typeof (deviceId) == "undefined" ||
          typeof (meid) == "undefined" || typeof (imsi) == "undefined" ||
          typeof (app_device) == "undefined") {
          getInfos();
      }

      var url = params.url;
      var data = params.data;
      var timeout = params.timeout || 30000;
      var successCallback = params.success || function () {};
      var failCallback = params.error || function () {};

      var appInfo = params.appInfo || {}; //app name ，version , server_charset等信息

      data.APP_DEVICE = appInfo.APP_DEVICEID || JSON.stringify(app_device) || "";
      data.APP_NAME = appInfo.APP_NAME || "";
      data.APP_VERSION = appInfo.APP_VERSION || "";
      data.SERVER_CHARSET = appInfo.SERVER_CHARSET || "";

      if (data.APP_URL != undefined && data.APP_URL.length > 0) {
          //如果有APP_URL，则不作处理
      } else {
          data.APP_URL = params.url; //将url当作参数
      }
      url = XH_FORWORD_URL;

      if (params.isUsingNativeGetAndPost == true) {
          $.ajax({
              url: url,
              data: data,
              type: "POST",
              async: app.utils.toJSON(params.data).async || true,
              timeout: timeout || 30000,
              success: successCallback,
              error: failCallback
          });
      } else {
          app.ajax({
              "url": url,
              "data": data,
              "method": "POST",
              "async": app.utils.toJSON(data).async || true,
              "timeout": timeout,
              "contentType": "application/x-www-form-urlencoded", // application/json
              "success": successCallback,
              "error": failCallback
          });
      }
  }

  /**
   * @desc 3、GET，对ajax进行转发操作
   */
  app.ajaxGet = function (params) {
      params.data.PostOrGet = "GET";
      app.ajaxPost({
          url: params.url,
          data: params.data,
          success: params.success,
          fail: params.fail,
          isCloud: params.isCloud
      });
  }


  //Setting 系列
  /**
   * @desc 1、持久化保存配置信息
   * @input  { name:" " , value:"  ", app_id:"  " }
   * @Cordova_input [ name, value, app.id]
                      name: 配置项名称
                      value: 配置项值
                      app.id: 应用ID

   * @output 
   */
  app.setSetting = function (params, callback, failCb) {
      var CordovaParams = [
          params.name,
          params.value,
          params.app_id,
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Setting", "set", CordovaParams);
  }

  /**
   * @desc 2、获取配置信息
   * @input  { name:" " , defaultvalue:"  ", app_id:"  " }
   * @Cordova_input [ name, defaultvalue, app.id]
                      name: 配置项名称
                      defaultvalue: 配置项值默认值
                      app.id: 应用ID
   * @output 
   */
  app.getSetting = function (params, callback, failCb) {
      var CordovaParams = [
          params.name,
          params.defaultvalue,
          params.app_id,
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Setting", "get", CordovaParams);
  }

  /**
   * @desc 3、删除某配置信息
   * @input  { name:" " , app_id:"  " }
   * @Cordova_input   [ name, app.id]
                      name: 配置项名称
                      app.id: 应用ID
   * @output 
   */
  app.removeSetting = function (params, callback, failCb) {
      var CordovaParams = [
          params.name,
          params.app_id,
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Setting", "remove", CordovaParams);
  }

  /**
   * @desc 4、清除所有配置：慎用
   * @input  { app_id:"  " }
   * @Cordova_input   [ app.id]
                      app.id: 应用ID
   * @output 
   */
  app.clearSetting = function (params, callback, failCb) {
      var CordovaParams = [
          params.app_id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Setting", "clear", CordovaParams);
  }

  /**
   * @desc 5、获取所有配置信息
   * @input  { app_id:"  " }
   * @Cordova_input   [ app.id]
                      app.id: 应用ID
   * @output 
   */
  app.loadSetting = function (params, callback, failCb) {
      var CordovaParams = [
          params.app_id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Setting", "load", CordovaParams);
  }

  //HashMapFile 系列
  /**
   * @desc 1、持久化存储数据, 独立文件的形式
   * @input  {  id:"  "  , key:"  ", value:"  "  }
   * @Cordova_input   [ id, key, value]
                      id: 哈希表ID
                      key: 哈希键
                      value: 哈希值
   * @output 
   */
  app.setHashmap = function (params, callback, failCb) {
      var cordovaParams = [
          params.id,
          params.key,
          params.value
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "set", cordovaParams);

  }

  /**
   * @desc 2、获取hashmap 数据
   * @input  {  id:"  "  , keys:["abc","e"] }
   * @Cordova_input   [ id, keys ]
                      id: 哈希表ID
                      keys: 哈希键  数组，可传多个
   * @output 
   */
  app.getHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      params.keys.map(function (item, index) {
          CordovaParams.push(item);
      })
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "get", CordovaParams);
  }

  /**
   * @desc 3、移除hasmap数据
   * @input  {  id:"  "  , keys:"  " }
   * @Cordova_input   [ id, keys ]
                      id: 哈希表ID
                      keys: 哈希键  可传多个
   * @output 
   */
  app.removeHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      params.keys.map(function (item, index) {
          CordovaParams.push(item);
      })
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "remove", CordovaParams);
  }

  /**
   * @desc 4、清除hashmap数据clear
   * @input  {  id:"  "  }
   * @Cordova_input   [ id  ]
                      id: 哈希表ID
   * @output 
   */
  app.clearHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "clear", CordovaParams);
  }

  /**
   * @desc 5、获取所有hashmap数据
   * @input  {  id:"  "  }
   * @Cordova_input   [ id  ]
                      id: 哈希表ID
   * @output 
   */
  app.readAllHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "readAll", CordovaParams);
  }

  /**
   * @desc 6、获取hashmap所有的key
   * @input  {  id:"  "  }
   * @Cordova_input   [ id  ]
                      id: 哈希表ID
   * @output 
   */
  app.readKeysHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "readKeys", CordovaParams);
  }

  /**
   * @desc 7、获取hashmap所有的value
   * @input  {  id:"  "  }
   * @Cordova_input   [ id  ]
                      id: 哈希表ID
   * @output 
   */
  app.readValuesHashmap = function (params, callback, failCb) {
      var CordovaParams = [
          params.id
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "HashMapFile", "readValues", CordovaParams);
  }



  //FilePlugin 系列
  /**
   * @desc 1、读取文件内容
   * @input  {  url:"  "  }
   * @Cordova_input   [url]
                      url: 文件路径
   * @output 
   */
  app.readAsString = function (params, callback, failCb) {
      var CordovaParams = [
          params.url
      ]

      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "FilePlugin", "readAsString", CordovaParams);
  }

  /**
   * @desc 2、内容写入文件
   * @input  {  url:"  " , content:""  }
   * @Cordova_input   [url, content]
                      url: 文件路径
                      content: 写入内容
   * @output 
   */
  app.writeAsString = function (params, callback, failCb) {
      var CordovaParams = [
          params.url,
          params.content
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "FilePlugin", "writeAsString", CordovaParams);
  }

  /**
   * @desc 3、删除文件
   * @input  {  url:"  "  }
   * @Cordova_input   [url, content]
                      url: 文件路径
   * @output 
   */
  app.removeFile = function (params, callback, failCb) {
      var CordovaParams = [
          params.url
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "FilePlugin", "removeFile", CordovaParams);
  }

  //DateTimePicker 系列
  /**
   * @desc 1、日期控件
   * @input  {  defaultTime:{ year:2018, month:05, day:28 }, format:"" }
   * @Cordova_input   [defaultTime, format]
                      defaultTime: 
                      format:
   * @output 
   */
  app.datePicker = function (params, callback, failCb) {
      var CordovaParams = [
          params.defaultTime,
          params.format
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "DateTimePicker", "date", CordovaParams);
  }

  /**
   * @desc 2、时间控件
   * @input  {  defaultTime:{ hour:19 , minute:05 }, format:"yyyy-mm-dd" , is24Hours:"" }
   * @Cordova_input   [defaultTime, format, is24Hours]
                      defaultTime: 
                      format:
                      is24Hours:
   * @output 
   */
  app.timePicker = function (params, callback, failCb) {
      var CordovaParams = [
          params.defaultTime,
          params.format,
          params.is24Hours
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "DateTimePicker", "time", CordovaParams);
  }

  //WheelSelectPlugin 系列
  /**
   * @desc 1、日期滚轮
   * @input  {  defaultTime:{ year:2018 , month:05 , day:28 }, format:"" }
   * @Cordova_input   [ defaultTime, format ]
                      defaultTime: 
                      format:
   * @output 
   */
  app.dateWheel = function (params, callback, failCb) {
      var CordovaParams = [
          params.defaultTime,
          params.format,
          params.range
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "WheelSelectPlugin", "date", CordovaParams);
  }

  /**
   * @desc 2、时间滚轮
   * @input  {  defaultTime:{ hour:19, minute:05 },  format:"",  is24Hours：true }
   * @Cordova_input   [ defaultTime, format ]
                      defaultTime: 
                      format:
                      is24Hours:
   * @output 
   */
  app.timeWheel = function (params, callback, failCb) {
      var CordovaParams = [
          params.defaultTime,
          params.format,
          params.is24Hours
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "WheelSelectPlugin", "time", CordovaParams);
  }

  /**
   * @desc 3、滚轮单选
   * @input  {  
   *          data:[{key:"o1",value:"平台"},{key:"o2",value:"电信"}] , //被选择的数据
              selectedKey:"o1" , //默认选中的key
              title:"选择部门",  //标题
              buttons:{ "sureBtn": "确定啦", "cancelBtn": "取消啦" } 
              }
   * @Cordova_input   [ data , selectedKey , title , buttons ]
                      data: 
                      selectedKey:
                      title:
                      buttons:
   * @output 
   */
  app.oneSelectWheel = function (params, callback, failCb) {
      var CordovaParams = [
          params.data,
          params.selectedKey,
          params.title,
          params.buttons
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "WheelSelectPlugin", "oneSelect", CordovaParams);
  }

  //PhonePlugin 系列
  /**
   * @desc 1、发送短信
   * @input  {  phone:"" , message:""  }
   * @Cordova_input   [  phone, message ]
                      phone: 
                      message:
   * @output 
   */
  app.sendMsg = function (params, callback, failCb) {
      var CordovaParams = [
          params.phone,
          params.message
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "PhonePlugin", "sms", CordovaParams);
  }

  /**
   * @desc 2、打电话dial
   * @input  {  phone:""  }
   * @Cordova_input   [  phone  ]
                      phone: 
   * @output 
   */
  app.dial = function (params, callback, failCb) {
      var CordovaParams = [
          params.phone
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "PhonePlugin", "dial", CordovaParams);
  }

  //DialogPlugin 系列
  /**
   * @desc 1、窗口展示图片showPic
   * @input  {  fileUri:"" , mode:""  }
   * @Cordova_input   [ fileUri, mode ]
                      fileUri: 
                      mode: 
   * @output 
   */
  app.showPic = function (params, callback, failCb) {
      var CordovaParams = [
          params.fileUri,
          params.mode
      ]
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "DialogPlugin", "showPic", CordovaParams);
  }

  /**
   * @desc 2、获取当前页面的地址
   * @input  {  }
   * @Cordova_input   []
   * @output 
   */
  app.getCurrentUri = function (callback, failCb) {
      var CordovaParams = [];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Page", "getCurrentUri", CordovaParams);
  }

  /**
   * @desc 3、刷新当前页面
   * @input  {  }
   * @Cordova_input   []
   * @output 
   */
  app.refresh = function (callback, failCb) {
      var CordovaParams = [];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Page", "refresh", CordovaParams);
  }

  //Interactive 系列
  /**
   * @desc 1.	获取地图定位图片
   * @input  
   * @Cordova_input   [  ]
   * @output 
   */
  app.shotMapInfo = function (callback, failCb) {
      var CordovaParams = [];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Interactive", "shotMapInfo", CordovaParams);
  }

  /**
   * @desc 2、获取地图带比例尺信息图片
   * @input  
   * @Cordova_input   [  ]
   * @output 
   */
  app.shotMapZoom = function (callback, failCb) {
      var CordovaParams = [];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Interactive", "shotMapZoom", CordovaParams);
  }

  /**
   * @desc 3、拍摄照片或者小视频
   * @input  { photoQuality:"0" }
   * @Cordova_input   [“0”]，拍摄质量级别，0=低质量、1=中等质量、2=高质量
   * @output      /*takePhoto   类型拍照/小视频，true=拍照，false=小视频
                  path    图片/视频路径
                  videoPreviewImagePath   视频预览图图片路径，拍照时返回空
                  base64Data  图片/视频预览图的base64数据
   */
  app.useCamera = function (params, callback, failCb) {
      var CordovaParams = [
          params.photoQuality
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Interactive", "camera", CordovaParams);
  }
  /**
   * @desc 1、播放视频
   * @input  { path: "" }
   * @Cordova_input   [path]， 需要播放的视频路径
   * @output      
   */
  app.playVideo = function (params, callback, failCb) {
      var CordovaParams = [
          params.path
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Interactive", "playVideo", CordovaParams);
  }

  // XhPaasPlugin 系列
  /**
   * @desc 1.获取网关地址
   * @input  { addr: 0 }
   * @Cordova_input   [0]， 移动网或者公安网代号
   * @output      
   */
  app.getGatewayAddr = function (params, callback, failCb) {
      params = params || {};
      params.addr = params.addr === "jmt" ? 0 : params.addr === "ga" ? 1 : params.addr;
      var CordovaParams = [
          params.addr
      ];
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "XhPaasPlugin", "getGatewayAddr", CordovaParams);
  }

  /**
   * @desc 1.获取本地地址
   * @output      
   */
  app.getIPAddr = function (callback, failCb) {
    var callback = callback || function () {};
    var failCb = failCb || function () {};
    var successCallback = function (result) {
        callback(result);
    };
    var failCallback = function (result) {
        failCb(result);
    };
    Cordova.exec(successCallback, failCallback, "XhPaasPlugin", "getIPAddr", []);
}

  /**
   * @desc NFC功能
   * @params: 整数（1-4）（method=1基础查询，包括姓名、身份证号、有效期限。
      method=2 首先在全项数据库中查询身份证上的所有信息，没有则查询基础信息。
      method=3首先在全项数据库中查询身份证上的所有信息，没有则不查询基础信息，直接交互处理。
      method=4为查询身份证文字信息，包括姓名、性别、民族、出生、住址、身份证号、签发机关、有效期限；不包括照片。没则查询基础信息。
      method=5为查询身份证文字信息，没有则不查询基础信息，直接交互处理。）
   */
  app.nfcReadActivity = function (param,callback,failCb) {
      var param = param || 1;
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "GsgaPlugin", "nfcGetIDInfo", [param]);
  }

  /**
   * @desc 录音功能
   */
  app.captureAudio = function(callback,failCb){
    var callback = callback || function () {};
    var failCb = failCb || function () {};
    var captureSuccess = function(res){
        callback(res);
    };
    var captureError = function(err){
        failCb(err);
    };
    navigator.device.capture.captureAudio(captureSuccess, captureError, {limit: 1});
  }

  /**
   * desc openH5
   * @param {*} 打开h5应用参数(appId,enclosure,md5,version,appEntry,activityName,ip)
   * @param {*} 可传空 param2 mqqt参数  param3 应用名字 
   * @param {*} callback 
   */
  app.openH5 = function(param1,param2,param3,callback){  
    var param2 = param2 || "";
    var param3 = param3 || "";
    var callback = callback || function () {};
    var successCallback = function(res){
        callback(res);
    };
    Cordova.exec(successCallback, null, "Interactive", "openH5", [param1,param2,param3]);
  }

  /**
   * @desc loadWebView方式跳转
   * @param {*} url地址
   * @param {*} 可传空 
   */
  app.loadWebView = function(url,params){
      Cordova.exec(null, null, "Interactive", "loadWebView", [url,params]);
  }

  /**
   * @desc loadApp方式跳转
   * @param {*} url地址
   */
  app.loadApp = function(url,callback,failCb){
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "Page", "loadApp", [url]);
  }
 

   /**
   * @desc openApk打开apk
   * @param {*} 打开apk应用参数(appId,enclosure,md5,version,appEntry,activityName,ip)
   */
  app.openApk = function(param1,param2,callback){
      var param2 = param2 || "";
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "Interactive", "openApk", [param1,param2]);
  }

  /**
   * @desc downloadZip下载apk
   * @param {*} 下载apk应用参数(appId,enclosure,md5,version,appEntry,activityName,ip)
   */
  app.downloadZip = function(params,callback){
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "Interactive", "downloadZip", [params]);
  }

       /**
   * @desc 获取终端ip
   * @param callback 成功回调
   * @param failCb 失败回调
   */
  app.getIPAddr = function( callback, failCb){
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "XhPaasPlugin", "getIPAddr", []);
  }

  /**
   * @desc 获取mac地址
   * @param callback 成功回调
   */
  app.getMac = function( callback){
      var callback = callback || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      Cordova.exec(successCallback, null, "ExtendApp", "getMac", []);
  }

  /**
   * @desc 获取media
   * @param callback 成功回调
   * @param failCb 失败回调
   */
  app.getMeid = function( callback, failCb){
      var callback = callback || function () {};
      var failCb = failCb || function () {};
      var successCallback = function (result) {
          callback(result);
      };
      var failCallback = function (result) {
          failCb(result);
      };
      Cordova.exec(successCallback, failCallback, "ExtendApp", "getMeid", []);
  }

    /**
     * @desc 卸载app弹窗
     * @param callback 成功回调
     * @param failCb 失败回调
     */
    app.showAlertDialog = function( callback){
        var callback = callback || function () {};
        var successCallback = function (result) {
            callback(result);
        };
        Cordova.exec(successCallback, null, "DialogPlugin", "showAlertDialog", ["是否卸载此应用?"]);
    }

     /**
     * @desc 卸载app
     * @param callback 成功回调
     * @param failCb 失败回调
     */
    app.silentUnInstall = function(appEntry,callback){
        var callback = callback || function () {};
        var successCallback = function (result) {
            callback(result);
        };
        Cordova.exec(successCallback, null, "Interactive", "silentUnInstall", [appEntry]);
    }

        /**
     * @desc 跳原生动态
     * @param {*} 
     */
    app.toDynamicMainPage = function(callback,failCb){
        var callback = callback || function () {};
        var failCb = failCb || function () {};
        Cordova.exec(null, null, "DynamicPlugin", "toDynamicMainPage", []);
    }
    
     /**
     * @desc 跳原生动态详情
     * @param {*} 动态详情id
     */
    app.toDynamicDetail = function(msgId,callback,failCb){
        var callback = callback || function () {};
        var failCb = failCb || function () {};
        var onSuccess = function (result) {
            callback(result);
        };
        var onError = function(err){
            console.log(err)
            failCb(err);
        };
        Cordova.exec(onSuccess, onError, "DynamicPlugin", "toDynamicDetail", [msgId]);
    }

      /**
     * @desc 跳原生动态详情
     * @param {*} 动态详情id
     */
    app.getAppDirectoryEntry = function(callback){
        var callback = callback || function () {};
        var onSuccess = function (result) {
            callback(result);
        };
        Cordova.exec(onSuccess, null, "ExtendApp", "getAppDirectoryEntry", []);
    }

    //文件下载，带上手机硬件参数 
    app.downloadFile = function (filePath, uri, success, fail) {
        var fileTransfer = new FileTransfer();

        var successCallback = function (result) {
        success(result);
        };
        var failCallback = function (result) {
        //检测错误返回，进行统一提示。
        checkFailCallback(result);
        fail(result);
        };

        fileTransfer.download(
        uri,
        filePath,
        successCallback,
        failCallback
        );
    }

      /**
     * @desc 打开文件
     * @param {*} 动态详情id
     */
    app.openFile = function(filePath, mime, success, fail) {
		filePath = filePath || "";
		mime = mime || "";
		success = success || function(result) {};
		fail = fail || function(result) {
			app.hint("没有找到合适的程序打开该文件");
		};
		Cordova.exec(success, fail, "ExtendApp", "openFile",[ filePath, mime ]);
	}

       /**
     * @desc 跳转待办
     * @param callback 成功回调
     * @param failCb 失败回调
     */
    app.newPage = function(params, callback, failCb){
        var callback = callback || function () {};
        var failCb = failCb || function () {};
        var successCallback = function (result) {
            callback(result);
        };
        var failCallback = function (result) {
            failCb(result);
        };
        Cordova.exec(successCallback, failCallback, "Page", "newPage", [
            params.appId,
            params.entry,
            params.query,
            params.appName
        ]);
    }

    /**
	 显示提示信息
	 @method app.hint
	 @param message {string} 提示信息
	 @param pasition {string}  提示语位置
	 @example 
		app.hint("Hello,BingoTouch");
	*/
	app.hint = function(message, position) {
		message = message || "Hello!";
		position = position || "bottom";
		Cordova.exec(null, null, "ExtendApp", "hint", [ message, position ]);
    }
  
})(window)