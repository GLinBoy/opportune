import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import NavigationLinks from './NavigationLinks.vue'
import ProfileMenu from '@/components/profile-menu/ProfileMenu.vue'
import SearchBar from './SearchBar.vue'

export default defineComponent({
  components: { NavigationLinks, ProfileMenu, SearchBar },
  setup() {
    const router = useRouter()

    function goToDashboard() {
      router.push('/dashboard')
    }

    return { goToDashboard }
  },
})
