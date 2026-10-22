<template>
  <div class="uploader">
    <label class="label">이미지</label>

    <div class="row">
      <input type="file" accept="image/*" @change="onFileChange" />
      <button class="btn" :disabled="!plantId || !file || uploading" @click="upload">
        {{ uploading ? '업로드중...' : '업로드' }}
      </button>
    </div>

    <div v-if="previewUrl" class="preview">
      <img :src="previewUrl" alt="preview" />
    </div>

    <div v-if="message" class="muted">{{ message }}</div>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";

const props = defineProps({
  plantId: { type: Number, default: null },
  // 기존 이미지 URL이 있으면(수정 화면) 보여주기
  initialImageUrl: { type: String, default: "" },
});

const emit = defineEmits(["uploaded"]); // uploaded(imageUrl)

const file = ref(null);
const uploading = ref(false);
const error = ref("");
const message = ref("");
const previewUrl = ref("");

watch(
    () => props.initialImageUrl,
    (v) => {
      // 파일 선택 전에는 기존 이미지 보여줌
      if (!file.value) previewUrl.value = v || "";
    },
    { immediate: true }
);

function onFileChange(e) {
  error.value = "";
  message.value = "";
  const f = e.target.files?.[0] || null;
  file.value = f;

  if (f) {
    previewUrl.value = URL.createObjectURL(f);
  } else {
    previewUrl.value = props.initialImageUrl || "";
  }
}

async function upload() {
  if (!props.plantId) {
    error.value = "먼저 화분을 저장한 뒤 이미지 업로드가 가능합니다.";
    return;
  }
  if (!file.value) return;

  uploading.value = true;
  error.value = "";
  message.value = "";

  try {
    const fd = new FormData();
    fd.append("file", file.value);

    const res = await fetch(`/api/plants/${props.plantId}/image`, {
      method: "POST",
      body: fd,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    const data = await res.json(); // { imageUrl: "/uploads/..." }
    message.value = "이미지 업로드 완료";
    emit("uploaded", data.imageUrl);

    // 업로드 후엔 파일 선택 상태 유지해도 되는데, 보통은 초기화
    // file.value = null;
  } catch (e) {
    error.value = `이미지 업로드 실패: ${e.message}`;
  } finally {
    uploading.value = false;
  }
}
</script>

<style scoped>
.uploader { display: grid; gap: 8px; margin-top: 12px; }
.label { font-weight: 600; }
.row { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.btn { padding: 10px 12px; border-radius: 10px; border: 1px solid #ddd; background: #fff; }
.preview img { max-width: 100%; border-radius: 12px; border: 1px solid #e6e6e6; }
.muted { color: #777; }
.error { color: #d33; }
</style>
