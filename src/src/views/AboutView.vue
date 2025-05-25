<script setup lang="ts">
import Timeline from 'primevue/timeline';
import Card from 'primevue/card';
import Fieldset from "primevue/fieldset";
import Carousel from 'primevue/carousel';
import Panel from 'primevue/panel';
import Button from 'primevue/button';

import { ref } from "vue";

const events = ref([
  {
    company: 'Calian',
    title: "Cloud Native Devops Engineer",
    date: 'July 2020 - Present',
    companyImage: "https://hub.calian.com/hs-fs/hubfs/Calian%20logo_Knockout_NoTag.png?width=400&height=200&name=Calian%20logo_Knockout_NoTag.png",
    oppositeImage: "https://upload.wikimedia.org/wikipedia/commons/3/39/Kubernetes_logo_without_workmark.svg",
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
    companyImage: "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/SaskTel_logo.svg/2560px-SaskTel_logo.svg.png",
    oppositeImage: "https://www.teranet.ca/wp-content/uploads/2025/03/63670e66c4e75fdb3d0415cf_ESRI_logo_logotype.png",
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
    companyImage: "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/SaskTel_logo.svg/2560px-SaskTel_logo.svg.png",
    oppositeImage: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Python-logo-notext.svg/115px-Python-logo-notext.svg.png",
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
    companyImage: "https://companieslogo.com/img/orig/EVZ.F_BIG.D-4f4d8d98.png?t=1720244491",
    oppositeImage: "https://www.svgrepo.com/show/303229/microsoft-sql-server-logo.svg",
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
    image: "https://upload.wikimedia.org/wikipedia/commons/3/39/Kubernetes_logo_without_workmark.svg"
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
    description: "Need your data highly available? HaHelper.",
    url: "https://github.com/QuinnBast/HaHelper",
    image: ""
  },
  {
    title: "Personal Website",
    date: "November 2023 - Present",
    description: "My personal website and blog hosted through github pages.",
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
          <Card class="mt-4">
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
            <Card>
              <template #header>
                <img style="max-width: 200px; max-height: 150px;" :src="slotProps.data.image" :alt="slotProps.data.title" class="w-full rounded" />
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
  top: 125px;
  position: relative;
}

.fixWidth {
  width: 100%;
}

.mt-4 {
  margin: 30px;
}
</style>
