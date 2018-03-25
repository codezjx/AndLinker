
## 简介

AndLinker是一款Android上的IPC (进程间通信) 库，结合了[AIDL][aidl]和[Retrofit][retrofit]的诸多特性，且可以与[RxJava][rxjava]和[RxJava2][rxjava2]的Call Adapters无缝结合使用。项目的设计与部分代码参考了伟大的[Retrofit][retrofit]项目。

## 配置

在项目根目录的`build.gradle`中添加`jcenter()`仓库
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```

在App的`build.gradle`中添加如下依赖
```groovy
dependencies {
    implementation 'com.codezjx.library:andlinker:0.7.1'
}
```

## 功能特性

- 以普通Java接口代替AIDL接口
- 像[Retrofit][retrofit]一样生成远程服务接口的IPC实现
- 支持的Call Adapters：`Call`，[RxJava][rxjava] `Observable`，[RxJava2][rxjava2] `Observable` & `Flowable`
- 支持远程服务回调机制
- 支持AIDL的所有数据类型
- 支持AIDL的所有数据定向tag：`in`，`out`，`inout`
- 支持AIDL的`oneway`关键字

## 快速开始

使用注解`@RemoteInterface`修饰远程服务接口`IRemoteService`，并实现它

```java
@RemoteInterface
public interface IRemoteService {

    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}

private final IRemoteService mRemoteService = new IRemoteService() {
    
    @Override
    public int getPid() {
        return android.os.Process.myPid();
    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean,
        float aFloat, double aDouble, String aString) {
        // Does something
    }
};
```

在服务端App中，创建`AndLinkerBinder`对象，并注册上面的接口实现。然后在`onBind()`方法中返回，暴露给客户端

```java
private AndLinkerBinder mLinkerBinder;

public class RemoteService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        mLinkerBinder = AndLinkerBinder.Factory.newBinder();
        mLinkerBinder.registerObject(mRemoteService);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLinkerBinder;
    }
}
```

在客户端App中，通过`Builder`创建`AndLinker`对象，并通过`create()`方法生成一个`IRemoteService`远程接口的IPC实现

```java
public class BindingActivity extends Activity {

    private AndLinker mLinker;
    private IRemoteService mRemoteService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinker = new AndLinker.Builder(this)
                .packageName("com.example.andlinker")
                .action("com.example.andlinker.REMOTE_SERVICE_ACTION")
                .build();
        mLinker.bind();

        mRemoteService = mLinker.create(IRemoteService.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLinker.unbind();
    }
}
```

一切就绪，现在`mRemoteService`对象中的所有方法都是IPC方法，直接调用即可

```java
int pid = mRemoteService.getPid();
mRemoteService.basicTypes(1, 2L, true, 3.0f, 4.0d, "str");
```

## 支持数据类型

AndLinker支持AIDL所有数据类型：
- Java语言中的所有原始类型 (如：`int`，`long`，`char`，`boolean`，等等)
- `String`
- `CharSequence`
- `Parcelable`
- `List` (List中的所有元素必须是此列表中支持的数据类型)
- `Map` (Map中的所有元素必须是此列表中支持的数据类型)

## 进阶使用

### Call Adapters
在客户端App中，你可以copy并修改远程服务接口，包装方法的返回值

```java
@RemoteInterface
public interface IRemoteService {

    Observable<Integer> getPid();

    Call<Void> basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, 
        double aDouble, String aString);
}
```

在`AndLinker.Builder`中注册对应的Call Adapter Factory，剩下的步骤基本和[Retrofit][retrofit]一致，不再赘述

```java
new AndLinker.Builder(this)
        ...
        .addCallAdapterFactory(OriginalCallAdapterFactory.create()) // Basic
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // RxJava2
        .build();
```

### 处理远程服务接口回调
使用`@RemoteInterface`注解修饰远程服务回调接口`IRemoteCallback`

```java
@RemoteInterface
public interface IRemoteCallback {

    void onValueChange(int value);
}
```

在远程方法中使用`@Callback`注解修饰callback参数

```java
void registerCallback(@Callback IRemoteCallback callback);
```

在客户端App中，实现上面定义的远程服务回调接口`IRemoteCallback`，并且注册到`AndLinker`中，就是这么简单

```java
private final IRemoteCallback mRemoteCallback = new IRemoteCallback() {
    @Override
    public void onValueChange(int value) {
        // Invoke when server side callback
    }
};
mLinker.registerObject(mRemoteCallback);
```

### 指定数据定向tag
你可以为远程方法的参数指定`@In`，`@Out`，或者`@Inout`注解，它标记了数据在底层Binder中的流向，跟AIDL中的用法一致

```java
void directionalParamMethod(@In KeyEvent event, @Out int[] arr, @Inout Rect rect);
```

>**注意**:
>- 所有非原始类型必须指定数据定向tag：`@In`，`@Out`，或者`@Inout`，用来标记数据的流向。原始类型默认是`@In`类型，并且不能指定其他值。
>- 使用`@Out`或者`@Inout`修饰的Parcelable参数必须实现`SuperParcelable`接口，否则你必须手动添加此方法`public void readFromParcel(Parcel in)`。

### 使用`@OneWay`注解
你可以在远程方法上使用`@OneWay`注解，这会修改远程方法调用的行为。当使用它时，远程方法调用不会堵塞，它只是简单的发送数据并立即返回，跟AIDL中的用法一致

```java
@OneWay
void onewayMethod(String msg);
```

## Proguard配置

在`proguard-rules.pro`文件中添加如下混淆规则，将需要序列化/反序列化的model类给keep掉
```
-keep class com.example.andlinker.model.** {
    public void readFromParcel(android.os.Parcel);
}
```

## 反馈

欢迎各位提issues和PRs！

## License

    Copyright 2018 codezjx <code.zjx@gmail.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[retrofit]: https://github.com/square/retrofit
[rxjava]: https://github.com/ReactiveX/RxJava/tree/1.x
[rxjava2]: https://github.com/ReactiveX/RxJava/tree/2.x
[aidl]: https://developer.android.com/guide/components/aidl.html