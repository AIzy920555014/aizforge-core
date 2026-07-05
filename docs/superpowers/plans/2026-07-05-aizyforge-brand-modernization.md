# AIzyForge「智企云枢」品牌改造实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将当前系统完整品牌化为“智企云枢 / AIzyForge”，清除对外旧品牌、演示数据、演示资源和明文密钥，并将仓库及生产部署技术标识统一为 `aizyforge`。

**Architecture:** 保留 `cn.iocoder.yudao`、`yudao-*` 和数据库表名等内部兼容性标识，只修改对外显示层、运行配置、初始化数据和可安全迁移的部署标识。先用自动化品牌审计脚本建立边界，再分别完成前端、后端、安全、数据库、仓库和服务器迁移，每一阶段都可独立验证和回滚。

**Tech Stack:** Vue 3、TypeScript、Vite、Element Plus、Spring Boot 3.5、Java 17、Maven、MariaDB、Redis、Nginx、systemd、Bash、GitHub CLI、Playwright。

---

## 文件结构与职责

### 新建文件

- `script/verify-aizyforge-brand.sh`：扫描源码关键入口与构建产物，阻止旧品牌和官方演示资源重新进入对外产物。
- `script/test-verify-aizyforge-brand.sh`：使用临时夹具验证品牌扫描器能正确放行和拦截。
- `yudao-ui/yudao-ui-admin-vue3/public/brand-mark.svg`：浏览器 favicon、加载页和二维码使用的公共品牌图形。
- `yudao-ui/yudao-ui-admin-vue3/src/assets/svgs/brand-mark.svg`：登录、导航、头像等组件导入的品牌图形。
- `sql/mysql/2026-07-05-aizyforge-brand-migration.sql`：可重复执行的生产数据库品牌与演示数据清理脚本。

### 主要修改文件

- `yudao-ui/yudao-ui-admin-vue3/.env*`：系统标题、租户、文档开关、统计、默认账号和外部系统地址。
- `yudao-ui/yudao-ui-admin-vue3/index.html`：favicon、SEO 元数据和加载图形。
- `yudao-ui/yudao-ui-admin-vue3/src/views/Login/**`：登录品牌、默认租户和推广链接。
- `yudao-ui/yudao-ui-admin-vue3/src/views/Home/Index.vue`：移除官方项目推广和虚构指标，改为企业管理入口。
- `yudao-ui/yudao-ui-admin-vue3/src/layout/components/{Logo,UserInfo,Message}/**`：品牌图形、默认头像和官方文档入口。
- `yudao-ui/yudao-ui-admin-vue3/src/components/{Cropper,DiyEditor,DocAlert}/**`：默认图形和文档提示。
- `yudao-ui/yudao-ui-admin-vue3/src/views/ai/**`、`src/views/mp/**`：旧品牌文字和远程演示图片。
- `yudao-ui/yudao-ui-admin-vue3/package.json`：前端技术品牌和新仓库地址。
- `yudao-server/src/main/resources/application*.yaml`：Spring 应用名、Swagger、水印、密钥环境变量和本地连接凭据。
- `yudao-framework/yudao-spring-boot-starter-web/src/main/resources/banner.txt`：启动横幅。
- `pom.xml`、`yudao-server/pom.xml`、`yudao-dependencies/pom.xml`：项目描述和仓库元数据，内部 artifactId 保持不变。
- `sql/mysql/ruoyi-vue-pro.sql`：新安装环境的品牌、账号和存储初始化数据。
- `README.md`、`LOCAL_STARTUP.md`：智企云枢项目说明和安全配置方法。

### 生产环境变更

- GitHub：`AIzy920555014/aizforge-core` → `AIzy920555014/aizyforge-core`
- systemd：`aizforge.service` → `aizyforge.service`
- 后端目录：`/opt/aizforge` → `/opt/aizyforge`
- 前端目录：`/var/www/aizforge` → `/var/www/aizyforge`
- 配置目录：`/etc/aizforge` → `/etc/aizyforge`
- Nginx：切换到新前端目录和 `aizyforge` 配置文件，域名与证书不变

---

### Task 1: 建立品牌审计护栏

**Files:**

- Create: `script/verify-aizyforge-brand.sh`
- Create: `script/test-verify-aizyforge-brand.sh`

- [ ] **Step 1: 编写品牌扫描器单元测试**

创建 `script/test-verify-aizyforge-brand.sh`：

```bash
#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERIFY_SCRIPT="${SCRIPT_DIR}/verify-aizyforge-brand.sh"
FIXTURE_ROOT="$(mktemp -d)"
trap 'rm -rf "${FIXTURE_ROOT}"' EXIT

mkdir -p "${FIXTURE_ROOT}/ok/dist-prod" "${FIXTURE_ROOT}/bad/dist-prod"
printf '%s\n' '智企云枢 AIzyForge' > "${FIXTURE_ROOT}/ok/dist-prod/app.js"
printf '%s\n' '访问 https://doc.iocoder.cn 获取芋道源码教程' \
  > "${FIXTURE_ROOT}/bad/dist-prod/app.js"

"${VERIFY_SCRIPT}" --artifact-root "${FIXTURE_ROOT}/ok"
if "${VERIFY_SCRIPT}" --artifact-root "${FIXTURE_ROOT}/bad"; then
  echo "Expected forbidden branding to fail" >&2
  exit 1
fi

echo "Brand verifier fixture tests passed"
```

- [ ] **Step 2: 运行测试并确认失败**

Run:

```bash
bash script/test-verify-aizyforge-brand.sh
```

Expected: FAIL，因为 `script/verify-aizyforge-brand.sh` 尚不存在。

- [ ] **Step 3: 实现品牌扫描器**

创建 `script/verify-aizyforge-brand.sh`：

