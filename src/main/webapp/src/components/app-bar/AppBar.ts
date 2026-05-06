import { computed, defineComponent } from 'vue'
import { useTheme, useDisplay } from 'vuetify'
import NavigationLinks from './NavigationLinks.vue'
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'
import SearchBar from './SearchBar.vue'
import HelpMenu from './HelpMenu.vue'
import NotificationBell from './NotificationBell.vue'

export default defineComponent({
  name: 'AppBar',
  components: { NavigationLinks, ProfileMenu, SearchBar, HelpMenu, NotificationBell },
  setup() {
    // ── Theme ─────────────────────────────────────────────────────────────────
    const vuetifyTheme = useTheme()
    const isDark = computed(() => vuetifyTheme.global.current.value.dark)

    // ── Breakpoints ───────────────────────────────────────────────────────────
    const { sm, smAndDown } = useDisplay()

    function toggleTheme() {
      vuetifyTheme.global.name.value =
        vuetifyTheme.global.name.value === 'tablerLight' ? 'tablerDark' : 'tablerLight'
    }

    return { isDark, toggleTheme, sm, smAndDown }
  },
})
