#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERIFY_SCRIPT="${SCRIPT_DIR}/verify-aizyforge-brand.sh"
FIXTURE_ROOT="$(mktemp -d)"
trap 'rm -rf "${FIXTURE_ROOT}"' EXIT

mkdir -p "${FIXTURE_ROOT}/ok/dist-prod" "${FIXTURE_ROOT}/bad/dist-prod"
printf '%s\n' '智企云枢 AIzyForge' >"${FIXTURE_ROOT}/ok/dist-prod/app.js"
printf '%s\n' '访问 https://doc.iocoder.cn 获取芋道源码教程' \
  >"${FIXTURE_ROOT}/bad/dist-prod/app.js"

"${VERIFY_SCRIPT}" --artifact-root "${FIXTURE_ROOT}/ok"
if "${VERIFY_SCRIPT}" --artifact-root "${FIXTURE_ROOT}/bad"; then
  echo "Expected forbidden branding to fail" >&2
  exit 1
fi

echo "Brand verifier fixture tests passed"
