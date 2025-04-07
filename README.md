
## 项目架构

主工程

模块 | 作用
--- | ---
BKPG | 主应用程序
BKPG_PROTOCAL | 三方扩展协议
BKPG_DEMO | 简单示例扩展

官方扩展插件工程

模块 | 作用
--- | ---
PLUGIN_BKPG_GROUP_PROCESS | 命令组


## 生产环境

- JAVA : 8

## 主应用程序执行逻辑

1. 应用启动，绘制UI，挂载到系统托盘
2. 扫描注册ext目录下的jar包(扫描实现了MiniApp的类)
3. 程序挂载

## 扩展协议

### 可用协议

```text
MiniApp                    基础协议
    ┗ ProcessAppTemplate   命令执行协议模板
↑    
MiniAppGroup               程序组合包
```

### 协议结构定义

MiniApp
   - String getName();  // 获取应用名称
   - JPanel getPanel(); // 获取应用面板
   - boolean protect(); // 保护应用，重启是否重新装载

MiniAppGroup
   - List\<MiniApp> getMiniApps(); // 获取所有的应用

ProcessAppTemplate: MiniApp
   - String getName();   // 获取应用名称
   - String getCmdStr(); // 需要执行的命令
   - String getRunPath(); // 默认实现返回null，表示指令执行目录环境
   - 其余基类函数已默认实现


### 详细说明

**MiniApp** 为基础协议，所有的扩展实现都需要继承或者实现该协议，
主应用程序只会扫描到实现了该接口的类，并且注册到容器中。
MiniAppGroup是对MiniApp的扩展，旨在一次性批量注入MiniApp，
同样也会被扫描到，其下的miniApp也都会被注册。

MiniApp中，getName 是用于获取任务名称的，不用担心重复导致程序异常
(因为注册时采用的是类完整访问路径注册的)， 不过建议名称具有标志性，
这样才能快速识别该任务所具备的特质。

getPanel 是绘制的面板，即右面板，实际的操作区，可以自定义，
也可以采用模板快速构建（如ProcessAppTemplate）。
如果自定义，或者制作模板，需要注意以下几点：

1. 长连接的任务，强烈建议做好资源的调度，适量增加 任务控制按钮
   (启动、终止)；如果可监测，建议增加连接心跳监测机制，
   在一定时间后，自动断开连接。
2. 从A任务切换到B任务，A任务面板会销毁，调用B任务面板，
   但是任务的实例不会销毁，所以，你可以将一些数据存于全局变量来存储
   上一会话的某些状态量。
3. 主程序面板的大小是固定的，所以，为了界面协调，建议纵向布局，
   并且内容较多的话，适量增加滚动条。
4. 任务自身不建议开辟太大的内存空间，以免出现任务切换卡顿现象，
   如果一定要用大量内存，也做好完备的内存管控，避免出现OOM的情况。

protect() 是保护函数，如果返回true，则表示在BKPG进行RELOAD时，该App不会被注销，
而是保持实例不变，状态也都保持。这通常作为服务器服务非常关键，比如想要添加一个指令，
需要RELOAD但是原有服务不能关闭，如果设置为true就可以实现保护。

**ProcessAppTemplate** 为命令执行协议模板，为抽象类，继承实现了MiniApp接口，
用于执行命令行指令，需要实现两个接口，一个是getName，作用同上，另外一个是该抽象类
独有的，getCmdStr，需要设置命令行的完整执行指令。当点击面板的执行按钮时，
会启动一个线程来执行该命令，并且每5s会检测一次命令心跳，如果心跳失去连接，
则会释放资源。
如果说有的指令需要在特定目录下执行，可以覆盖getRunPath，来指定运行环境。
可以作为一些应用或者服务的启动端。


为了更加高效的添加命令（常用），
官方内置了一个命令AppGroup: **PLUGIN_BKPG_GROUP_PROCESS** 。
通过配置ext/ProgressGroup.yml来添加指令应用，
会自动创建多组ProcessAppTemplate。格式参考：
```yml
processBeanList:
  - name: PING百度
    cmd: ping www.baidu.com
  - name: PING主站
    cmd: ping www.dreamcenter.top
  - name: nginx
    cmd: D:\basic\nginx-1.24.0\nginx
    path: D:\basic\nginx-1.24.0
```



### 第三方扩展指南
首先，下载BKPG_PROTOCAL协议JAR包，引入自己的项目中，作为环境依赖。
之后在自己项目中实现MiniApp/MiniAppGroup接口或者ProcessAppTemplate抽象类即可。
将JAR包生成路径配置到ext目录下，此时生成JAR包，点击主程序系统托盘右键的RELOAD选项，
即可看到JAR包中的MiniApp都已经加载完毕了。

你可以参考BKPG_DEMO的项目或者直接套用该demo来架构一个项目。
注意，最后打包时，不应该包含BKPG_PROTOCAL这个依赖，是冗余的，建议排除在外。

一个JAR包项目中可以包含多个MiniApp，这些MiniApp如果没有特殊机制劫持，
最后全部会被装载进入到容器之中。

如果您开发了自己的工具包，并且认为非常具有价值，你也可以分享自己的JAR包给别人，
或者分享给官方，由我们加入README进行大众分享。





