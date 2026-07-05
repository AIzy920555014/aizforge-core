# ICP Footer and Domain Mapping Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Display `冀ICP备2026021324号` on the public login page and authenticated footer, then serve the application at `https://aixy99.site`.

**Architecture:** A focused `IcpLink` component owns the filing text and MIIT URL and is rendered by both login and global footer surfaces. The existing static SPA and backend proxy configuration remains unchanged; a dedicated Nginx virtual host adds explicit domain matching, TLS termination, and HTTP-to-HTTPS redirect.

**Tech Stack:** Vue 3, TypeScript, UnoCSS, Vite, Nginx, Certbot/Let’s Encrypt, systemd

---

### Task 1: Add the reusable ICP link

**Files:**
- Create: `yudao-ui/yudao-ui-admin-vue3/src/components/IcpLink/src/IcpLink.vue`
- Create: `yudao-ui/yudao-ui-admin-vue3/src/components/IcpLink/index.ts`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/Login/Login.vue`
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/layout/components/Footer/src/Footer.vue`

- [ ] **Step 1: Run the failing content check**

Run:

```bash
! rg -n "冀ICP备2026021324号" \
  yudao-ui/yudao-ui-admin-vue3/src/views/Login/Login.vue \
  yudao-ui/yudao-ui-admin-vue3/src/layout/components/Footer/src/Footer.vue
```

Expected: exit code `0`, proving neither target currently contains the filing number.

- [ ] **Step 2: Create the minimal reusable component**

Create `src/components/IcpLink/src/IcpLink.vue`:

```vue
<template>
  <a
    href="https://beian.miit.gov.cn/"
    target="_blank"
    rel="noopener noreferrer"
    class="transition-colors hover:text-[var(--el-color-primary)]"
  >
    冀ICP备2026021324号
  </a>
</template>

<script lang="ts" setup>
defineOptions({ name: 'IcpLink' })
</script>
```

Create `src/components/IcpLink/index.ts`:

```ts
import IcpLink from './src/IcpLink.vue'

export { IcpLink }
```

- [ ] **Step 3: Render it on the login page**

Import it in `src/views/Login/Login.vue`:

```ts
import { IcpLink } from '@/components/IcpLink'
```

Add this child immediately before the login page root container closes:

```vue
<div
  class="fixed bottom-12px left-1/2 z-20 -translate-x-1/2 whitespace-nowrap text-12px text-[var(--el-text-color-secondary)]"
>
  <IcpLink />
</div>
```

- [ ] **Step 4: Render it in the authenticated footer**

Import it in `src/layout/components/Footer/src/Footer.vue`:

```ts
import { IcpLink } from '@/components/IcpLink'
```

Replace the existing copyright span with:

```vue
<span class="text-14px">
  Copyright ©{{ currentYear }} {{ title }}
  <span class="mx-8px">·</span>
  <IcpLink />
</span>
```

- [ ] **Step 5: Run focused and type checks**

Run:

```bash
test "$(rg -l "冀ICP备2026021324号" \
  yudao-ui/yudao-ui-admin-vue3/src/components/IcpLink/src/IcpLink.vue | wc -l | tr -d ' ')" = "1"
rg -n 'href="https://beian.miit.gov.cn/"|target="_blank"|rel="noopener noreferrer"' \
  yudao-ui/yudao-ui-admin-vue3/src/components/IcpLink/src/IcpLink.vue
cd yudao-ui/yudao-ui-admin-vue3
pnpm ts:check
```

Expected: component assertions pass and `pnpm ts:check` exits `0`.

- [ ] **Step 6: Commit the frontend change**

```bash
git add \
  yudao-ui/yudao-ui-admin-vue3/src/components/IcpLink \
  yudao-ui/yudao-ui-admin-vue3/src/views/Login/Login.vue \
  yudao-ui/yudao-ui-admin-vue3/src/layout/components/Footer/src/Footer.vue
git commit -m "feat: display ICP filing link"
```

### Task 2: Build and package the frontend release

**Files:**
- Verify: `yudao-ui/yudao-ui-admin-vue3/dist-prod/`
- Create outside repository: `/tmp/aizforge-ui-<timestamp>.tar.gz`

