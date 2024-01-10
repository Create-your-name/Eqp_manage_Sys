var isIE = true; //是否IE
var isSecondDev = false; //是否有两个canvas显示视频
var camidMain = 0; //主头ID
var camidSub = 0; //副头ID
var width = 1280;
var height = 960;
var captureType = 1; // 裁边类型 0无裁边 1自动裁边 2手动裁边
var imageType = 0; // 图片保存格式 0jpg 1png 2bmp 3gif  4tif
var colorMode = 0; // 颜色格式 0彩色 1灰色 2黑白
var imagePath = "D:\\checklist";
var imgIndex = 1;

//页面关闭时,停止摄像头,停止身份证读卡
window.onbeforeunload = function(event) {
  StopICWork();
  CloseCam();
};

//加载控件
function loadActiveX() {
  //if (navigator.userAgent.indexOf('MSIE') >= 0) {
  if (!!window.ActiveXObject || "ActiveXObject" in window) {
    isIE = true;
    //IE浏览器加载控件
    document.getElementById("ActiveXDivOne").innerHTML =
      '<OBJECT id="axCam_Ocx"  classid="clsid:D5BD5B4A-4FC0-4869-880B-27EE9869D706" width="1px" height="1px" ></OBJECT>';
    //document.getElementById("ActiveXDivTwo").innerHTML = "<OBJECT id=\"axCam_Ocx2\"  classid=\"clsid:341014BA-CD4A-4918-A11F-8A33B152915A\" width=\"500px\" height=\"400px\" ></OBJECT>";
    imgIndex = 1;
    OcxInit();
  } else {
    isIE = false;
    if (!window.WebSocket) {
      alert("WebSocket not supported by this browser!");
    }
    //其他浏览器加载控件
    document.getElementById("ActiveXDivOne").innerHTML =
      ' <canvas id="cameraCanvas" width="500px" height="400px" style="border:1px solid #d3d3d3;">';
    // document.getElementById("ActiveXDivTwo").innerHTML =" <canvas id=\"cameraCanvasSecond\" width=\"500px\" height=\"400px\" style=\"border:1px solid #d3d3d3;\">";
    WsInit(500, 400, 500, 400, true);
  }
}

//必需重写---获取设备名称(num为当前摄像头数量,strUsbNamr为摄像头名字数组)
function GetDevName(num, strUsbNamr) {}

//必需重写---获取分辨率(data为分辨率数组,每2个为一组,宽高)
function GetDeviceResolution(data) {}

//必需重写---获取分辨率副头(data为分辨率数组,每2个为一组,宽高)
function GetDeviceResolutionSecond(data) {}
//设备1初始化完成,可以进行相关操作
function LoadOver() {
  StartVideo();
}

//设备2初始化完成,可以进行相关操作
function LoadOver2() {
  StartVideo2();
}

//开启摄像头
function StartVideo() {
  StartCam(camidMain, width, height);
  //StartCam(camidSub,1600,1200);

  // 自动爆光
  // funSetAutoExposure();
}

//关闭摄像头
function CloseVideo() {
  CloseCam();
}

//抓图拍照
function Capture(formIndex) {
  //今天的时间
  var day2 = new Date();
  day2.setTime(day2.getTime());
  var s2 = day2.getFullYear() + "." + (day2.getMonth()+1) + "." + day2.getDate();

  // 设置主头裁边模式
  SetCamCutType(captureType);

  //设置图片类型
  SetImageType(imageType);

  //设置图片颜色格式
  SetColorMode(colorMode);

  // 设置保存的文件路径
  SetImagePath(imagePath);

  // 设置文件名，默认使用时间
  SetFileNameCustom(formIndex + "-" + s2 + "-", imgIndex++);

  // 拍摄
  CaptureImage(captureType);
}

//刷新设备
function RefreshDev() {
  CloseCam();
  RefreshDevice();
}

