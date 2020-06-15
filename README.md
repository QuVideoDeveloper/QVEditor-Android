# QVEditor-Android

## Android 剪辑SDK 接入文档

### 一、名词解释
1. 工程：分为剪辑工程（IQEWorkSpace）和卡点视频工程（ISlideWorkSpace），后续统称workspace。其中小影提供的所有剪辑玩法，都是针对剪辑工程进行的操作。卡点视频即使用素材一键生成大片，目前仅支持片段替换和片段排序。
2. 播放流：每个工程会独立的播放流，将播放器View和工程绑定后，即可完成视频流的显示。同时可以对工程的播放器做相关操作。
3. 主题：theme，一系列效果的合集，包括片头、片尾、转场、音乐、滤镜等。对工程设置主题，可以实现模板视频功能。
4. 片段：Clip，片段可以是图片或视频，是工程的基础组成部分。工程将按片段顺序生成一段视频。
5. 转场：Transition, 转场效果是设定在两个片段之间的，是两个片段的切换效果。
6. 滤镜/特效滤镜：Filter/FxFilter，是添加给单个片段的，覆盖整个片段，可以实现调色滤镜、边框滤镜、特效滤镜等。
7. 效果：Effect，贴纸、画中画、字幕、特效、马赛克，都属于效果，是直接在工程上增加的效果。水印也是一种特殊的效果。
8. 图层：Layer，Clip都在同一个图层中，效果和音频可以设置自己的图层，图层的层级大小将影响工程视频的实际效果。详情可以参考【基础结构和概念】中的【图层轨道】一节。
9. 音频：Audio，音频也是一种特殊的效果。分为背景音乐、音效和录音。背景音乐都在同一图层中，即一个时间点不可同时存在多个音频；音效和录音则可以单独设置图层，即一个时间点可以同时存在多个音频。
10. 源文件区间：SrcRange，片段中表示加入片段源文件的起始点和长度，效果中表示效果裁剪的起始点和长度。详情可以参考【基础结构和概念】中的【Range相关】一节。
11. 裁剪区间：TrimRange，裁剪片段的起始点和长度。详情可以参考【基础结构和概念】中的【Range相关】一节。
12. 出入区间：DestRange，效果在工程上的起始点和长度。详情可以参考【基础结构和概念】中的【Range相关】一节。
13. 导出：Export，将工程以指定分辨率、码率、帧速率和压缩格式输出文件。
14. 码率：Bitrate，每秒传送的比特数，码率越高，导出视频质量越好。
15. 帧速率：FPS，每秒刷新图像的帧数，帧速率越高，视频的连续性越好。
16. 素材包：一种资源文件，用于给工程添加效果使用。特地效果有特地的素材包，主题和卡点视频主题也都有素材包。详情可以参考【素材管理】一节。
17. 素材包ID：素材包的唯一标识，安装素材后，可以通过解析素材获取。详情可以参考【素材管理】一节。


### 二、基础结构与概念（要不要写待定）
####  1. 支持格式
* 输入规范：

	视频格式：MP4、MOV、WMV。
	音频格式：MP3、AAC、M4A。
	图片格式：JPG、PNG。
	视频编码：H264、WMV、MPEG4。
	音频编码：MP3、AAC。

* 输出规范：

	视频格式：MP4、MOV
	视频编码：H264
	音频编码：AAC

#### 2. 模块结构
剪辑SDK核心模块包括剪辑工程、片段、音频、效果、播放器等。

剪辑工程是SDK中最基础的模块，它负责生成、保存并维护SDK引擎剪辑的上下文环境。片段是工程的基础，是导出视频的组成元素。效果包括贴纸、画中画、字幕、特效、水印、马赛克等，各种效果、音频和片段共同组合形成最终的视频输出。片段上可以添加各种滤镜，片段之间可以设置不同的转场效果。

<img src="https://github.com/QuVideoDeveloper/QVEditor-Android/blob/master/IMG/image_module.png" width="631" height="655" align="center">

#### 3. 图层轨道
效果和音频可以在指定区间设置自己的图层（水印和背景音乐除外），高图层的效果可以对低图层的效果起作用或遮挡低图层效果。当两个效果在同一图层时，如果出入点时间不覆盖，则不互相影响；如果出入点时间覆盖，则覆盖时间区间的效果将无法预期。所以尽量给每个效果设定独立的图层，以免最终视频效果不符合预期。

图层限制区间：
	音效/录音图层：[10,10000)，左边闭区间，右边开区间。
	效果图层：[10000,1000000)，左边闭区间，右边开区间。(贴纸、字幕、画中画、特效、马赛克)

例：
	1）贴纸1在图层100000，贴纸2在图层90000，如果贴纸1和贴纸2的位置和时间相同时，则贴纸1会遮挡贴纸2。
	2）特效1在图层100000，贴纸1在图层90000，贴纸2在图层110000，如果特效1、贴纸1和贴纸2的时间相同，则特效对贴纸1产生影响，不对贴纸2产生影响。

#### 4. 区间Range相关：

  srcRange:：源文件区间，视频源文件选择的时间区间。
  trimRange：裁剪区间，裁剪片段的起始点和长度。
  destRange：出入区间，效果在工程上的起始点和长度。


<img src="https://github.com/QuVideoDeveloper/QVEditor-Android/blob/master/IMG/image_range.png" width="637" height="441" align="center">

#### 5. 坐标系
剪辑中使用的坐标系，统一使用视频流(stream)的坐标系，即视频流的左上角为（0, 0），右下角为（stream.width，stream.height）。角度水平向右为0度，顺时针为增大。


<img src="https://github.com/QuVideoDeveloper/QVEditor-Android/blob/master/IMG/image_xyz.png" width="574" height="542" align="center">



#### 6. 剪辑操作符
由于剪辑需要始终保持单线程操作，所以在工程剪辑时，我们将每个操作定义为一个剪辑操作符BaseOperate。剪辑操作符有sdk已经预设的大量操作符，开发者也可以自行组合实现新的操作符。执行时，开发者只需要创建操作符，并将操作符交给workspace执行即可，接下来就是等待执行完成的回调。
```
BaseOperate operate = new BaseOperate();
workspace.handleOperation(operate)；
```
为满足开发者可能存在的特殊需求，操作符也可以进行同步操作，使用代码：
```
BaseOperate operate = new BaseOperate();
workspace.syncOperation(operate)；
```
由于工程操作时，必须保证只有单个操作正在执行，所以操作符建议只使用一种方式，或者在异步操作完成时，再进行同步操作。
需要注意的是，同步操作暂不支持【高级玩法-对撤销/重做的支持】，强烈建议尽量使用操作符的异步编辑方式。以免出现不必要的并发问题。


