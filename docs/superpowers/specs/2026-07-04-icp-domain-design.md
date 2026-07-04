# 备案号展示与域名映射设计

## 目标

- 在公众可访问的登录页底部展示 `冀ICP备2026021324号`。
- 在登录后的全局页脚同步展示该备案号。
- 备案号链接到工信部备案查询页 `https://beian.miit.gov.cn/`，使用新窗口打开。
- 将 `aixy99.site` 明确映射到当前管理系统，并启用 HTTPS。
- HTTP 请求自动跳转到 HTTPS，现有后端 API、Swagger 和 WebSocket 代理保持可用。

## 前端设计

### 登录页

在 `yudao-ui/yudao-ui-admin-vue3/src/views/Login/Login.vue` 的根容器底部增加备案链接。链接采用低对比度文字，不遮挡登录表单，并在桌面和移动视口保持居中可见。

### 登录后页脚

在 `yudao-ui/yudao-ui-admin-vue3/src/layout/components/Footer/src/Footer.vue` 的现有版权信息后增加分隔符和备案链接。沿用当前页脚高度与主题色，避免改变页面布局。

### 链接属性

两个入口统一使用：

- `href="https://beian.miit.gov.cn/"`
- `target="_blank"`
- `rel="noopener noreferrer"`

## 域名与 HTTPS 设计

- Nginx 的站点 `server_name` 显式设置为 `aixy99.site`。
- 保持现有静态资源、SPA 回退、`/admin-api`、`/app-api`、Swagger、Actuator 和 WebSocket 代理规则。
- 使用 Let’s Encrypt 为 `aixy99.site` 申请证书。
- 证书签发成功后开启 443，并将 80 端口请求重定向到同路径的 HTTPS 地址。
- 不立即启用 HSTS，避免证书或 DNS 调整期间产生不可逆的浏览器缓存影响。

## 部署与回滚

- 前端通过 `pnpm ts:check` 和 `pnpm build:prod` 后生成新发布目录。
- 上传前保留当前前端发布目录和 Nginx 配置备份。
- Nginx 配置必须先通过 `nginx -t` 才允许 reload。
- 若证书申请失败，保留可用的 HTTP 域名访问，不影响现有 IP 和后端服务。
- 若新前端验证失败，将 `current` 软链接切回上一发布目录。

## 验收标准

- 未登录访问 `https://aixy99.site/` 时，登录页可见备案号。
- 登录后任意后台页面的全局页脚可见备案号。
- 点击备案号会在新窗口打开工信部备案查询页。
- `http://aixy99.site/` 自动跳转到 `https://aixy99.site/`。
- HTTPS 证书域名匹配且浏览器验证通过。
- 登录、后台首页、后端健康检查和 Swagger 均可通过域名正常访问。
- 浏览器控制台没有与本次改动相关的错误。