function InfoCallback(op) {
  var text = "";
  if (op == 0) {
    text = "连接成功\r\n";
  } else if (op == 0x01) {
    text = "断开成功\r\n";
  } else if (op == 0x02) {
    text = "设备已经连接\r\n";
  } else if (op == 0x03) {
    text = "设备已经关闭\r\n";
  } else if (op == 0x04) {
    text = "拍照成功\r\n";
  } else if (op == 0x05) {
    text = "pdf添加文件成功\r\n";
  } else if (op == 0x06) {
    text = "pdf保存成功\r\n";
  } else if (op == 0x07) {
    text = "图片合并成功\r\n";
  } else if (op == 0x08) {
    text = "智能连拍启动\r\n";
  } else if (op == 0x09) {
    text = "定时连拍启动\r\n";
  } else if (op == 0x10) {
    text = "定时连拍成功\r\n";
  } else if (op == 0x11) {
    text = "定时连拍关闭\r\n";
  } else if (op == 0x12) {
    text = "文件上传服务器成功\r\n";
  } else if (op == 0x13) {
    text = "水印开启\r\n";
  } else if (op == 0x14) {
    text = "水印关闭\r\n";
  } else if (op == 0x15) {
    text = "此设备属于本公司\r\n";
  } else if (op == 0x16) {
    text = "此设备不属于本公司\r\n";
  } else if (op == 0x17) {
    text = "自动曝光启动\r\n";
  } else if (op == 0x18) {
    text = "自动曝光关闭\r\n";
  } else if (op == 0x19) {
    text = "身份证功能启动成功\r\n";
  } else if (op == 0x1a) {
    text = "身份证功能启动失败\r\n";
  } else if (op == 0x1b) {
    text = "身份证读卡成功\r\n";
  } else if (op == 0x1c) {
    text = "身份证读卡失败\r\n";
  } else if (op == 0x1d) {
    text = "重新操作\r\n";
  } else if (op == 0x1e) {
    text = "未发现模块\r\n";
  } else if (op == 0x1f) {
    text = "未启动身份证功能\r\n";
  } else if (op == 0x20) {
    text = "启动身份证自动读卡\r\n";
  } else if (op == 0x21) {
    text = "关闭身份证自动读卡\r\n";
  } else if (op == 0x22) {
    text = "启动拍照只生成base64\r\n";
  } else if (op == 0x23) {
    text = "关闭拍照只生成base64\r\n";
  } else if (op == 0x25) {
    text = "初始化指纹模块成功\r\n";
  } else if (op == 0x26) {
    text = "初始化指纹模块失败\r\n";
  } else if (op == 0x27) {
    text = "未初始化指纹模块\r\n";
  } else if (op == 0x28) {
    text = "身份证没有指纹数据\r\n";
  } else if (op == 0x29) {
    text = "指纹认证成功\r\n";
  } else if (op == 0x30) {
    text = "开始指纹认证\r\n";
  } else if (op == 0x31) {
    text = "正在指纹认证\r\n";
  } else if (op == 0x32) {
    text = "停止指纹认证\r\n";
  } else if (op == 0x33) {
    text = "指纹认证失败\r\n";
  } else if (op == 0x34) {
    text = "开始录像\r\n";
  } else if (op == 0x35) {
    text = "停止录像\r\n";
  } else if (op == 0x36) {
    text = "正在录像中\r\n";
  } else if (op == 0x37) {
    text = "开始录像副头\r\n";
  } else if (op == 0x38) {
    text = "停止录像副头\r\n";
  } else if (op == 0x39) {
    text = "正在录像中副头\r\n";
  } else if (op == 0x44) {
    text = "建立文件成功\r\n";
  } else if (op == 0x45) {
    text = "建立文件失败\r\n";
  } else if (op == 0x46) {
    text = "人脸识别初始化成功\r\n";
  } else if (op == 0x47) {
    text = "启动人脸对比\r\n";
  } else if (op == 0x48) {
    text = "人脸识别初始化成功\r\n";
  } else if (op == 0x49) {
    text = "主头正在连接中\r\n";
  } else if (op == 0x4a) {
    text = "主头等待连接\r\n";
  } else if (op == 0x4b) {
    text = "副头正在连接中\r\n";
  } else if (op == 0x4c) {
    text = "副头等待连接\r\n";
  } else if (op == 0xa0) {
    text = "没有对应分辨率\r\n";
  } else if (op == 0xa1) {
    text = "pdf没有添加任何文件\r\n";
  } else if (op == 0xa2) {
    text = "文件不存在\r\n";
  } else if (op == 0xa3) {
    text = "意外断开\r\n";
  } else if (op == 0xa4) {
    text = "连接不上\r\n";
  } else if (op == 0xa5) {
    text = "pdf添加文件不是jpg格式\r\n";
  } else if (op == 0xa6) {
    text = "没有发现摄像头\r\n";
  } else if (op == 0xa7) {
    text = "camid无效\r\n";
  } else if (op == 0xa8) {
    text = "图片尺寸太小\r\n";
  } else if (op == 0xa9) {
    text = "文件上传服务器失败\r\n";
  } else if (op == 0xaa) {
    text = "该设备没有副头\r\n";
  } else if (op == 0xab) {
    text = "条码识别失败\r\n";
  } else if (op == 0xac) {
    text = "二维码识别失败\r\n";
  } else if (op == 0xad) {
    text = "图片合并失败\r\n";
  } else if (op == 0xae) {
    text = "设置路径文件不存在\r\n";
  } else if (op == 0xaf) {
    text = "摄像头切换太频繁\r\n";
  } else if (op == 0xb1) {
    text = "未发现指纹模块\r\n";
  } else if (op == 0xb2) {
    text = "录像分辨率不能高于1600*1200\r\n";
  } else if (op == 0xb3) {
    text = "副头录像分辨率不能高于1600*1200\r\n";
  } else if (op == 0xb4) {
    text = "没发现麦克风\r\n";
  } else if (op == 0xb8) {
    text = "人脸识别初始化失败\r\n";
  } else if (op == 0xb9) {
    text = "请读取身份证信息\r\n";
  } else if (op == 0xba) {
    text = "请先初始化人脸识别\r\n";
  } else if (op == 0xbb) {
    text = "没有发现合适的人脸\r\n";
  }
  // var obj = document.getElementById("TextArea1");
  // obj.value = text + obj.value;
}

