<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const API_BASE_URL = 'http://localhost:9090/api/account'

const viewMode = ref('month')
const currentDate = ref(new Date())
const selectedDate = ref(formatDate(new Date()))
const showForm = ref(false)
const editId = ref(null)
const loading = ref(false)

const expenseCategories = ['식비', '교통', '생활', '쇼핑', '병원', '기타']
const incomeCategories = ['급여', '부수입', '용돈', '이자', '기타']

const form = ref({
  type: 'expense',
  category: '식비',
  title: '',
  amountText: '',
  payment: 'card',
  memo: ''
})

const accountItems = ref([])

onMounted(() => {
  fetchAccountItems()
})

watch(currentDate, () => {
  fetchAccountItems()
})

watch(
    () => form.value.type,
    (type) => {
      if (type === 'expense') {
        form.value.category = expenseCategories[0]
        if (form.value.payment === 'bank') {
          form.value.payment = 'card'
        }
      } else {
        form.value.category = incomeCategories[0]
        form.value.payment = 'bank'
      }
    }
)

const categoryOptions = computed(() => {
  return form.value.type === 'expense' ? expenseCategories : incomeCategories
})

function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function formatWon(value) {
  return Number(value).toLocaleString('ko-KR') + '원'
}

function parseAmount(text) {
  return Number(String(text).replaceAll(',', ''))
}

function onAmountInput() {
  const onlyNumber = form.value.amountText.replace(/[^0-9]/g, '')

  if (!onlyNumber) {
    form.value.amountText = ''
    return
  }

  form.value.amountText = Number(onlyNumber).toLocaleString('ko-KR')
}

const year = computed(() => currentDate.value.getFullYear())
const month = computed(() => currentDate.value.getMonth())

const monthTitle = computed(() => {
  return `${year.value}년 ${month.value + 1}월`
})

const monthStartDate = computed(() => {
  return formatDate(new Date(year.value, month.value, 1))
})

const monthEndDate = computed(() => {
  return formatDate(new Date(year.value, month.value + 1, 0))
})

async function fetchAccountItems() {
  loading.value = true

  try {
    const url = `${API_BASE_URL}?from=${monthStartDate.value}&to=${monthEndDate.value}`

    const response = await fetch(url)

    if (!response.ok) {
      throw new Error('가계부 데이터를 불러오지 못했습니다.')
    }

    const data = await response.json()

    accountItems.value = data.map(item => ({
      id: item.id,
      date: item.transactionDate,
      type: item.transactionType,
      category: item.category,
      title: item.title,
      amount: item.amount,
      payment: item.paymentMethod,
      memo: item.memo
    }))
  } catch (error) {
    console.error(error)
    alert('가계부 데이터를 불러오는 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

const selectedDateObject = computed(() => {
  return new Date(selectedDate.value)
})

const dayTitle = computed(() => {
  const date = new Date(selectedDate.value)
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
})

const calendarDays = computed(() => {
  const firstDay = new Date(year.value, month.value, 1)
  const lastDay = new Date(year.value, month.value + 1, 0)

  const startDay = firstDay.getDay()
  const days = []

  for (let i = 0; i < startDay; i++) {
    days.push(null)
  }

  for (let day = 1; day <= lastDay.getDate(); day++) {
    const date = new Date(year.value, month.value, day)

    days.push({
      day,
      date: formatDate(date)
    })
  }

  return days
})

const weekDays = computed(() => {
  const base = selectedDateObject.value
  const day = base.getDay()

  const start = new Date(base)
  start.setDate(base.getDate() - day)

  const days = []

  for (let i = 0; i < 7; i++) {
    const date = new Date(start)
    date.setDate(start.getDate() + i)

    days.push({
      day: date.getDate(),
      date: formatDate(date),
      label: ['일', '월', '화', '수', '목', '금', '토'][date.getDay()]
    })
  }

  return days
})

const weekTitle = computed(() => {
  const start = weekDays.value[0].date
  const end = weekDays.value[6].date
  return `${start} ~ ${end}`
})

const monthlyItems = computed(() => {
  const targetMonth = String(month.value + 1).padStart(2, '0')
  const targetPrefix = `${year.value}-${targetMonth}`

  return accountItems.value.filter(item => item.date.startsWith(targetPrefix))
})

const monthlyIncome = computed(() => {
  return monthlyItems.value
      .filter(item => item.type === 'income')
      .reduce((sum, item) => sum + item.amount, 0)
})

const monthlyExpense = computed(() => {
  return monthlyItems.value
      .filter(item => item.type === 'expense')
      .reduce((sum, item) => sum + item.amount, 0)
})

const balance = computed(() => {
  return monthlyIncome.value - monthlyExpense.value
})

const selectedItems = computed(() => {
  return accountItems.value.filter(item => item.date === selectedDate.value)
})

function getItemsByDate(date) {
  return accountItems.value.filter(item => item.date === date)
}

function getDailyIncome(date) {
  return getItemsByDate(date)
      .filter(item => item.type === 'income')
      .reduce((sum, item) => sum + item.amount, 0)
}

function getDailyExpense(date) {
  return getItemsByDate(date)
      .filter(item => item.type === 'expense')
      .reduce((sum, item) => sum + item.amount, 0)
}

function selectDate(date) {
  selectedDate.value = date
}

function openForm(date) {
  if (date) {
    selectedDate.value = date
  }

  editId.value = null
  resetForm()
  showForm.value = true
}

function openEditForm(item) {
  editId.value = item.id
  selectedDate.value = item.date

  form.value = {
    type: item.type,
    category: item.category,
    title: item.title,
    amountText: Number(item.amount).toLocaleString('ko-KR'),
    payment: item.payment,
    memo: item.memo || ''
  }

  showForm.value = true
}

function prevMonth() {
  currentDate.value = new Date(year.value, month.value - 1, 1)
}

function nextMonth() {
  currentDate.value = new Date(year.value, month.value + 1, 1)
}

function prevDay() {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() - 1)
  selectedDate.value = formatDate(date)
}

function nextDay() {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + 1)
  selectedDate.value = formatDate(date)
}

function prevWeek() {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() - 7)
  selectedDate.value = formatDate(date)
}

