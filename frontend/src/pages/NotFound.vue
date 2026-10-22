<template>
  <div class="notfound">
    <h1>404</h1>
    <p>페이지를 찾을 수 없습니다.</p>

    <router-link to="/" class="home-btn">
      홈으로 가기
    </router-link>
  </div>
</template>

<style scoped>
.notfound {
  text-align: center;
  padding: 80px 20px;
}

h1 {
  font-size: 72px;
  margin-bottom: 16px;
}

p {
  color: #666;
  margin-bottom: 24px;
}

.home-btn {
  display: inline-block;
  padding: 10px 16px;
  background: #27ae60;
  color: white;
  border-radius: 8px;
  text-decoration: none;
}
</style>
