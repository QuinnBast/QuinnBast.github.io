<script setup lang="ts">
import Timeline from 'primevue/timeline';
import Card from 'primevue/card';
import Fieldset from "primevue/fieldset";
import Carousel from 'primevue/carousel';
import Panel from 'primevue/panel';
import Button from 'primevue/button';
import CalianLogo from '@/assets/images/Calian.png';
import KubernetesLogo from '@/assets/images/Kubernetes_logo.svg.png';
import EsriLogo from '@/assets/images/EsriLogo.png';
import EvrazLogo from '@/assets/images/Evraz.png';
import SasktelLogo from '@/assets/images/SaskTel_logo.svg.png';
import PythonLogo from '@/assets/images/Python-logo-notext.svg.png';
import SqlServerLogo from '@/assets/images/microsoft-sql-server-logo.svg';

import { ref } from "vue";

const events = ref([
  {
    company: 'Calian',
    title: "Cloud Native Devops Engineer",
    date: 'July 2020 - Present',
    companyImage: CalianLogo,
    oppositeImage: KubernetesLogo,
    details: [
        "Creating highly available software solutions",
        "Kubernetes Administrator",
        "Fullstack Developer using Kotlin and React"
    ]
  },
  {
    company: 'Sasktel',
    title: "Engineering Manager",
    date: 'August 2018 - May 2019',
    companyImage: SasktelLogo,
    oppositeImage: EsriLogo,
    details: [
        "Vue.js Frontend GIS Developer",
        "Utilized Esri APIs in Javascript and Python",
        "Oracle Geospatial database, FME, and ArcGIS Pro"
    ]
  },
  {
    company: 'Sasktel',
    title: "Engineering Assistant",
    date: 'May - August 2018',
    companyImage: SasktelLogo,
    oppositeImage: PythonLogo,
    details: [
      "Python GIS scripting",
      "Esri product experience",
      "Microsoft Access databas migrations"
    ]
  },
  {
    company: 'Evraz North America',
    title: "Programmer Analyst",
    date: 'September - December 2017',
    companyImage: EvrazLogo,
    oppositeImage: SqlServerLogo,
    details: [
      "Optimizing SQL queries",
      "C# Windows Forms",
      "SSMS Reporting"
    ]
  },
]);

const personalProjects = ref([
  {
    title: "Pair'd",
    date: "March 2023 - Present",
    description: "A 2D puzzle game about making pairs! Steam release coming soon! Built with Gamemaker.",
    url: "",
    image: "https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/gamemaker/gamemaker.png"
  },
  {
    title: "Kube Common",
    date: "March, 2025",
    description: "A Kubernetes cluster management software that allows you to create and manage deployments as well as make use of re-usable templates. Built with Electron.",
    url: "https://github.com/QuinnBast/KubeCommon",
    image: "@/assets/images/Kubernetes_logo.svc.png"
  },
  {
    title: "Personal Finance Analyzer",
    date: "2022 - 2024",
    description: "A website and local database where you can import all of your banking transactions and view various stats about how terribly you spend money.",
    url: "https://github.com/QuinnBast/Personal-Finance-Analyzer",
    image: "https://github.com/QuinnBast/Personal-Finance-Analyzer/raw/master/images/AccountOverview.png"
  },
  {
    title: "HaHelper",
    date: "March 2023 - Present",
    description: "Need to make your software highly available? HaHelper lets you cluster your own software using RAFT consensus algorithm.",
    url: "https://github.com/QuinnBast/HaHelper",
    image: ""
  },
  {
    title: "Personal Website",
    date: "November 2023 - Present",
    description: "This website and blog! Hosted through github pages.",
    url: "https://quinnbast.github.io/",
    image: "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Vue.js_Logo_2.svg/512px-Vue.js_Logo_2.svg.png"
  },
  {
    title: "Subterfuge Community Edition",
    date: "August 2019 - November 2023",
    description: "A remaster of a popular mobile game called Subterfuge. Built with C#, ASP.NET, and Unity.",
    url: "https://github.com/Subterfuge-Revived",
    image: "https://is1-ssl.mzstatic.com/image/thumb/Purple111/v4/17/22/1e/17221e39-3d64-6a07-fd45-7d2f1d573147/mzl.noavmpso.png/230x0w.webp"
  },
  {
    title: "Sasktel Communications Portal",
    date: "April 2019",
    description: "A website that integrates with Sasktel RTC servers to allow making phone calls over the web! Built with React and Python.",
    url: "https://github.com/QuinnBast/SaskTel-Communication-Portal",
    image: "https://upload.wikimedia.org/wikipedia/commons/e/e1/SaskTel_logo.svg"
  },
  {
    title: "CueB Game Engine",
    date: "August 2018",
    description: "A custom Java-based game engine for creating 2D games inspired by Gamemaker.",
    url: "https://github.com/QuinnBast/CueB-Game-Engine",
    image: ""
  },
  {
    title: "Gamemaker Game Archive",
    date: "2010-2013",
    description: "A collection of games I made in high-school using Game Maker. This was my first experinces with coding and I fell in love with it since.",
    url: "https://github.com/QuinnBast/GameMaker-Games-2010-2013",
    image: "https://github.com/QuinnBast/GameMaker-Games-2012/raw/master/Screenshots/TowerDefence.PNG"
  },
]);

