import { ref } from 'vue'
import { defineStore } from 'pinia'

type ToastColor = 'success' | 'error' | 'warning' | 'info'

const DEFAULT_TIMEOUT = 3000

export const useToastStore = defineStore('toast', () => {
  const show = ref(false)
  const message = ref('')
  const color = ref<ToastColor>('info')
  const timeout = ref(DEFAULT_TIMEOUT)

  function notify(msg: string, c: ToastColor, ms = DEFAULT_TIMEOUT) {
    message.value = msg
    color.value = c
    timeout.value = ms
    show.value = true
  }

  function success(msg: string, ms = DEFAULT_TIMEOUT) {
    notify(msg, 'success', ms)
  }

  function error(msg: string, ms = DEFAULT_TIMEOUT) {
    notify(msg, 'error', ms)
  }

  function warning(msg: string, ms = DEFAULT_TIMEOUT) {
    notify(msg, 'warning', ms)
  }

  function info(msg: string, ms = DEFAULT_TIMEOUT) {
    notify(msg, 'info', ms)
  }

  function dismiss() {
    show.value = false
  }

  return { show, message, color, timeout, success, error, warning, info, dismiss }
})
