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

if rg -l --hidden --glob '!**/*.map' "${FORBIDDEN_REGEX}" "${TARGETS[@]}"; then
  echo "Forbidden public branding detected" >&2
  exit 1
fi

if ! rg -q '智企云枢' "${TARGETS[@]}"; then
  echo "Required Chinese brand not found" >&2
  exit 1
fi

echo "AIzyForge public branding verification passed"
