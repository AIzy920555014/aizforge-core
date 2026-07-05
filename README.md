# 智企云枢

**AIzyForge 智企云枢** 是面向企业的一体化智能管理平台，提供权限管理、工作流、CRM、ERP、会员、支付、报表与 AI 等能力。

- 生产地址：<https://aixy99.site>
- 后端：Java 17+、Spring Boot 3.5
- 前端：Vue 3、TypeScript、Element Plus
- 数据库：MariaDB / MySQL
- 项目仓库：<https://github.com/AIzy920555014/aizyforge-core>

## 项目结构

- `yudao-server`：后端启动模块
- `yudao-module-*`：业务模块
- `yudao-ui/yudao-ui-admin-vue3`：管理后台前端
- `sql/mysql`：数据库初始化与迁移脚本

## 本地启动

本项目需要 MySQL、Redis、Java、Maven、Node.js 和 pnpm。完整配置与 IntelliJ IDEA 启动步骤见 [LOCAL_STARTUP.md](./LOCAL_STARTUP.md)。

前端启动：

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm install
pnpm dev
```

## 安全说明

- 首次部署后必须修改管理员密码。
- 数据库、缓存及第三方服务密钥只通过环境变量注入，不提交到 Git。
- 默认关闭未配置凭据的外部集成。

## 开源许可

本项目包含 MIT 许可证代码，许可文本见 [LICENSE](./LICENSE)。
