import { useDisplay } from 'vuetify'

export function useBreakpoints() {
  const { xs, sm, md, lg, xl, smAndDown, mdAndDown, lgAndUp, mobile } = useDisplay()
  return { xs, sm, md, lg, xl, smAndDown, mdAndDown, lgAndUp, mobile }
}
