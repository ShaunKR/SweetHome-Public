<template>
  <div class="wrap">
    <div class="header">
      <h2>화분 목록</h2>
      <router-link class="btnPrimary" to="/plants/add">+ 추가</router-link>
    </div>

    <div v-if="loading" class="muted">불러오는 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else-if="plants.length === 0" class="muted">등록된 화분이 없습니다.</div>

    <ul v-else class="list">
      <li v-for="p in plants" :key="p.id" class="item">
        <router-link class="left" :to="`/plants/${p.id}`">
          <div class="thumb">
            <img v-if="p.imageUrl" :src="p.imageUrl" alt="thumb" />
            <div v-else class="noThumb">no image</div>
          </div>
          <div class="meta">
            <div class="name">{{ p.name }}</div>
            <div class="sub">주기: {{ p.wateringCycle }}일 · 마지막: {{ fmt(p.lastWateredAt) }}</div>
          </div>
        </router-link>

        <div class="actions">
          <router-link class="btn" :to="`/plants/edit/${p.id}`">수정</router-link>
          <button class="btn danger" @click.prevent="remove(p.id)">삭제</button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";

const plants = ref([]);
const loading = ref(false);
const error = ref("");

function fmt(v) {
  if (!v) return "-";
  return String(v).replace("T", " ").slice(0, 16);
}

async function load() {
  loading.value = true;
  error.value = "";
  try {
    const res = await fetch("http://127.0.0.1:9090/api/plants");

    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    plants.value = await res.json();
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
  }
}
async function remove(id) {
  if (!confirm("정말 삭제할까요?")) return;

  try {
    const res = await fetch(`/api/plants/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error(`삭제 실패 HTTP ${res.status}`);
    await load(); // ✅ 삭제 후 목록 갱신
  } catch (e) {
    alert(e.message);
  }
}

onMounted(load);
</script>

<style scoped>
.wrap { max-width: 900px; margin: 0 auto; }
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom: 12px; }
.list { list-style:none; padding:0; margin:0; display:grid; gap:10px; }
.item { border:1px solid #eee; border-radius:14px; padding:12px; display:flex; justify-content:space-between; gap:12px; align-items:center; }
.left { display:flex; gap:12px; align-items:center; text-decoration:none; color:#111; flex: 1; }
.thumb { width:72px; height:72px; border-radius:12px; overflow:hidden; border:1px solid #eee; display:flex; align-items:center; justify-content:center; }
.thumb img { width:100%; height:100%; object-fit:cover; }
.noThumb { font-size:12px; color:#777; }
.meta { display:grid; gap:4px; }
.name { font-weight:800; }
.sub { color:#666; font-size:13px; }
.actions { display:flex; gap:8px; }
.btnPrimary { padding:10px 14px; border-radius:12px; background:#111; color:#fff; text-decoration:none; }
.btn { padding:10px 14px; border-radius:12px; border:1px solid #ddd; background:#fff; text-decoration:none; color:#111; }
.muted { color:#777; }
.error { color:#d33; white-space: pre-wrap; }
.btn.danger { color:#c62828; border-color:#ef9a9a; }

</style>
