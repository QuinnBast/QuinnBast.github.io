<script setup lang="ts">
import Menubar from "primevue/Menubar"
import { ref } from "vue";

const items = ref([
  {
    label: 'About',
    icon: 'pi pi-home icon-pad',
    route: "/about",
  },
  {
    label: 'Blog',
    icon: 'pi pi-star icon-pad',
    route: "/blog",
  }
]);

</script>

<template>
  <div class="padding card">
    <Menubar class="padding" :model="items">
      <template #start>
        <h1 class="m-5 padding pad-right"><router-link class="clear-router" to="/">Quinn Bast</router-link></h1>
      </template>
      <template class="menu-item" #item="{ item, props, hasSubmenu, root }">
        <router-link class="clear-router flex items-center" v-slot="{ href, navigate }" :to="item.route">
          <a v-ripple >
            <span><i :class="item.icon"/>{{ item.label }}</span>
          </a>
        </router-link>
      </template>
    </Menubar>
  </div>
</template>

<style scoped>
.padding {
  padding: 8px;
}

.menu-item {
  padding: 15px;
  padding-right: 20px;
}

.icon-pad {
  padding: 5px;
}

.pad-right {
  padding-right: 30px;
}

.clear-router {
  text-decoration: none;
  color: inherit;
}
</style>