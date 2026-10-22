<script setup>
import { computed, onMounted, ref } from 'vue'

const loading = ref(false)

const summary = ref({
  income: 0,
  expense: 0,
  balance: 0
})

const recentItems = ref([])
const plantWatering = ref([])

function formatWon(value) {
  return Number(value).toLocaleString('ko-KR') + '원'
}

function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')

  return `${y}-${m}-${d}`
}

const expenseRate = computed(() => {
  if (summary.value.income <= 0) {
    return 0
  }

  return Math.min(
      100,
      Math.round(
          (summary.value.expense / summary.value.income) * 100
      )
  )
})

async function loadDashboard() {
  loading.value = true

  try {
    await Promise.all([
      loadAccountSummary(),
      loadPlantWidget()
    ])
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function loadAccountSummary() {
  const now = new Date()

  const from = formatDate(
      new Date(now.getFullYear(), now.getMonth(), 1)
  )

  const to = formatDate(
      new Date(now.getFullYear(), now.getMonth() + 1, 0)
  )

  const response = await fetch(
      `/api/account?from=${from}&to=${to}`
  )

  if (!response.ok) {
    throw new Error('가계부 데이터를 불러오지 못했습니다.')
  }

  const data = await response.json()

  const mapped = data.map(item => ({
    id: item.id,
    title: item.title,
    type: item.transactionType,
    amount: item.amount,
    date: item.transactionDate
  }))

  recentItems.value = mapped
      .sort((a, b) => b.id - a.id)
      .slice(0, 5)

  const income = mapped
      .filter(item => item.type === 'income')
      .reduce((sum, item) => sum + item.amount, 0)

  const expense = mapped
      .filter(item => item.type === 'expense')
      .reduce((sum, item) => sum + item.amount, 0)

  summary.value = {
    income,
    expense,
    balance: income - expense
  }
}

async function loadPlantWidget() {
  const response = await fetch('/api/plants')

  if (!response.ok) {
    throw new Error('식물 데이터를 불러오지 못했습니다.')
  }

  const data = await response.json()

  const today = new Date()

  plantWatering.value = data
      .map(plant => {
        const lastDate = plant.lastWateredAt
            ? new Date(plant.lastWateredAt)
            : null

        let nextWaterDate = null

        if (lastDate && plant.wateringCycle) {
          nextWaterDate = new Date(lastDate)

          nextWaterDate.setDate(
              nextWaterDate.getDate() + plant.wateringCycle
          )
        }

        let status = '정보 없음'

        if (nextWaterDate) {
          const diff =
              Math.ceil(
                  (nextWaterDate - today) /
                  (1000 * 60 * 60 * 24)
              )

          if (diff <= 0) {
            status = '오늘 물주기'
          } else if (diff === 1) {
            status = '내일 예정'
          } else {
            status = `${diff}일 후`
          }
        }

        return {
          id: plant.id,
          name: plant.name,
          status
        }
      })
      .sort((a, b) => {
        if (a.status.includes('오늘')) return -1
        if (b.status.includes('오늘')) return 1
        return 0
      })
      .slice(0, 5)
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <main class="home-page">
    <div v-if="loading" class="loading-box">
      데이터를 불러오는 중입니다...
    </div>

    <section class="account-dashboard">
      <div class="page-title">
        <h1>가계부 대시보드</h1>
        <p>이번 달 소비 현황</p>
      </div>

      <div class="summary-card balance">
        <span>현재 잔액</span>
        <strong>{{ formatWon(summary.balance) }}</strong>
      </div>

      <div class="summary-grid">
        <div class="summary-card">
          <span>수입</span>
          <strong class="income">
            {{ formatWon(summary.income) }}
          </strong>
        </div>

        <div class="summary-card">
          <span>지출</span>
          <strong class="expense">
            {{ formatWon(summary.expense) }}
          </strong>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span>지출 비율</span>
          <strong>{{ expenseRate }}%</strong>
        </div>

        <div class="progress">
          <div
              class="progress-bar"
              :style="{ width: expenseRate + '%' }"
          ></div>
        </div>

        <p>
          이번 달 수입 대비 지출 비율입니다.
        </p>
      </div>

      <div class="recent-card">
        <div class="section-header">
          <h2>최근 내역</h2>

          <router-link to="/account">
            전체보기
          </router-link>
        </div>

        <div
            v-if="recentItems.length === 0"
            class="empty-box"
        >
          등록된 내역이 없습니다.
        </div>

        <ul v-else>
          <li
              v-for="item in recentItems"
              :key="item.id"
          >
            <div>
              <strong>{{ item.title }}</strong>
              <span>{{ item.date }}</span>
            </div>

            <em :class="item.type">
              {{ item.type === 'income' ? '+' : '-' }}
              {{ formatWon(item.amount) }}
            </em>
          </li>
        </ul>
      </div>
    </section>

    <section class="plant-widget">
      <div class="section-header">
        <h2>식물 물주기</h2>

        <router-link to="/plants">
          관리
        </router-link>
      </div>

      <div
          v-if="plantWatering.length === 0"
          class="empty-box"
      >
        등록된 식물이 없습니다.
      </div>

      <div v-else class="plant-list">
        <div
            v-for="plant in plantWatering"
            :key="plant.id"
            class="plant-item"
        >
          <div class="plant-icon">
            🌱
          </div>

          <div>
            <strong>{{ plant.name }}</strong>
            <span>{{ plant.status }}</span>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.home-page {
  padding: 16px;
  padding-bottom: 84px;
  background: #f6f7f9;
  min-height: 100vh;
}

.loading-box {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 12px;
  text-align: center;
  color: #777;
  font-size: 13px;
}

.empty-box {
  margin-top: 12px;
  color: #888;
  font-size: 13px;
}

.page-title {
  margin-bottom: 16px;
}

.page-title h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
}

.page-title p {
  margin: 4px 0 0;
  color: #777;
  font-size: 14px;
}

.account-dashboard {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.summary-card,
.chart-card,
.recent-card,
.plant-widget {
  background: #fff;
  border-radius: 18px;
  padding: 16px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}

.summary-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-card span {
  color: #777;
  font-size: 13px;
}

.summary-card strong {
  font-size: 18px;
}

.summary-card.balance strong {
  font-size: 26px;
}

.income {
  color: #1f8f4d;
}

.expense {
  color: #e04545;
}

.chart-header,
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 17px;
}

.section-header a {
  color: #4b6cff;
  font-size: 13px;
  text-decoration: none;
}

.progress {
  margin-top: 14px;
  height: 12px;
  background: #edf0f4;
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: #4b6cff;
  border-radius: 999px;
}

.chart-card p {
  margin: 10px 0 0;
  font-size: 13px;
  color: #777;
}

.recent-card ul {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
}

.recent-card li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.recent-card li:last-child {
  border-bottom: none;
}

.recent-card li div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recent-card li span {
  font-size: 12px;
  color: #888;
}

.recent-card em {
  font-style: normal;
  font-weight: 700;
}

.recent-card em.income {
  color: #1f8f4d;
}

.recent-card em.expense {
  color: #e04545;
}

.plant-widget {
  margin-top: 14px;
}

.plant-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plant-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f5faf6;
  border-radius: 14px;
  padding: 10px;
}

.plant-icon {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #e1f4e5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.plant-item strong {
  display: block;
  font-size: 14px;
}

.plant-item span {
  display: block;
  margin-top: 2px;
  font-size: 12px;
  color: #5f8f65;
}
</style>
