// 1. Vuetify base styles first
import 'vuetify/styles'
// 2. MDI icons
import '@mdi/font/css/materialdesignicons.css'
// 3. App typography + tabler-overrides (loaded after Vuetify so overrides win)
import './assets/main.scss'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import vuetify from './plugins/vuetify'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(vuetify)
app.use(createPinia())
app.use(router)

app.mount('#app')