### 三、项目搭建

 为了方便开发者进行开发，可以先下载适用于Android Studio的Demo代码。强烈建议使用Android Studio(http://developer.android.com/sdk/index.html)进行开发。

#### 1. 前期准备
请向趣维公司申请允许使用剪辑SDK的license文件，该文件具备有效期。如有升级需要，请自行实现更新系统。

对剪辑SDK文档中的名词解释和基础结构与概念先做了解。

#### 2. 创建一个Android Studio工程
1）剪辑SDK使用的minSdkVersion是21。为了保证兼容性，请在创建工程时将minSdkVersion设置为21。

打开Android Studio，新建工程如下，填写Application name和Company Domain。点击Next。将minimum SDK更改为API 21:Android 5.0(Lollipop)。点击Finish。


<img src="https://github.com/QuVideoDeveloper/QVEditor-Android/blob/master/IMG/image_create.png" width="633" height="474" align="center">


2）在项目根目录的build.gradle文件中，添加配置

```
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url 'https://dl.bintray.com/quvideo/release'
        }
    }
}
```

3）在app目录的build.gradle中，添加配置，ndk可以根据实际需要选择配置，以降低APK包体大小，当前支持armeabi、armeabi-v7a、arm64-v8a。

```
android {
    defaultConfig {
        ndk {
	        // 按需选择，以降低APK包体大小
            abiFilters "armeabi", "armeabi-v7a", "arm64-v8a"
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    sourceSets {
	    main {
	      jniLibs.srcDirs = ['libs']
	    }
	}
  }
}

dependencies {
    //剪辑SDK
    implementation "com.quvideo.mobile.external:sdk-engine:1.0.17"
}
```

4）由于android文件系统权限在targetSdkVersion = 29之后有分区和沙盒概念，sdk对此暂无兼容。所以如果targetSdkVersion设置的是29以上，请在app的AndroidManifest.xml文件的application中，新增以下设置
```
    android:requestLegacyExternalStorage="true"
```

#### 3. 剪辑SDK初始化
在开始使用剪辑功能前，必须对剪辑SDK初始化
```
QEInitData.Builder builder = new QEInitData.Builder(licensePath);
QEEngineClient.init(context, builder.build());
```

QEInitData参数说明：
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| licensePath | license文件路径地址 | String | 必须 |
| projectDir | 剪辑工程文件存放路径，默认存放剪辑工程的地址，删除APP或清除用户数据时会被清除 | number | 非必须 |
| hwCodecCapPath | 设备软硬件配置文件 | string | 非必须 |
| corruptImgPath | clip错误时显示图片地址 | string | 非必须 |
| isUseStuffClip | 是否末尾补黑帧,默认false（详解【高级玩法-自由黑帧模式】一章说明） | boolean | 非必须 |
| iTextPrepareListener | 默认文本宏替换数据 | ITextPrepareListener | 非必须 |

### 四、素材管理开发接入
#### 1. 素材安装
同一个素材，只需要安装一次即可，后续直接通过素材id即可查询素材信息。

注意，assets目录下的素材安装，每次需要完整列表重新安装，会进行增删处理。对于asset目录下的素材，升级时如有需要，可以完整重新安装一次，用于素材变更。
```
/** 安装单个素材文件,zip包或者xyt文件 */
XytManager.install(xytZipPath, xytInstallListener);

/** List<String> xytZipPaths,zip包或者xyt文件列表 */
XytManager.install(xytZipPaths, xytInstallListener);

/** 安装多个asset目录下的素材文件，只能是xyt文件列表 */
XytManager.installAsset(assetPathList, xytInstallListener);

/** 卸载单个素材，只能是xyt文件列表，ttid为素材id */
XytManager.unInstall(ttid, xytInstallListener);
```

XytInstallListener接口信息：
```
public interface XytInstallListener {

  void onSuccess();

  void onFailed(int errorCode);
}
```

#### 2. 素材信息查询
```
/**
* 通过素材id查询素材信息
*/
XytInfo xytInfo = XytManager.getXytInfo(ttidLong);

/**
* 通过素材路径查询素材信息
*/
XytInfo xytInfo = XytManager.getXytInfo(xytPath);
```

XytInfo参数说明：

| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| ttidLong | 素材id| long |
| ttidHexStr | 素材id的十六进制 如：0x06000000000000D9| String |
| filePath | 素材路径 | String |
| title | 素材名称 | String |


#### 3. 素材工具
```
 /**
   * Long转成16进制ttid字符串
   * 如：1225031875203433521-->"0x1100300000080431"
   */
  String hexStr = XytManager.ttidLongToHex(ttidLong);

  /**
   * 16进制ttid转成Long
   * 如：""0x1100300000080431"-->1225031875203433521
   */
   long ttid = XytManager.ttidHexStrToLong(ttidHexStr);

  /**
   * 是否mv主题
   */
  boolean isMvTheme = XytManager.isMVTheme(ttidLong);

  /**
   * 获取主题上配置的 获取封面的最佳时间点
   */
  int timePos = XytManager.getThemeCoverPos(themePath);

  /**
   * 获取转场配置时长
   */
  int duration = XytManager.getTranDuration(transPath);

  /**
   * 获取转场时长是否可编辑
   */
  boolean isEditable = XytManager.getTranEditable(transPath);

```


### 五、录制功能开发接入
#### 1. 录制视频
1）初始化。

创建XYCamreaEngine实例。
```
XYCameraEngine mXYCamera = new XYCameraEngine(activity, screenSize, ICameraEventCallback);
```
ICameraEventCallback说明：
```
public interface ICameraEventCallback {

  /**
   * 拍照完成
   * @param filePath 文件路径
   */
  void onCaptureDone(String filePath);

  /**
   * 录制中回调
   * @param duration 录制时长
   */
  void onRecorderRunning(long duration);

  /**
   * 停止录制
   */
  void onRecorderStop(WorkThreadTaskItem workThreadTaskItem);

  /**
   * 暂停录制
   */
  void onRecorderPaused();

  /**
   * 录制准备完成
   */
  void onRecorderReady();

  /**
   * 录制时长溢出
   */
  void onRecorderDurationExceeded();

  /**
   * 录制文件大小溢出
   */
  void onRecorderSizeExceeded();

  /**
   * 人脸检测结果
   * 暂时不可用
   * @param isDetected 是否检测到人脸
   */
  void onFaceDetectResult(boolean isDetected);

  /**
   * 相机连接结果
   * @param isConnected 是否成功
   */
  void onConnectResult(boolean isConnected);

  /**
   * 相机断开连接
   */
  void onDisConnect();

  /**
   * 开始预览
   */
  void onPreviewStart();

  /**
   * 停止预览
   */
  void onPreviewStop();

  void onPipSrcObjEnd();

  void onPasterDisplayStatusChanged(QExpressionPasterStatus status);
}
```


2）搭建Camera预览

初始化Preview
需要传入FrameLayout作为SurfaceView的父布局，内部会自动创建SurfaceView  add到FrameLayout中
```
mXYCamera.initPreview(mSurfaceContainer);
```

连接Camera
```
mXYCamera.openCamera();
```

注意：连接Camera之前需要确保已申请过Camera权限，如果没有需要向用户申请
```
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

启动预览
在Camera连接成功的回调中启动预览
```
@Override public void onConnectResult(boolean isConnected) {
  if (isConnected) {
    mXYCamera.setDeviceIsPortrait(true,
XYCameraConst.CameraDegrees.DEGREES_PORTRAIT);
    // 启动预览
    mXYCamera.startPreview();
  }
}
```

关闭Camera
```
mXYCamera.closeCamera();
```

停止预览，在Camera断开连接的回调中停止预览
```
@Override public void onDisConnect() {
  mXYCamera.stopPreview();
}
```

3) Camera 录制

开始录制需要传递XYRecorderParam参数，该参数可以自定义一些录制参数。
```
mXYCamera.startRecording(new      XYRecorderParam(filePath,mXYCamera.getOutPutSize(),mXYCamera.getCurCameraId() == XYCameraConst.CameraId.CAMERA_FRONT));
```

暂停录制会返回当次录制的start位置与end位置，这样可以知道本次录制在mp4文件中的range
```
int[] range = mXYCamera.pauseRecording();
```

继续录制
```
mXYCamera.resumeRecording();
```

停止录制
停止录制同样会返回当次录制的start位置与end位置，如果在当前本来就是暂停录制状态，则可以忽略该range
```
int[] range = mXYCamera.stopRecording();
```

设置录制方向
需要固定Activity的屏幕方向android:screenOrientation="portrait"
如果需要横屏录制，可以用以下接口设置。参考XYCameraConst.CameraDegrees
```
mXYCamera.setDeviceIsPortrait(true, XYCameraConst.CameraDegrees.DEGREES_PORTRAIT);
```

4) Camera设置

切换镜头，参数参见XYCameraConst. CameraId
```
mXYCamera.switchCameraId(XYCameraConst.CameraId.CAMERA_FRONT);
```

闪光灯，参数参见XYCameraConst.FlashMode
```
mXYCamera.getCameraDevice().setFlashMode(XYCameraConst.FlashMode.FLASH_TORCH);
```

对焦
```
mXYCamera.getCameraDevice().autoFocus(new Camera.AutoFocusCallback() {
  @Override public void onAutoFocus(boolean success, Camera camera) {

  }
});
```

焦距调节
```
mXYCamera.getCameraDevice().setCameraZoom(zoomValue);
```
焦距范围0～max
```
mXYCamera.getCameraDevice().getCameraZoomMax();
```

曝光调节
```
mXYCamera.getCameraDevice().setCameraExposure(value);
```
曝光参数获取
```
mXYCamera.getCameraDevice().getCameraExposureStep();
mXYCamera.getCameraDevice().getCameraExposureMin();
mXYCamera.getCameraDevice().getCameraExposureMax();
```

比例调节
参数参见XYCameraConst.RatioMode，第2个参数为距离屏幕上边的距离，用于调节SurfaceView的区域
```
mXYCamera.setRatio(XYCameraConst.RatioMode.RATIO_4_3, 200);
```

其他Camera设置，可以使用Camera Parameters来设置
```
mXYCamera.getCameraDevice().getParameters();
mXYCamera.getCameraDevice().setParameters();
```

5) Camera预览效果设置

滤镜设置
```
mXYCamera.setEffect(effectPath);
```

美颜设置
开启美颜，value为默认指。范围为0～100
```
mXYCamera.initFaceBeautyMode(value);
```
美颜参数调节
```
mXYCamera.setFaceBeautyParam(value);
```
关闭美颜
```
mXYCamera.clearFaceBeautyParam();
```

6)  拍照

传入照片保存路径。
注意：该照片分辨率为preview size。
```
mXYCamera.takePicture(filePath);
```
接收拍照完成回调
```
@Override public void onCaptureDone(String filePath) {
}
```

7) 音乐镜头
音乐镜头需要结合开发者晚饭，实现逻辑如下：
- 使用MediaPlayer加载音乐
- 在开始录制时，同时播放音乐，录制过程中，音乐会同时录制进去
- 选取一段音乐就是加载音乐后，seek到start位置，并在end位置停止录制。
详情参见demo中的实现逻辑


#### 2. 音频录制

初始化
```
XYAudioRecorder.init()
```

开始录制
```
// audioFilePath表示录音文件路径
XYAudioRecorder.startRecord(audioFilePath);
```

停止录制
```
XYAudioRecorder.stopRecord();
```

获取录制时长
```
XYAudioRecorder.getRecordDuration()
```

### 六、剪辑工程功能开发接入
#### 1. 剪辑工程
##### 创建和加载
```
  /**
   * 创建新的工程
   */
  QEEngineClient.createNewProject(QEWorkSpaceListener)
  /**
   * 加载工程
   */
  QEEngineClient.loadProject(String url, QEWorkSpaceListener qeWorkSpaceListener);

```

##### 工程删除
方式一:
```
  /**
   * 删除工程
   */
  QEEngineClient.deleteProject(String projectPath)
```
方式二：
```
  /**
   * 删除工程
   */
  IQEWorkSpace.deleteProject(String projectPath)
```

##### 工程释放
工程编辑结束，需要完整释放工程。
```
  /**
   * 释放工程缓存
   */
  IQEWorkSpace.destory()
```
如果只是想临时释放工程注册的各项监听器，则调用：
```
  /**
   * 销毁播放器和注册的监听器，播放器监听和操作队列监听
   * 当需要重新使用时，需要重新绑定播放器和各种监听器
   */
  IQEWorkSpace.unbindUI()
```

#### 2. 播放器
1）在Activity的layout中添加播放器View
```
  <com.quvideo.mobile.engine.player.EditorPlayerView
      android:id="@+id/editor_play_view"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      />
```
2）在工程加载成功后，可以绑定工程和播放器
```
 //
 // initTime为初始博翻墙需要定位的时间点，默认0即可
 mWorkSpace.getPlayerAPI().bindPlayerView(editorPlayerView, initTime);
```
3）获取播放器控制器
```
IPlayerController playerController = mWorkSpace.getPlayerAPI().getPlayerController();
```
IPlayerController 说明：
```
public interface IPlayerController {
  /** 是否播放中 */
  boolean isPlaying();
  /** 是否暂停 */
  boolean isPause();
  /** 是否停止 */
  boolean isStop();
  /** 播放 */
  int play();
  /** 暂停 */
  int pause();
  /** 播放或暂停切换 */
  int playOrPause();
  /** 设置音量 */
  int setVolume(int volume);
  /** 异步seek到时间点 */
  void seek(int time);
  /** 异步seek到时间点，playAfterSeek为seek后自动播放 */
  void seek(int time, boolean playAfterSeek);
  /** 同步seek到时间点 */
  int synSeek(int time);
  /** 获取当前时间 */
  int getCurrentPlayerTime();
  /** 获取播放器总时长 */
  int getPlayerDuration();
  /** 设置播放区域 */
  int setPlayRange(int start, int length);
  /** 获取播放区域 */
  VeRange getPlayerRange();
}
```

4）注册播放器监听器
注册：
```
mWorkSpace.getPlayerAPI().registerListener(QEPlayerListener);
```
注销：
```
mWorkSpace.getPlayerAPI().unregisterListener(QEPlayerListener);
```
QEPlayerListener说明：
```
public interface QEPlayerListener {

  enum PlayerStatus {
    STATUS_READY,
    STATUS_PLAYING,
    STATUS_SEEKING,
    STATUS_PAUSE,
    STATUS_STOP
  }

  /** 播放器状态回调 */
  void onPlayerCallback(PlayerStatus playerStatus, int progress);
  /** 播放器刷新 */
  void onPlayerRefresh();
  /** 播放器尺寸变化 */
  void onSizeChanged(Rect resultRect);
}
```

5) 关于PlayerAPI

通过mWorkSpace.getPlayerAPI()可以获取PlayerAPI播放器相关接口
```
public interface PlayerAPI {
  /** 绑定播放器 */
  void bindPlayerView(EditorPlayerView editorPlayerView, int initTime);
  /** 注册播放器监听 */
  void registerListener(QEPlayerListener listener);
  /** 注销播放器监听 */
  void unregisterListener(QEPlayerListener listener);
  /** 获取播放器控制器 */
  IPlayerController getPlayerControl();
  /** 获取预览区尺寸 */
  VeMSize getSurfaceSize();
  /** 获取播放器view尺寸 */
  VeMSize getPreviewSize();
}
```

#### 3. 获取剪辑工程信息
##### 获取工程相关信息
```
  /** 获取工程路径 */
  mWorkSpace.getProjectUrl();
  /** 获取工程目录 */
  mWorkSpace.getProjectDir();
  /** 获取更多工程信息 */
  StoryboardAPI storyboardAPI = mWorkSpace.getStoryboardAPI();
  /** 获取片段信息 */
  ClipAPI clipAPI = mWorkSpace.getClipAPI();
  /** 获取效果信息 */
  EffectAPI effectAPI = mWorkSpace.getEffectAPI();
```
StoryboardAPI信息:
```
public interface StoryboardAPI {
  /** 获取分辨率 */
  VeMSize getStreamSize();

  /** 获取总时长 */
  int getDuration();

  /** 获取主题id */
  long getThemeId();

  /** 获取是否mv主题相册工程 */
  boolean isMVProject();

  /** 获取主题片头 */
  ClipData getCover();

  /** 获取主题片尾 */
  ClipData getBackCover();

  /** 获取主题字幕列表 */
  List<ThemeSubtitleEffect> getThemeTitleInfoList();
}
```

ClipAPI信息:
```
public interface ClipAPI {

  /** 获取所有clip信息 */
  List<ClipData> getClipList();

  /** 获取指定位置的clip信息 */
  ClipData getClipByIndex(int clipIndex);
}
```

EffectAPI信息:
```
public interface EffectAPI {
  /** 获取所有效果信息并按groupid分组 */
  SparseArray<List<BaseEffect>> getAllEffect();

  /** 获取groupid的所有效果信息 */
  List<BaseEffect> getEffectList(int groupId);

  /** 根据groupId和effectIndex所有获取效果信息 */
  BaseEffect getEffect(int groupId, int effectIndex);
}
```
备注：由于StoryboardAPI、ClipAPI和EffectAPI返回的数据都是clone数据，所有直接对返回的数据修改，是不起作用的。

##### 效果分类groupId说明
```
public class QEGroupConst {
  /** 背景音乐 */
  public static final int GROUP_ID_BGMUSIC = 1;
  /** 音效 */
  public static final int GROUP_ID_DUBBING = 4;
  /** 录音 **/
  public static final int GROUP_ID_RECORD = 11;
  /** 字幕 */
  public static final int GROUP_ID_SUBTITLE = 3;
  /** 特效, 和特效滤镜不同 */
  public static final int GROUP_ID_STICKER_FX = 6;
  /** 贴纸 */
  public static final int GROUP_ID_STICKER = 8;
  /** 画中画 */
  public static final int GROUP_ID_COLLAGES = 20;
  /** 马赛克 */
  public static final int GROUP_ID_MOSAIC = 40;
  /** 自定义水印 */
  public static final int GROUP_ID_WATERMARK = 50;
}
```

##### 数据结构说明
1) 片段Clip相关


ClipData参数说明：
| 名称 | 解释 | 类型 |
| :-: | :-: | :-: |
| uniqueId | clip的唯一识别码 | String |
| mType | 类型{@see ClipData.ClipType}  | ClipType |
| mClipFilePath | 片段文件路径 | String |
| isVideo | 是否视频 | boolean |
| srcRange | 源文件区间 | VeRange |
| trimRange | 片段裁切区间 | VeRange |
| destRange | 片段出入区间 | VeRange |
| cropRect | 裁剪区域 | Rect |
| sourceSize | 源视频宽高，相对streamSize的尺寸 | VeMSize |
| rotateAngle | 旋转角度 | int |
| isMute | 是否静音 | boolean |
| audioVolume | 音量，默认100 | int |
| soundTone | 变声，-60~60，正常0。{@see QEDftSoundTone}类中有提供的特定音调 | float |
| timeScale | 变速值，默认1.0f，设置变速时，也会对音调产生影响 | float |
| mirror | 镜像{@see ClipData.Mirror} | Mirror |
| bReversed | 是否倒放 | boolean |
| isPicAnimOn | 是否开启图片动画，只允许对图片clip设置 | boolean |
| crossInfo | 转场，null表示无。当前片段和下一个片段的转场数据{@see CrossInfo} | CrossInfo |
| filterInfo | 滤镜信息，null表示无{@see FilterInfo} | FilterInfo |
| fxFilterInfo | 特效滤镜信息，null表示无{@see FxFilterInfo} | FxFilterInfo |
| mParamAdjust | 参数调节信息{@see ParamAdjust} | ParamAdjust |
| mClipPosInfo | 片段位置信息{@see ClipPosInfo} | ClipPosInfo |
| mClipBgData | 片段背景信息{@see ClipBgData} | ClipBgData |


ClipData.ClipType参数说明：
| 名称  | 解释 |
| :-: | :-: |
| NORMAL | 正常clip |
| THEME_COVER | 主题片头 |
| THEME_BACKCOVER | 主题片尾 |

ClipData.Mirror参数说明：
| 名称  | 解释  |
| :-: | :-: |
| CLIP_FLIP_NONE | 正常 |
| CLIP_FLIP_X | 沿X方向镜像 |
| CLIP_FLIP_Y | 沿Y方向镜像 |
| CLIP_FLIP_XY | 沿XY方向镜像 |

CrossInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| crossPath | 转场路径 | String |
| duration | 转场时长 | int |
| cfgIndex | 转场效果样式，有些素材包含多种效果，表示使用第几个效果，默认0 | int |

FilterInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| filterPath | 滤镜路径 | String |
| filterLevel | 滤镜程度,0~100 | int  |

FxFilterInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| filterPath | 特效滤镜路径 | String |

ParamAdjust参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| luminance | 亮度,0~100,默认50 | int |
| contrast | 对比度,0~100,默认50 | int  |
| saturation | 饱和度,0~100,默认50 | int|
| sharpness | 锐度,0~100,默认50 | int |
| colourTemp | 色温,0~100,默认50 | int |
| vignette | 暗角,0~100,默认50| int |
| hue | 色调,0~100,默认50| int |
| shadow | 阴影,0~100,默认50| int |
| highlight | 高光,0~100,默认50| int |
| fade | 褪色,0~100,默认0| int |

ClipPosInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| centerPosX | 中心点-X，在streamSize的坐标系中 | int |
| centerPosY | 中心点-Y，在streamSize的坐标系中 | int  |
| widthScale | 宽放大倍数，默认1 | float|
| heightScale | 高放大倍数，默认1 | float |
| degree | 旋转角度，0~359度 | float |

ClipBgData参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| clipBgType | 背景类型 | ClipBgType |
| colorArray | color,最多可以支持三色渐变 | int[] |
| colorAngle | 颜色渐变角度：默认0-水平方向。0~360 | int |
| blurLen | 模糊程度：0~100 | int |
| imagePath | 图片背景，自定义图片背景使用 | String |

ClipBgData.ClipBgType参数说明：
| 名称  | 解释  |
| :-: | :-: |
| BLUR | 模糊背景 |
| COLOR | 纯色背景 |
| PICTURE | 图片背景 |

2) 效果Effect相关

效果类继承结构：

<img src="https://github.com/QuVideoDeveloper/QVEditor-Android/blob/master/IMG/image_effect.png" width="633" height="261" align="center">


BaseEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| uniqueId | effect的唯一识别码 | String |
| groupId | 效果Group分类id | int|
| mEffectPath | 效果素材文件路径 | String |
| isApplyByTheme | 是否主题添加 | boolean |
| srcRange | 源文件区间 | VeRange |
| trimRange | 效果裁切区间 | VeRange |
| destRange | 效果出入区间 | VeRange |
| effectLayerId | 效果图层id，数字越大 层级越高 | float |
| isHadAudio | 效果是否带有音频数据 | boolean |
| audioVolume | 音量 | int |

AudioEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| mAudioInfo | 音频数据信息 {@see EffectAudioInfo} | EffectAudioInfo |

EffectAudioInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| isRepeat | 是否循环,默认开始 | boolean |
| soundTone | 变声，-60~60，正常0。{@see QEDftSoundTone}类中有提供的特定音调 | float |
| audioFadeIn | 渐入，只对背景音乐有效 {@see AudioFade} | AudioFade |
| audioFadeOut | 渐出，只对背景音乐有效 {@see AudioFade} | AudioFade |
| audioLyric | 歌曲字幕信息 {@see AudioLyric} | AudioLyric |
| musicMsg | 音乐信息,开发者可以用于存储音乐相关的信息 | String |

AudioFade参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| type | 渐入渐入类型 {@see AudioFade.Type} | AudioFade.Type |
| duration | 渐变时长,0则无效果 | int |

AudioFade.Type参数说明：
| 名称  | 解释  |
| :-: | :-: |
| FadeIn | 渐入 |
| FadeOut | 渐出 |

AudioLyric参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| lyricPath | 歌曲字幕lyric文件路径 | string |
| lyricTtid | 歌词模板的素材id | long |

FloatEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| alpha | 透明度 0~100 | int |
| anchor | 锚点,(0,0)为效果的左上角位置，（0.5，0.5）表示效果的中心，（1.0，1.0）表示效果的右下角。默认是(0.5,0.5) 。取值范围是0~1 | PointF |
| mEffectPosInfo | 效果位置数据信息 {@see EffectPosInfo} | EffectPosInfo |


EffectPosInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| centerPosX | 中心点-X，在streamSize的坐标系中 | float |
| centerPosY | 中心点-Y，在streamSize的坐标系中 | float |
| width | 宽，在streamSize的坐标系中 | float |
| height | 高，在streamSize的坐标系中 | float |
| degree | 旋转角度， 0~360 | float |
| isHorFlip | 水平反转 | boolean |
| isVerFlip | 垂直反转 | boolean |


AnimEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| mEffectOverlayInfo | 混合模式信息数据 {@see EffectOverlayInfo} | EffectOverlayInfo |
| mEffectMaskInfo | 蒙版位置信息数据 {@see EffectMaskInfo} | EffectMaskInfo |
| mEffectChromaInfo | 抠色信息数据（绿幕） {@see EffectChromaInfo} | EffectChromaInfo |
| mFilterInfo | 滤镜信息数据 {@see FilterInfo} | FilterInfo |
| mParamAdjust | 参数调节信息数据 {@see ParamAdjust} | ParamAdjust |
| mEffectSubFxList | 子特效列表信息数据 {@see EffectSubFx} | EffectSubFx |
| keyFrameRanges | 关键帧信息数据 {@see EffectKeyFrameInfo}（由于功能复杂，后期可能调整数据结构） | EffectKeyFrameInfo |



EffectOverlayInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| overlayPath | 混合模式素材路径 | String |
| level | 混合程度，改参数和透明度一个效果,0~100 | int |


EffectMaskInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| maskType | 蒙版类型{@see EffectMaskInfo.MaskType} | MaskType |
| centerX | 中心点-X，在streamSize的坐标系中，中心点尽量保持在素材位置内 | float |
| centerY | 中心点-Y，在streamSize的坐标系中，中心点尽量保持在素材位置内 | float |
| radiusY | 垂直方向半径，在streamSize的坐标系中 | float |
| radiusX | 水平方向半径，在streamSize的坐标系中 | float |
| rotation | 旋转角度， 0~360 | float |
| softness | 羽化程度，取值范围：[0~10000] | int |
| reverse | 是否反选 | boolean |


EffectMaskInfo.MaskType
| 名称  | 解释 |
| :-: | :-: |
| MASK_NONE | 无蒙版 |
| MASK_LINEAR | 线性蒙版 |
| MASK_MIRROR | 镜像蒙版 |
| MASK_RADIAL | 径向蒙版 |
| MASK_RECTANGLE | 矩形蒙版 |


EffectChromaInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| enable | 是否开启 | boolean |
| color | 抠色的颜色值, 如0xFFFFFF | int|
| accuracy | 抠色的精度（0~5000） | int|


EffectSubFx参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| subFxPath | 子特效素材路径 | String |
| subType | 子特效索引，不可修改 | 1000~2000 |
| destRange | 子特效出入点区间，相对效果的时间 | VeRange |


EffectKeyFrameInfo参数说明：（由于功能复杂，后期可能调整数据结构）
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| relativeTime | 相对时间，相对于效果入点时间 | int |
| centerX | 中心点x，在streamSize的坐标系中 | int |
| centerY | 中心点y，在streamSize的坐标系中 | int |
| widthRatio | 宽缩放倍率 | float |
| heightRatio | 高缩放倍率 | float |
| rotation | 旋转角度 | int |


MosaicEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| mosaicInfo | 马赛克模糊程度数据信息 {@see MosaicInfo} | MosaicInfo |

MosaicInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| horValue | 水平模糊程度 | int |
| verValue | 垂直模糊程度 | int |

SubtitleEffect参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| textBubbleInfo | 字幕数据信息 {@see TextBubbleInfo} | TextBubbleInfo |

TextBubbleInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| bSupportAnim | 是否支持动画 | boolean |
| isAnimOn | 是否开启字幕动画 | boolean |
| isDftTemplate | 是否默认字幕 | boolean |
| dftDuration | 字幕默认时长 | int |
| mTextBubbleList | 字幕数据信息 {@see TextBubble} | List(TextBubble) |


TextBubble参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| bSupportAnim | 是否支持动画 | boolean |
| isAnimOn | 是否开启字幕动画 | boolean |
| isDftTemplate | 是否默认字幕 | boolean |
| mDftText | 默认文字 | String |
| mTextColor | 颜色，如0xFFFFFFFF,即ARGB | int |
| mText | 文字 | String |
| mTextAlignment | 对齐方式，在{@class TextBuble} 中定义，可以进行 '|' 位运算合并。 | int |
| mFontPath | 字体文件路径 | String |
| mDftTextColor | 默认颜色，如0xFFFFFFFF,即ARGB | int |
| mShadowInfo | 字幕阴影信息 {@see ShadowInfo} | ShadowInfo |
| mStrokeInfo | 字幕描边信息 {@see StrokeInfo} | StrokeInfo |

ShadowInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| enable | 是否开启阴影 | boolean |
| shadowXShift | 横向阴影偏移，百分比小数值，0表示无偏移，0~1，不可小于0 | float |
| shadowYShift | 垂直阴影偏移，百分比小数值，0表示无偏移，0~1，不可小于0 | float |
| shadowColor | 阴影颜色，如0xAA000000,即ARGB | int |
| shadowBlurRadius | 阴影宽度，百分比小数值，0表示无阴影，0~1，不可小于0 | float |

StrokeInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| strokeWPersent | 描边，百分比小数值,0表示无描边,1表示描边宽度和文字高度相同，不可小于0 | float |
| strokeColor | 描边的颜色，如0xFFFFFFFF,即ARGB | int |

#### 4. 主题剪辑功能接口
由于剪辑操作都是在线程中异步进行的，所以操作结束后，如果需要获取成功失败信息。可以向工程注册剪辑操作监听器。
```
  /** 注册操作符监听器 */
	mWorkSpace.addObserver(BaseObserver);
```
不再需要监听时，可以取消监听：
```
  /** 注销操作符监听器 */
	mWorkSpace.removeObserver(BaseObserver);
```
BaseObserver说明：
```
public interface BaseObserver {

  void onChange(BaseOperate operate);
}
```
BaseOperate说明：
```
// 是否操作成功
operate.success();
```


1）应用/切换主题
```
	// themePath表示主题素材路径
	ThemeOPApply themeOPApply = new ThemeOPApply(themePath);
	mWorkSpace.handleOperation(themeOPApply);
```

2）恢复主题背景音乐设置
```
	// 该操作将删除所以背景应用，切回主题自带的背景音乐
	ThemeOPBgmReset themeOPBgmReset = new ThemeOPBgmReset();
	mWorkSpace.handleOperation(themeOPBgmReset);
```


3）修改主题关联字幕文本
```
	// themeSubtitleEffect表示需要修改的主题字幕，是通过工程获取到的信息 {@see ThemeSubtitleEffect}
	// text表示修改的文本信息
	ThemeOPSubtitleText themeOPSubtitleText = new ThemeOPSubtitleText(themeSubtitleEffect, text);
	mWorkSpace.handleOperation(themeOPSubtitleText);
```

ThemeSubtitleEffect参数说明：
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| mGroupType | 字幕类型 | GroupType | 必须 |
| mIndex | 字幕在类型中的排序位置 | int | 必须 |
| destRange | 切入切出区间 | VeRange | 必须 |
| mText | 字幕文本 | String | 必须 |
| effectPosInfo | 字幕位置信息 | EffectPosInfo | 必须 |

#### 5. Clip剪辑功能接口
1）添加
```
	ArrayList<ClipAddItem> list;
	// clipIndex为clip添加的位置，0为第一个
	// list需要添加的clip列表
	ClipOPAdd clipOPAdd = new ClipOPAdd(clipIndex, list);
	mWorkSpace.handleOperation(clipOPAdd);
```
ClipAddItem参数说明：
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| clipFilePath | 文件地址 | String | 必须 |
| trimRange | 切入点 | VeRange | 非必须 |
| cropRect | 裁切区域 | Rect | 非必须 |
| rotateAngle | 旋转角度 | int | 非必须 |
| filterInfo | 滤镜 | FilterInfo | 非必须 |

2）复制
```
	// clipIndex表示第几个片段，从0开始
	ClipOPCopy clipOPCopy = new ClipOPCopy(clipIndex);
	mWorkSpace.handleOperation(clipOPCopy);
```

3）删除
```
方式一：
	// clipIndex表示第几个片段，从0开始
	ClipOPDel clipOPDel = new ClipOPDel(clipIndex);
	mWorkSpace.handleOperation(clipOPDel);
方式二：
	// uniqueId为ClipData中的片段唯一id
	ClipOPDel clipOPDel = new ClipOPDel(uniqueId);
	mWorkSpace.handleOperation(clipOPDel);
```

4）排序
```
	// 将位置clipIndex的片段移动到toIndex的位置
	ClipOPMove clipOPMove = new ClipOPMove(clipIndex, toIndex);
	mWorkSpace.handleOperation(clipOPMove);
```

5）静音
```
	// clipIndex表示第几个片段，从0开始
	// isMute是否静音
	ClipOPMute clipOPMute = new ClipOPMute(clipIndex, isMute);
	mWorkSpace.handleOperation(clipOPMute);
```

6）音量
```
	// clipIndex表示第几个片段，从0开始
	// volume表示音量,100为正常音量，200为放大1倍
	ClipOPVolume clipOPVolume = new ClipOPVolume(clipIndex, volume);
	mWorkSpace.handleOperation(clipOPVolume);
```

7）变声
```
	// clipIndex表示第几个片段，从0开始
	// soundTone表示音调，从-60~60，{@see QEDftSoundTone}类中有提供的特定音调
	ClipOPMagicSound clipOPMagicSound = new ClipOPMagicSound(clipIndex, soundTone);
	mWorkSpace.handleOperation(clipOPMagicSound);
```

8）镜像
```
	// clipIndex表示第几个片段，从0开始
	// mirror表示镜像类型 {@see ClipData.Mirror}
	ClipOPMirror clipOPMirror = new ClipOPMirror(clipIndex, mirror);
	mWorkSpace.handleOperation(clipOPMirror);
```


9）旋转
```
	// clipIndex表示第几个片段，从0开始
	// rotation旋转角度 0~359度
	ClipOPRotate clipOPRotate = new ClipOPRotate(clipIndex, rotation);
	mWorkSpace.handleOperation(clipOPRotate);
```


10）分割
```
	// clipIndex表示第几个片段，从0开始
	// splitTime分割时间，在加入片段的时间，如从片段的第5s分割，则splitTime=5
	ClipOPSplit clipOPSplit = new ClipOPSplit(clipIndex, splitTime);
	mWorkSpace.handleOperation(clipOPSplit);
```

11）变速
```
	// clipIndex表示第几个片段，从0开始
	// speedValue变速缩放值。如0.25表示4倍速。保留两位小数
	// isKeepTone表示变速是否需要变调
	ClipOPSpeed clipOPSpeed = new ClipOPSpeed(clipIndex, speedValue, isKeepTone);
	mWorkSpace.handleOperation(clipOPSpeed);
```

12）倒放
```
	// clipIndex表示第几个片段，从0开始
	// toReverse是否倒放
	ClipOPReverse clipOPReverse = new ClipOPReverse(clipIndex, toReverse);
	mWorkSpace.handleOperation(clipOPReverse);
```


13）比例
```
	// size 比例,只需要宽高比即可
	// isRatioOriginal 是否原比例。用于添加clip时是否需要重新适配分辨率
	ClipOPRatio clipOPRatio = new ClipOPRatio(size, isRatioOriginal);
	mWorkSpace.handleOperation(clipOPRatio);
```


14）裁切
```
	// clipIndex表示第几个片段，从0开始
	// cropRect表示裁切区域
	ClipOPCrop clipOPCrop = new ClipOPCrop(clipIndex, cropRect);
	mWorkSpace.handleOperation(clipOPCrop);
```


15）视频裁剪
```
	// clipIndex表示第几个片段，从0开始
	// trimRange裁剪区域， length=-1表示到结尾
	ClipOPTrimRange clipOPTrimRange = new ClipOPTrimRange(clipIndex, trimRange);
	mWorkSpace.handleOperation(clipOPTrimRange);
```

16）图片时长
```
	// clipIndex表示第几个片段，从0开始
	// duration图片时长
	ClipOPPicTrim clipOPPicTrim = new ClipOPPicTrim(clipIndex, duration);
	mWorkSpace.handleOperation(clipOPPicTrim);
```

17）图片动画
```
	// clipIndex表示第几个片段，从0开始
	// isAnimEnable是否开启图片动画
	ClipOPPicAnim clipOPPicAnim = new ClipOPPicAnim(clipIndex, isAnimEnable);
	mWorkSpace.handleOperation(clipOPPicAnim);
```

18）背景
```
	// clipIndex表示第几个片段，从0开始
	// clipBgData背景数据 {@see ClipBgData}
	ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
	mWorkSpace.handleOperation(clipOPBackground);
```

ClipBgData构造器
```
  /**
   * 模糊背景
   */
  public ClipBgData(int blurLen);

  /**
   * 图片背景
   */
  public ClipBgData(String imagePath, int blurLen);

  /**
   * 颜色背景
   *
   * @param colorAngle colorArray最多支持三色。渐变色  0-1-2
   * @param colorAngle 渐变色方向。默认为水平方向，取值范围：0~360，对应的角度：0~360，单位为°
   */
  public ClipBgData(int[] colorArray, int colorAngle);
```


19）位置修改
```
	// clipIndex表示第几个片段，从0开始
	// clipPosInfo镜头位置数据 {@see ClipPosInfo}
	ClipOPPosInfo clipOPPosInfo = new ClipOPPosInfo(clipIndex, clipPosInfo);
	mWorkSpace.handleOperation(clipOPPosInfo);
```

20）镜头参数调节
```
	// clipIndex表示第几个片段，从0开始
	// paramAdjust镜头参数调节数据 {@see ParamAdjust}
	ClipOPParamAdjust clipOPParamAdjust = new ClipOPParamAdjust(clipIndex, paramAdjust);
	mWorkSpace.handleOperation(clipOPParamAdjust);
```

21）滤镜
```
	// clipIndex表示第几个片段，从0开始
	// filterInfo滤镜信息 {@see FilterInfo}，null表示不使用滤镜
	ClipOPFilter clipOPFilter = new ClipOPFilter(clipIndex, filterInfo);
	mWorkSpace.handleOperation(clipOPFilter);
```

22）特效滤镜
```
	// clipIndex表示第几个片段，从0开始
	// fxFilterInfo特效滤镜信息 {@see FxFilterInfo}，null表示不使用特效滤镜
	ClipOPFxFilter clipOPFxFilter = new ClipOPFxFilter(clipIndex, fxFilterInfo);
	mWorkSpace.handleOperation(clipOPFxFilter);
```

23）转场
```
	// clipIndex表示第几个片段，从0开始
	// crossInfo转场信息 {@see CrossInfo}，null表示不使用转场
	ClipOPTrans clipOPTrans = new ClipOPTrans(clipIndex, crossInfo);
	mWorkSpace.handleOperation(clipOPTrans);
```

#### 6. Effect剪辑功能接口
1）添加
```
	// groupId为effect的类型
	// effectIndex为effect添加的位置，0为第一个
	// effectAddItem需要的effect {@see EffectAddItem}
	EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, effectIndex, effectAddItem);
	mWorkSpace.handleOperation(effectOPAdd);
