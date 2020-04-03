# ScratchCardView
一款在android上实现刮刮卡效果的控件，类似支付宝刮刮乐效果。


<p>    
    <img src="https://github.com/BeiHaiCoding/ScratchCardView/blob/master/image/Screenshot_20200326-155641.png" alt="Latest Stable      Version" />
     <img src="https://github.com/BeiHaiCoding/ScratchCardView/blob/master/image/Screenshot_20200326-155649.png" alt="Latest Stable  Version" />
</p>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)


使用说明
-----

在项目build.gradle文件下添加
```
allprojects {
     repositories {
         maven { url 'https://jitpack.io' }
     }
 }
 ```

在模块build.gradle文件下添加
```
implementation 'com.github.BeiHaiCoding:ScratchCardView:0.0.2'
```

XML
-----

```
    <com.czq.scratchcardview.ScratchCardView
        android:id="@+id/scv_main"
        android:layout_width="200dp"
        android:layout_height="100dp"
        android:layout_centerInParent="true"
        app:scratch_drawable="@drawable/first_lv1"
        app:scratch_finish_rate="50"
        app:scratch_mark_color="@color/colorPrimaryDark" />
```

####属性说明
* `scratch_drawable`     (reference)     -> default  null      刮刮卡底层图片
* `scratch_mark_color`   (color)     -> default #CCCCCC      刮刮卡涂层颜色
* `scratch_finish_rate`  (integer)     -> default  85      判定用户涂到多少百分比算成功
* `scratch_radius`       (dimension) -> default  40px        用户刮卡时路径的尺寸

Java
-----

```
//涂卡完成时的监听事件。
scratchCardView.setOnScratchFinishedListener(new ScratchCardView.OnScratchFinishedListener() {
    @Override
    public void finish() {
        Toast.makeText(MainActivity.this, "刮出了一阵风", Toast.LENGTH_SHORT).show();
    }
});

//涂卡完成后的点击事件。
scratchCardView.setOnScratchFinishedClickListener(new ScratchCardView.OnScratchFinishedClickListener() {
            @Override
            public void click() {
                Toast.makeText(MainActivity.this,"您点击了背景图片",Toast.LENGTH_SHORT).show();
            }
        });
```

致谢
-----
[YScratchView](https://github.com/GitHubZJY/ZJYWidget/blob/master/widget/src/main/java/com/zjywidget/widget/scratchview/YScratchView.java) 本文采用该控件的实现逻辑。




