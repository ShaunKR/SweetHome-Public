<template>
  <div class="wrap">
    <h2>화분 추가</h2>

    <div class="card">
      <label class="label">이름</label>
      <input class="input" v-model.trim="form.name" placeholder="예: 스파티필름" />

      <label class="label">구입일</label>
      <input class="input" type="date" v-model="form.purchaseDate" />

      <label class="label">물주기 주기(일)</label>
      <input class="input" type="number" min="1" v-model.number="form.wateringCycle" />

      <label class="label">이미지(선택)</label>
      <input type="file" accept="image/*" @change="onFileChange" />

      <div v-if="previewUrl" class="preview">
        <img :src="previewUrl" alt="preview" />
      </div>

      <div class="actions">
        <button class="btnPrimary" :disabled="saving" @click="createWithOptionalImage">
          {{ saving ? "저장중..." : "저장" }}
        </button>
        <router-link class="btn" to="/plants">목록</router-link>
      </div>

      <div v-if="message" class="muted">{{ message }}</div>
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  name: "",
  purchaseDate: "",
  wateringCycle: 7,
});

const file = ref(null);
const previewUrl = ref("");

const saving = ref(false);
const error = ref("");
const message = ref("");

function onFileChange(e) {
  const f = e.target.files?.[0] || null;
  file.value = f;
  previewUrl.value = f ? URL.createObjectURL(f) : "";
}

async function uploadImage(plantId) {
  const fd = new FormData();
  fd.append("file", file.value);

  const res = await fetch(`/api/plants/${plantId}/image`, {
    method: "POST",
    body: fd,
  });
  if (!res.ok) throw new Error(`이미지 업로드 실패 HTTP ${res.status}`);
  await res.json(); // { imageUrl }
}

async function createWithOptionalImage() {
  error.value = "";
  message.value = "";

  if (!form.value.name) {
    error.value = "이름은 필수입니다.";
    return;
  }

  saving.value = true;
  try {
    const payload = {
      name: form.value.name,
      purchaseDate: form.value.purchaseDate || null,
      wateringCycle: form.value.wateringCycle ?? 7,
    };

    const res = await fetch("/api/plants", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(`저장 실패 HTTP ${res.status}`);

    const created = await res.json();
    const id = created.id;
    if (!id) throw new Error("생성 응답에 id가 없습니다.");

    if (file.value) {
      await uploadImage(id);
    }

    router.push(`/plants/${id}`);
  } catch (e) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
}
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
