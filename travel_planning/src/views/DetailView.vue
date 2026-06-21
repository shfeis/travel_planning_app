<template>
  <main class="detail-page">
    <van-nav-bar
      :title="`${params.city || '目的地'}行程规划`"
      left-text="返回"
      left-arrow
      fixed
      placeholder
      @click-left="router.back()"
    />

    <section class="summary-card">
      <div>
        <h1>{{ params.city || '目的地' }} · {{ params.days || '-' }}天行程</h1>
        <p v-if="isLoading">正在生成专属路线</p>
      </div>
      <strong>预算：{{ params.budget || '-' }}元</strong>
    </section>

    <section v-if="isLoading" class="loading-card">
      <van-loading color="#3f86f7" size="36px" />
      <p>行程规划中，请稍后......</p>
    </section>

    <section v-else-if="errorMessage" class="state-card">
      <van-icon name="warning-o" />
      <h2>规划失败</h2>
      <p>{{ errorMessage }}</p>
      <van-button type="primary" round block @click="loadRecommendation">重新规划</van-button>
    </section>

    <template v-else>
      <van-collapse v-if="daySections.length" v-model="activeDays" class="day-list">
        <van-collapse-item
          v-for="(section, index) in daySections"
          :key="section.title"
          :name="String(index)"
          :title="section.title"
        >
          <ul class="line-list">
            <li v-for="item in section.items" :key="item">{{ item }}</li>
          </ul>
        </van-collapse-item>
      </van-collapse>

      <section v-if="budgetLines.length" class="content-card">
        <h2>预算明细</h2>
        <ul class="budget-list">
          <li
            v-for="row in budgetRows"
            :key="row.raw"
            class="budget-row"
            :class="{ 'budget-row--total': row.isTotal }"
          >
            <div>
              <span class="budget-name">{{ row.name }}</span>
              <small v-if="row.detail">{{ row.detail }}</small>
            </div>
            <strong v-if="row.amount">{{ row.amount }}</strong>
          </li>
        </ul>
      </section>

      <section v-if="tipLines.length" class="content-card">
        <h2>温馨提示</h2>
        <p v-for="line in tipLines" :key="line" class="tip-line">{{ line }}</p>
      </section>

      <section v-if="!daySections.length && cleanRecommendation" class="content-card">
        <h2>推荐结果</h2>
        <pre>{{ cleanRecommendation }}</pre>
      </section>
    </template>

    <div class="bottom-action">
      <van-button type="primary" round block @click="router.push('/chat')">咨询 AI 助手</van-button>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTravelRecommend } from '@/utils/request'

interface PlanSection {
  title: string
  items: string[]
}

interface BudgetRow {
  raw: string
  name: string
  amount: string
  detail: string
  isTotal: boolean
}

const route = useRoute()
const router = useRouter()

const isLoading = ref(false)
const errorMessage = ref('')
const recommendation = ref('')
const activeDays = ref<string[]>([])

const params = computed(() => ({
  budget: String(route.query.budget ?? ''),
  days: String(route.query.days ?? ''),
  city: String(route.query.city ?? ''),
}))

const cleanLines = computed(() =>
  recommendation.value
    .split(/\r?\n/)
    .map((line) => cleanLine(line))
    .filter(Boolean),
)

const cleanRecommendation = computed(() => cleanLines.value.join('\n'))

const daySections = computed<PlanSection[]>(() => {
  const sections: PlanSection[] = []
  let current: PlanSection | null = null

  for (const line of cleanLines.value) {
    if (isBudgetHeading(line) || isTipHeading(line) || isTotalHeading(line)) {
      current = null
      continue
    }

    const dayMatch = line.match(/^(第[一二三四五六七八九十0-9]+天)[:：\s]*(.*)$/)
    if (dayMatch) {
      const section: PlanSection = {
        title: dayMatch[1] ?? '行程安排',
        items: dayMatch[2] ? [dayMatch[2]] : [],
      }
      current = section
      sections.push(section)
      continue
    }

    if (current && !isGenericHeading(line)) {
      current.items.push(line)
    }
  }

  return sections
})

const budgetLines = computed(() => collectSectionLines(isBudgetHeading, isTipHeading))
const budgetRows = computed<BudgetRow[]>(() => budgetLines.value.map(parseBudgetLine))
const tipLines = computed(() => collectSectionLines(isTipHeading))

const loadRecommendation = async () => {
  isLoading.value = true
  errorMessage.value = ''
  recommendation.value = ''

  try {
    const result = await getTravelRecommend(params.value)
    recommendation.value = result
    activeDays.value = daySections.value.map((_, index) => String(index))
    console.log('推荐结果:', result)
  } catch (error) {
    console.error('接口调用失败:', error)
    errorMessage.value = '接口调用失败，请检查后端服务或稍后重试。'
  } finally {
    isLoading.value = false
  }
}

