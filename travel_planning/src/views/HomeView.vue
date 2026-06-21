<template>
  <main class="home-page">
    <van-nav-bar title="智能旅游助手" fixed placeholder />

    <section class="notice-wrap">
      <van-notice-bar
        left-icon="info-o"
        text="基于 AI 的智能景点介绍与行程规划系统"
        background="#fff8e8"
        color="#c58a45"
      />
    </section>

    <section class="panel planning-panel">
      <h2 class="panel-title">规划你的旅程</h2>

      <van-cell-group class="form-list" :border="false">
        <van-field
          v-model="city"
          label="目的地"
          placeholder="请选择城市"
          readonly
          right-icon="arrow"
          input-align="left"
          @click="openCitySelector"
        />
        <van-field
          v-model="budget"
          label="预算（元）"
          placeholder="请输入预算金额"
          type="number"
          input-align="left"
        />
        <van-field
          v-model="days"
          label="天数"
          placeholder="请输入天数"
          type="digit"
          input-align="left"
        />
      </van-cell-group>

      <van-button class="plan-button" type="primary" block round @click="startPlanning">
        开始规划
      </van-button>
    </section>

    <section class="panel">
      <h2 class="panel-title">快捷入口</h2>
      <div class="quick-grid">
        <button class="quick-item" type="button" @click="router.push('/chat')">
          <van-icon name="chat-o" />
          <span>AI 对话</span>
        </button>
        <button class="quick-item" type="button" @click="router.push('/profile')">
          <van-icon name="user-o" />
          <span>我的</span>
        </button>
      </div>
    </section>

    <section class="panel">
      <h2 class="panel-title">热门目的地</h2>
      <div class="destination-grid">
        <button
          v-for="city in citys"
          :key="city"
          class="destination-item"
          type="button"
          @click="selectCity(city)"
        >
          <span>{{ city }}</span>
        </button>
      </div>
    </section>

    <van-popup v-model:show="showCitySelector" class="city-selector-popup">
      <div class="city-selector">
        <van-nav-bar
          title="选择目的地"
          left-arrow
          fixed
          placeholder
          @click-left="showCitySelector = false"
        />

        <van-loading v-if="cityLoading" class="city-loading" color="#3f86f7" vertical>
          城市加载中
        </van-loading>

        <van-empty
          v-else-if="!groupedCities.length"
          class="city-empty"
          image="search"
          description="暂无城市数据"
        >
          <van-button size="small" type="primary" @click="loadCities">重新加载</van-button>
        </van-empty>

        <van-index-bar v-else :index-list="cityIndexList" :sticky-offset-top="46">
          <template v-for="group in groupedCities" :key="group.letter">
            <van-index-anchor :index="group.letter" />
            <van-cell
              v-for="item in group.cities"
              :key="`${group.letter}-${item}`"
              :title="item"
              clickable
              @click="selectCity(item)"
            />
          </template>
        </van-index-bar>
      </div>
    </van-popup>
  </main>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { getAllCity } from '@/utils/request'

const router = useRouter()

const city = ref('')
const budget = ref('')
const days = ref('')
const showCitySelector = ref(false)
const cityLoading = ref(false)
const allCities = ref<string[]>([])

const citys = ['北京', '上海', '广州', '深圳', '成都', '杭州', '西安', '重庆']
const cityCollator = new Intl.Collator('zh-Hans-u-co-pinyin')

const cityInitialMap: Record<string, string> = {
  北京: 'B',
  上海: 'S',
  广州: 'G',
  深圳: 'S',
  成都: 'C',
  杭州: 'H',
  西安: 'X',
  重庆: 'C',
  天津: 'T',
  苏州: 'S',
  南京: 'N',
  武汉: 'W',
  长沙: 'C',
  厦门: 'X',
  青岛: 'Q',
  大连: 'D',
  宁波: 'N',
}

const pinyinBounds = [
  ['A', '阿'],
  ['B', '八'],
  ['C', '嚓'],
  ['D', '咑'],
  ['E', '妸'],
  ['F', '发'],
  ['G', '旮'],
  ['H', '哈'],
  ['J', '讥'],
  ['K', '咔'],
  ['L', '垃'],
  ['M', '妈'],
  ['N', '拏'],
  ['O', '噢'],
  ['P', '妑'],
  ['Q', '七'],
  ['R', '呥'],
  ['S', '仨'],
  ['T', '他'],
  ['W', '屲'],
  ['X', '夕'],
  ['Y', '丫'],
  ['Z', '帀'],
] as const

const getCityInitial = (name: string) => {
  const trimmedName = name.trim()
  const firstChar = trimmedName.charAt(0)
  const latinInitial = firstChar.toUpperCase()

  if (cityInitialMap[trimmedName]) {
    return cityInitialMap[trimmedName]
  }

  if (/^[A-Z]$/.test(latinInitial)) {
    return latinInitial
  }

  for (let index = pinyinBounds.length - 1; index >= 0; index -= 1) {
    const bound = pinyinBounds[index]
    if (bound && cityCollator.compare(trimmedName, bound[1]) >= 0) {
      return bound[0]
    }
  }

  return '#'
}

