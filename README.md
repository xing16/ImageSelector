# ImageSelector
ImageSelector Material Design 可配置化沉浸式状态栏图片选择器，支持单选，多选，预览，裁剪，自定义图片加载引擎。

效果预览图：







### 使用方法

#### 1. 打开图片选择器

##### 单选

```java
ImageConfig imageConfig = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_SINGLE)
                        .needCrop(false)
                        .build();
ImageSelector.open(this, imageConfig, REQUEST_SINGLE);
```

##### 单选裁剪

```
ImageConfig imageConfig2 = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_SINGLE)
                        .needCrop(true)
                        .build();
ImageSelector.open(this, imageConfig2, REQUEST_SINGLE_CROP);
```



##### 多选预览

```java
ImageConfig imageConfig1 = new ImageConfig.Builder(new GlideImageEngine())
                        .showCamera(true)
                        .statusBarColor(R.color.colorPrimary)
                        .titleBarColor(R.color.colorPrimary)
                        .titleBarTextColor(android.R.color.white)
                        .selectMode(ImageConfig.MODE_MULTI)
                        .needCrop(true)
ImageSelector.open(this, imageConfig1, REQUEST_MULTI);
```



#### 2. 选择图片结果回调

**单选： ImageSelector.getSingleSelectPath(intent)**

**单选裁剪： String cpath = ImageSelector.getCropPath(intent);**

**多选： ArrayList<String> selectImgList = ImageSelector.getMultiSelectPath(data);**

```
@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> list = new ArrayList<>();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE:
                    String path = ImageSelector.getSingleSelectPath(data);
                    Log.e(TAG, "onActivityResult: " + path );
                    if (!TextUtils.isEmpty(path)) {
                        list.clear();
                        list.add(path);
                        adapter.setData(list);
                    }
                    break;
                case REQUEST_SINGLE_CROP:
                    String cpath = ImageSelector.getCropPath(data);
                    if (!TextUtils.isEmpty(cpath)) {
                        list.clear();
                        list.add(cpath);
                        adapter.setData(list);
                    }
                    break;
                case REQUEST_MULTI:
                    ArrayList<String> selectImgList = ImageSelector.getMultiSelectPath(data);
                    if (selectImgList != null) {
                        adapter.setData(selectImgList);
                    }
                    break;
                default:
                    break;
            }
        }
    }
```



#### 3. AndroidManifest.xml 配置

```xml
		<activity
            android:name=".widget.ImageSelectorActivity"
            android:screenOrientation="portrait" />
        <activity
            android:name=".widget.ImagePreviewActivity"
            android:screenOrientation="portrait" />
        <activity
            android:name="com.yalantis.ucrop.UCropActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar" />

        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="{applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_path" />
        </provider>
```










