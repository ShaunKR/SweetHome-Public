<script setup>
import { useRoute } from 'vue-router'
import { computed, ref, watch } from 'vue'

const route = useRoute()
const section = computed(() => route.meta.section)

const isMenuOpen = ref(false)

function openMenu() {
  isMenuOpen.value = true
}

function closeMenu() {
  isMenuOpen.value = false
}

watch(
    () => route.fullPath,
    () => {
      closeMenu()
    }
)
</script>

<template>
  <div class="app">
    <header class="top">
      <div class="header-container">
        <div class="left">
          <router-link class="brand" to="/">MyApp</router-link>

          <!-- PC 글로벌 메뉴 -->
          <nav class="global-nav desktop-only">
            <router-link to="/">홈</router-link>
            <router-link to="/account">가계부</router-link>
            <router-link to="/plants">식물관리</router-link>
          </nav>
        </div>

        <!-- PC 섹션 메뉴 -->
        <nav class="section-nav desktop-only">
          <template v-if="section === 'plant'">
            <router-link to="/plants">목록</router-link>
            <router-link to="/plants/add">추가</router-link>
          </template>

          <template v-else-if="section === 'account'">
            <router-link to="/account">가계부</router-link>
            <router-link to="/account/add">추가</router-link>
          </template>
        </nav>

        <!-- 모바일 햄버거 버튼 -->
        <button class="menu-button mobile-only" type="button" @click="openMenu">
          ☰
        </button>
      </div>
    </header>

    <!-- 모바일 사이드바 배경 -->
    <div
        v-if="isMenuOpen"
        class="overlay mobile-only"
        @click="closeMenu"
    ></div>

    <!-- 모바일 사이드바 -->
    <aside class="side-menu mobile-only" :class="{ open: isMenuOpen }">
      <div class="side-header">
        <router-link class="side-brand" to="/">MyApp</router-link>
        <button class="close-button" type="button" @click="closeMenu">
          ×
        </button>
      </div>

      <nav class="side-nav">
        <p class="side-title">메뉴</p>
        <router-link to="/">홈</router-link>
        <router-link to="/account">가계부</router-link>
        <router-link to="/plants">식물관리</router-link>
      </nav>

      <nav class="side-nav" v-if="section">
        <p class="side-title">
          {{ section === 'plant' ? '식물관리' : '가계부' }}
        </p>

        <template v-if="section === 'plant'">
          <router-link to="/plants">목록</router-link>
          <router-link to="/plants/add">추가</router-link>
        </template>

        <template v-else-if="section === 'account'">
          <router-link to="/account">가계부</router-link>
          <router-link to="/account/add">추가</router-link>
        </template>
      </nav>
    </aside>

    <main class="main">
      <div class="content-container">
        <router-view :key="$route.fullPath" />
      </div>
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background-color: #ffffff;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
}

.top {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
}

.header-container {
  width: 100%;
  max-width: 1024px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.brand {
  font-size: 1.3rem;
  font-weight: 800;
  color: #2c3e50;
  text-decoration: none;
}

.global-nav,
.section-nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.global-nav a,
.section-nav a {
  white-space: nowrap;
  text-decoration: none;
  color: #666;
  font-size: 0.95rem;
  padding: 7px 10px;
  border-radius: 8px;
  transition: all 0.2s;
}

.global-nav a.router-link-active,
.section-nav a.router-link-active,
.side-nav a.router-link-active {
  color: #27ae60;
  font-weight: 700;
  background-color: #f0fff4;
}

.menu-button {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 10px;
  background: #f5f7f8;
  color: #2c3e50;
  font-size: 1.4rem;
  cursor: pointer;
}

.overlay {
  position: fixed;
  inset: 0;
  z-index: 150;
  background: rgba(0, 0, 0, 0.35);
}

.side-menu {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 200;
  width: 280px;
  max-width: 82vw;
  height: 100vh;
  padding: 18px;
  background: #ffffff;
  box-shadow: -8px 0 24px rgba(0, 0, 0, 0.12);
  transform: translateX(100%);
  transition: transform 0.25s ease;
}

.side-menu.open {
  transform: translateX(0);
}

.side-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.side-brand {
  font-size: 1.25rem;
  font-weight: 800;
  color: #2c3e50;
  text-decoration: none;
}

.close-button {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  background: #f5f7f8;
  color: #333;
  font-size: 1.6rem;
  cursor: pointer;
}

.side-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 26px;
}

.side-title {
  margin: 0 0 6px;
  color: #999;
  font-size: 0.8rem;
  font-weight: 700;
}

.side-nav a {
  display: block;
  padding: 12px 10px;
  border-radius: 10px;
  color: #555;
  text-decoration: none;
  font-size: 1rem;
}

.main {
  padding: 20px 0;
}

.content-container {
  max-width: 1024px;
  margin: 0 auto;
  padding: 0 16px;
}

.mobile-only {
  display: none;
}

@media (max-width: 768px) {
  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: block;
  }

  .header-container {
    height: 56px;
  }

  .brand {
    font-size: 1.15rem;
  }

  .main {
    padding: 14px 0;
  }

  .content-container {
    padding: 0 12px;
  }
}
</style>
