<template>
  <div class="wrap">
    <h2>화분 수정</h2>

    <div v-if="loading" class="muted">불러오는 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="card">
      <label class="label">이름</label>
      <input class="input" v-model.trim="form.name" />

      <label class="label">구입일</label>
      <input class="input" type="date" v-model="form.purchaseDate" />

      <label class="label">물주기 주기(일)</label>
      <input class="input" type="number" min="1" v-model.number="form.wateringCycle" />

      <label class="label">이미지 업로드</label>
      <input type="file" accept="image/*" @change="onFileChange" />
      <button class="btn" :disabled="!file || uploading" @click="upload">
        {{ uploading ? "업로드중..." : "업로드" }}
      </button>

      <div v-if="previewUrl" class="preview">
        <img :src="previewUrl" alt="preview" />
      </div>

      <div class="actions">
        <button class="btnPrimary" :disabled="saving" @click="save">
          {{ saving ? "저장중..." : "저장" }}
        </button>
        <router-link class="btn" :to="`/plants/${plantId}`">상세</router-link>
        <router-link class="btn" to="/plans">목록</router-link>
      </div>

      <div v-if="message" class="muted">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const plantId = Number(route.params.id);

const loading = ref(false);
const saving = ref(false);
const uploading = ref(false);
const error = ref("");
const message = ref("");

const form = ref({
  name: "",
  purchaseDate: "",
  wateringCycle: 7,
  imageUrl: "",
});

const file = ref(null);
const previewUrl = ref("");

function onFileChange(e) {
  const f = e.target.files?.[0] || null;
  file.value = f;
  previewUrl.value = f ? URL.createObjectURL(f) : (form.value.imageUrl || "");
}

async function load() {
  loading.value = true;
  error.value = "";
  try {
    const res = await fetch(`/api/plants/${plantId}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const p = await res.json();
    form.value.name = p.name || "";
    form.value.purchaseDate = p.purchaseDate || "";
    form.value.wateringCycle = p.wateringCycle ?? 7;
    form.value.imageUrl = p.imageUrl || "";
    if (!file.value) previewUrl.value = form.value.imageUrl;
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
  }
}

async function save() {
  saving.value = true;
  message.value = "";
  error.value = "";
  try {
    const payload = {
      name: form.value.name,
      purchaseDate: form.value.purchaseDate || null,
      wateringCycle: form.value.wateringCycle ?? 7,
    };

    const res = await fetch(`/api/plants/${plantId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    message.value = "저장 완료";
  } catch (e) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
}

async function upload() {
  if (!file.value) return;
  uploading.value = true;
  message.value = "";
  error.value = "";
  try {
    const fd = new FormData();
    fd.append("file", file.value);

    const res = await fetch(`/api/plants/${plantId}/image`, {
      method: "POST",
      body: fd,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json(); // { imageUrl }

    form.value.imageUrl = data.imageUrl || "";
    previewUrl.value = form.value.imageUrl
        ? `${form.value.imageUrl}?v=${Date.now()}`
        : "";
    message.value = "이미지 업로드 완료";
  } catch (e) {
    error.value = e.message;
  } finally {
    uploading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.wrap { max-width: 640px; margin: 0 auto; }
.card { border:1px solid #eee; border-radius:16px; padding:16px; display:grid; gap:10px; }
.label { font-weight:700; margin-top:6px; }
.input { padding:10px 12px; border-radius:12px; border:1px solid #ddd; }
.actions { display:flex; gap:10px; flex-wrap: wrap; margin-top: 10px; }
.btnPrimary { padding:10px 14px; border-radius:12px; background:#111; color:#fff; border:none; }
.btn { padding:10px 14px; border-radius:12px; border:1px solid #ddd; background:#fff; text-decoration:none; color:#111; }
.preview img { max-width:100%; border-radius:14px; border:1px solid #eee; }
.muted { color:#777; }
.error { color:#d33; white-space: pre-wrap; }
</style>