const responsiveOptions = ref([
  {
    breakpoint: '1400px',
    numVisible: 3,
    numScroll: 1
  },
  {
    breakpoint: '1199px',
    numVisible: 3,
    numScroll: 1
  },
  {
    breakpoint: '767px',
    numVisible: 2,
    numScroll: 1
  },
  {
    breakpoint: '575px',
    numVisible: 1,
    numScroll: 1
  }
]);


</script>

<template>
  <div class="card">

    <Fieldset class="about" legend="Professional Experience">
      <Timeline :value="events" align="alternate" class="customized-timeline">
        <template #content="slotProps">
          <Card class="mt-4 timeline-event">
            <template #title>
              <img v-if="slotProps.item.companyImage" :src="slotProps.item.companyImage" :alt="slotProps.item.company" width="200" class="shadow-sm" />
            </template>
            <template #subtitle>
              {{ slotProps.item.date }}
            </template>
            <template #content>
              <ul>
                <li v-for="detail in slotProps.item.details">{{ detail }}</li>
              </ul>
            </template>
          </Card>
        </template>
        <template #opposite="slotProps">
            <img v-if="slotProps.item.oppositeImage" :src="slotProps.item.oppositeImage" :alt="slotProps.item.company" height="100" class="opposite-img shadow-sm" />
        </template>
      </Timeline>
    </Fieldset>

    <Panel header="Personal Projects" class="mt-4">
        <Carousel class="fixWidth" :value="personalProjects" :numVisible="4" :numScroll="2" :responsiveOptions="responsiveOptions" circular :autoplayInterval="8000">
          <template #item="slotProps">
            <Card class="project-card">
              <template #header>
                <img style="max-width: 200px; max-height: 150px;" :src="slotProps.data.image" :alt="slotProps.data.title" class="w-full rounded img-fluid" />
              </template>
              <template #title>{{ slotProps.data.title }}</template>
              <template #subtitle>{{ slotProps.data.date }}</template>
              <template #content>{{ slotProps.data.description }}</template>
              <template #footer v-if="slotProps.data.url !== ''">
                <Button as="a" class="block-button" :href="slotProps.data.url" target="_blank"><i class="pi pi-heart"/>See More!</Button>
              </template>
            </Card>
          </template>
        </Carousel>
    </Panel>
  </div>
</template>

<style>
.opposite-img {
  padding: 0 0;
  position: relative;
}

.fixWidth {
  width: 100%;
}

.mt-4 {
  margin: 30px;
}

.p-timeline-event {
  display: flex;
  flex-wrap: wrap; /* Allow wrapping for timeline items */
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.p-timeline-event-opposite img {
  max-width: 150px;
  height: auto;
  display: block;
  object-fit: contain;
}


@media (max-width: 768px) {
  .p-timeline-event-opposite img {
    display: none;
  }

  .p-timeline-event {
    flex-direction: column; /* Stack items vertically */
    text-align: center; /* Center align for better readability */
  }

  .p-timeline-event .event-details {
    margin-top: 1rem; /* Add spacing between items vertically */
  }
}

.project-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 3 cards per row on larger screens */
  gap: 1.5rem; /* Space between cards */
}

.project-card img {
  max-width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 5px;
}

@media (max-width: 1024px) {
  .project-cards {
    grid-template-columns: repeat(2, 1fr); /* 2 cards per row for medium screens */
  }
}

@media (max-width: 768px) {
  .project-cards {
    grid-template-columns: 1fr; /* 1 card per row on mobile */
  }
}

img {
  max-width: 100%;
  height: auto;
  display: block; /* Avoid inline padding issues */
  margin: 0 auto; /* Center images */
}

.image-container {
  text-align: center; /* Center align image within its container */
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .image-container img {
    width: 80%; /* Shrink images slightly on mobile */
  }
}

fieldset,
.p-panel {
  margin: 1rem auto;
  padding: 1rem;
  border-radius: 5px; /* Add slight rounding for better design */
}

@media (max-width: 768px) {
  fieldset,
  .p-panel {
    margin: 0.5rem auto;
    padding: 0.5rem;
  }
}
</style>