const groupedCities = computed(() => {
  const groups = allCities.value
    .filter((item) => item.trim())
    .reduce<Record<string, string[]>>((result, item) => {
      const initial = getCityInitial(item)
      result[initial] = result[initial] ?? []
      result[initial].push(item)
      return result
    }, {})

  return Object.entries(groups)
    .map(([letter, cities]) => ({
      letter,
      cities: [...new Set(cities)].sort(cityCollator.compare),
    }))
    .sort((current, next) => {
      if (current.letter === '#') return 1
      if (next.letter === '#') return -1
      return current.letter.localeCompare(next.letter)
    })
})

const cityIndexList = computed(() => groupedCities.value.map((group) => group.letter))

const loadCities = async () => {
  cityLoading.value = true
  try {
    const cityList = await getAllCity()
    allCities.value = cityList.filter((item): item is string => typeof item === 'string')
  } catch {
    showToast('城市加载失败，请稍后重试')
  } finally {
    cityLoading.value = false
  }
}

const openCitySelector = () => {
  showCitySelector.value = true
  void loadCities()
}

const startPlanning = () => {
  if (!city.value) {
    showToast('请选择目的地')
    return
  }
  if (!budget.value || Number(budget.value) < 100) {
    showToast('预算金额不能低于100元')
    return
  }
  if (!days.value || Number(days.value) < 1 || Number(days.value) > 30) {
    showToast('旅游天数必须在1-30天之间')
    return
  }
  //点击开始规划按钮后，携带目的地、预算、天数跳转到详情页
  router.push({
    path: '/detail',
    query: {
      city: city.value,
      budget: budget.value,
      days: days.value,
    },
  })
}

const selectCity = (selectedCity: string) => {
  city.value = selectedCity
  showCitySelector.value = false
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 12px 16px 18px;
  background: #f7f8fa;
}

.notice-wrap {
  margin: 0 0 14px;
}

:deep(.van-nav-bar) {
  background: #ffffff;
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

:deep(.van-notice-bar) {
  height: 38px;
  padding: 0 14px;
  border-radius: 0;
  font-size: 14px;
}

.panel {
  margin-bottom: 14px;
  padding: 16px;
  overflow: hidden;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(31, 41, 55, 0.06);
}

.panel-title {
  margin: 0 0 12px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.35;
}

.form-list {
  display: grid;
  gap: 10px;
  margin-bottom: 12px;
  background: transparent;
}

:deep(.form-list .van-cell) {
  min-height: 42px;
  padding: 0 14px;
  align-items: center;
  background: #f6f7fb;
  border-radius: 6px;
}

:deep(.form-list .van-field__label) {
  width: 78px;
  margin-right: 12px;
  color: #111827;
  font-size: 14px;
}

:deep(.form-list .van-field__control) {
  color: #111827;
  font-size: 14px;
}

:deep(.form-list .van-field__control::placeholder) {
  color: #c4c9d2;
}

.plan-button {
  height: 46px;
  margin-top: 2px;
  border: 0;
  background: #3f86f7;
  font-size: 16px;
  font-weight: 500;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 0 10px;
}

.quick-item {
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: #ffffff;
  border: 1px solid #edf0f5;
  color: #111827;
  font: inherit;
}

.quick-item .van-icon {
  font-size: 25px;
}

.quick-item span {
  font-size: 13px;
  line-height: 1.2;
}

.destination-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  padding: 0 6px;
}

.destination-item {
  min-height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border: 1px solid #edf0f5;
  color: #4b5563;
  font: inherit;
}

.destination-item span {
  min-width: 52px;
  padding: 7px 12px;
  background: #f7f8fb;
  border-radius: 999px;
  font-size: 14px;
  line-height: 1.2;
}

.city-selector-popup {
  width: min(100vw, 430px);
  height: 100%;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  background: #ffffff;
}

.city-selector {
  min-height: 100%;
  background: #ffffff;
}

:deep(.city-selector .van-nav-bar) {
  background: #ffffff;
}

:deep(.city-selector .van-nav-bar__title) {
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

.city-loading,
.city-empty {
  padding-top: 96px;
}

:deep(.city-selector .van-index-anchor) {
  padding: 10px 16px 8px;
  background: #ffffff;
  color: #2f3338;
  font-size: 20px;
  font-weight: 500;
  line-height: 1.2;
}

:deep(.city-selector .van-cell) {
  min-height: 66px;
  padding: 0 48px 0 18px;
  align-items: center;
  color: #2f3338;
  font-size: 26px;
  line-height: 1.2;
}

:deep(.city-selector .van-cell::after) {
  left: 18px;
  right: 48px;
  border-bottom-color: #eeeeee;
}

:deep(.city-selector .van-index-bar__sidebar) {
  right: max(8px, calc((100vw - 430px) / 2 + 8px));
}

:deep(.city-selector .van-index-bar__index) {
  padding: 3px 8px;
  color: #45a5ee;
  font-size: 15px;
  font-weight: 500;
}

@media (max-width: 340px) {
  .home-page {
    padding-inline: 12px;
  }

  .destination-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  :deep(.city-selector .van-cell) {
    font-size: 22px;
  }
}
</style>
