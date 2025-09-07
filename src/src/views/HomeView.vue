<script setup lang="ts">
// @ts-nocheck
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
      <Fieldset legend="About Me">
          <p class="m-0">
            I’m a software engineer with over 7 years of experience,<br/>
            I specialize in high availability system design and deployments.<br/>
            Thanks for visiting!<br/>
          </p>
      </Fieldset>
      <div class="about-imgs center">
        <img class="about-img" src="@/assets/images/kubernetes_logo.svg.png">
        <img class="about-img" src="@/assets/images/Kotlin_Icon.png">
        <img class="about-img" src="@/assets/images/JavaScript-logo.png">
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
  flex-wrap: wrap; /* Allow wrapping for narrow screens */
  justify-content: center; /* Center align items */
  gap: 1rem; /* Add spacing between elements */
}

@media (max-width: 768px) {
  .about-flex {
    flex-direction: column; /* Stack items vertically on smaller screens */
  }
}

.about {
  flex-grow: 2;
}

.about-imgs {
  display: flex;
  justify-content: center;
  flex-wrap: wrap; /* Make images wrap on smaller screens */
  gap: 0.5rem; /* Add space between images */
}

.about-img {
  width: 64px;
  height: 64px;
}

@media (max-width: 768px) {
  .about-img {
    width: 48px; /* Reduce size on mobile */
    height: 48px;
  }
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

@media (max-width: 768px) {
  .greeting {
    font-size: 3rem; /* Reduced size for smaller devices */
  }

  .name {
    font-size: 4rem; /* Keep it large but readable on mobile */
  }
}


.gradient {
  background: linear-gradient(#1e293b, #181818);
  height: auto;
  min-height: 30rem;
  padding: 2rem; /* Add some inner spacing */
}

@media (max-width: 768px) {
  .gradient {
    min-height: 20rem; /* Reduce height for smaller devices */
    padding: 1rem;
  }
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

.p-2.center .p-button {
  font-size: 1.2rem; /* Adjust font size for smaller screens */
  padding: 0.5rem 1rem;
}

@media (max-width: 768px) {
  .p-2.center .p-button {
    font-size: 1rem; /* Reduce font size on smaller devices */
    margin: 10px 0; /* Add vertical spacing for stacking */
  }
}

</style>