function InfoTextCallback(type, str) {
  var text = "";
  if (type == 0) {
    // text = "图片路径:" + str + "\r\n";
    // var imgobj1 = document.getElementById("img1");
    // imgobj1.src = "file:///" + str;
    CaptureCallBack(str + "\r\n");
  } else if (type == 1) {
    text = "默认路径:    " + str + "\r\n";
  } else if (type == 2) {
    text = "条码:    " + str + "\r\n";
  } else if (type == 3) {
    text = "文件上传服务器成功:" + str + "\r\n";
  } else if (type == 4) {
    text = "文件上传服务器失败:" + str + "\r\n";
  } else if (type == 5) {
    text = "base64编码成功,请自行处理str\r\n";
    // text ="data:;base64," +str+"\r\n";
    var imgobj1 = document.getElementById("img1");
    imgobj1.src = "data:;base64," + str;
  } else if (type == 6) {
    text = "删除文件成功:" + str + "\r\n";
  } else if (type == 7) {
    text = "二维码:" + str + "\r\n";
  } else if (type == 8) {
    text = "拍照失败:" + str + "\r\n";
  } else if (type == 9) {
    text = "身份证名字:" + str + "\r\n";
  } else if (type == 10) {
    text = "身份证号码:" + str + "\r\n";
  } else if (type == 11) {
    text = "身份证性别:" + str + "\r\n";
  } else if (type == 12) {
    text = "身份证民族:" + str + "\r\n";
  } else if (type == 13) {
    text = "身份证生日:" + str + "\r\n";
  } else if (type == 14) {
    text = "身份证地址:" + str + "\r\n";
  } else if (type == 15) {
    text = "身份证签发机关:" + str + "\r\n";
  } else if (type == 16) {
    text = "身份证有效开始日期:" + str + "\r\n";
  } else if (type == 17) {
    text = "身份证有效截至日期:" + str + "\r\n";
  } else if (type == 18) {
    text = "安全模块号:" + str + "\r\n";
  } else if (type == 19) {
    //身份证头像
    text = "身份证头像base64\r\n";
    var imgobj1 = document.getElementById("img1");
    imgobj1.src = "data:;base64," + str;
  } else if (type == 21) {
    text = "base64编码成功,请自行处理str(副头)\r\n";
    //text ="data:;base64," +str+"\r\n";
    var imgobj1 = document.getElementById("img2");
    imgobj1.src = "data:;base64," + str;
  } else if (type == 22) {
    text = "曝光范围:" + str + "\r\n";
  } else if (type == 23) {
    text = "亮度范围:" + str + "\r\n";
  } else if (type == 24) {
    text = "上传服务器返回:" + str + "\r\n";
  } else if (type == 25) {
    var imgobj1 = document.getElementById("img1");
    imgobj1.src = "data:;base64," + str;
    text = "身份证复印件" + "\r\n";
  } else if (type == 26) {
    text = "当前设备数量:" + str + "\r\n";
  } else if (type == 27) {
    text = "麦克风:" + str + "\r\n";
  } else if (type == 28) {
    text = "人脸抓拍base64编码成功,请自行处理str\r\n";
    // text ="data:;base64," +str+"\r\n";
    var imgobj1 = document.getElementById("img1");
    imgobj1.src = "data:;base64," + str;
  }

  // var obj = document.getElementById("TextArea1");
  // obj.value = text + obj.value;
}

// 拍照回调
function CaptureCallBack(file) {
  var imgobj = document.getElementById("TextArea1");
  imgobj.value = imgobj.value + file;
}