```bash
#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ARTIFACT_ROOT="${REPO_ROOT}/yudao-ui/yudao-ui-admin-vue3"

if [[ "${1:-}" == "--artifact-root" ]]; then
  ARTIFACT_ROOT="${2:?artifact root is required}"
fi

FORBIDDEN_REGEX='芋道|芋道源码|芋艿|https?://([^/]*\.)?iocoder\.cn|github\.com/(YunaiV|yudaocode)|gitee\.com/(zhijiantianya|yudaocode)'
TARGETS=()

[[ -d "${ARTIFACT_ROOT}/dist-prod" ]] && TARGETS+=("${ARTIFACT_ROOT}/dist-prod")
[[ -f "${ARTIFACT_ROOT}/index.html" ]] && TARGETS+=("${ARTIFACT_ROOT}/index.html")

if ((${#TARGETS[@]} == 0)); then
  echo "No brand audit targets found under ${ARTIFACT_ROOT}" >&2
  exit 2
fi

if rg -n --hidden --glob '!**/*.map' "${FORBIDDEN_REGEX}" "${TARGETS[@]}"; then
  echo "Forbidden public branding detected" >&2
  exit 1
fi

if ! rg -q '智企云枢' "${TARGETS[@]}"; then
  echo "Required Chinese brand not found" >&2
  exit 1
fi

echo "AIzyForge public branding verification passed"
```

为两个脚本增加执行权限：

```bash
chmod +x script/verify-aizyforge-brand.sh script/test-verify-aizyforge-brand.sh
```

- [ ] **Step 4: 运行夹具测试**

Run:

```bash
bash script/test-verify-aizyforge-brand.sh
```

Expected: 输出 `Brand verifier fixture tests passed`。

- [ ] **Step 5: 对当前生产构建运行审计并记录红灯**

Run:

```bash
bash script/verify-aizyforge-brand.sh
```

Expected: FAIL，并列出当前构建中的旧品牌或官方演示地址。

- [ ] **Step 6: 提交审计护栏**

```bash
git add script/verify-aizyforge-brand.sh script/test-verify-aizyforge-brand.sh
git commit -m "test: add AIzyForge brand audit"
```

---

### Task 2: 创建并接入统一品牌图形

**Files:**

- Create: `yudao-ui/yudao-ui-admin-vue3/public/brand-mark.svg`
- Create: `yudao-ui/yudao-ui-admin-vue3/src/assets/svgs/brand-mark.svg`
- Modify: `yudao-ui/yudao-ui-admin-vue3/index.html`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/Login.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/SocialLogin.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/components/QrCodeForm.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/Logo/src/Logo.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/UserInfo/src/UserInfo.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/UserInfo/src/components/LockDialog.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/UserInfo/src/components/LockPage.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/Message/src/Message.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/components/Cropper/src/CropperAvatar.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/components/DiyEditor/index.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Home/Index.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/ai/chat/index/components/message/MessageList.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/mp/components/wx-msg/main.vue`
- Delete: `yudao-ui/yudao-ui-admin-vue3/public/favicon.ico`
- Delete: `yudao-ui/yudao-ui-admin-vue3/public/logo.gif`
- Delete: `yudao-ui/yudao-ui-admin-vue3/src/assets/imgs/logo.png`
- Delete: `yudao-ui/yudao-ui-admin-vue3/src/assets/imgs/avatar.gif`
- Delete: `yudao-ui/yudao-ui-admin-vue3/src/assets/imgs/profile.jpg`

- [ ] **Step 1: 创建 SVG 品牌图形**

两个 `brand-mark.svg` 使用相同内容：

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" role="img" aria-label="智企云枢">
  <defs>
    <linearGradient id="brand-gradient" x1="8" y1="8" x2="56" y2="56">
      <stop offset="0" stop-color="#2563eb"/>
      <stop offset="1" stop-color="#7c3aed"/>
    </linearGradient>
  </defs>
  <rect width="64" height="64" rx="16" fill="url(#brand-gradient)"/>
  <path d="M20 25 32 18l12 7v14L32 46 20 39Z" fill="none" stroke="#fff" stroke-width="4" stroke-linejoin="round"/>
  <circle cx="20" cy="25" r="4" fill="#fff"/>
  <circle cx="44" cy="25" r="4" fill="#fff"/>
  <circle cx="32" cy="46" r="4" fill="#fff"/>
  <circle cx="32" cy="32" r="6" fill="#fff"/>
  <path d="m23 27 5 3m13-3-5 3m-4 8v4" stroke="#fff" stroke-width="3" stroke-linecap="round"/>
</svg>
```

- [ ] **Step 2: 将组件资源引用统一改为 SVG**

在 Vue/TypeScript 组件中统一：

```ts
import brandMark from '@/assets/svgs/brand-mark.svg'
```

模板统一：

```vue
<img :src="brandMark" alt="智企云枢" />
```

公共页面使用：

```html
<link rel="icon" type="image/svg+xml" href="/brand-mark.svg" />
<img src="/brand-mark.svg" class="app-loading-logo" alt="智企云枢" />
```

二维码 Logo 使用：

```vue
<Qrcode :text="previewUrl" logo="/brand-mark.svg" />
```

- [ ] **Step 3: 删除旧图片并验证无引用**

Run:

```bash
rg -n 'logo\\.(png|gif)|avatar\\.gif|profile\\.jpg|favicon\\.ico' \
  yudao-ui/yudao-ui-admin-vue3/src \
  yudao-ui/yudao-ui-admin-vue3/public \
  yudao-ui/yudao-ui-admin-vue3/index.html
```

Expected: 无输出。

- [ ] **Step 4: 运行前端类型检查**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm ts:check
```

Expected: exit 0。

- [ ] **Step 5: 保留前端改动到品牌清理完成**

当前完整前端源码尚未全部被 Git 跟踪，且 `.env` 仍将在下一任务清理。此时不执行 `git add yudao-ui/yudao-ui-admin-vue3`，避免把旧密钥和旧品牌写入新的 Git 历史。使用以下命令确认改动仅位于前端目录：

```bash
git status --short yudao-ui/yudao-ui-admin-vue3
```

---

### Task 3: 更新前端标题、登录和环境配置

**Files:**

- Modify: `yudao-ui/yudao-ui-admin-vue3/.env`
- Modify: `yudao-ui/yudao-ui-admin-vue3/.env.local`
- Modify: `yudao-ui/yudao-ui-admin-vue3/.env.dev`
- Modify: `yudao-ui/yudao-ui-admin-vue3/.env.test`
- Modify: `yudao-ui/yudao-ui-admin-vue3/.env.stage`
- Modify: `yudao-ui/yudao-ui-admin-vue3/.env.prod`
- Modify: `yudao-ui/yudao-ui-admin-vue3/index.html`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/components/LoginForm.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/components/MobileForm.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/SocialLogin.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/ai/chat/index/components/message/MessageListEmpty.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/components/DiyEditor/components/mobile/UserCard/index.vue`

