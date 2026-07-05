# 智企云枢管理后台

智企云枢是 AIzyForge 打造的企业一体化智能管理平台。本目录为 Vue 3 管理后台源码。

## 环境要求

- Node.js 20.19+
- pnpm 8.6+
- 后端服务默认运行在 `http://localhost:48080`

## 本地启动

```bash
pnpm install
pnpm dev
```

浏览器访问终端输出的本地地址。生产构建与检查：

```bash
pnpm ts:check
pnpm build:prod
```

## 环境变量

- `VITE_BASE_URL`：前端部署路径，默认 `/`
- `VITE_API_URL`：后端 API 地址；本地开发通常使用 `/admin-api`
- `VITE_DEV`：是否启用开发模式

真实账号、密码和第三方密钥不得写入 `.env` 文件或提交到 Git。

线上地址：[https://aixy99.site](https://aixy99.site)

项目仓库：[AIzyForge/aizyforge-core](https://github.com/AIzy920555014/aizyforge-core)
