import { ref, computed, defineComponent } from 'vue'
import { useTheme, useDisplay } from 'vuetify'
import NavigationLinks from './NavigationLinks.vue'
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'
import SearchBar from './SearchBar.vue'

interface Notification {
  id: number
  title: string
  body: string
  time: string
  icon: string
  color: string
  read: boolean
}

export default defineComponent({
  name: 'AppBar',
  components: { NavigationLinks, ProfileMenu, SearchBar },
  setup() {
    // ── Theme ─────────────────────────────────────────────────────────────────
    const vuetifyTheme = useTheme()
    const isDark = computed(() => vuetifyTheme.global.current.value.dark)

    // ── Breakpoints ───────────────────────────────────────────────────────────
    const { smOnly, smAndDown } = useDisplay()

    function toggleTheme() {
      vuetifyTheme.global.name.value =
        vuetifyTheme.global.name.value === 'tablerLight' ? 'tablerDark' : 'tablerLight'
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    const notifOpen = ref(false)

    const notifications = ref<Notification[]>([
      {
        id: 1,
        title: 'Application status updated',
        body: 'Your application at Acme Corp moved to Interview.',
        time: '2 min ago',
        icon: 'mdi-briefcase-check-outline',
        color: 'success',
        read: false,
      },
      {
        id: 2,
        title: 'New company added',
        body: 'Globex Inc. was added to your watchlist.',
        time: '1 hr ago',
        icon: 'mdi-domain',
        color: 'info',
        read: false,
      },
      {
        id: 3,
        title: 'Profile reminder',
        body: 'Complete your profile to improve matches.',
        time: 'Yesterday',
        icon: 'mdi-account-alert-outline',
        color: 'warning',
        read: true,
      },
    ])

    const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length)

    function markRead(id: number) {
      const n = notifications.value.find((n) => n.id === id)
      if (n) n.read = true
    }

    function markAllRead() {
      notifications.value.forEach((n) => (n.read = true))
    }

    return { isDark, toggleTheme, notifOpen, notifications, unreadCount, markRead, markAllRead, smOnly, smAndDown }
  },
})
