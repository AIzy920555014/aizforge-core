# AizForge Core 本机启动文档

这是一份基于你当前这台 Mac 的后端本机启动说明，按步骤操作即可。

项目目录：

- `/Users/aizy/IdeaProjects/ruoyi-vue-pro`

后端端口：

- `48080`

当前依赖：

- Java `21`
- Maven `3.9+`
- MySQL `8.4.x`
- Redis `7.x`

## 1. 先加载本机环境

新开一个终端，先执行：

```zsh
source /Users/aizy/.local/share/java-mysql/env.zsh
```

然后检查核心命令是否可用：

```zsh
java -version
mvn -version
mysql --version
redis-server --version
```

如果这几条有任何一条报错，先不要继续，先把环境修好。

## 2. 检查 MySQL 是否正常

当前项目使用的是你本机已经安装好的 MySQL。

连接信息如下：

- 主机：`127.0.0.1`
- 端口：`3306`
- 数据库：`ruoyi-vue-pro`
- 用户名：`ruoyi`
- 密码：`123456`

检查命令：

```zsh
mysql -uruoyi -p123456 -h127.0.0.1 -P3306 -D "ruoyi-vue-pro" -e "show tables;" | head
```

如果失败，优先检查：

- MySQL 是否已经启动
- 数据库 `ruoyi-vue-pro` 是否存在
- 用户 `ruoyi` 是否还有权限

## 3. 检查 Redis 是否正常

后端当前连接的是：

- 主机：`127.0.0.1`
- 端口：`6379`
- 数据库：`0`

检查命令：

```zsh
redis-cli -h 127.0.0.1 -p 6379 ping
```

正常结果应该是：

```text
PONG
```

如果 Redis 没启动，可以用本机配置启动：

```zsh
redis-server /Users/aizy/.local/redis/redis.conf
```

如果提示端口已占用，先确认是不是已经有 Redis 在运行，不要重复起多个实例。

## 4. 检查本地后端配置

当前本地配置文件是：

- `/Users/aizy/IdeaProjects/ruoyi-vue-pro/yudao-server/src/main/resources/application-local.yaml`

里面已经配置好了这些关键项：

- 后端端口：`48080`
- MySQL：`127.0.0.1:3306/ruoyi-vue-pro`
- Redis：`127.0.0.1:6379`

正常情况下你不需要再改这个文件，除非本机数据库或 Redis 地址变了。

## 5. 编译项目

进入项目根目录执行：

```zsh
cd /Users/aizy/IdeaProjects/ruoyi-vue-pro
mvn -pl yudao-server -am clean package -DskipTests
```

这条命令的作用：

- 编译 `yudao-server` 需要的模块
- 跳过测试，加快本机启动速度
- 生成可运行的 jar 包

生成物在这里：

- `yudao-server/target/yudao-server.jar`

## 6. 启动后端

编译成功后执行：

```zsh
cd /Users/aizy/IdeaProjects/ruoyi-vue-pro
java -jar yudao-server/target/yudao-server.jar --spring.profiles.active=local
```

正常情况下：

- 服务会启动在 `48080`
- 终端会持续打印启动日志
- 不要关闭这个终端，关掉就等于后端停止

## 7. 验证是否启动成功

新开一个终端，执行：

```zsh
curl -I http://127.0.0.1:48080
```

也可以检查端口是否在监听：

```zsh
lsof -iTCP:48080 -sTCP:LISTEN
```

如果服务没异常退出，并且端口在监听，说明后端已经起来了。

## 8. 日常启动顺序建议

以后你每天本机开发，建议按这个顺序来：

1. `source /Users/aizy/.local/share/java-mysql/env.zsh`
2. 确认 MySQL 正常
3. 确认 Redis 正常
4. 启动后端
5. 再启动前端

如果你主要用 IntelliJ IDEA 开发，也可以把项目导入 IDEA 后直接在 IDEA 里运行，但命令行 jar 启动是当前最稳定、最容易复现的方式。

## 9. 如何停止后端

如果后端正在当前终端前台运行：

- 直接按 `Ctrl + C`

如果你想按端口杀掉：

```zsh
lsof -tiTCP:48080 -sTCP:LISTEN | xargs kill
```

## 10. 常见问题

### 问题 1：`48080` 端口被占用

先查是谁占用了端口：

```zsh
lsof -iTCP:48080 -sTCP:LISTEN
```

停掉旧进程后再重新启动。

### 问题 2：数据库连接失败

执行：

```zsh
mysql -uruoyi -p123456 -h127.0.0.1 -P3306 -D "ruoyi-vue-pro" -e "select 1;"
```

这条如果失败，后端基本不可能正常启动。

### 问题 3：Redis 连接失败

执行：

```zsh
redis-cli -h 127.0.0.1 -p 6379 ping
```

如果不是 `PONG`，先启动 Redis。

### 问题 4：启动时 profile 不对

确认你使用的是：

```text
--spring.profiles.active=local
```

### 问题 5：前端能打开，但调后端接口失败

前端本地模式会请求：

- `http://localhost:48080`

所以后端必须先启动，前端才有意义。