- [ ] **Step 1: 写入统一前端品牌配置**

`.env` 中使用：

```dotenv
VITE_APP_TITLE=智企云枢
VITE_APP_TENANT_ENABLE=true
VITE_APP_CAPTCHA_ENABLE=true
VITE_APP_DOCALERT_ENABLE=false
VITE_APP_BAIDU_CODE=
VITE_APP_DEFAULT_LOGIN_TENANT=智企云枢
VITE_APP_DEFAULT_LOGIN_USERNAME=
VITE_APP_DEFAULT_LOGIN_PASSWORD=
VITE_APP_API_ENCRYPT_ENABLE=false
VITE_APP_API_ENCRYPT_HEADER=X-Api-Encrypt
VITE_APP_API_ENCRYPT_ALGORITHM=AES
VITE_APP_API_ENCRYPT_REQUEST_KEY=
VITE_APP_API_ENCRYPT_RESPONSE_KEY=
VITE_BAIDU_MAP_KEY=
```

各环境统一规则：

- `.env.local`：`VITE_BASE_URL='http://localhost:48080'`
- `.env.dev`：`VITE_BASE_URL='http://localhost:48080'`
- `.env.test`：`VITE_BASE_URL='http://localhost:48080'`
- `.env.stage`：`VITE_BASE_URL=''`、`VITE_BASE_PATH='/'`
- `.env.prod`：`VITE_BASE_URL=''`、`VITE_BASE_PATH='/'`
- 所有环境的 `VITE_MALL_H5_DOMAIN=''`
- 未配置 GoView 时 `VITE_GOVIEW_URL=''`

- [ ] **Step 2: 更新 HTML 元数据**

`index.html` 中设置：

```html
<meta
  name="description"
  content="智企云枢是 AIzyForge 打造的企业一体化智能管理平台。"
/>
<meta
  name="keywords"
  content="智企云枢,AIzyForge,企业管理,ERP,CRM,MES,WMS"
/>
<title>%VITE_APP_TITLE%</title>
```

- [ ] **Step 3: 删除登录页推广区**

从 `LoginForm.vue` 删除“萌新必读”分隔线以及四个官方链接所在的整个 `el-col`。保留账号登录、注册、社交登录等功能。

- [ ] **Step 4: 替换辅助登录默认租户**

`MobileForm.vue` 和 `SocialLogin.vue` 中的表单初始值统一改为：

```ts
tenantName: import.meta.env.VITE_APP_DEFAULT_LOGIN_TENANT || ''
```

- [ ] **Step 5: 更新页面内品牌文案**

```vue
<!-- MessageListEmpty.vue -->
<div class="text-28px font-bold text-center">智企云枢 AI</div>

<!-- UserCard/index.vue -->
<span class="text-18px font-bold">智企云枢</span>
```

- [ ] **Step 6: 验证直接显示位置**

Run:

```bash
rg -n --hidden '芋道|芋道源码|api-dashboard\\.yudao|mall\\.yudao|static-vue3\\.yudao' \
  yudao-ui/yudao-ui-admin-vue3/.env* \
  yudao-ui/yudao-ui-admin-vue3/index.html \
  yudao-ui/yudao-ui-admin-vue3/src/views/Login \
  yudao-ui/yudao-ui-admin-vue3/src/views/ai/chat/index/components/message/MessageListEmpty.vue \
  yudao-ui/yudao-ui-admin-vue3/src/components/DiyEditor/components/mobile/UserCard/index.vue
```

Expected: 只允许代码注释中的作者信息；任何模板文本、字符串或 URL 输出都必须修复。

- [ ] **Step 7: 类型检查并保留改动**

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm ts:check
cd ../../..
git status --short yudao-ui/yudao-ui-admin-vue3
```

Expected: 类型检查通过；前端品牌改动继续保留，待远程演示资源全部清理后一次性提交完整可构建前端。

---

### Task 4: 清理首页、导航与远程演示资源

**Files:**

- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Home/Index.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/UserInfo/src/UserInfo.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/components/DocAlert/index.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/config/axios/service.ts`
- Modify: all frontend files containing `doc.iocoder.cn`, `*.yudao.iocoder.cn`, upstream GitHub/Gitee links, or remote demo images

- [ ] **Step 1: 将首页改为无推广的企业工作台**

`Home/Index.vue` 保留用户欢迎区，删除以下内容：

- 官方 GitHub “更多”链接
- `projects` 官方仓库卡片
- 虚构的项目数、访问量和待办数量
- 官方项目兼容性和开源宣传公告

新增静态模块入口数据：

```ts
const shortcuts = [
  { name: '用户管理', icon: 'ep:user', url: '/system/user', color: '#2563eb' },
  { name: '流程中心', icon: 'ep:connection', url: '/bpm/task/todo', color: '#7c3aed' },
  { name: '客户管理', icon: 'ep:briefcase', url: '/crm/backlog', color: '#0891b2' },
  { name: 'ERP 管理', icon: 'ep:goods', url: '/erp/home', color: '#059669' },
  { name: 'AI 助手', icon: 'ep:magic-stick', url: '/ai/chat', color: '#db2777' },
  { name: '系统配置', icon: 'ep:setting', url: '/system/tenant', color: '#475569' }
]
```

欢迎说明使用：

```vue
<div class="text-24px font-bold">欢迎使用智企云枢</div>
<div class="mt-8px text-14px text-gray-500">
  面向企业的一体化智能管理平台
</div>
```

- [ ] **Step 2: 删除用户菜单中的官方文档入口**

删除 `toDocument` 方法和对应 `ElDropdownItem`，保留个人中心、锁屏和退出登录。

- [ ] **Step 3: 关闭并收紧 DocAlert**

`DocAlert/index.vue` 的显示条件同时要求开关开启且 URL 非空：

