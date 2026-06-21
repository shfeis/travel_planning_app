<template>
  <main class="profile-page">
    <van-nav-bar title="我的" left-arrow fixed placeholder @click-left="router.back()" />

    <section class="profile-hero">
      <div class="avatar">
        <van-image :src="avatarUrl" fit="cover" width="100%" height="100%" />
      </div>
      <div class="profile-copy">
        <h1>{{ userName }}</h1>
        <p>欢迎使用智能旅游助手</p>
      </div>
    </section>

    <section class="section-block">
      <h2>我的服务</h2>
      <van-cell title="我的收藏" icon="star-o" is-link @click="showPendingToast" />
      <van-cell title="历史记录" is-link @click="showPendingToast" />
      <van-cell title="设置" is-link @click="showPendingToast" />
    </section>

    <section class="section-block about-block">
      <h2>关于</h2>
      <van-cell title="关于我们" is-link @click="showAbout" />
      <van-cell title="版本信息" value="v1.0.0" />
    </section>

    <van-button class="logout-button" plain block @click="logout">退出登录</van-button>
  </main>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { clearStoredUser, getAvatarUrl, getStoredUser } from '@/utils/auth'

const router = useRouter()
const currentUser = ref(getStoredUser())

const userName = computed(() => currentUser.value?.name || '游客')
const avatarUrl = computed(() => getAvatarUrl(currentUser.value?.logo))

const showPendingToast = () => {
  showToast('功能开发中')
}

const showAbout = () => {
  void showDialog({
    title: '关于我们',
    message: '作者：叶翔，项目源码：https://github.com/shfeis/travel_planning_app.git',
    confirmButtonText: '知道了',
  })
}

const logout = async () => {
  clearStoredUser()
  showToast('已退出登录')
  await router.replace('/login')
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding-bottom: 22px;
  background: #ffffff;
}

:deep(.van-nav-bar) {
  background: #ffffff;
}

:deep(.van-nav-bar--fixed) {
  left: 50%;
  width: min(100vw, 430px);
  transform: translateX(-50%);
}

:deep(.van-nav-bar__title) {
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

:deep(.van-nav-bar .van-icon) {
  color: #5d86c9;
}

.profile-hero {
  min-height: 140px;
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 28px 20px;
  background: linear-gradient(135deg, #5a8ff0 0%, #78c9bf 100%);
}

.avatar {
  width: 78px;
  height: 78px;
  flex: 0 0 auto;
  display: grid;
  place-items: center;
  overflow: hidden;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.26);
  border: 2px solid rgba(255, 255, 255, 0.42);
  border-radius: 50%;
  box-shadow: 0 10px 24px rgba(31, 41, 55, 0.18);
  font-size: 30px;
  font-weight: 700;
}

.profile-copy {
  min-width: 0;
  color: #ffffff;
}

.profile-copy h1 {
  margin: 0 0 10px;
  font-size: 22px;
  font-weight: 800;
  line-height: 1.25;
  word-break: break-word;
}

.profile-copy p {
  margin: 0;
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
  line-height: 1.4;
}

.section-block {
  padding: 28px 10px 0;
  background: #ffffff;
}

.section-block h2 {
  margin: 0 12px 10px;
  color: #5b6472;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
}

.about-block {
  padding-top: 34px;
}

:deep(.section-block .van-cell) {
  min-height: 52px;
  padding: 0 14px;
  align-items: center;
  color: #2f3338;
  font-size: 15px;
}

:deep(.section-block .van-cell::after) {
  left: 14px;
  right: 14px;
  border-bottom-color: #eef0f4;
}

:deep(.section-block .van-cell__left-icon) {
  margin-right: 5px;
  color: #5b6472;
  font-size: 17px;
}

:deep(.section-block .van-cell__right-icon) {
  color: #8d96a4;
  font-size: 18px;
}

:deep(.section-block .van-cell__value) {
  color: #9aa0a8;
}

.logout-button {
  width: calc(100% - 44px);
  height: 42px;
  margin: 34px auto 0;
  color: #d84a3a;
  border-color: #f0c7c0;
  font-size: 15px;
}

@media (max-width: 340px) {
  .profile-hero {
    gap: 16px;
    padding-inline: 16px;
  }

  .avatar {
    width: 68px;
    height: 68px;
  }
}
</style>
