<template>
  <Teleport to="body">
    <Transition name="fade">
      <v-btn
        v-show="showButton"
        icon="mdi-chevron-up"
        color="primary"
        @click="scrollToTop"
        class="go-to-top-btn"
        :style="{ opacity: scrollOpacity }"
        elevation="4"
      />
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const showButton = ref(false)
const scrollOpacity = ref(0)

const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const scrollHeight = document.documentElement.scrollHeight
  const clientHeight = document.documentElement.clientHeight

  // Show button after scrolling 200px
  showButton.value = scrollTop > 200

  // Calculate opacity based on scroll position (0.7 to 1.0)
  const maxScroll = scrollHeight - clientHeight
  const scrollPercentage = Math.min(scrollTop / maxScroll, 1)
  scrollOpacity.value = 0.7 + scrollPercentage * 0.3
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.go-to-top-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  transition: opacity 0.3s ease-in-out;
}

.go-to-top-btn:hover {
  opacity: 1 !important;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
