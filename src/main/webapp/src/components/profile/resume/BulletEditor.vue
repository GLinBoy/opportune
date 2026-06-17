<template>
  <v-list class="bullet-editor bg-transparent pa-0" density="compact">
    <v-list-item
      v-for="(bullet, index) in localBullets"
      :key="index"
      class="bullet-item pa-0 mb-1"
    >
      <template #prepend>
        <span class="bullet-number text-caption text-medium-emphasis mr-1">{{ index + 1 }}.</span>
      </template>
      <v-textarea
        :model-value="bullet.content"
        variant="outlined"
        density="compact"
        hide-details
        auto-grow
        rows="2"
        placeholder="Describe an accomplishment or responsibility..."
        class="bullet-input"
        @update:model-value="updateBullet(index, $event as string)"
      />
      <template #append>
        <v-btn
          icon="mdi-close"
          variant="text"
          size="x-small"
          color="error"
          class="ml-1"
          @click="removeBullet(index)"
        />
      </template>
    </v-list-item>
    <v-btn
      variant="text"
      color="primary"
      size="small"
      prepend-icon="mdi-plus"
      class="mt-1"
      @click="addBullet"
    >
      Add bullet point
    </v-btn>
    <div v-if="dirty" class="d-flex ga-2 mt-2">
      <v-btn
        color="success"
        prepend-icon="mdi-content-save"
        size="small"
        @click="emitBullets"
      >
        Save
      </v-btn>
      <v-btn
        color="warning"
        prepend-icon="mdi-undo"
        size="small"
        @click="revertBullets"
      >
        Revert
      </v-btn>
    </div>
  </v-list>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { IWorkExperienceBullet } from '../../../models/resume-data.model'

const props = defineProps<{
  bullets: IWorkExperienceBullet[]
}>()

const emit = defineEmits<{
  'update:bullets': [value: IWorkExperienceBullet[]]
}>()

const localBullets = ref<IWorkExperienceBullet[]>(props.bullets.length
  ? [...props.bullets]
  : [{ content: '', displayOrder: 0 }]
)

const dirty = computed(() => {
  const current = localBullets.value
  const original = props.bullets
  const effectiveCurrent = current.filter(b => b.content)
  const effectiveOriginal = original.filter(b => b.content)
  if (effectiveCurrent.length !== effectiveOriginal.length) return true
  return effectiveCurrent.some((b, i) => b.content !== effectiveOriginal[i]?.content)
})

watch(
  () => props.bullets,
  (val) => {
    if (val.length === 0) {
      localBullets.value = [{ content: '', displayOrder: 0 }]
    } else {
      localBullets.value = val.map((b, i) => ({ ...b, displayOrder: i }))
    }
  },
  { deep: true }
)

function emitBullets() {
  emit('update:bullets', localBullets.value.map((b, i) => ({
    ...b,
    displayOrder: i,
  })))
}

function revertBullets() {
  if (props.bullets.length === 0) {
    localBullets.value = [{ content: '', displayOrder: 0 }]
  } else {
    localBullets.value = props.bullets.map((b, i) => ({ ...b, displayOrder: i }))
  }
}

function addBullet() {
  localBullets.value.push({ content: '', displayOrder: localBullets.value.length })
}

function removeBullet(index: number) {
  localBullets.value.splice(index, 1)
  if (localBullets.value.length === 0) {
    localBullets.value.push({ content: '', displayOrder: 0 })
  }
}

function updateBullet(index: number, content: string) {
  if (localBullets.value[index]) {
    localBullets.value[index].content = content
  }
}
</script>

<style scoped>
.bullet-editor :deep(.v-list-item__prepend) {
  align-self: flex-start;
  padding-top: 16px;
}
.bullet-number {
  min-width: 20px;
  line-height: 40px;
}
</style>
