<template>
  <main class="chat-page">
    <van-nav-bar
      title="AI 旅游助手"
      left-text="返回"
      left-arrow
      fixed
      placeholder
      @click-left="router.back()"
    />

    <section
      ref="chatBodyRef"
      class="chat-body"
      :class="{ 'chat-body--empty': messages.length === 0 }"
    >
      <div v-if="messages.length === 0" class="welcome-state">
        <div class="welcome-icon" aria-hidden="true">
          <van-icon name="description-o" />
        </div>
        <p class="welcome-copy">开始和 AI 助手对话吧!</p>

        <div class="question-panel">
          <p>常见问题</p>
          <div class="question-list">
            <button
              v-for="item in quickQuestions"
              :key="item"
              type="button"
              :disabled="isSending"
              @click="sendQuestion(item)"
            >
              {{ item }}
            </button>
          </div>
        </div>
      </div>

      <div v-else class="message-list">
        <article
          v-for="message in messages"
          :key="message.id"
          class="message-row"
          :class="`message-row--${message.role}`"
        >
          <div class="message-bubble">
            <van-loading
              v-if="message.role === 'assistant' && isSending && !message.content"
              color="#3f86f7"
              size="18px"
            />
            <span v-else>{{ message.content }}</span>
          </div>
        </article>
      </div>
    </section>

    <form class="composer" @submit.prevent="sendQuestion()">
      <van-field
        v-model="question"
        class="composer-field"
        type="text"
        clearable
        :border="false"
        :disabled="isSending"
        placeholder="输入您的问题..."
      />
      <van-button
        class="send-button"
        native-type="submit"
        type="primary"
        size="small"
        :loading="isSending"
        :disabled="!question.trim() || isSending"
      >
        发送
      </van-button>
    </form>
  </main>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { aiChat } from '@/utils/request'

type MessageRole = 'user' | 'assistant'

interface ChatMessage {
  id: number
  role: MessageRole
  content: string
}

const router = useRouter()
const question = ref('')
const isSending = ref(false)
const messages = ref<ChatMessage[]>([])
const chatBodyRef = ref<HTMLElement | null>(null)

let messageId = 0

const quickQuestions = ['北京有哪些必去的景点?', '上海美食推荐', '成都三日游攻略', '如何选择旅行保险?']

