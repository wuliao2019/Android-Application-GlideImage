# Android-Application-GlideImage
重庆大学2019级计算机学院移动应用开发实验二：Android拼图游戏

#### 实验目的
1. 本次实验的目的是掌握Android Studio开发工具的基本操作，了解所开发Android项目的基本结构。掌握常用组件和布局的使用，尤其图像组件的使用，bitmap的处理等。
2. 设计完成一个拼图小游戏APP。

#### 实验内容
从多张图片中，选择一张，分割为4块以上的小图片，打乱后分布，可以拼成原图。
评分点：（1）可以从多张图片中选择一张图片；（2）通过开始按钮开始游戏，把分隔后的图片打乱分布，并开始计时；（3）通过点击图片进行移动。（4）通过结束按钮判断游戏是否成功完成；（5）APP界面自行设计。（6）动画、复杂度、美观度、自定义功能、程序结构等都是加分项。

#### 实验成果
应用主要有两个界面组成：主界面和游戏界面。主界面选择图片和难度，并在点击开始游戏时将图片编号或地址、难度等信息通过intent传递到游戏界面Activity。游戏界面加载时将图片切割成对应难度的多个部分并打乱顺序，如果无解则重新打乱。

进入应用后的主页面，有七张内置的图片可以选择，点击选择一张图片后，选中的图片会被框起来，并显示在上方大图中。下方是难度下拉选择框，有四种难度可选。
（打开应用时图片有渐变动画）


    
点击预置图片后的加号会弹出选择图片对话框，可以选择相机拍照或选择相册照片，自定义图片会被显示在上方，并被裁剪成正方形。点击开始游戏后，会跳转到游戏界面，上方展示了原图、难度、步数、用时等信息以及重置和返回按钮，下方则是游戏拼图。
拼图是随机打乱的，并通过算法确保有解。

    
点击任意一块拼图后开始计时，游戏开始，走过的步数会被记录，拼图移动时有平移动画。点击重置会重新打乱拼图并重置时间和步数。点击返回会回到主界面。当正确走完最后一块拼图时，游戏自动判定成功，计时会停止且拼图不可点击，弹出提示成功字样并补齐右下角图片。

#### 应用截图
<center class="half">
    <img src="Screenshot/1.main.jpg" width="200"/><img src="Screenshot/2.diff.jpg" width="200"/><img src="Screenshot/3.choose.jpg" width="200"/>
</center>
<center class="half">
    <img src="Screenshot/4.game.jpg" width="200"/><img src="Screenshot/5.game.jpg" width="200"/><img src="Screenshot/6.finish.jpg" width="200"/>
</center>

![image1](Screenshot/1.main.jpg) | ![image2](Screenshot/2.diff.jpg) | ![image3](Screenshot/3.choose.jpg)
![image](Screenshot/4.game.jpg) | ![image](Screenshot/5.game.jpg) | ![image](Screenshot/6.finish.jpg)
