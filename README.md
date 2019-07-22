# MiniTikTok for Android

项目为字节跳动校园12天实训的大作业, 功能是利用老师给的api实现一个迷你版的抖音. 项目由陆俊奇和陈家祺共同完成. 由于我们俩都是第一次接触安卓, 所以项目难免出现bug, 用户体验不会尽如人意, 页面设计也比较粗糙. 希望下载的同学不要有什么期待(如果有人下载的话).

## 如何使用程序:

* 1. 安装android studio
  2. clone项目到本地/下载zip文件到本地并解压
  3. 使用android studio打开该项目并运行

* 下载项目的[.apk](app/release/app-release.apk)程序并安装在您的Android手机上

以上两种方法请确保您手机的SDK version在21以上, 否则可能无法运行项目

## 外部依赖库:
在无法联网的情况下请确保`build.gradle`中下列库的存在
    
    ```
    dependencies {
        implementation 'com.google.code.gson:gson:2.8.5'
        implementation 'com.github.bumptech.glide:glide:4.9.0'
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
        annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
        implementation 'com.google.android.material:material:1.1.0-alpha08'
        implementation 'com.squareup.retrofit2:retrofit:2.5.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
        implementation 'com.squareup.okhttp3:logging-interceptor:3.11.0'
        implementation 'com.airbnb.android:lottie:2.7.0'
    }
    ```

## 具体分工:

**陆俊奇:**
1. 利用retrofit从老师给的api上获取json文件.
2. 利用gson库解析json文件, 获得必要信息.
3. 利用recyclerView + glide实现在主页上带有图片和上传者姓名的feed流展示.
4. 利用ViewPager实现在视频播放页面左右滑动切换视频的功能.
5. 实现了通过视频url全屏播放视频带有暂停/播放功能的VideoView.
6. 实现了存放用户点赞视频的数据库
7. 实现了在视频页面双击点赞出现红心, 再双击取消点赞的功能.
8. 编写 README.md

**陈家祺:**
1. 完成自定义相机, 实现录制, 切换摄像头, 手势缩放功能. 
2. 利用retrofit上传文件.
3. 实现上传之前预览视频的功能.
4. 实现自动生成封面的功能.
5. 实现录制10秒自动停止的功能.
6. 添加动画效果.
7. 制作答辩ppt

## 感谢:
这次暑期实训的所有老师, 远在土澳的[mmgg](https://github.com/AtlasRE), 以及Google, StackOverflow, CSDN.