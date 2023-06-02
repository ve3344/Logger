# Kotlin特性实现的轻量Logger库
<p align="center">
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<a href="https://github.com/ve3344/Logger/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-Apache-blue"/></a>
<a href="https://jitpack.io/#ve3344/Logger"><img src="https://jitpack.io/v/ve3344/Logger.svg"/></a>
</p>

日志库千千万，但是使用起来基本都是一样的，只是核心实现不同。
本库十分精简，没有精美的格式化，没有复杂的写入逻辑，旨在提供一个易用可拓展的门面，其他功能均通过拦截器实现。

### Kotlin inline + 拦截器 + 跨模块配置Logger

# 特性

- 通过Kotlin inline 提升性能，统一配置Logger的tag,level,跨模块配置Logger
- Logger 可以配置任意输出目标到文件，Socket等等，并控制日志记录线程
- Logger 可通过拦截器进行配置，实现过滤，格式化
- 支持Jvm使用,支持JUnit

# 集成

## 添加仓库

```groovy
//in build.gradle(Project)
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

// 新版方式 settings.gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

## 添加依赖

```groovy
//in build.gradle(module)
dependencies {
    implementation "com.github.ve3344.Logger:Logger:1.0.0"
}
```
# 使用
## 基础使用
```kotlin
Logger.v("Test verbose")
Logger.d("Test debug")
Logger.i("Test info")
Logger.w("Test warning")
Logger.e("Test error")
Logger.wtf("Test wtf")

Logger.v { "Test verbose" }
Logger.d { "Test debug" }
Logger.i { "Test info" }
Logger.w { "Test warning" }
Logger.e { "Test error" }
Logger.wtf { "Test wtf" }

```

## 子Logger使用
```kotlin
class XxActivity {
    val logger = Logger["XxActivity"] //二级Logger的tag为“APP-XxActivity”
    val logger = loggerForClass() //使用当前类名生成二级Logger的tag为“APP-XxActivity”
    inner class XxFragment {
        val fragmentLogger = logger["XxFragment"]//三级Logger的tag为“APP-XxActivity-XxFragment”
    }
}
```
## 全局配置
```kotlin
Logger.level = LogLevel.VERBOSE
Logger.tag = "AppName"
Logger.logPrinter = AndroidLogPrinter()

```
## 跨模块配置
通过配置子Logger工厂的方式，来控制子Logger的level等，从而实现对别的模块中的logger进行配置。
比如设置A模块中的日志输出等级为ERROR，
```kotlin
Logger.loggerFactory = { subTag ->
    Logger("$tag-$it", level, logPrinter).also { child ->
        if (child.tag == "AModule") {
            logger.level = LogLevel.ERROR
        }
    }
}
```

## 非Android中使用
仅仅是全局Logger的logPrinter的默认值不同，
在Android中默认AndroidLogPrinter ，在非Android中默认为ConsoleLogPrinter。

## 拦截器和拓展
通过Kotlin拓展函数的方式实现了拦截器和拓展功能。
```kotlin
Logger.logPrinter = AndroidLogPrinter()
    .logAlso(FileLogPrinter({ File("warning.log") })
        .format { _, _, messageAny, _ -> "【${Thread.currentThread().name}】:$messageAny" }
        .logAt(Executors.newSingleThreadExecutor())
        .filterLevel(LogLevel.WARNING)
    )    
    .logAlso(FileLogPrinter({ File("error.log") })
        .format { _, _, messageAny, _ -> "【${Thread.currentThread().name}】:$messageAny" }
        .logAt(Executors.newSingleThreadExecutor())
        .filter { _, tag, _, _ -> tag.contains("CHILD") }
        .filterLevel(LogLevel.ERROR)
    )
```

您可以通过拦截器自己实现拓展, 比如切换线程的实现是这样的：
```kotlin
fun Logger.LogPrinter.logAt(executor: Executor) =
    intercept { logPrinter, level, tag, messageAny, throwable ->
        executor.execute {
            logPrinter.log(level, tag, messageAny, throwable)
        }
    }
```

## 格式化
不内置Json格式化，有需要可通过format拓展实现
```kotlin
logPrinter.format { _, _, messageAny, _ -> gson.toJson(messageAny) }
```
也可通过拓展函数实现
```kotlin
inline fun Logger.logJson(level: Logger.LogLevel = Logger.LogLevel.INFO, any: () -> Any) {
    log(level, block = { gson.toJson(any()) })
}
```


# License

``` license
 Copyright 2021, ve3344@qq.com 
  
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at 
 
       http://www.apache.org/licenses/LICENSE-2.0 

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
