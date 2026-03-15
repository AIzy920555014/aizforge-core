# AizForge Core Local Startup Guide

This document is a step-by-step startup guide for the local backend project on this Mac.

Repository path:

- `/Users/aizy/IdeaProjects/ruoyi-vue-pro`

Backend port:

- `48080`

Dependencies used by this project:

- Java `21`
- Maven `3.9+`
- MySQL `8.4.x`
- Redis `7.x`

## 1. Load the local environment

Open a new terminal and run:

```zsh
source /Users/aizy/.local/share/java-mysql/env.zsh
```

Verify the core tools:

```zsh
java -version
mvn -version
mysql --version
redis-server --version
```

If one of these commands fails, stop here and fix the environment first.

## 2. Confirm MySQL is available

This project uses the local MySQL instance already installed on this machine.

Expected connection:

- host: `127.0.0.1`
- port: `3306`
- database: `ruoyi-vue-pro`
- username: `ruoyi`
- password: `123456`

Check MySQL login:

```zsh
mysql -uruoyi -p123456 -h127.0.0.1 -P3306 -D "ruoyi-vue-pro" -e "show tables;" | head
```

If this command fails:

- make sure MySQL is running
- make sure the database `ruoyi-vue-pro` exists
- make sure user `ruoyi` still has access

## 3. Confirm Redis is available

The backend is configured to use:

- host: `127.0.0.1`
- port: `6379`
- database: `0`

Quick check:

```zsh
redis-cli -h 127.0.0.1 -p 6379 ping
```

Expected output:

```text
PONG
```

If Redis is not running, start a local Redis process:

```zsh
redis-server /Users/aizy/.local/redis/redis.conf
```

If the port is already occupied, check whether Redis is already running before starting a new one.

## 4. Confirm the local backend config

The current local profile file is:

- `/Users/aizy/IdeaProjects/ruoyi-vue-pro/yudao-server/src/main/resources/application-local.yaml`

Key values already set:

- server port: `48080`
- MySQL: `127.0.0.1:3306/ruoyi-vue-pro`
- Redis: `127.0.0.1:6379`

You normally do not need to edit this file unless the local database or Redis address changes.

## 5. Build the project

From the repository root:

```zsh
cd /Users/aizy/IdeaProjects/ruoyi-vue-pro
mvn -pl yudao-server -am clean package -DskipTests
```

What this command does:

- builds the backend modules needed by `yudao-server`
- skips tests for faster local startup
- produces the runnable jar under `yudao-server/target/`

## 6. Start the backend

After a successful build:

```zsh
cd /Users/aizy/IdeaProjects/ruoyi-vue-pro
java -jar yudao-server/target/yudao-server.jar --spring.profiles.active=local
```

Expected behavior:

- the service starts on port `48080`
- startup logs scroll in the terminal
- the process keeps running until you stop it

## 7. Verify startup

Open a second terminal and run:

```zsh
curl -I http://127.0.0.1:48080
```

You can also test a login-related endpoint from the frontend later, but the simplest verification is:

- the process stays up
- no continuous exception loop appears
- port `48080` is listening

Check the port:

```zsh
lsof -iTCP:48080 -sTCP:LISTEN
```

## 8. Recommended way to work day to day

Daily startup order:

1. `source /Users/aizy/.local/share/java-mysql/env.zsh`
2. confirm MySQL
3. confirm Redis
4. start the backend jar
5. start the frontend from `aizforge-web`

If you mainly work in IntelliJ IDEA, you can also run the backend from IDEA after importing the Maven project, but the jar command above is the simplest known-good path.

## 9. Stop the backend

If the backend is running in the foreground terminal:

- press `Ctrl + C`

If you need to kill it by port:

```zsh
lsof -tiTCP:48080 -sTCP:LISTEN | xargs kill
```

## 10. Common problems

### Problem: port `48080` is already in use

Check:

```zsh
lsof -iTCP:48080 -sTCP:LISTEN
```

Then stop the old process and start again.

### Problem: database connection failed

Check:

```zsh
mysql -uruoyi -p123456 -h127.0.0.1 -P3306 -D "ruoyi-vue-pro" -e "select 1;"
```

If this fails, the backend will not start correctly.

### Problem: Redis connection failed

Check:

```zsh
redis-cli -h 127.0.0.1 -p 6379 ping
```

If this fails, start Redis first.

### Problem: build succeeds but startup fails with profile issues

Make sure you are using:

```text
--spring.profiles.active=local
```

### Problem: frontend opens but cannot call backend

The frontend local mode is configured to call:

- `http://localhost:48080`

So the backend must be running before the frontend is useful.
