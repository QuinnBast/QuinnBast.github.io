<script setup lang="ts">

import { ref } from "vue";
import Button from 'primevue/button';
import Fieldset from 'primevue/fieldset';

const greeting = ref("Hello!")

function randomizeGreeting() {
  var greetingOptions = [
    "Hello!", "Welcome.", "Bienvenu,", "Howdy!", "Hi there!", "👋", "Hey,", "✌️", "🤙"
  ];
  var optionsToSelect = greetingOptions.filter((option) => option !== greeting.value);
  greeting.value = optionsToSelect[Math.floor(Math.random()*optionsToSelect.length)];
}

setInterval(randomizeGreeting, 4000);
</script>

<template>
  <main class="big-text">
    <div class="gradient center">
      <Transition appear name="fade" mode="out-in">
        <h3 class="greeting" :key="greeting">{{ greeting }}</h3>
      </Transition><br/>
      <h1 class="name">I'm Quinn</h1>
    </div>
    <div class="about-flex">
      <Fieldset class="about" legend="About Me">
          <p class="m-0">
            I’m a software engineer with over 7 years of experience,<br/>
            I specialize in high availability system design and deployments.<br/>
            Thanks for visiting!<br/>
          </p>
      </Fieldset>
      <div class="about-imgs center">
        <img class="about-img" src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Kubernetes_logo_without_workmark.svg/1055px-Kubernetes_logo_without_workmark.svg.png">
        <img class="about-img" src="https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Kotlin_Icon.svg/1200px-Kotlin_Icon.svg.png">
        <img class="about-img" src="https://static-00.iconduck.com/assets.00/node-js-icon-1817x2048-g8tzf91e.png">
      </div>
    </div>
    <div class="p-2 center">
      <Button class="m-4" asChild v-slot="slotProps">
        <RouterLink to="/about" :class="slotProps.class + ' m-4'"><h1><i class="pi pi-info-circle"/>&nbsp;About Me</h1></RouterLink>
      </Button>
      <Button class="m-4" asChild v-slot="slotProps">
        <RouterLink to="/blog" :class="slotProps.class + ' m-4'"><h1><i class="pi pi-book"/>&nbsp;Read my Blog</h1></RouterLink>
      </Button>
    </div>
  </main>
</template>

<style>
.about-flex {
  display: flex;
}

.about {
  flex-grow: 2;
}

.about-imgs {
  height: 14px;
  flex-grow: 1;
}

.about-img {
  width: 64px;
  height: 64px;
  margin: 10px;
}

.m-4 {
  margin: 15px;
}

.big-text {
  font-size: 24px;
}

.greeting {
  font-size: 5rem;
}

.name {
  font-size: 7rem;
}

.gradient {
  background: linear-gradient(#1e293b, #181818);
  height: 30rem;
}

.theme-default-content:has(.full) {
  max-width: 100%;
}

.center {
  text-align: center;
  padding: 6rem 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>