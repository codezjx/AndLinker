
[![Build Status](https://travis-ci.org/codezjx/AndLinker.svg?branch=master)](https://travis-ci.org/codezjx/AndLinker)
[![JCenter](https://api.bintray.com/packages/codezjx/maven/and-linker/images/download.svg)](https://bintray.com/codezjx/maven/and-linker/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# AndLinker

[中文文档](README_CN.md)

## Introduction

AndLinker is a IPC(Inter-Process Communication) library for Android, which combines the features of [AIDL][aidl] and [Retrofit][retrofit]. Allows IPC call seamlessly compose with [RxJava][rxjava] and [RxJava2][rxjava2] call adapters. Project design and partial code refer to the great project [Retrofit][retrofit].

## Setup

Add the `jcenter()` repository in your root `build.gradle`.
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```

Add the dependencies in your app level `build.gradle`.
```groovy
dependencies {
    implementation 'com.codezjx.library:andlinker:0.7.1'
}
```

## Features

- Define normal Java Interface instead of AIDL Interface
- Generates the IPC implementation of the remote service interface like [Retrofit][retrofit]
- Support call adapter: `Call`, [RxJava][rxjava] `Observable`, [RxJava2][rxjava2] `Observable` & `Flowable`
- Support remote service callback
- Support all AIDL data types
- Support all AIDL directional tag, either `in`, `out`, or `inout`
- Support the AIDL `oneway` keyword

## Getting Started

Define a normal java Interface with `@RemoteInterface` annotation, and implements the interface.

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

In your server app, create the `AndLinkerBinder` object and register interface implementation above, then expose the linker binder to clients.

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

In your client app, create the `AndLinker` object and generates an implementation of the `IRemoteService` interface.

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

Now your client app is ready, all methods from the created `IRemoteService` are IPC methods, call it directly!

```java
int pid = mRemoteService.getPid();
mRemoteService.basicTypes(1, 2L, true, 3.0f, 4.0d, "str");
```

## Supported data types

AndLinker supports all AIDL data types:
- All primitive types in the Java programming language (such as `int`, `long`, `char`, `boolean`, and so on)
- `String`
- `CharSequence`
- `Parcelable`
- `List` (All elements in the List must be one of the supported data types in this list)
- `Map` (All elements in the Map must be one of the supported data types in this list)

## Advanced

### Call Adapters
You can modify the client side app's remote service interface, wrap the return type of the method.

```java
@RemoteInterface
public interface IRemoteService {

    Observable<Integer> getPid();

    Call<Void> basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, 
        double aDouble, String aString);
}
```

Register the call adapter factory in `AndLinker.Builder`, the remaining steps are consistent with [Retrofit][retrofit].

```java
new AndLinker.Builder(this)
        ...
        .addCallAdapterFactory(OriginalCallAdapterFactory.create()) // Basic
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // RxJava2
        .build();
```

### Deal with callbacks
Define callback interface to receive callbacks from the remote service with `@RemoteInterface` annotation.

```java
@RemoteInterface
public interface IRemoteCallback {

    void onValueChange(int value);
}
```

Use `@Callback` annotation for callback parameter.

```java
void registerCallback(@Callback IRemoteCallback callback);
```

In your client app, implements the remote callback and register to `AndLinker`, and that's it!

```java
private final IRemoteCallback mRemoteCallback = new IRemoteCallback() {
    @Override
    public void onValueChange(int value) {
        // Invoke when server side callback
    }
};
mLinker.registerObject(mRemoteCallback);
```

### Specify directional tag
You can specify `@In`, `@Out`, or `@Inout` annotation for parameter, indicating which way the data goes, same as AIDL.

```java
void directionalParamMethod(@In KeyEvent event, @Out int[] arr, @Inout Rect rect);
```

>**Caution**:
>- All non-primitive parameters require a directional annotation indicating which way the data goes. Either `@In`, `@Out`, or `@Inout`. Primitives are `@In` by default, and cannot be otherwise.
>- Parcelable parameter with `@Out` or `@Inout` annotation must implements from `SuperParcelable`, or you must add method `public void readFromParcel(Parcel in)` by yourself.

### Use `@OneWay` annotation
You can use `@OneWay` for a method which modifies the behavior of remote calls. When used, a remote call does not block, it simply sends the transaction data and immediately returns, same as AIDL.

```java
@OneWay
void onewayMethod(String msg);
```

## Proguard Configuration

Add following rules to `proguard-rules.pro` file, keep classes that will be serialized/deserialized over AndLinker.
```
-keep class com.example.andlinker.model.** {
    public void readFromParcel(android.os.Parcel);
}
```

## Feedback

Any issues or PRs are welcome!

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