<template>
  <div class="app-container" :class="{ 'app-container--full': hideTabbar }">
    <router-view />

    <van-tabbar
      v-if="!hideTabbar"
      route
      safe-area-inset-bottom
      active-color="#3f86f7"
      inactive-color="#1f2933"
    >
      <van-tabbar-item replace to="/" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item replace to="/chat" icon="chat-o">对话</van-tabbar-item>
      <van-tabbar-item replace to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const hideTabbar = computed(() => route.meta.hideTabbar === true)
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  padding-bottom: calc(50px + env(safe-area-inset-bottom));
  background: #f7f8fa;
}

.app-container--full {
  padding-bottom: 0;
}

:deep(.van-tabbar) {
  left: 50%;
  width: min(100vw, 430px);
  transform: translateX(-50%);
}
</style>