```
EffectAddItem参数说明：
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| mEffectPath | 素材资源路径 | String | 必须 |
| srcRange | 效果选取的时长，可以选取某一部分，默认（0， -1） | VeRange | 非必须 |
| destRange | effect在storyboard上的 mVeRange（起始点，时长） | VeRange | 非必须 |
| effectLayerId | 效果的层级信息，是一个浮点数，数字越大 层级越高 | float | 非必须 |
| mEffectPosInfo | 素材位置数据,基于steamsize的，使用的话EffectPosInfo有关于surfacesize的转化 | EffectPosInfo | 非必须 |


2）复制
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	EffectOPCopy effectOPCopy = new EffectOPCopy(groupId, effectIndex);
	mWorkSpace.handleOperation(effectOPCopy);
```

3）删除
方式一：
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	EffectOPDel effectOPDel = new EffectOPDel(groupId, effectIndex);
	mWorkSpace.handleOperation(effectOPDel);
```
方式二：
```
	// uniqueId为effect的唯一id {@see BaseEffect.uniqueId}
	EffectOPDel effectOPDel = new EffectOPDel(uniqueId);
	mWorkSpace.handleOperation(effectOPDel);
```

4）修改图层
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// layerId表示图层，float类型，各类型的图层有区间限制
	EffectOPLayerId effectOPLayerId = new EffectOPLayerId(groupId, effectIndex, layerId);
	mWorkSpace.handleOperation(effectOPLayerId);
```

