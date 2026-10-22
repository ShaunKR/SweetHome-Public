<template>
  <div class="wrap">
    <div v-if="loading" class="muted">불러오는 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else-if="plant" class="card">
      <div class="header">
        <h2 class="title">{{ plant.name }}</h2>
        <div class="headerActions">
          <router-link class="btn" :to="`platns/edit/${plant.id}`">수정</router-link>
          <button class="btn danger" @click="remove" :disabled="deleting">삭제</button>
        </div>
      </div>

      <div v-if="plant.imageUrl" class="imageBox">
        <img :src="plant.imageUrl" alt="plant" />
      </div>
      <div v-else class="noImage">이미지 없음</div>

      <ul class="info">
        <li><span>구입일</span><b>{{ plant.purchaseDate || "-" }}</b></li>
        <li><span>물주기 주기</span><b>{{ plant.wateringCycle }}일</b></li>
        <li><span>마지막 물준날</span><b>{{ fmt(plant.lastWateredAt) }}</b></li>
      </ul>

      <div class="actions primary">
        <button class="btnPrimary" @click="water" :disabled="watering">
          {{ watering ? "처리중..." : "💧 물 주기" }}
        </button>
      </div>

      <div v-if="message" class="muted">{{ message }}</div>

      <div class="sectionHeader">
        <h3>물주기 이력</h3>
        <button class="btn" @click="loadAll" :disabled="historyLoading">새로고침</button>
      </div>

      <div v-if="historyLoading" class="muted">이력 불러오는 중...</div>
      <div v-else-if="histories.length === 0" class="muted">이력이 없습니다.</div>

      <ul v-else class="history">
        <li v-for="h in histories" :key="h.id" class="historyItem">
          <div class="dt">{{ fmt(h.wateredAt) }}</div>
          <div class="src">{{ h.source || "-" }}</div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const plantId = Number(route.params.id);

const loading = ref(false);
const historyLoading = ref(false);
const watering = ref(false);
const deleting = ref(false);

const error = ref("");
const message = ref("");

const plant = ref(null);
const histories = ref([]);

function fmt(v) {
  if (!v) return "-";
  return String(v).replace("T", " ").slice(0, 16);
}

async function fetchPlant() {
  const res = await fetch(`/api/plants/${plantId}`);
  if (!res.ok) throw new Error(`plant 조회 실패 HTTP ${res.status}`);
  return await res.json();
}

async function fetchHistory() {
  const res = await fetch(`/api/plants/${plantId}/history`);
  if (!res.ok) throw new Error(`history 조회 실패 HTTP ${res.status}`);
  return await res.json();
}

async function loadAll() {
  error.value = "";
  message.value = "";
  loading.value = true;
  historyLoading.value = true;
  try {
    const [p, hs] = await Promise.all([fetchPlant(), fetchHistory()]);
    plant.value = p;
    histories.value = Array.isArray(hs) ? hs : [];
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
    historyLoading.value = false;
  }
}

async function water() {
  watering.value = true;
  error.value = "";
  message.value = "";
  try {
    //const res = await fetch(`/api/plants/${plantId}/water`, { method: "POST" });
    const res = await fetch(`/api/plants/${plantId}/water?source=UI`, { method: "POST" });

    if (!res.ok) throw new Error(`물주기 실패 HTTP ${res.status}`);

    // 가장 확실: 서버 처리 후 상세+이력 재조회
    const [p, hs] = await Promise.all([fetchPlant(), fetchHistory()]);
    plant.value = p;
    histories.value = Array.isArray(hs) ? hs : [];
    message.value = "물주기 업데이트 완료";
  } catch (e) {
    error.value = e.message;
  } finally {
    watering.value = false;
  }
}

async function remove() {
  if (!confirm("정말 삭제할까요?")) return;
  deleting.value = true;
  error.value = "";
  try {
    const res = await fetch(`/api/plants/${plantId}`, { method: "DELETE" });
    if (!res.ok) throw new Error(`삭제 실패 HTTP ${res.status}`);
    router.push("/");
  } catch (e) {
    error.value = e.message;
  } finally {
    deleting.value = false;
  }
}

onMounted(loadAll);
</script>

<style scoped>
.wrap { max-width: 720px; margin: 0 auto; }
.card { border:1px solid #eee; border-radius:16px; padding:16px; display:grid; gap:12px; }

.header { display:flex; justify-content:space-between; gap:12px; align-items:center; }
.title { margin:0; font-size:22px; }
.headerActions { display:flex; gap:8px; }

.imageBox img { width:100%; max-height:360px; object-fit:cover; border-radius:14px; border:1px solid #eee; }
.noImage { padding:14px; border:1px dashed #ddd; border-radius:14px; color:#777; text-align:center; }

.info { list-style:none; padding:0; margin:0; display:grid; gap:8px; }
.info li { display:flex; justify-content:space-between; padding:10px 12px; border:1px solid #f0f0f0; border-radius:12px; }
.info span { color:#666; }

.actions.primary { display:flex; justify-content:center; }
.btnPrimary { padding:14px 20px; border-radius:14px; background:#111; color:#fff; border:none; min-width:180px; font-size:16px; }
.btn { padding:10px 14px; border-radius:12px; border:1px solid #ddd; background:#fff; text-decoration:none; color:#111; }
.btn.danger { color:#c62828; border-color:#ef9a9a; }

.sectionHeader { display:flex; justify-content:space-between; align-items:center; margin-top:8px; }
.history { list-style:none; padding:0; margin:0; display:grid; gap:8px; }
.historyItem { display:flex; justify-content:space-between; padding:10px 12px; border:1px solid #f0f0f0; border-radius:12px; }
.dt { font-weight:700; }
.src { color:#666; }

.muted { color:#777; }
.error { color:#d33; white-space: pre-wrap; }
</style>
