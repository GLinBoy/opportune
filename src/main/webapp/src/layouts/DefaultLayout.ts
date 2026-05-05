import { computed, defineComponent } from 'vue'
import { useTheme } from 'vuetify'
import AppBar from '@/components/app-bar/AppBar.vue'
import GoToTopButton from '@/components/GoToTopButton.vue'

export default defineComponent({
  name: 'DefaultLayout',
  components: {
    AppBar,
    GoToTopButton,
  },
  setup() {
    const vuetifyTheme = useTheme()
    const currentTheme = computed(() => vuetifyTheme.global.name.value)
    return { currentTheme }
  },
})
