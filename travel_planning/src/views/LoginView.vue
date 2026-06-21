<template>
  <main class="login-page">
    <section class="brand-panel">
      <div class="brand-mark" aria-hidden="true">
        <van-icon name="guide-o" />
      </div>
      <h1>智能旅游助手</h1>
      <p>登录后开启行程规划、AI 对话和个人服务</p>
    </section>

    <section class="auth-panel">
      <van-tabs v-model:active="activeTab" class="auth-tabs" shrink>
        <van-tab title="登录" />
        <van-tab title="注册" />
      </van-tabs>

      <van-form class="auth-form" @submit="submitAuth">
        <van-field
          v-model.trim="form.id"
          name="id"
          label="手机号"
          placeholder="请输入手机号"
          type="tel"
          maxlength="11"
          clearable
          :rules="phoneRules"
        >
          <template #left-icon>
            <van-icon name="phone-o" />
          </template>
        </van-field>

        <van-field
          v-if="isRegister"
          v-model.trim="form.name"
          name="name"
          label="用户名"
          placeholder="请输入用户名"
          maxlength="20"
          clearable
          :rules="nameRules"
        >
          <template #left-icon>
            <van-icon name="user-o" />
          </template>
        </van-field>

        <van-field
          v-model.trim="form.pwd"
          name="pwd"
          label="密码"
          placeholder="请输入密码"
          type="password"
          maxlength="50"
          clearable
          :rules="passwordRules"
        >
          <template #left-icon>
            <van-icon name="lock" />
          </template>
        </van-field>

        <van-field
          v-if="isRegister"
          v-model.trim="confirmPwd"
          name="confirmPwd"
          label="确认密码"
          placeholder="请再次输入密码"
          type="password"
          maxlength="50"
          clearable
          :rules="confirmPasswordRules"
        >
          <template #left-icon>
            <van-icon name="passed" />
          </template>
        </van-field>

        <van-button
          class="submit-button"
          type="primary"
          native-type="submit"
          block
          round
          :loading="isSubmitting"
        >
          {{ isRegister ? '注册并进入首页' : '登录并进入首页' }}
        </van-button>
      </van-form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { loginUser, registerUser, type UserAuthPayload } from '@/utils/request'
import { saveStoredUser } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const activeTab = ref(0)
const confirmPwd = ref('')
const isSubmitting = ref(false)
const form = reactive<UserAuthPayload>({
  id: '',
  name: '',
  pwd: '',
})

const isRegister = computed(() => activeTab.value === 1)

const phoneRules = [
  { required: true, message: '请输入手机号' },
  { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
]
const nameRules = [
  { required: true, message: '请输入用户名' },
  { validator: (value: string) => value.trim().length >= 2, message: '用户名至少2个字符' },
]
const passwordRules = [
  { required: true, message: '请输入密码' },
  { validator: (value: string) => value.length >= 6, message: '密码至少6位' },
]
const confirmPasswordRules = [
  { required: true, message: '请再次输入密码' },
  { validator: (value: string) => value === form.pwd, message: '两次输入的密码不一致' },
]

const submitAuth = async () => {
  if (isSubmitting.value) {
    return
  }

  isSubmitting.value = true

  try {
    const payload: UserAuthPayload = {
      id: form.id,
      name: form.name,
      pwd: form.pwd,
    }
    const user = isRegister.value ? await registerUser(payload) : await loginUser(payload)
    saveStoredUser(user)
    showToast(isRegister.value ? '注册成功' : '登录成功')
    await router.replace(getRedirectPath())
  } catch (error) {
    showToast(getErrorMessage(error))
  } finally {
    isSubmitting.value = false
  }
}

const getRedirectPath = () => {
  const redirect = Array.isArray(route.query.redirect)
    ? route.query.redirect[0]
    : route.query.redirect

  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/'
}

const getErrorMessage = (error: unknown) => {
  if (typeof error === 'object' && error !== null && 'response' in error) {
    const response = (error as { response?: { data?: { message?: string } } }).response

    if (response?.data?.message) {
      return response.data.message
    }
  }

  return isRegister.value ? '注册失败，请稍后重试' : '登录失败，请检查账号或密码'
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 52px 20px 28px;
  background:
    linear-gradient(160deg, rgba(63, 134, 247, 0.16), rgba(94, 190, 177, 0.12) 42%, transparent 42%),
    #f7f8fa;
}

.brand-panel {
  padding: 8px 4px 30px;
  color: #111827;
}

.brand-mark {
  width: 54px;
  height: 54px;
  display: grid;
  place-items: center;
  margin-bottom: 18px;
  color: #ffffff;
  background: linear-gradient(135deg, #3f86f7, #62beb1);
  border-radius: 8px;
  box-shadow: 0 14px 28px rgba(63, 134, 247, 0.24);
}

.brand-mark .van-icon {
  font-size: 28px;
}

.brand-panel h1 {
  margin: 0 0 8px;
  color: #111827;
  font-size: 28px;
  font-weight: 800;
  line-height: 1.25;
}

.brand-panel p {
  margin: 0;
  color: #5b6472;
  font-size: 14px;
  line-height: 1.6;
}

.auth-panel {
  overflow: hidden;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 12px 30px rgba(31, 41, 55, 0.08);
}

.auth-tabs {
  padding: 6px 18px 0;
}

:deep(.auth-tabs .van-tabs__wrap) {
  height: 48px;
}

:deep(.auth-tabs .van-tab) {
  flex: 0 0 auto;
  padding: 0 18px;
  color: #6b7280;
  font-size: 16px;
}

:deep(.auth-tabs .van-tab--active) {
  color: #111827;
  font-weight: 700;
}

:deep(.auth-tabs .van-tabs__line) {
  background: #3f86f7;
}

.auth-form {
  padding: 12px 18px 22px;
}

:deep(.auth-form .van-cell) {
  min-height: 56px;
  padding: 0;
  align-items: center;
}

:deep(.auth-form .van-field__left-icon) {
  margin-right: 8px;
  color: #3f86f7;
  font-size: 18px;
}

:deep(.auth-form .van-field__label) {
  width: 70px;
  color: #374151;
  font-size: 14px;
}

:deep(.auth-form .van-field__control) {
  color: #111827;
  font-size: 14px;
}

:deep(.auth-form .van-field__control::placeholder) {
  color: #b8bec8;
}

.submit-button {
  height: 46px;
  margin-top: 22px;
  border: 0;
  background: #3f86f7;
  font-size: 16px;
  font-weight: 600;
}

@media (max-width: 340px) {
  .login-page {
    padding-inline: 14px;
  }

  .brand-panel h1 {
    font-size: 25px;
  }

  :deep(.auth-form .van-field__label) {
    width: 64px;
  }
}
</style>