```ts
const show = computed(() => {
  return import.meta.env.VITE_APP_DOCALERT_ENABLE === 'true' && Boolean(props.url)
})
```

- [ ] **Step 4: 批量清空官方文档 URL**

对前端源码执行受控机械替换：

```bash
ruby -pi -e 'gsub(%r{https?://doc\\.iocoder\\.cn/[^\"'\"'\"'\\s<]*}, \"\")' \
  $(rg -l 'https?://doc\\.iocoder\\.cn/' yudao-ui/yudao-ui-admin-vue3/src)
```

随后逐项处理剩余运行时字符串：

```bash
rg -n 'iocoder\\.cn|github\\.com/(YunaiV|yudaocode)|gitee\\.com/(zhijiantianya|yudaocode)' \
  yudao-ui/yudao-ui-admin-vue3/src
```

规则：

- 运行时链接、图片和错误提示删除或改为本地资源。
- 仅保留不会进入构建产物的技术注释和 upstream 问题引用。
- `service.ts` 的错误提示改为“请联系系统管理员”。

- [ ] **Step 5: 生产构建并扫描产物**

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm build:prod
cd ../../..
bash script/verify-aizyforge-brand.sh
```

Expected: 构建成功，品牌审计通过；如构建产物仍含官方域名，使用 `rg` 定位源文件并清理后重建。

- [ ] **Step 6: 提交前端内容清理**

```bash
git add yudao-ui/yudao-ui-admin-vue3
test -z "$(git diff --cached --name-only | rg '(^|/)(node_modules|dist[^/]*)/')"
! git diff --cached -- yudao-ui/yudao-ui-admin-vue3/.env \
  | rg 'admin123|a1ff8825baa73c3a78eb96aa40325abc|Y2aJXiswwPxy6mwFs1z9c7U5gwX9WfUN'