5）裁切区间
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// trimRange表示裁切区间
	EffectOPTrimRange effectOPTrimRange = new EffectOPTrimRange(groupId, effectIndex, trimRange);
	mWorkSpace.handleOperation(effectOPTrimRange);
```

6）出入区间
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// destRange表示切入切出区间
	EffectOPDestRange effectOPDestRange = new EffectOPDestRange(groupId, effectIndex, destRange);
	mWorkSpace.handleOperation(effectOPDestRange);
```


7）源文件区间
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// srcRange表示源文件区间信息
	EffectOPSrcRange effectOPSrcRange = new EffectOPSrcRange(groupId, effectIndex, srcRange);
	mWorkSpace.handleOperation(effectOPSrcRange);
```


8）透明度
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// alpha表示透明度，0~100
	EffectOPAlpha effectOPAlpha = new EffectOPAlpha(groupId, effectIndex, alpha);
	mWorkSpace.handleOperation(effectOPAlpha);
```


9）音量
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// volume表示音量,100为正常音量，200为放大1倍
	EffectOPVolume effectOPVolume = new EffectOPVolume(groupId, effectIndex, volume);
	mWorkSpace.handleOperation(effectOPVolume);
```


10）音频渐入渐出
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// audioFade表示渐入渐出信息 {@see AudioFade}
	EffectOPAudioFade effectOPAudioFade = new EffectOPAudioFade(groupId, effectIndex, audioFade);
	mWorkSpace.handleOperation(effectOPAudioFade);
```

