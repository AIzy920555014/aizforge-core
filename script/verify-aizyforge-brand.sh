#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ARTIFACT_ROOT="${REPO_ROOT}/yudao-ui/yudao-ui-admin-vue3"
REDIS_CONFIGS=(
  "${REPO_ROOT}/yudao-server/src/main/resources/application-local.yaml"
  "${REPO_ROOT}/yudao-server/src/main/resources/application-dev.yaml"
  "${REPO_ROOT}/yudao-module-iot/yudao-module-iot-gateway/src/main/resources/application.yaml"
)

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

if rg -l --hidden --glob '!**/*.map' "${FORBIDDEN_REGEX}" "${TARGETS[@]}"; then
  echo "Forbidden public branding detected" >&2
  exit 1
fi

if ! rg -q '智企云枢' "${TARGETS[@]}"; then
  echo "Required Chinese brand not found" >&2
  exit 1
fi

if rg -l 'password: \$\{REDIS_PASSWORD:\}' "${REDIS_CONFIGS[@]}"; then
  echo "Empty Redis password placeholder would trigger AUTH" >&2
  exit 1
fi

if ! rg -q '^[[:space:]]+client: \$\{EXPRESS_CLIENT:NOT_PROVIDE\}$' \
  "${REPO_ROOT}/yudao-server/src/main/resources/application.yaml"; then
  echo "Express integration must be disabled by default" >&2
  exit 1
fi

if rg -q '\$\{(KD_NIAO|KD100)_[A-Z_]+:\}' \
  "${REPO_ROOT}/yudao-server/src/main/resources/application.yaml"; then
  echo "Blank express credentials would trigger startup validation" >&2
  exit 1
fi

for ai_safe_default in \
  'chat: none' \
  'embedding: none' \
  'image: none' \
  'moderation: none'; do
  if ! rg -q "^[[:space:]]+${ai_safe_default}$" \
    "${REPO_ROOT}/yudao-server/src/main/resources/application.yaml"; then
    echo "Missing AI safe default: ${ai_safe_default}" >&2
    exit 1
  fi
done

if ! rg -q '^[[:space:]]+enabled: \$\{DASHSCOPE_ENABLED:false\}$' \
  "${REPO_ROOT}/yudao-server/src/main/resources/application.yaml"; then
  echo "DashScope must be disabled by default" >&2
  exit 1
fi

echo "AIzyForge public branding verification passed"
