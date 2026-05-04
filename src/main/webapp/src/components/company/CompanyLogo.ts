import { defineComponent, ref, watch } from 'vue'
import defaultCompanyLogo from '@/assets/images/office-building.png'

export default defineComponent({
    name: 'CompanyLogo',
    props: {
        logo: { type: String, default: '' },
        website: { type: String, default: '' },
        alt: { type: String, default: 'Company Logo' },
        size: { type: [String, Number], default: 40 },
    },
    setup(props) {
        const getFaviconUrl = (website: string): string => {
            try {
                const normalized = website.startsWith('http') ? website : `https://${website}`
                const url = new URL(normalized)
                return `${url.origin}/favicon.ico`
            } catch {
                return ''
            }
        }

        const buildSources = (): string[] => {
            const sources: string[] = []
            if (props.logo) sources.push(props.logo)
            if (props.website) {
                const favicon = getFaviconUrl(props.website)
                if (favicon) sources.push(favicon)
            }
            sources.push(defaultCompanyLogo)
            return sources
        }

        const sources = ref<string[]>(buildSources())
        const currentIndex = ref(0)
        const currentSrc = ref(sources.value[0])

        watch(
            () => [props.logo, props.website],
            () => {
                sources.value = buildSources()
                currentIndex.value = 0
                currentSrc.value = sources.value[0]
            },
        )

        const onError = () => {
            currentIndex.value++
            if (currentIndex.value < sources.value.length) {
                currentSrc.value = sources.value[currentIndex.value]
            }
        }

        return { currentSrc, onError }
    },
})