11）音频循环
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// isRepeat表示在切入切成区域是否循环播放
	EffectOPAudioRepeat effectOPAudioRepeat = new EffectOPAudioRepeat(groupId, effectIndex, isRepeat);
	mWorkSpace.handleOperation(effectOPAudioRepeat);
```


12）音频变声
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// soundTone表示音调，从-60~60，{@see QEDftSoundTone}类中有提供的特定音调
	EffectOPAudioTone effectOPAudioTone = new EffectOPAudioTone(groupId, effectIndex, soundTone);
	mWorkSpace.handleOperation(effectOPAudioTone);
```


13）替换音频
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// audioPath表示音频路径
	// srcRange表示引擎裁切区间
	EffectOPAudioReplace effectOPAudioReplace = new EffectOPAudioReplace(groupId, effectIndex, audioPath, srcRange);
	mWorkSpace.handleOperation(effectOPAudioReplace);
```

14）音频歌词字幕设置
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// audioLyric表示引擎歌词字幕信息 {@see AudioLyric}
	EffectOPAudioLyric effectOPAudioLyric = new EffectOPAudioLyric(groupId, effectIndex, audioLyric);
	mWorkSpace.handleOperation(effectOPAudioLyric);
```

14）音频信息
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// message表示音频信息，如可以保存歌手、歌名等。
	EffectOPAudioMsg effectOPAudioMsg = new EffectOPAudioMsg(groupId, effectIndex, message);
	mWorkSpace.handleOperation(effectOPAudioMsg);
