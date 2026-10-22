<template>
  <div class="wrap">
    <h2>물주기 처리</h2>

    <div v-if="loading" class="muted">처리 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="card">
      <div class="ok">✅ 물주기 완료</div>
      <div class="muted">잠시 후 상세 화면으로 이동합니다.</div>

      <div class="actions">
        <router-link class="btnPrimary" :to="`/plants/${plantId}`">상세로 가기</router-link>
        <router-link class="btn" to="/plants">목록</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const plantId = Number(route.params.id);

const loading = ref(true);
const error = ref("");

async function waterByQR() {
  loading.value = true;
  error.value = "";

  try {
    const res = await fetch(`/api/plants/${plantId}/water?source=QR`, {
      method: "POST",
    });
    if (!res.ok) throw new Error(`물주기 실패 HTTP ${res.status}`);

    // 1.5초 후 상세로 이동(원하면 바로 이동도 가능)
    setTimeout(() => {
      router.replace(`/plant/${plantId}`);
    }, 1500);
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (!Number.isFinite(plantId)) {
    error.value = "잘못된 QR 입니다.";
    loading.value = false;
    return;
  }
  waterByQR();
});
</script>

<style scoped>
.wrap { max-width: 560px; margin: 24px auto; padding: 16px; }
.card { border:1px solid #eee; border-radius:16px; padding:16px; display:grid; gap:12px; }
.ok { font-weight: 900; font-size: 18px; }
.actions { display:flex; gap:10px; flex-wrap:wrap; margin-top: 10px; }
.btnPrimary { padding:10px 14px; border-radius:12px; background:#111; color:#fff; text-decoration:none; }
.btn { padding:10px 14px; border-radius:12px; border:1px solid #ddd; background:#fff; text-decoration:none; color:#111; }
.muted { color:#777; }
.error { color:#d33; white-space: pre-wrap; }
</style>
