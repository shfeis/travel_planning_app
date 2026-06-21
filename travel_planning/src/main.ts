import './assets/main.css'
import 'vant/lib/index.css'
import { Button, Card, Tabbar, TabbarItem } from 'vant'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vant from 'vant'

import App from './App.vue'
import router from './router/index.js'

const app = createApp(App)
app.use(Button)
app.use(Card)
app.use(Tabbar)
app.use(TabbarItem)
app.use(createPinia())
app.use(Vant)
app.use(router)

app.mount('#app')