```


15）锁定播放器刷新效果
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// isLock表示是否锁定
	EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, isLock);
	mWorkSpace.handleOperation(effectOPLock);
```
该操作配合EffectOPPosInfo使用，当需要快速刷新播放器某个效果位置时，需要先锁定该效果，当位置刷新结束后，需要对改效果解锁。

16）位置信息
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// effectPosInfo表示位置信息 {@see EffectPosInfo}
	EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, effectPosInfo);
	mWorkSpace.handleOperation(effectOPPosInfo);
```
备注：EffectOPPosInfo可以设置是否快速刷新，只有在操作前设置才起作用
```
	effectOPPosInfo.setFastRefresh(fastRefresh);
```
快速刷新用于快速刷新播放器，提高播放器刷新性能使用，不会保存到工程中，所以快速刷新操作结束后，需要再进行一次非快速刷新的修改，才能真实起作用。需要结合EffectOPLock操作使用，锁定播放器中的素材刷新。



17）画中画混合模式设置
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// effectOverlayInfo表示混合模式信息 {@see EffectOverlayInfo}
	EffectOPOverlayInfo effectOPOverlayInfo = new EffectOPOverlayInfo(groupId, effectIndex, effectOverlayInfo);
	mWorkSpace.handleOperation(effectOPOverlayInfo);
```


18）画中画蒙版设置
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// effectMaskInfo表示蒙版信息 {@see EffectMaskInfo}
	EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
	mWorkSpace.handleOperation(effectMaskInfo);
```


19）画中画抠色设置（绿幕）
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// chromaInfo表示抠色信息 {@see EffectChromaInfo}
	EffectOPChroma effectOPChroma = new EffectOPChroma(groupId, effectIndex, chromaInfo);
	mWorkSpace.handleOperation(effectOPChroma);
```