git commit -m "feat: remove upstream promotions and demo resources"
```

---

### Task 5: 更新后端品牌、Swagger 和接口示例

**Files:**

- Modify: `yudao-server/src/main/resources/application.yaml`
- Modify: `yudao-framework/yudao-spring-boot-starter-web/src/main/resources/banner.txt`
- Modify: `pom.xml`
- Modify: `yudao-server/pom.xml`
- Modify: `yudao-dependencies/pom.xml`
- Modify: Java files under `*/src/main/java/**` whose `@Schema` or `@Parameter` example contains old brand text

- [ ] **Step 1: 修改后端对外品牌配置**

`application.yaml` 设置：

```yaml
spring:
  application:
    name: aizyforge-server

aj:
  captcha:
    water-mark: 智企云枢

yudao:
  web:
    admin-ui:
      url: https://aixy99.site
  api-encrypt:
    enable: false
    request-key: ${AIZYFORGE_API_ENCRYPT_REQUEST_KEY:}
    response-key: ${AIZYFORGE_API_ENCRYPT_RESPONSE_KEY:}
  swagger:
    title: 智企云枢开放接口
    description: 面向企业的一体化智能管理平台接口说明
    version: ${yudao.info.version}
    url: ${yudao.web.admin-ui.url}
    email: ''
    license: MIT
    license-url: https://github.com/AIzy920555014/aizyforge-core/blob/master-jdk17/LICENSE
```

保留 `yudao:` 配置前缀和 `cn.iocoder.yudao` 基础包。

- [ ] **Step 2: 修改启动横幅**

`banner.txt`：

```text
AIzyForge 智企云枢 https://aixy99.site
Application Version: ${yudao.info.version}
Spring Boot Version: ${spring-boot.version}
```

- [ ] **Step 3: 更新 Maven 元数据**

只修改 `<description>` 和 `<url>`：

```xml
<description>AIzyForge 智企云枢企业一体化智能管理平台</description>
<url>https://github.com/AIzy920555014/aizyforge-core</url>
```

不得修改 `<groupId>cn.iocoder.boot</groupId>`、`yudao-*` artifactId 或 module。

- [ ] **Step 4: 替换 Swagger 示例品牌**

仅处理同时包含 `@Schema` 或 `@Parameter` 的行：

```bash
ruby <<'RUBY'
files = `rg -l '(@Schema|@Parameter).*芋道' --glob '*.java' --glob '**/src/main/**'`.lines.map(&:strip)
files.each do |path|
  text = File.read(path)
  text = text.lines.map do |line|
    next line unless line.include?('@Schema') || line.include?('@Parameter')
    line
      .gsub('芋道技术交流群', '示例交流群')
      .gsub('芋道源码', '示例用户')
      .gsub('芋道', '示例')
  end.join
  File.write(path, text)
end
RUBY
```

验证：

```bash
rg -n '(@Schema|@Parameter).*芋道' --glob '*.java' --glob '**/src/main/**'
```

Expected: 无输出。`@author 芋道源码` 不修改。

- [ ] **Step 5: 编译后端**

```bash
mvn -pl yudao-server -am -DskipTests package
```

Expected: `BUILD SUCCESS`，生成 `yudao-server/target/yudao-server.jar`。

- [ ] **Step 6: 保留后端品牌改动到密钥清理完成**

```bash
git status --short pom.xml yudao-server/pom.xml yudao-dependencies/pom.xml \
  yudao-server/src/main/resources/application.yaml \
  yudao-framework/yudao-spring-boot-starter-web/src/main/resources/banner.txt
```

Expected: 品牌改动保留在工作区，下一任务清除同一配置文件中的明文密钥后再提交。

---

### Task 6: 移除硬编码密钥和弱默认配置

**Files:**

- Modify: `yudao-server/src/main/resources/application.yaml`
- Modify: `yudao-server/src/main/resources/application-local.yaml`
- Modify: `yudao-server/src/main/resources/application-dev.yaml`
- Modify: `LOCAL_STARTUP.md`

- [ ] **Step 1: 将第三方密钥全部改为环境变量**

使用无密钥默认值，例如：

```yaml
spring:
  ai:
    qianfan:
      api-key: ${QIANFAN_API_KEY:}
      secret-key: ${QIANFAN_SECRET_KEY:}
    zhipuai:
      api-key: ${ZHIPUAI_API_KEY:}
    openai:
      api-key: ${OPENAI_API_KEY:}
    anthropic:
      api-key: ${ANTHROPIC_API_KEY:}
    stabilityai:
      api-key: ${STABILITY_AI_API_KEY:}
    dashscope:
      api-key: ${DASHSCOPE_API_KEY:}
    minimax:
      api-key: ${MINIMAX_API_KEY:}
    moonshot:
      api-key: ${MOONSHOT_API_KEY:}
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}

yudao:
  ai:
    gemini:
      enable: ${GEMINI_ENABLE:false}
      api-key: ${GEMINI_API_KEY:}
    doubao:
      enable: ${DOUBAO_ENABLE:false}
      api-key: ${DOUBAO_API_KEY:}
    hunyuan:
      enable: ${HUNYUAN_ENABLE:false}
      api-key: ${HUNYUAN_API_KEY:}
    siliconflow:
      enable: ${SILICONFLOW_ENABLE:false}
      api-key: ${SILICONFLOW_API_KEY:}
    xinghuo:
      enable: ${XINGHUO_ENABLE:false}
      appKey: ${XINGHUO_APP_KEY:}
      secretKey: ${XINGHUO_SECRET_KEY:}
    baichuan:
      enable: ${BAICHUAN_ENABLE:false}
      api-key: ${BAICHUAN_API_KEY:}
    midjourney:
      enable: ${MIDJOURNEY_ENABLE:false}
      api-key: ${MIDJOURNEY_API_KEY:}
    web-search:
      enable: ${WEB_SEARCH_ENABLE:false}
      api-key: ${WEB_SEARCH_API_KEY:}
```

快递配置使用：

```yaml
kd-niao:
  api-key: ${KD_NIAO_API_KEY:}
  business-id: ${KD_NIAO_BUSINESS_ID:}
kd100:
  key: ${KD100_KEY:}
  customer: ${KD100_CUSTOMER:}
```

- [ ] **Step 2: 外部化本地和开发数据库凭据**

`application-local.yaml` 和 `application-dev.yaml` 的数据库、Redis、Spring Boot Admin 登录配置统一引用环境变量：

```yaml
username: ${MYSQL_USERNAME:root}
password: ${MYSQL_PASSWORD:}
```

不保留真实密码或非空弱密码默认值。

- [ ] **Step 3: 更新本地启动说明**

`LOCAL_STARTUP.md` 增加：

```bash
export MYSQL_USERNAME=root
export MYSQL_PASSWORD='你的本地数据库密码'
export REDIS_PASSWORD=''
```

说明第三方 AI、地图和快递功能未提供环境变量时默认关闭。

- [ ] **Step 4: 执行密钥模式扫描**

```bash
rg -n --hidden \
  '(sk-[A-Za-z0-9_-]{16,}|AKID[A-Za-z0-9]{12,}|LTAI[A-Za-z0-9]{12,}|accessSecret\\\\?\"?:\\\\?\"[^\" ]{8,}|api-key:[[:space:]]+[^$#[:space:]][^#]*)' \
  yudao-server/src/main/resources \
  yudao-ui/yudao-ui-admin-vue3/.env* \
  sql/mysql
```

Expected: 不得出现可用密钥；只允许权限名、字段名、空值或 `${ENV_VAR:}`。

- [ ] **Step 5: 编译并提交安全配置**

```bash
mvn -pl yudao-server -am -DskipTests package
git add pom.xml yudao-server/pom.xml yudao-dependencies/pom.xml \
  yudao-server/src/main/resources/application*.yaml \
  yudao-framework/yudao-spring-boot-starter-web/src/main/resources/banner.txt \
  LOCAL_STARTUP.md \
  $(rg -l '示例用户|示例交流群' --glob '*.java' --glob '**/src/main/**')
git commit -m "security: brand backend and externalize credentials"
```

---

### Task 7: 创建并验证数据库迁移

**Files:**

- Create: `sql/mysql/2026-07-05-aizyforge-brand-migration.sql`
- Modify: `sql/mysql/ruoyi-vue-pro.sql`

- [ ] **Step 1: 编写幂等迁移脚本**

迁移脚本使用：

```sql
START TRANSACTION;

UPDATE system_tenant
SET name = '智企云枢',
    contact_name = '系统管理员',
    contact_mobile = '',
    websites = 'aixy99.site',
    updater = '1',
    update_time = NOW()
WHERE id = 1;

UPDATE system_tenant
SET status = 1, deleted = b'1', updater = '1', update_time = NOW()
WHERE id <> 1 AND deleted = b'0';

UPDATE system_users
SET nickname = '系统管理员',
    email = '',
    mobile = '',
    avatar = NULL,
    remark = '系统管理员',
    updater = '1',
    update_time = NOW()
WHERE id = 1 AND username = 'admin';

UPDATE system_users
SET status = 1, deleted = b'1', updater = '1', update_time = NOW()
WHERE id <> 1 AND deleted = b'0';

DELETE FROM system_oauth2_access_token;
DELETE FROM system_oauth2_refresh_token;
DELETE FROM system_oauth2_approve WHERE user_id <> 1;
DELETE FROM system_oauth2_code WHERE user_id <> 1;

UPDATE system_dept
SET name = '智企云枢', updater = '1', update_time = NOW()
WHERE id = 100;

UPDATE system_menu
SET status = 1, visible = b'0', deleted = b'1', updater = '1', update_time = NOW()
WHERE id IN (1254, 2159, 2160);

UPDATE system_oauth2_client
SET name = '智企云枢',
    logo = 'https://aixy99.site/brand-mark.svg',
    description = '智企云枢默认客户端',
    redirect_uris = '[\"https://aixy99.site\"]',
    updater = '1',
    update_time = NOW()
WHERE id = 1 AND client_id = 'default';

UPDATE system_oauth2_client
SET status = 1, deleted = b'1', updater = '1', update_time = NOW()
WHERE id <> 1 AND deleted = b'0';

UPDATE system_notice
SET status = 1, deleted = b'1', updater = '1', update_time = NOW()
WHERE deleted = b'0';

UPDATE infra_file_config
SET master = b'0', deleted = b'1', updater = '1', update_time = NOW()
WHERE id <> 4 AND deleted = b'0';

UPDATE infra_file_config
SET name = '数据库存储',
    remark = '智企云枢默认文件存储',
    master = b'1',
    config = '{\"@class\":\"cn.iocoder.yudao.module.infra.framework.file.core.client.db.DBFileClientConfig\",\"domain\":\"https://aixy99.site\"}',
    deleted = b'0',
    updater = '1',
    update_time = NOW()
WHERE id = 4;

COMMIT;
```

- [ ] **Step 2: 同步修改 MySQL 初始化 SQL**

初始化数据必须满足：

- 只保留 `admin` 作为可登录管理账号。
- 只保留主租户作为可用租户。
- 不插入演示 OAuth 客户端、演示公告和官方文档菜单。
- 文件配置只保留 id 4 的数据库存储，不包含 `accessKey` 或 `accessSecret`。
- 管理员密码哈希保留初始化值，但 README 明确首次登录必须修改；生产环境继续保留当前随机密码。

- [ ] **Step 3: 在服务器创建数据库备份和测试库**

```bash
install -d -m 700 /opt/aizforge/backups
BACKUP_TS=$(date +%Y%m%d%H%M%S)
mariadb-dump --single-transaction --routines --triggers ruoyi-vue-pro \
  | gzip > "/opt/aizforge/backups/ruoyi-vue-pro-before-brand-${BACKUP_TS}.sql.gz"

mariadb -e "DROP DATABASE IF EXISTS ruoyi_vue_pro_brand_test;
CREATE DATABASE ruoyi_vue_pro_brand_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
gzip -dc "/opt/aizforge/backups/ruoyi-vue-pro-before-brand-${BACKUP_TS}.sql.gz" \
  | mariadb ruoyi_vue_pro_brand_test
mariadb ruoyi_vue_pro_brand_test \
  < /tmp/2026-07-05-aizyforge-brand-migration.sql
```

- [ ] **Step 4: 验证测试库并重复执行迁移**

Run:

```sql
SELECT id, name, websites, status, deleted FROM system_tenant ORDER BY id;
SELECT id, username, nickname, status, deleted FROM system_users ORDER BY id;
SELECT id, name, path, status, visible, deleted
FROM system_menu WHERE id IN (1254, 2159, 2160);
SELECT id, client_id, name, status, deleted FROM system_oauth2_client ORDER BY id;
SELECT id, name, storage, master, deleted FROM infra_file_config ORDER BY id;
```

Expected:

- 只有租户 1、用户 1、OAuth 客户端 1 和文件配置 4 可用。
- 官方菜单与公告均不可用。
- 再次执行迁移结果不变且无 SQL 错误。

- [ ] **Step 5: 提交数据库变更**

```bash
git add sql/mysql/2026-07-05-aizyforge-brand-migration.sql sql/mysql/ruoyi-vue-pro.sql
git commit -m "feat: migrate seed data to 智企云枢"
```

---

### Task 8: 更新仓库元数据和项目文档

**Files:**

- Modify: `README.md`
- Modify: `yudao-ui/yudao-ui-admin-vue3/README.md`
- Modify: `yudao-ui/yudao-ui-admin-vue3/package.json`
- Modify: `LOCAL_STARTUP.md`
- Preserve: `LICENSE`
- Preserve: `yudao-ui/yudao-ui-admin-vue3/LICENSE`

- [ ] **Step 1: 更新前端包元数据**

`package.json`：

```json
{
  "name": "aizyforge-ui-admin",
  "repository": {
    "type": "git",
    "url": "git+https://github.com/AIzy920555014/aizyforge-core.git"
  },
  "bugs": {
    "url": "https://github.com/AIzy920555014/aizyforge-core/issues"
  },
  "homepage": "https://aixy99.site"
}
```

保持依赖和脚本不变，运行 `pnpm install --lockfile-only` 更新 lockfile 的根包名。

- [ ] **Step 2: 重写根 README**

README 至少包含：

```markdown
# 智企云枢

**AIzyForge 智企云枢** 是面向企业的一体化智能管理平台，提供权限管理、工作流、CRM、ERP、会员、支付、报表与 AI 等能力。

- 生产地址：<https://aixy99.site>
- 后端：Java 17 + Spring Boot 3.5
- 前端：Vue 3 + TypeScript + Element Plus
- 数据库：MariaDB / MySQL

## 本地启动

参见 [LOCAL_STARTUP.md](./LOCAL_STARTUP.md)。

## 安全说明

首次部署后必须修改管理员密码；所有第三方密钥通过环境变量注入，不提交到 Git。

## 开源许可

本项目包含 MIT 许可证代码，许可文本见 [LICENSE](./LICENSE)。
```

- [ ] **Step 3: 更新前端 README 和启动说明**

前端 README 只保留安装、启动、构建和环境变量说明，不保留官方推广、交流群、二维码或演示地址。

- [ ] **Step 4: 验证 LICENSE 未被修改**

```bash
git diff --exit-code HEAD -- LICENSE yudao-ui/yudao-ui-admin-vue3/LICENSE
```

Expected: exit 0。

- [ ] **Step 5: 提交文档元数据**

```bash
git add README.md LOCAL_STARTUP.md \
  yudao-ui/yudao-ui-admin-vue3/README.md \
  yudao-ui/yudao-ui-admin-vue3/package.json \
  yudao-ui/yudao-ui-admin-vue3/pnpm-lock.yaml
git commit -m "docs: document 智企云枢 project identity"
```

---

### Task 9: 完成本地构建与浏览器验证

**Files:**

- Modify only if verification finds defects.

- [ ] **Step 1: 检查工作区和品牌残留**

```bash
git status --short
rg -n --hidden '芋道|芋道源码|https?://([^/]*\\.)?iocoder\\.cn' \
  yudao-ui/yudao-ui-admin-vue3/.env* \
  yudao-ui/yudao-ui-admin-vue3/index.html \
  yudao-ui/yudao-ui-admin-vue3/src/views/Login \
  yudao-ui/yudao-ui-admin-vue3/src/views/Home \
  yudao-server/src/main/resources \
  sql/mysql/2026-07-05-aizyforge-brand-migration.sql \
  README.md
```

Expected: 无运行时旧品牌或官方域名。Java 包名、模块名、作者注释、LICENSE 和历史设计文档不在禁止范围。

- [ ] **Step 2: 运行前端全量检查**

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm install --frozen-lockfile
pnpm ts:check
pnpm lint
pnpm build:prod
cd ../../..
bash script/verify-aizyforge-brand.sh
```

Expected: 所有命令 exit 0。若官方原始代码存在与本次无关的 lint 基线问题，记录具体文件，但类型检查、生产构建和品牌审计必须通过。

- [ ] **Step 3: 运行后端测试与打包**

```bash
mvn -pl yudao-server -am test
mvn -pl yudao-server -am -DskipTests package
```

Expected: `BUILD SUCCESS`。

- [ ] **Step 4: 本地启动并执行浏览器 QA**

启动后端和前端后验证：

- 桌面登录页：标题、Logo、备案号、无推广链接。
- 390×844 移动端登录页：无横向溢出，Logo 和备案号可见。
- 登录后首页：只显示智企云枢欢迎区和企业模块入口。
- 用户菜单：没有官方文档入口。
- 页脚：`Copyright ©2026 智企云枢 · 冀ICP备2026021324号`。
- 浏览器 title 和 favicon 使用新品牌。
- 控制台没有混合内容、远程资源失败或本次改造错误。

保存截图到：

```text
/tmp/aizyforge-login-desktop.png
/tmp/aizyforge-login-mobile.png
/tmp/aizyforge-dashboard.png
```

- [ ] **Step 5: 提交验证期间修复**

```bash
git add -p
git commit -m "fix: complete AIzyForge brand verification"
```

如果没有修复，不创建空提交。

---

### Task 10: 重命名 GitHub 仓库并推送完整源码

**Files:**

- External: GitHub repository
- Modify local Git remote only.

- [ ] **Step 1: 确认 GitHub 身份和仓库状态**

```bash
gh auth status
gh repo view AIzy920555014/aizforge-core --json name,nameWithOwner,url,defaultBranchRef
```

Expected: 当前账号为 `AIzy920555014`，旧仓库可管理。

- [ ] **Step 2: 重命名远程仓库**

```bash
gh repo rename aizyforge-core --repo AIzy920555014/aizforge-core --yes
```

- [ ] **Step 3: 更新并核对 origin**

```bash
git remote set-url origin git@github.com:AIzy920555014/aizyforge-core.git
git remote -v
```

Expected:

- `origin` 指向 `aizyforge-core.git`
- `upstream` 仍指向官方开源项目

- [ ] **Step 4: 确认完整前端已被 Git 跟踪**

```bash
test -z "$(git ls-files --others --exclude-standard yudao-ui/yudao-ui-admin-vue3)"
git ls-files yudao-ui/yudao-ui-admin-vue3/src | wc -l
```

Expected: 第一条 exit 0，第二条数量与本地源文件规模相符。

如仍有未跟踪前端文件，先执行：

```bash
git add yudao-ui/yudao-ui-admin-vue3
git commit -m "chore: track complete admin frontend source"
```

提交前必须确认不包含 `node_modules`、`dist*` 和本地缓存。

- [ ] **Step 5: 推送当前分支**

```bash
git push -u origin master-jdk17
```

Expected: GitHub 的 `master-jdk17` 指向本地 HEAD。

---

### Task 11: 迁移生产数据库与部署标识

**Files:**

- Server create: `/etc/systemd/system/aizyforge.service`
- Server create: `/etc/aizyforge/aizyforge.env`
- Server create: `/etc/nginx/conf.d/aizyforge-domain.conf`
- Server create: `/opt/aizyforge/**`
- Server create: `/var/www/aizyforge/**`
- Server preserve until acceptance: all old `/opt/aizforge`、`/var/www/aizforge` and `aizforge.service`

- [ ] **Step 1: 上传已验证发布包和迁移脚本**

本地生成：

```bash
RELEASE_TS=$(date +%Y%m%d%H%M%S)
cp yudao-server/target/yudao-server.jar "/tmp/aizyforge-server-${RELEASE_TS}.jar"
COPYFILE_DISABLE=1 tar --no-xattrs -czf "/tmp/aizyforge-ui-${RELEASE_TS}.tar.gz" \
  -C yudao-ui/yudao-ui-admin-vue3/dist-prod .
scp "/tmp/aizyforge-server-${RELEASE_TS}.jar" \
  "/tmp/aizyforge-ui-${RELEASE_TS}.tar.gz" \
  sql/mysql/2026-07-05-aizyforge-brand-migration.sql \
  root@82.156.106.136:/tmp/
```

- [ ] **Step 2: 备份生产数据库和部署配置**

服务器执行：

```bash
install -d -m 700 /opt/aizyforge/backups
BACKUP_TS=$(date +%Y%m%d%H%M%S)
mariadb-dump --single-transaction --routines --triggers ruoyi-vue-pro \
  | gzip > "/opt/aizyforge/backups/ruoyi-vue-pro-${BACKUP_TS}.sql.gz"
cp -a /etc/systemd/system/aizforge.service \
  "/opt/aizyforge/backups/aizforge.service-${BACKUP_TS}"
cp -a /etc/nginx/conf.d/aizforge-domain.conf \
  "/opt/aizyforge/backups/aizforge-domain.conf-${BACKUP_TS}"
```

- [ ] **Step 3: 在测试库验证迁移并应用生产**

按 Task 7 创建测试库并重复执行两次迁移。验证通过后：

```bash
mariadb ruoyi-vue-pro < /tmp/2026-07-05-aizyforge-brand-migration.sql
```

- [ ] **Step 4: 创建新目录和发布软链接**

```bash
if ! id aizyforge >/dev/null 2>&1; then
  useradd --system --home-dir /opt/aizyforge --shell /sbin/nologin aizyforge
fi

install -d -o aizyforge -g aizyforge /opt/aizyforge/releases
install -d -o nginx -g nginx /var/www/aizyforge/releases
install -d -o root -g aizyforge -m 750 /etc/aizyforge

cp "/tmp/aizyforge-server-${RELEASE_TS}.jar" /opt/aizyforge/releases/
ln -sfn "/opt/aizyforge/releases/aizyforge-server-${RELEASE_TS}.jar" \
  /opt/aizyforge/aizyforge-server.jar

UI_RELEASE="/var/www/aizyforge/releases/ui-${RELEASE_TS}"
install -d -o nginx -g nginx "${UI_RELEASE}"
tar -xzf "/tmp/aizyforge-ui-${RELEASE_TS}.tar.gz" -C "${UI_RELEASE}"
ln -sfn "${UI_RELEASE}" /var/www/aizyforge/current
```

- [ ] **Step 5: 创建安全环境文件**

将旧环境文件中的数据库值迁移到 `/etc/aizyforge/aizyforge.env`，只保留当前服务确实需要的变量。设置：

```bash
chown root:aizyforge /etc/aizyforge/aizyforge.env
chmod 0640 /etc/aizyforge/aizyforge.env
```

不得迁移已暴露的第三方演示密钥。

- [ ] **Step 6: 创建并启动新 systemd 服务**

`/etc/systemd/system/aizyforge.service`：

```ini
[Unit]
Description=AIzyForge 智企云枢
After=network-online.target mariadb.service redis.service
Wants=network-online.target

[Service]
Type=simple
User=aizyforge
Group=aizyforge
EnvironmentFile=/etc/aizyforge/aizyforge.env
WorkingDirectory=/opt/aizyforge
ExecStart=/usr/bin/java -Xms256m -Xmx768m -jar /opt/aizyforge/aizyforge-server.jar --spring.profiles.active=local
Restart=on-failure
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

旧服务占用同一个 48080 端口，因此采用短暂停机切换，并在失败时立即回滚：

```bash
systemctl daemon-reload
systemctl stop aizforge
if ! systemctl start aizyforge; then
  systemctl start aizforge
  exit 1
fi
systemctl is-active aizyforge
if ! curl -fsS http://127.0.0.1:48080/actuator/health; then
  systemctl stop aizyforge
  systemctl start aizforge
  exit 1
fi
systemctl enable aizyforge
```

Expected: 服务 active，健康检查返回 `{"status":"UP"}`。确认后停用旧服务：

```bash
systemctl disable --now aizforge
```

- [ ] **Step 7: 切换 Nginx**

新配置继续使用 `aixy99.site` 和现有证书，将静态目录改为 `/var/www/aizyforge/current`，保留 API、Swagger、Actuator 和 WebSocket 代理规则。

```bash
nginx -t
systemctl reload nginx
```

Expected: 语法检查成功后才 reload。

---

### Task 12: 线上完成审计与交付

**Files:**

- Modify only if audit finds defects.

- [ ] **Step 1: 验证域名和服务**

```bash
curl -fsSI http://aixy99.site/
curl -fsSI https://aixy99.site/
curl -fsS https://aixy99.site/actuator/health
curl -fsS -o /dev/null -w '%{http_code}\n' https://aixy99.site/doc.html
systemctl is-active aizyforge nginx mariadb redis
systemctl is-enabled aizyforge nginx mariadb redis
```

Expected: HTTP 301 到 HTTPS；HTTPS、健康检查和 Swagger 正常；所有服务 active 且 enabled。

- [ ] **Step 2: 验证数据库品牌与账号**

```sql
SELECT id, name, websites, status, deleted FROM system_tenant ORDER BY id;
SELECT id, username, nickname, status, deleted FROM system_users ORDER BY id;
SELECT id, name, path, status, visible, deleted
FROM system_menu WHERE id IN (1254, 2159, 2160);
SELECT id, client_id, name, status, deleted FROM system_oauth2_client ORDER BY id;
SELECT id, name, storage, master, deleted FROM infra_file_config ORDER BY id;
```

Expected: 仅主租户、管理员、默认 OAuth 客户端和数据库存储可用；管理员密码仍为服务器凭据文件记录的当前随机密码。

- [ ] **Step 3: 执行线上浏览器 QA**

使用桌面和 390×844 移动视口验证：

- 登录页只显示智企云枢、品牌 Logo 和备案号。
- 使用 `admin` 与服务器保存的随机密码登录成功。
- 首页、页脚、用户菜单、AI 空状态没有旧品牌。
- 页面没有官方教程、作者动态或官方仓库入口。
- favicon、title 和 Swagger 使用智企云枢。
- 控制台没有混合内容、证书错误或远程演示资源失败。

- [ ] **Step 4: 扫描线上静态资源**

下载线上首页和静态 JS 后执行：

```bash
SITE_AUDIT_DIR=$(mktemp -d)
curl -fsS https://aixy99.site/ -o "${SITE_AUDIT_DIR}/index.html"
rg -o 'src=\"[^\"]+\\.js' "${SITE_AUDIT_DIR}/index.html" \
  | cut -d'"' -f2 \
  | while read -r asset; do
      curl -fsS "https://aixy99.site${asset}" \
        -o "${SITE_AUDIT_DIR}/$(basename "${asset}")"
    done
if rg -n '芋道|芋道源码|iocoder\\.cn|github\\.com/(YunaiV|yudaocode)' "${SITE_AUDIT_DIR}"; then
  echo "Forbidden production branding remains" >&2
  exit 1
fi
```

- [ ] **Step 5: 核对 GitHub 与工作区**

```bash
gh repo view AIzy920555014/aizyforge-core --json nameWithOwner,url
git remote -v
git status --short
git log -10 --oneline
git rev-parse HEAD
git rev-parse origin/master-jdk17
```

Expected:

- 新仓库名称正确。
- 本地 HEAD 与远程分支一致。
- 工作区只剩用户原有且明确保留的无关修改；品牌改造文件全部提交。

- [ ] **Step 6: 最终验收**

逐项对照设计文档的验收标准，保留以下证据：

- 前后端构建输出
- 数据库迁移前备份路径和迁移后查询结果
- systemd、Nginx、健康检查输出
- 桌面、移动端和后台首页截图
- 线上静态资源品牌扫描结果
- GitHub 新仓库地址和远程分支哈希

所有证据通过后，才能声明品牌改造完成并清理旧部署目录。
