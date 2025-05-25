<script setup lang="ts">
import { RouterView } from 'vue-router'
import Navigation from "@/components/Navigation.vue";
</script>

<template>
  <Navigation class="wide"/>
  <RouterView />
</template>

<style scoped>
.wide {
  width: 100%;
}
</style>
