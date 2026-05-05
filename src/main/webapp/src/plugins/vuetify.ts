import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import type { ThemeDefinition } from 'vuetify'

const tablerLight: ThemeDefinition = {
  dark: false,
  colors: {
    primary: '#0054a6',
    secondary: '#616876',
    success: '#2fb344',
    warning: '#f76707',
    error: '#d63939',
    info: '#4299e1',
    background: '#f4f6fb',
    surface: '#ffffff',
    'surface-variant': '#f1f5f9',
    'on-background': '#1d273b',
    'on-surface': '#1d273b',
  },
  variables: {
    'border-radius': '4px',
    'font-size-root': '15px',
    'shadow-key-umbra-opacity': '0.06',
    'shadow-key-penumbra-opacity': '0.04',
  },
}

const tablerDark: ThemeDefinition = {
  dark: true,
  colors: {
    primary: '#4299e1',
    secondary: '#858d9b',
    success: '#47c272',
    warning: '#fd9c4e',
    error: '#e35d6a',
    info: '#79b8f3',
    background: '#1a1f2e',
    surface: '#222736',
    'surface-variant': '#2a2f3e',
    'on-background': '#c8d3e1',
    'on-surface': '#c8d3e1',
  },
  variables: {
    'border-radius': '4px',
    'font-size-root': '15px',
    'shadow-key-umbra-opacity': '0.06',
    'shadow-key-penumbra-opacity': '0.04',
  },
}

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'tablerLight',
    themes: {
      tablerLight,
      tablerDark,
    },
  },
})
