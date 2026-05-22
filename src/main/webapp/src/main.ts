// 1. Vuetify base styles first
import 'vuetify/styles'
// 2. MDI icons
import '@mdi/font/css/materialdesignicons.css'
// 3. App typography + tabler-overrides
import './assets/main.scss'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import vuetify from './plugins/vuetify'
import App from './App.vue'
import router from './router'

// ECharts — register only used components to keep bundle lean
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import {
  PieChart,
  LineChart,
  BarChart,
  RadarChart,
  FunnelChart,
  HeatmapChart,
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  RadarComponent,
  CalendarComponent,
  VisualMapComponent,
} from 'echarts/components'

use([
  CanvasRenderer,
  PieChart, LineChart, BarChart, RadarChart, FunnelChart, HeatmapChart,
  TitleComponent, TooltipComponent, LegendComponent, GridComponent,
  RadarComponent, CalendarComponent, VisualMapComponent,
])

const app = createApp(App)

app.use(vuetify)
app.use(createPinia())
app.use(router)
app.component('VChart', VChart)  // global registration

app.mount('#app')
