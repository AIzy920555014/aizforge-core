<template>
  <div>
    <el-card shadow="never">
      <div class="flex items-center gap-16px">
        <img :src="brandMark" alt="智企云枢" class="h-64px w-64px rounded-16px" />
        <div>
          <div class="text-24px font-bold">欢迎使用智企云枢</div>
          <div class="mt-8px text-14px text-gray-500">
            {{ username }}，面向企业的一体化智能管理平台
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="mt-12px" shadow="never">
      <template #header>
        <span class="font-600">快捷入口</span>
      </template>
      <el-row :gutter="12" class="gap-y-12px">
        <el-col
          v-for="item in shortcuts"
          :key="item.url"
          :xl="4"
          :lg="6"
          :md="8"
          :sm="12"
          :xs="24"
        >
          <button
            class="w-full flex items-center gap-12px rounded-10px border border-[var(--el-border-color-light)] bg-transparent p-16px text-left transition-colors hover:border-[var(--el-color-primary)]"
            type="button"
            @click="handleShortcutClick(item.url)"
          >
            <span
              class="h-40px w-40px flex flex-none items-center justify-center rounded-10px text-white"
              :style="{ backgroundColor: item.color }"
            >
              <Icon :icon="item.icon" :size="22" />
            </span>
            <span class="font-500">{{ item.name }}</span>
          </button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="mt-12px" shadow="never">
      <template #header>
        <span class="font-600">平台能力</span>
      </template>
      <div class="flex flex-wrap gap-10px">
        <el-tag v-for="capability in capabilities" :key="capability" effect="plain" size="large">
          {{ capability }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import brandMark from '@/assets/svgs/brand-mark.svg'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'Index' })

const router = useRouter()
const userStore = useUserStore()
const username = computed(() => userStore.getUser.nickname || '管理员')

const shortcuts = [
  { name: '用户管理', icon: 'ep:user', url: '/system/user', color: '#2563eb' },
  { name: '流程中心', icon: 'ep:connection', url: '/bpm/task/todo', color: '#7c3aed' },
  { name: '客户管理', icon: 'ep:briefcase', url: '/crm/backlog', color: '#0891b2' },
  { name: 'ERP 管理', icon: 'ep:goods', url: '/erp/home', color: '#059669' },
  { name: 'AI 助手', icon: 'ep:magic-stick', url: '/ai/chat', color: '#db2777' },
  { name: '代码生成', icon: 'ep:setting', url: '/infra/codegen', color: '#475569' }
]

const capabilities = ['统一权限', '工作流审批', '客户管理', '进销存管理', '数据报表', 'AI 助手']

const handleShortcutClick = (url: string) => {
  router.push(url)
}
</script>