const sendQuestion = async (presetQuestion?: string) => {
  const content = (presetQuestion ?? question.value).trim()

  if (!content) {
    showToast('请输入您的问题')
    return
  }

  if (isSending.value) {
    return
  }

  const userMessage: ChatMessage = {
    id: ++messageId,
    role: 'user',
    content,
  }
  const assistantMessage: ChatMessage = {
    id: ++messageId,
    role: 'assistant',
    content: '',
  }

  messages.value.push(userMessage, assistantMessage)
  question.value = ''
  isSending.value = true
  await scrollToBottom()

  try {
    const response = await aiChat(content)

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    if (!response.body) {
      assistantMessage.content = await response.text()
      return
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    const streamParser = createStreamParser(response.headers.get('content-type') ?? '')

    while (true) {
      const { done, value } = await reader.read()

      if (done) {
        break
      }

      const text = decoder.decode(value, { stream: true })
      assistantMessage.content += streamParser.read(text)
      await scrollToBottom()
    }

    assistantMessage.content += streamParser.read(decoder.decode(), true)
    assistantMessage.content = assistantMessage.content.trim()

    if (!assistantMessage.content) {
      assistantMessage.content = '暂未收到回复，请稍后再试。'
    }
  } catch (error) {
    console.error('AI 聊天接口调用失败:', error)
    assistantMessage.content = '接口调用失败，请检查后端服务是否已启动。'
  } finally {
    isSending.value = false
    await scrollToBottom()
  }
}

const scrollToBottom = async () => {
  await nextTick()
  const chatBody = chatBodyRef.value

  if (!chatBody) {
    return
  }

  chatBody.scrollTo({
    top: chatBody.scrollHeight,
    behavior: 'smooth',
  })
}

const createStreamParser = (contentType: string) => {
  const isSse = contentType.includes('text/event-stream')
  let pending = ''

  return {
    read(rawText: string, flush = false) {
      if (!isSse) {
        return rawText
      }

      pending += rawText
      const lines = pending.split(/\r?\n/)

      if (flush) {
        pending = ''
      } else {
        pending = lines.pop() ?? ''
      }

      return lines
        .filter((line) => line.startsWith('data:'))
        .map((line) => line.slice(5).replace(/^ /, ''))
        .filter((line) => line !== '[DONE]')
        .join('')
    },
  }
}
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: #f7f7f9;
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

:deep(.van-nav-bar__left) {
  padding-left: 16px;
}

:deep(.van-nav-bar__text),
:deep(.van-nav-bar .van-icon) {
  color: #5d86c9;
}

.chat-body {
  height: calc(100vh - 46px - 58px - 50px - env(safe-area-inset-bottom));
  overflow-y: auto;
  padding: 16px 14px 18px;
  background: #f7f7f9;
}

.chat-body--empty {
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-state {
  width: 100%;
  max-width: 320px;
  margin-top: -24px;
  text-align: center;
}

.welcome-icon {
  position: relative;
  width: 92px;
  height: 92px;
  display: grid;
  place-items: center;
  margin: 0 auto 16px;
  color: #e0e4eb;
  font-size: 48px;
}

.welcome-icon::before,
.welcome-icon::after {
  position: absolute;
  content: '';
  background: #eef1f5;
}

.welcome-icon::before {
  right: 13px;
  bottom: 18px;
  left: 13px;
  height: 26px;
}

.welcome-icon::after {
  right: 25px;
  bottom: 25px;
  left: 25px;
  height: 6px;
  background: #ffffff;
  opacity: 0.86;
}

.welcome-icon .van-icon {
  position: relative;
  z-index: 1;
}

.welcome-copy {
  margin: 0 0 76px;
  color: #9aa0a8;
  font-size: 14px;
  line-height: 1.5;
}

.question-panel p {
  margin: 0 0 18px;
  color: #9aa0a8;
  font-size: 14px;
  line-height: 1.4;
}

.question-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px 14px;
}

.question-list button {
  min-height: 28px;
  max-width: 156px;
  padding: 4px 10px;
  color: #ffffff;
  background: #9b9ca0;
  border: 0;
  border-radius: 8px;
  font: inherit;
  font-size: 14px;
  line-height: 1.3;
}

.question-list button:disabled {
  opacity: 0.65;
}

.message-list {
  display: grid;
  gap: 12px;
}

.message-row {
  display: flex;
}

.message-row--user {
  justify-content: flex-end;
}

.message-row--assistant {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 78%;
  min-height: 36px;
  display: flex;
  align-items: center;
  padding: 9px 12px;
  color: #2f343b;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
  font-size: 14px;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-row--user .message-bubble {
  color: #ffffff;
  background: #2563eb;
  box-shadow: none;
}

.composer {
  position: fixed;
  right: max(0px, calc((100vw - 430px) / 2));
  bottom: calc(50px + env(safe-area-inset-bottom));
  left: max(0px, calc((100vw - 430px) / 2));
  z-index: 20;
  height: 58px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 26px;
  background: rgba(255, 255, 255, 0.96);
  border-top: 1px solid rgba(239, 241, 245, 0.9);
}

.composer-field {
  min-width: 0;
  flex: 1;
  height: 40px;
  padding: 0 12px;
  background: #f9fafc;
  border-radius: 8px;
}

:deep(.composer-field .van-cell__value),
:deep(.composer-field .van-field__body) {
  height: 100%;
}

:deep(.composer-field .van-field__control) {
  height: 100%;
  color: #111827;
  font-size: 14px;
}

:deep(.composer-field .van-field__control::placeholder) {
  color: #c7cbd2;
}

.send-button {
  flex: 0 0 auto;
  width: 48px;
  height: 34px;
  padding: 0;
  background: #2563eb;
  border: 0;
  border-radius: 6px;
  font-size: 14px;
}

.send-button:disabled {
  background: #92bdff;
  opacity: 1;
}

@media (max-width: 360px) {
  .composer {
    padding-inline: 18px;
  }

  .question-list button {
    max-width: 140px;
    font-size: 13px;  
  }
}
</style>