- [ ] **Step 1: Build the production bundle**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm build:prod
```

Expected: Vite exits `0` and prints `built in`.

- [ ] **Step 2: Verify the bundle contains the ICP text and no localhost API URL**

Run:

```bash
rg -l "冀ICP备2026021324号" dist-prod
! rg -n "localhost:48080" dist-prod
test -s dist-prod/index.html
```

Expected: at least one built JavaScript asset contains the filing number, no localhost backend URL is present, and `index.html` is non-empty.

- [ ] **Step 3: Create a clean archive**

Run:

```bash
RELEASE_TS=$(date +%Y%m%d-%H%M%S)
COPYFILE_DISABLE=1 tar --no-xattrs -czf "/tmp/aizforge-ui-${RELEASE_TS}.tar.gz" -C dist-prod .
shasum -a 256 "/tmp/aizforge-ui-${RELEASE_TS}.tar.gz"
```

Expected: archive exists, has a SHA-256 hash, and contains no AppleDouble metadata.

### Task 3: Deploy the new static release

**Files on server:**
- Create: `/var/www/aizforge/releases/ui-<timestamp>/`
- Update symlink: `/var/www/aizforge/current`
- Preserve: previous symlink target for rollback

- [ ] **Step 1: Upload and verify the archive**

Upload with `scp`, then compare local and remote SHA-256 values:

```bash
scp "/tmp/aizforge-ui-${RELEASE_TS}.tar.gz" root@82.156.106.136:/opt/aizforge/releases/
sha256sum "/opt/aizforge/releases/aizforge-ui-${RELEASE_TS}.tar.gz"
```

Expected: hashes match exactly.

- [ ] **Step 2: Extract into a versioned directory**

Run on the server:

```bash
PREVIOUS_RELEASE=$(readlink -f /var/www/aizforge/current)
NEW_RELEASE="/var/www/aizforge/releases/ui-${RELEASE_TS}"
mkdir -p "$NEW_RELEASE"
tar -xzf "/opt/aizforge/releases/aizforge-ui-${RELEASE_TS}.tar.gz" -C "$NEW_RELEASE"
test -s "$NEW_RELEASE/index.html"
chown -R nginx:nginx "$NEW_RELEASE"
ln -sfn "$NEW_RELEASE" /var/www/aizforge/current
```

Expected: `current` resolves to the new versioned release.

- [ ] **Step 3: Verify HTTP before enabling TLS**

Run on the server:

```bash
curl -fsS -H 'Host: aixy99.site' http://127.0.0.1/ | grep -q '<div id="app"'
curl -fsS -H 'Host: aixy99.site' http://127.0.0.1/actuator/health | grep -q '"status":"UP"'
```

Expected: static SPA and backend health both pass.

### Task 4: Issue the certificate and add the domain virtual host

**Files on server:**
- Create: `/etc/nginx/conf.d/aizforge-domain.conf`
- Create by Certbot: `/etc/letsencrypt/live/aixy99.site/`
- Preserve: `/etc/nginx/default.d/aizforge.conf`

- [ ] **Step 1: Install Certbot**

Run:

```bash
dnf install -y certbot
certbot --version
```

Expected: Certbot is installed and prints its version.

- [ ] **Step 2: Issue a webroot certificate**

Run:

```bash
certbot certonly \
  --webroot \
  --webroot-path /var/www/aizforge/current \
  --domain aixy99.site \
  --non-interactive \
  --agree-tos \
  --register-unsafely-without-email
```

Expected: `/etc/letsencrypt/live/aixy99.site/fullchain.pem` and `privkey.pem` exist.

- [ ] **Step 3: Write the dedicated Nginx domain configuration**

Create `/etc/nginx/conf.d/aizforge-domain.conf`:

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name aixy99.site;

    location ^~ /.well-known/acme-challenge/ {
        root /var/www/aizforge/current;
    }

    location / {
        return 301 https://aixy99.site$request_uri;
    }
}

server {
    listen 443 ssl;
    listen [::]:443 ssl;
    http2 on;
    server_name aixy99.site;

    ssl_certificate /etc/letsencrypt/live/aixy99.site/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/aixy99.site/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;

    include /etc/nginx/default.d/aizforge.conf;
}
```

- [ ] **Step 4: Validate and reload Nginx**

Run:

```bash
nginx -t
systemctl reload nginx
systemctl is-active nginx
ss -lntp | grep -E ':(80|443)\b'
```

Expected: syntax test succeeds, Nginx remains active, and ports 80 and 443 listen.

- [ ] **Step 5: Verify renewal**

Run:

```bash
systemctl enable --now certbot-renew.timer 2>/dev/null || true
certbot renew --dry-run
```

Expected: renewal simulation succeeds. If the package uses a different timer name, confirm a Certbot renewal timer exists with `systemctl list-timers | grep -i certbot`.

### Task 5: Browser and service verification

**Surfaces:**
- Public login: `https://aixy99.site/`
- Authenticated home: `https://aixy99.site/index`
- Backend health: `https://aixy99.site/actuator/health`
- Swagger: `https://aixy99.site/doc.html`

- [ ] **Step 1: Verify redirect and certificate**

Run:

```bash
curl -I http://aixy99.site/
curl -fsS -I https://aixy99.site/
openssl s_client -connect aixy99.site:443 -servername aixy99.site </dev/null 2>/dev/null \
  | openssl x509 -noout -subject -issuer -dates -ext subjectAltName
```

Expected: HTTP returns `301` to HTTPS, HTTPS returns `200`, and SAN contains `DNS:aixy99.site`.

- [ ] **Step 2: Verify public filing link in the browser**

Open `https://aixy99.site/` in the in-app browser and assert:

- title is `芋道管理系统 - 登录`;
- visible text includes `冀ICP备2026021324号`;
- the link has the MIIT URL, `_blank`, and `noopener noreferrer`;
- browser console has no relevant errors;
- desktop screenshot shows the link without overlapping the login controls.

- [ ] **Step 3: Verify authenticated footer**

Log in with the server-generated administrator credential and assert:

- URL becomes `https://aixy99.site/index`;
- global footer visibly contains `冀ICP备2026021324号`;
- backend home content renders;
- browser console has no relevant errors.

- [ ] **Step 4: Run the final server audit**

Run:

```bash
nginx -t
systemctl is-active nginx aizforge mariadb redis
systemctl is-enabled nginx aizforge mariadb redis
curl -fsS https://aixy99.site/actuator/health
curl -fsS -o /dev/null https://aixy99.site/doc.html
```

Expected: all services are active and enabled, health returns `{"status":"UP"}`, and Swagger returns HTTP `200`.

- [ ] **Step 5: Roll back only if verification fails**

Run on the server:

```bash
ln -sfn "$PREVIOUS_RELEASE" /var/www/aizforge/current
nginx -t
systemctl reload nginx
```

Expected: the previous release is restored without restarting the Java backend.