20）画中画滤镜设置
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// FilterInfo表示滤镜信息 {@see FilterInfo}
	EffectOPFilterInfo effectOPFilterInfo = new EffectOPFilterInfo(groupId, effectIndex, filterInfo);
	mWorkSpace.handleOperation(effectOPFilterInfo);
```


21）画中画参数调节设置
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// ParamAdjust表示参数调节信息 {@see ParamAdjust}
	EffectOPParamAdjust effectOPParamAdjust = new EffectOPParamAdjust(groupId, effectIndex, paramAdjust);
	mWorkSpace.handleOperation(effectOPParamAdjust);
```



22）画中画添加子特效
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// subFxPath表示特效素材路径
	// destRange表示特效出入相对时间区间
	EffectOPSubFxAdd effectOPSubFxAdd = new EffectOPSubFxAdd(groupId, effectIndex, subFxPath, destRange);
	mWorkSpace.handleOperation(effectOPSubFxAdd);
```



23）画中画修改子特效出入点时间区间
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// subType表示子特效索引{@see EffectSubFx}
	// destRange表示特效出入相对时间区间
	EffectOPSubFxDestRange effectOPSubFxDestRange = new EffectOPSubFxDestRange(groupId, effectIndex, subType, destRange);
	mWorkSpace.handleOperation(effectOPSubFxDestRange);
```


24）画中画删除子特效
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// subType表示子特效索引{@see EffectSubFx}
	EffectOPSubFxDel effectOPSubFxDel = new EffectOPSubFxDel(groupId, effectIndex, subType);
	mWorkSpace.handleOperation(effectOPSubFxDel);
```


25）锚点修改
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// anchor锚点位置数据
	EffectOPAnchor effectOPAnchor = new EffectOPAnchor(groupId, effectIndex, anchor);
	mWorkSpace.handleOperation(effectOPAnchor);
```

26）显示静态图片
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// showStaticPic表示是否显示静态图片
	EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, showStaticPic);
	mWorkSpace.handleOperation(effectOPStaticPic);
```
备注：由于一些动态贴纸/字幕，有效果变化，可以通过该操作，使效果关闭动画显示固定效果。


27）马赛克模糊程度
```
	// groupId默认为GROUP_ID_MOSAIC
	// effectIndex为同类型中第几个效果
	// mosaicInfo表示马赛克模糊程度 {@see MosaicInfo}
	EffectOPMosaicInfo effectOPMosaicInfo = new EffectOPMosaicInfo(effectIndex, mosaicInfo);
	mWorkSpace.handleOperation(effectOPMosaicInfo);
```


28）字幕动画开关
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// animOn表示是否开启动画
	EffectOPSubtitleAnim effectOPSubtitleAnim = new EffectOPSubtitleAnim(effectIndex, animOn);
	mWorkSpace.handleOperation(effectOPSubtitleAnim);
```

29）字幕文本
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// text表示字幕文本
	EffectOPSubtitleText effectOPSubtitleText = new EffectOPSubtitleText(effectIndex, text);
	mWorkSpace.handleOperation(effectOPSubtitleText);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// text表示字幕文本
	EffectOPMultiSubtitleText effectOPMultiSubtitleText = new EffectOPMultiSubtitleText(effectIndex, textIndex, text);
	mWorkSpace.handleOperation(effectOPMultiSubtitleText);
```

30）字幕字体
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// fontPath表示字体文件路径
	EffectOPSubtitleFont effectOPSubtitleFont = new EffectOPSubtitleFont(effectIndex, fontPath);
	mWorkSpace.handleOperation(effectOPSubtitleFont);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// fontPath表示字体文件路径
	EffectOPMultiSubtitleFont effectOPMultiSubtitleFont = new EffectOPMultiSubtitleFont(effectIndex, textIndex, fontPath);
	mWorkSpace.handleOperation(effectOPMultiSubtitleFont);
```


31）字幕文本颜色
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// color表示文本颜色，如0xFFFFFFFF,即ARGB
	EffectOPSubtitleColor effectOPSubtitleColor = new EffectOPSubtitleColor(effectIndex, color);
	mWorkSpace.handleOperation(effectOPSubtitleColor);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// color表示文本颜色，如0xFFFFFFFF,即ARGB
	EffectOPMultiSubtitleColor effectOPMultiSubtitleColor = new EffectOPMultiSubtitleColor(effectIndex, textIndex, color);
	mWorkSpace.handleOperation(effectOPMultiSubtitleColor);
```

32）字幕文本对齐方式
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// align表示对齐方式 {@see TextBuble}
	EffectOPSubtitleAlign effectOPSubtitleAlign = new EffectOPSubtitleAlign(effectIndex, align);
	mWorkSpace.handleOperation(effectOPSubtitleAlign);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// align表示对齐方式
	EffectOPMultiSubtitleAlign effectOPMultiSubtitleAlign = new EffectOPMultiSubtitleAlign(effectIndex, textIndex, align);
	mWorkSpace.handleOperation(effectOPMultiSubtitleAlign);
```

备注：对齐方式为下列参数，在{@class TextBuble} 中定义，可以进行 '|' 位运算合并。
```
  public static final int ALIGNMENT_NONE = 0;
  public static final int ALIGNMENT_FREE_STYLE = 0;
  public static final int ALIGNMENT_LEFT = 1;
  public static final int ALIGNMENT_RIGHT = 2;
  public static final int ALIGNMENT_TOP = 4;
  public static final int ALIGNMENT_BOTTOM = 8;
  public static final int ALIGNMENT_MIDDLE = 16;
  public static final int ALIGNMENT_HOR_CENTER = 32;
  public static final int ALIGNMENT_VER_CENTER = 64;
  public static final int ALIGNMENT_HOR_FULLFILL = 128;
  public static final int ALIGNMENT_VER_FULLFILL = 256;
  public static final int ALIGNMENT_UNDER_CENTER = 512;
  public static final int ALIGNMENT_ABOVE_CENTER = 1024;
```

33）字幕文本阴影
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// shadowInfo表示文本阴影信息 {@see ShadowInfo}
	EffectOPSubtitleShadow effectOPSubtitleShadow = new EffectOPSubtitleShadow(effectIndex, shadowInfo);
	mWorkSpace.handleOperation(effectOPSubtitleShadow);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// shadowInfo表示文本阴影信息 {@see ShadowInfo}
	EffectOPMultiSubtitleShadow effectOPMultiSubtitleShadow = new EffectOPMultiSubtitleShadow(effectIndex, textIndex, shadowInfo);
	mWorkSpace.handleOperation(effectOPMultiSubtitleShadow);
```

34）字幕文本描边
单字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// strokeInfo表示文本描边信息 {@see StrokeInfo}
	EffectOPSubtitleStroke effectOPSubtitleStroke = new EffectOPSubtitleStroke(effectIndex, strokeInfo);
	mWorkSpace.handleOperation(effectOPSubtitleStroke);
```
组合字幕：
```
	// groupId默认为GROUP_ID_SUBTITLE
	// effectIndex为同类型中第几个效果
	// textIndex表示组合字幕中的第几个字幕
	// strokeInfo表示文本描边信息 {@see StrokeInfo}
	EffectOPMultiSubtitleStroke effectOPMultiSubtitleStroke = new EffectOPMultiSubtitleStroke(effectIndex, textIndex, strokeInfo);
	mWorkSpace.handleOperation(effectOPMultiSubtitleStroke);
```



35）关键帧设置（由于功能复杂，后期可能调整接口方式）
```
	// groupId为effect的类型
	// effectIndex为同类型中第几个效果
	// keyFrameRange表示关键帧列表，每次都需要设置完整列表 {@see EffectKeyFrameInfo}
	EffectOPKeyFrame effectOPKeyFrame = new EffectOPKeyFrame(groupId, effectIndex, keyFrameRange);
	mWorkSpace.handleOperation(effectOPKeyFrame);
```


#### 7. 导出
```
  /**
   * 开始导出
   *
   * @return 导出控制器, 用于停止导出
   */
  IExportController controller = workSpace.startExport(ExportParams params, IExportListener listener);
```
ExportParams参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| outputPath | 导出文件路径，需要带后缀，提取音频则只支持m4a | String |
| expType | 导出分辨率类型 | int |
| isGif | 是否导出Gif图片 | boolean |
| isSoftwareCodec | 是否软件编解码,默认使用硬件 | boolean |
| videoBitrateScales | 视频比特率浮动 参数,默认不变,设置了customBitrate 后，就只使用customBitrate | float |
| customBitrate | 自定义比特率, <=0表示非自定义 | int |
| customFps | 自定义帧率, <=0表示非自定义 | int |
| isFullKeyFrame | 是否纯i帧，只支持转码时使用 | boolean |
| exportRange | 导出时间区域 | VeRange |
| customLimitSize | 自定义的导出分辨率限制，使用自定义的话，expType就不重要了，只会判断是否gif | VeMSize |
IExportController导出控制器说明：
```
public interface IExportController {
  /** 切换后台导出，降低导出时的cpu使用率，导出时间将拉长，存在失败风险 */
  int change2Back();

  /** 切换前台导出，恢复导出时的cpu使用率 */
  int change2Fore();

  /** 暂停 */
  int pause();

  /** 开始 */
  int resume();

  /** 取消 */
  int cancel();
}
```