const cleanLine = (line: string) =>
  line
    .trim()
    .replace(/^#{1,6}\s*/, '')
    .replace(/^\s*[-*]\s*/, '')
    .replace(/\*\*/g, '')
    .trim()

const parseBudgetLine = (line: string): BudgetRow => {
  const [namePart = line, restPart = ''] = line.split(/[:：]/, 2)
  const name = namePart.trim()
  const rest = restPart.trim()
  const amountMatch = rest.match(/(?:¥|￥)?\s*\d+(?:\.\d+)?\s*元?/)
  const amount = amountMatch?.[0].replace(/\s+/g, '') ?? ''
  const detail = rest.replace(amountMatch?.[0] ?? '', '').trim()

  return {
    raw: line,
    name,
    amount: amount ? (amount.includes('元') ? amount : `${amount}元`) : rest,
    detail: detail.replace(/^（|）$/g, ''),
    isTotal: name.includes('总计') || name.includes('合计'),
  }
}

const isBudgetHeading = (line: string) =>
  /^预算(?:明细|细明|详情|清单|分配|汇总|总览|说明|费用|金额|金額)/.test(line)
const isTipHeading = (line: string) => line.includes('温馨提示')
const isTotalHeading = (line: string) => line.includes('总计')
const isGenericHeading = (line: string) =>
  /^(行程规划|每天具体行程及预算细明|温馨提示|总计)$/.test(line) || isBudgetHeading(line)

const collectSectionLines = (
  startMatcher: (line: string) => boolean,
  stopMatcher?: (line: string) => boolean,
) => {
  const lines: string[] = []
  let collecting = false

  for (const line of cleanLines.value) {
    if (startMatcher(line)) {
      collecting = true
      continue
    }

    if (!collecting) {
      continue
    }

    if (stopMatcher?.(line) || (startMatcher !== isTipHeading && isTipHeading(line))) {
      break
    }

    if (!isGenericHeading(line)) {
      lines.push(line)
    }
  }

  return lines
}

onMounted(loadRecommendation)
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 14px 14px calc(82px + env(safe-area-inset-bottom));
  background: #f6f6f8;
}

:deep(.van-nav-bar--fixed) {
  left: 50%;
  width: min(100vw, 430px);
  transform: translateX(-50%);
}

:deep(.van-nav-bar__title) {
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

.summary-card,
.content-card,
.state-card,
.loading-card {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(31, 41, 55, 0.06);
}

.summary-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
  padding: 18px 14px;
}

.summary-card h1 {
  margin: 0;
  color: #111827;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.35;
}

.summary-card p {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.summary-card strong {
  flex: 0 0 auto;
  color: #d84a3a;
  font-size: 16px;
  line-height: 1.4;
  white-space: nowrap;
}

.loading-card,
.state-card {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 28px 20px;
  text-align: center;
}

.loading-card p {
  margin: 0;
  color: #4b5563;
  font-size: 15px;
}

.state-card .van-icon {
  color: #d84a3a;
  font-size: 36px;
}

.state-card h2,
.content-card h2 {
  margin: 0;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.state-card p {
  margin: 0 0 6px;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.7;
}

.day-list {
  margin-bottom: 14px;
  overflow: hidden;
  border-radius: 8px;
}

:deep(.day-list .van-cell) {
  min-height: 50px;
  padding: 0 14px;
  align-items: center;
}

:deep(.day-list .van-cell__title) {
  color: #374151;
  font-size: 15px;
}

:deep(.day-list .van-collapse-item__content) {
  padding: 4px 14px 14px;
  color: #4b5563;
}

.line-list,
.budget-list {
  display: grid;
  margin: 0;
  padding: 0;
  list-style: none;
}

.line-list {
  gap: 12px;
}

.budget-list {
  gap: 8px;
}

.line-list li {
  position: relative;
  padding-left: 14px;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.7;
}

.line-list li::before {
  position: absolute;
  top: 11px;
  left: 0;
  width: 5px;
  height: 5px;
  content: '';
  background: #3f86f7;
  border-radius: 50%;
}

.content-card {
  margin-bottom: 14px;
  padding: 18px 14px;
}

.content-card h2 {
  margin-bottom: 16px;
}

.tip-line {
  color: #4b5563;
  font-size: 14px;
  line-height: 1.7;
}

.budget-row {
  min-height: 46px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 8px 14px;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.45;
}

.budget-name {
  display: block;
  color: #4b5563;
}

.budget-row small {
  display: block;
  margin-top: 4px;
  color: #9ca3af;
  font-size: 12px;
  line-height: 1.4;
}

.budget-row strong {
  flex: 0 0 auto;
  color: #8b919b;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.budget-row--total {
  min-height: 50px;
  margin-top: 6px;
  background: #f7f8fb;
  border-radius: 6px;
}

.budget-row--total .budget-name {
  color: #111827;
  font-weight: 700;
}

.budget-row--total strong {
  color: #d84a3a;
  font-size: 16px;
  font-weight: 700;
}

.tip-line {
  margin: 0 0 12px;
}

.tip-line:last-child {
  margin-bottom: 0;
}

pre {
  margin: 0;
  color: #4b5563;
  font: inherit;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.bottom-action {
  position: fixed;
  right: max(14px, calc((100vw - 430px) / 2 + 14px));
  bottom: 0;
  left: max(14px, calc((100vw - 430px) / 2 + 14px));
  z-index: 10;
  padding: 12px 0 calc(12px + env(safe-area-inset-bottom));
  background: #f6f6f8;
}

.bottom-action .van-button {
  height: 48px;
  background: #3f86f7;
  border: 0;
  font-size: 16px;
  font-weight: 500;
}

@media (max-width: 360px) {
  .summary-card {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