function nextWeek() {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + 7)
  selectedDate.value = formatDate(date)
}

function resetForm() {
  form.value = {
    type: 'expense',
    category: '식비',
    title: '',
    amountText: '',
    payment: 'card',
    memo: ''
  }
}

function buildRequestBody() {
  return {
    transactionDate: selectedDate.value,
    transactionType: form.value.type,
    category: form.value.category,
    title: form.value.title,
    amount: parseAmount(form.value.amountText),
    paymentMethod: form.value.payment,
    memo: form.value.memo
  }
}

async function saveItem() {
  const amount = parseAmount(form.value.amountText)

  if (!form.value.title || !amount) {
    alert('항목명과 금액을 입력해주세요.')
    return
  }

  const body = buildRequestBody()

  try {
    const url = editId.value
        ? `${API_BASE_URL}/${editId.value}`
        : API_BASE_URL

    const method = editId.value ? 'PUT' : 'POST'

    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    })

    if (!response.ok) {
      throw new Error('저장 실패')
    }

    editId.value = null
    resetForm()
    showForm.value = false

    await fetchAccountItems()
  } catch (error) {
    console.error(error)
    alert('저장 중 오류가 발생했습니다.')
  }
}

async function deleteItem(id) {
  if (!confirm('이 내역을 삭제하시겠습니까?')) {
    return
  }

  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      throw new Error('삭제 실패')
    }

    if (editId.value === id) {
      editId.value = null
      showForm.value = false
      resetForm()
    }

    await fetchAccountItems()
  } catch (error) {
    console.error(error)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

function closeForm() {
  showForm.value = false
  editId.value = null
  resetForm()
}
</script>

<template>
  <main class="account-page">
    <section class="page-header">
      <div>
        <h1>가계부</h1>
        <p>달력에서 날짜를 선택해 수입/지출을 등록하세요.</p>
      </div>

      <button class="header-add-button" @click="openForm()">
        +
      </button>
    </section>

    <section class="summary-grid">
      <div class="summary-card">
        <span>수입</span>
        <strong class="income">{{ formatWon(monthlyIncome) }}</strong>
      </div>

      <div class="summary-card">
        <span>지출</span>
        <strong class="expense">{{ formatWon(monthlyExpense) }}</strong>
      </div>

      <div class="summary-card">
        <span>잔액</span>
        <strong>{{ formatWon(balance) }}</strong>
      </div>
    </section>

    <section class="mode-tabs">
      <button :class="{ active: viewMode === 'day' }" @click="viewMode = 'day'">
        일별
      </button>

      <button :class="{ active: viewMode === 'week' }" @click="viewMode = 'week'">
        주간
      </button>

      <button :class="{ active: viewMode === 'month' }" @click="viewMode = 'month'">
        월별
      </button>
    </section>

    <div v-if="loading" class="loading-box">
      데이터를 불러오는 중입니다...
    </div>

    <section v-if="viewMode === 'day'" class="calendar-card">
      <div class="calendar-header">
        <button @click="prevDay">‹</button>
        <strong>{{ dayTitle }}</strong>
        <button @click="nextDay">›</button>
      </div>

      <div class="day-view">
        <div class="day-total">
          <div>
            <span>수입</span>
            <strong class="income">{{ formatWon(getDailyIncome(selectedDate)) }}</strong>
          </div>

          <div>
            <span>지출</span>
            <strong class="expense">{{ formatWon(getDailyExpense(selectedDate)) }}</strong>
          </div>
        </div>

        <button class="add-large-button" @click="openForm()">
          + 내역 추가
        </button>
      </div>
    </section>

    <section v-else-if="viewMode === 'week'" class="calendar-card">
      <div class="calendar-header">
        <button @click="prevWeek">‹</button>
        <strong>{{ weekTitle }}</strong>
        <button @click="nextWeek">›</button>
      </div>

      <div class="week-view">
        <div
            v-for="day in weekDays"
            :key="day.date"
            class="week-day-card"
            :class="{ selected: day.date === selectedDate }"
            @click="selectDate(day.date)"
            @dblclick="openForm(day.date)"
        >
          <div class="week-day-title">
            <span>{{ day.label }}</span>
            <strong>{{ day.day }}</strong>
          </div>

          <div class="daily-money">
            <span v-if="getDailyIncome(day.date) > 0" class="income">
              +{{ formatWon(getDailyIncome(day.date)) }}
            </span>

            <span v-if="getDailyExpense(day.date) > 0" class="expense">
              -{{ formatWon(getDailyExpense(day.date)) }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <section v-else class="calendar-card">
      <div class="calendar-header">
        <button @click="prevMonth">‹</button>
        <strong>{{ monthTitle }}</strong>
        <button @click="nextMonth">›</button>
      </div>

      <div class="week-row">
        <span>일</span>
        <span>월</span>
        <span>화</span>
        <span>수</span>
        <span>목</span>
        <span>금</span>
        <span>토</span>
      </div>

      <div class="calendar-grid">
        <div
            v-for="(day, index) in calendarDays"
            :key="index"
            class="day-cell"
            :class="{ selected: day?.date === selectedDate }"
            @click="day && selectDate(day.date)"
            @dblclick="day && openForm(day.date)"
        >
          <template v-if="day">
            <div class="day-number">{{ day.day }}</div>

            <div class="daily-money">
              <span v-if="getDailyIncome(day.date) > 0" class="income">
                +{{ formatWon(getDailyIncome(day.date)) }}
              </span>

              <span v-if="getDailyExpense(day.date) > 0" class="expense">
                -{{ formatWon(getDailyExpense(day.date)) }}
              </span>
            </div>
          </template>
        </div>
      </div>
    </section>

    <section class="selected-list">
      <div class="section-header">
        <h2>{{ selectedDate }} 내역</h2>
        <button @click="openForm()">추가</button>
      </div>

      <div v-if="selectedItems.length === 0" class="empty">
        등록된 내역이 없습니다.
      </div>

      <ul v-else>
        <li v-for="item in selectedItems" :key="item.id" @click="openEditForm(item)">
          <div>
            <strong>{{ item.title }}</strong>
            <span>
              {{ item.category }} ·
              {{ item.payment === 'card' ? '카드' : item.payment === 'cash' ? '현금' : '계좌' }}
            </span>
            <small v-if="item.memo">{{ item.memo }}</small>
          </div>

          <em :class="item.type">
            {{ item.type === 'income' ? '+' : '-' }}
            {{ formatWon(item.amount) }}
          </em>
        </li>
      </ul>
    </section>

    <div v-if="showForm" class="modal-backdrop" @click.self="closeForm">
      <div class="input-modal">
        <div class="modal-header">
          <h2>{{ editId ? '내역 수정' : selectedDate + ' 등록' }}</h2>
          <button @click="closeForm">×</button>
        </div>

        <div class="type-tabs">
          <button
              :class="{ active: form.type === 'expense' }"
              @click="form.type = 'expense'"
          >
            지출
          </button>

          <button
              :class="{ active: form.type === 'income' }"
              @click="form.type = 'income'"
          >
            수입
          </button>
        </div>

        <label>
          항목명
          <input v-model="form.title" placeholder="예: 점심, 월급" />
        </label>

        <label>
          카테고리
          <select v-model="form.category">
            <option
                v-for="category in categoryOptions"
                :key="category"
                :value="category"
            >
              {{ category }}
            </option>
          </select>
        </label>

        <label>
          금액
          <input
              v-model="form.amountText"
              inputmode="numeric"
              placeholder="금액 입력"
              @input="onAmountInput"
          />
        </label>

        <label>
          결제수단
          <select v-model="form.payment">
            <option v-if="form.type === 'expense'" value="card">카드</option>
            <option v-if="form.type === 'expense'" value="cash">현금</option>
            <option value="bank">계좌</option>
          </select>
        </label>

        <label>
          메모
          <textarea
              v-model="form.memo"
              rows="3"
              placeholder="메모를 입력하세요."
          ></textarea>
        </label>

        <div class="modal-actions">
          <button
              v-if="editId"
              class="delete-button"
              @click="deleteItem(editId)"
          >
            삭제
          </button>

          <button class="save-button" @click="saveItem">
            {{ editId ? '수정 저장' : '저장' }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.account-page {
  min-height: 100vh;
  padding: 16px;
  padding-bottom: 84px;
  background: #f6f7f9;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
}

.page-header p {
  margin: 6px 0 0;
  font-size: 14px;
  color: #777;
}

.header-add-button {
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 50%;
  background: #4b6cff;
  color: white;
  font-size: 26px;
  font-weight: 600;
}

.summary-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.summary-card {
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}

.summary-card span {
  display: block;
  font-size: 12px;
  color: #777;
}

.summary-card strong {
  display: block;
  margin-top: 6px;
  font-size: 14px;
}

.income {
  color: #1f8f4d;
}

.expense {
  color: #e04545;
}

.mode-tabs {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background: #e9ecf2;
  padding: 4px;
  border-radius: 14px;
}

.mode-tabs button {
  border: none;
  background: transparent;
  padding: 10px 0;
  border-radius: 11px;
  font-weight: 700;
  color: #777;
}

.mode-tabs button.active {
  background: #fff;
  color: #222;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.loading-box {
  margin-top: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  font-size: 13px;
  color: #777;
  text-align: center;
}

.calendar-card,
.selected-list {
  margin-top: 16px;
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}

.calendar-header,
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar-header button {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  background: #f1f3f6;
  font-size: 22px;
}

.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-top: 14px;
  text-align: center;
  font-size: 12px;
  color: #777;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-top: 8px;
  gap: 6px;
}

.day-cell {
  min-height: 74px;
  background: #f8f9fb;
  border-radius: 12px;
  padding: 6px;
  cursor: pointer;
}

.day-cell.selected,
.week-day-card.selected {
  outline: 2px solid #4b6cff;
  background: #f4f6ff;
}

.day-number {
  font-size: 13px;
  font-weight: 700;
}

.daily-money {
  margin-top: 6px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.daily-money span {
  font-size: 10px;
  font-weight: 700;
  word-break: keep-all;
}

.day-view {
  margin-top: 16px;
}

.day-total {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.day-total div {
  background: #f8f9fb;
  border-radius: 14px;
  padding: 14px;
}

.day-total span {
  display: block;
  font-size: 13px;
  color: #777;
}

.day-total strong {
  display: block;
  margin-top: 8px;
  font-size: 18px;
}

.add-large-button {
  width: 100%;
  margin-top: 14px;
  border: none;
  border-radius: 14px;
  background: #4b6cff;
  color: white;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 800;
}

.week-view {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.week-day-card {
  min-width: 96px;
  background: #f8f9fb;
  border-radius: 14px;
  padding: 12px;
  cursor: pointer;
}

.week-day-title span {
  display: block;
  font-size: 12px;
  color: #777;
}

.week-day-title strong {
  display: block;
  margin-top: 4px;
  font-size: 18px;
}

.section-header h2 {
  margin: 0;
  font-size: 16px;
}

.section-header button {
  border: none;
  background: #4b6cff;
  color: white;
  border-radius: 10px;
  padding: 8px 12px;
  font-weight: 700;
}

.empty {
  margin-top: 14px;
  color: #888;
  font-size: 14px;
}

.selected-list ul {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
}

.selected-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.selected-list li:last-child {
  border-bottom: none;
}

.selected-list li div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.selected-list li span {
  font-size: 12px;
  color: #777;
}

.selected-list li small {
  font-size: 12px;
  color: #999;
}

.selected-list em {
  font-style: normal;
  font-weight: 800;
  white-space: nowrap;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}

.input-modal {
  width: 100%;
  background: #fff;
  border-radius: 24px 24px 0 0;
  padding: 18px;
  box-sizing: border-box;
  max-height: 88vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
}

.modal-header button {
  border: none;
  background: #f1f3f6;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 20px;
}

.type-tabs {
  margin: 16px 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: #f1f3f6;
  border-radius: 14px;
  padding: 4px;
}

.type-tabs button {
  border: none;
  background: transparent;
  padding: 10px 0;
  border-radius: 11px;
  font-weight: 700;
}

.type-tabs button.active {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 12px;
  font-size: 13px;
  color: #555;
  font-weight: 700;
}

input,
select,
textarea {
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 12px;
  font-size: 15px;
  font-family: inherit;
}

textarea {
  resize: none;
}

.modal-actions {
  display: flex;
  gap: 8px;
  margin-top: 18px;
}

.save-button,
.delete-button {
  border: none;
  border-radius: 14px;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 800;
}

.save-button {
  flex: 1;
  background: #4b6cff;
  color: white;
}

.delete-button {
  width: 90px;
  background: #ffe8e8;
  color: #e04545;
}
</style>