IExportListener导出回调监听
```
public interface IExportListener {

  /** 导出准备就绪 */
  void onExportReady();

  /** 导出中 */
  void onExportRunning(int percent);

  /** 导出成功，exportPath导出视频路径 */
  void onExportSuccess(String exportPath);

  /** 导出取消 */
  void onExportCancel();

  /** 导出失败 */
  void onExportFailed(int nErrCode, String errMsg);

  /** 导出资源已释放,取消、失败、成功后都会释放 */
  void onProducerReleased();
}
```

#### 8. 高级玩法-剪辑操作拓展功能
由于各开发者对剪辑玩法关联的期望不同，为了支持开发者在玩法上的创意想法，剪辑操作允许开发者进行自定义组合。
如：开发者期望对Clip倒放后立刻对Clip进行静音。则开发者可以自定义操作符：
```
public class ClipOPReverseMute extends BaseOperate {

  private int clipIndex;

  public ClipOPReverseMute(int clipIndex) {
    this.clipIndex = clipIndex;
  }

  @Override public boolean operateRun(IEngine engine) {
    // 编辑倒放
    ClipOPReverse clipOPReverse = new ClipOPReverse(clipIndex, true);
    clipOPReverse.operateRun(engine);
    // 编辑静音
    ClipOPMute clipOPMute = new ClipOPMute(clipIndex, true);
    clipOPMute.operateRun(engine);
    // 返回最终的成功/失败
    return true;
  }
}
```
然后再需要时执行操作ClipOPReverseMute即可
```
ClipOPReverseMute clipOPReverseMute = new ClipOPReverseMute(clipIndex);
workspace.handleOperation(clipOPReverseMute);
```

#### 9. 高级玩法-对撤销/重做的支持（此功能开发中，如遇问题，可以联系我们）
当开发者想进行一些撤销/重做的玩法时，可以在进行剪辑操作前，对需要支持撤销的剪辑设置是否支持undo。即：
```
	// 设置支持undo
	baseOperate.setSupportUndo(true);
```
设置后，workspace将对当前操作前的状态，记录一个状态点。(注：目前最多支持30个undo/redo的记录点)

当需要返回上一个记录点时，执行撤销：
```
	// 执行撤销
	workspace.undo();
```
当需要取消撤销动作时时，可以返回上一次执行撤销前的状态。执行重做：
```
	// 执行重做
	workspace.redo();
```
由于撤销/重做都有数量，所以可以通过注册监听器的方式，获取undo/redo的数量回调。
```
	workspace.setDequeCountListener(IDequeCountListener);
```
IDequeCountListener说明：
```
public interface IDequeCountListener {
  // undoSize为数量变化时的目前可以支持的undo()操作数量
  // redoSize为数量变化时的目前可以支持的redo()操作数量
  void onDequeCountChange(int undoSize, int redoSize);
}
```

执行Undo/Redo时，对通知出去的Operate将会进行包装。Undo通知的为UndoOperate，Redo通知的为RedoOperate。获取源操作符的方式为：
```
UndoOperate.getSrcOperate();

RedoOperate.getSrcOperate();
```
BaseOperate中关于Undo/Redo的信息：
```
// 是否处理过undo
operate.isUndoHandled();
// 是否处理undo
operate.isDoingUndo();
// 获取操作类型
BaseOperate.EngineWorkType type = operate.getOperateType();
```
BaseOperate.EngineWorkType参数说明：
| 名称  | 解释 |
| :-: | :-: |
| normal | 正常|
| undo | 撤销 |
| redo | 重做 |


#### 10. 高级玩法-自由黑帧模式
当初始化时设置isUseStuffClip为true时，即开启自由黑帧模式。自由黑帧模式和普通剪辑模式的主要区别，即在对效果出入区间的限制上。
##### 普通剪辑模式
如果片段clip的播放效果总时长在60s，而贴纸效果的出入区间是在55s~70s。则导出时，视频总长度只会是60s，贴纸时间为55s~60s，60s以后的内容将自动截断。
普通剪辑模式下，导出视频的总长度，由片段的最终时长决定。
##### 自有黑帧模式
如果片段clip的播放效果总时长在60s，而贴纸效果的出入区间是在55s~70s。则导出时，视频总长度将会是70s，贴纸时间为55s~70s，60s以后的片段内容将是黑帧画面，但是不影响贴纸的内容展示。
自有黑帧模式下，导出视频的总长度，由效果和片段的最终时长共同决定。

### 七、卡点视频工程功能开发接入

#### 1. 卡点视频工程
##### 创建和加载
```
  /**
   * 创建新的卡点视频工程
   */
  QEEngineClient.createNewSlideProject(long themeId, List<String> nodePathList, QESlideWorkSpaceListener listener);

  /**
   * 加载卡点视频工程
   */
  QEEngineClient.loadSlideProject(String projectPath, QESlideWorkSpaceListener listener);

```

##### 工程删除
【详情请参看剪辑工程工程删除相关。】

#### 2. 播放器
【详情请参看剪辑工程播放器相关。】

#### 3. 获取片段节点信息
```
ArrayList<SlideInfo> slideInfos = workspace.getSlideInfoList();
```
SlideInfo参数说明：
| 名称  | 解释 | 类型 |
| :-: | :-: | :-: |
| filePath | 片段文件路径 | String |
| sourceType | 文件类型{@see Type} | SlideInfo.Type |
| index | 索引 | int |
| duration | 片段时长 | int |
| previewPos | 预览位置 | int |

SlideInfo.Type参数说明：
| 名称  | 解释 |
| :-: | :-: |
| Image | 图片|
| Video | 视频 |

#### 4. 卡点视频剪辑功能接口
1）排序
```
	// 将from位置的片段移动到to位置
	SlideOPMove slideOPMove = new SlideOPMove(from, to);
	mWorkSpace.handleOperation(slideOPMove);
```

2）替换
```
	// clipIndex表示第几个片段，从0开始
	// filePath表示视频/图片路径
	SlideOPReplace slideOPReplace = new SlideOPReplace(clipIndex, filePath);
	mWorkSpace.handleOperation(slideOPReplace);
```

#### 5. 导出
【详情请参看剪辑工程导出相关。】


### 八、 缩略图获取
##### 1. 工程相关缩略图获取
```
  /**
   * 获取工程封面
   */
  Bitmap bitmap = wrokspace.getProjectThumbnail();

  /**
   * 获取工程封面
   */
  Bitmap bitmap = wrokspace.getProjectThumbnail(int offset);

  /**
   * engine接口createThumbnailManager，需要配对调用 destroyThumbnailManager；
   * 否则容易造成内存泄漏；
   */
  Bitmap bitmap = wrokspace.getClipThumbnail(int index, int width, int height);

  /**
   * engine接口createThumbnailManager，需要配对调用 destroyThumbnailManager；
   * 否则容易造成内存泄漏；
   */
  Bitmap bitmap = wrokspace.getClipThumbnail(int index, int offset, int width, int height);
```

##### 2.素材缩略图获取
工具：QEThumbnailTools
```
  /**
   * 获取素材xyt文件中的缩略图
   */
  Bitmap bitmap = QEThumbnailTools.getTemplateThumbnail(String filePath, int width, int height);

  /**
   * 获取图片文件的缩略图（jpeg/png/gif等)
   *
   * @param offset gif时，可以取某个时间点的缩略图
   */
  Bitmap bitmap = QEThumbnailTools.getPicFileThumbnail(String filePath, int width, int height, int offset);

  /**
   * 获取视频文件缩略图
   */
  Bitmap bitmap = QEThumbnailTools.getVideoThumbnail(String filePath, int width, int height, int offset);
```

### 九、 工具类QETools
```
  /**
   * 视频倒放
   *
   * @param reverseSrcFile 原始文件全路径。
   */
  IExportController controller = QETools.reverseFile(final String reverseSrcFile, ExportParams params, IExportListener listener);

  /**
   * 将外部文件导出成适合添加到我们工程的文件(转码TRANSCODE etc...)。 MPEG4编码
   * 转码
   */
  IExportController controller = QETools.convertVideo(String srcPath, ExportParams params, IExportListener listener);

  /**
   * 提取音频
   */
  IExportController controller = QETools.directAudio(String srcPath, ExportParams params, IExportListener listener);

  /**
   * 通过引擎解析音频波形数据，数据是一段一段解析回调出来
   * 由于数据量比较大(32k左右),app采用1s 40帧的筛选
   * 目前是去两极，value [0,1]
   * 取的左声道
   *
   */
  int iRes = QETools.extractAudioWave(String audioPath, VeRange srcRange, IAudioDataListener audioDataListener);
```
