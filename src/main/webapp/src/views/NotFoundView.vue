<template>
  <div class="error-page">
    <v-container class="text-center py-16">
      <v-row justify="center">
        <v-col cols="12" md="6">
          <div class="error-content">
            <div class="error-code">404</div>
            <h1 class="error-title mb-4">Page Not Found</h1>
            <p class="error-description mb-6">
              Sorry, the page you are looking for doesn't exist or has been moved.
            </p>
            <div class="error-actions">
              <v-btn
                color="primary"
                size="large"
                @click="goHome"
                prepend-icon="mdi-home"
                class="mr-4"
              >
                Go to Dashboard
              </v-btn>
              <v-btn variant="outlined" size="large" @click="goBack" prepend-icon="mdi-arrow-left">
                Go Back
              </v-btn>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.error-content {
  text-align: center;
}

.error-code {
  font-size: 8rem;
  font-weight: bold;
  line-height: 1;
  opacity: 0.8;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #fff, rgba(255, 255, 255, 0.7));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.error-title {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.error-description {
  font-size: 1.125rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.error-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

@media (max-width: 600px) {
  .error-code {
    font-size: 5rem;
  }

  .error-title {
    font-size: 2rem;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;
  }

  .error-actions .v-btn {
    margin: 0.5rem 0;
  }
}
</style>
