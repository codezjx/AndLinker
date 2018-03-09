
[![Build Status](https://travis-ci.org/codezjx/AndLinker.svg?branch=master)](https://travis-ci.org/codezjx/AndLinker)
[![JCenter](https://api.bintray.com/packages/codezjx/maven/and-linker/images/download.svg)](https://bintray.com/codezjx/maven/and-linker/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Introduction

AndLinker is a library for using AIDL like [Retrofit][retrofit] in Android App, allows IPC call seamlessly compose with [rxjava][rxjava] and [rxjava2][rxjava2] call adapters.


## Setup

Add the `jcenter` repository in your root `build.gradle`.
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
    implementation 'com.codezjx.library:andlinker:0.6.0'
}
```

## Getting Started

Defining a normal java Interface with `@ClassName` and `@MethodName` annotation.

```java
@ClassName("com.codezjx.example.IRemoteService")
public interface IRemoteService {

    @MethodName("getPid")
    int getPid();

    @MethodName("basicTypes")
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
```

Implement the interface.

```java
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

Create `AndLinkerBinder` and register interface implementation above, then expose the linker binder to clients.

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
        // Return the linker binder
        return mLinkerBinder;
    }
}
```

In you client app, create the `AndLinker` object and generates an implementation of the `IRemoteService` interface.

```java
public class BindingActivity extends Activity {

    private AndLinker mLinker;
    private IRemoteService mRemoteService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinker = new AndLinker.Builder(this)
                .packageName("com.codezjx.example")
                .action("com.codezjx.example.REMOTE_SERVICE_ACTION")
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