<script setup lang="ts">
// @ts-nocheck
import {type Ref, ref, computed, watch} from "vue";
import { default as matter } from 'gray-matter'
import type {GrayMatterFile} from "gray-matter";
import Button from "primevue/button";
import Card from "primevue/card";
import Skeleton from 'primevue/skeleton';
import router from "@/router";
import { Markdown, VNodeRenderer } from 'vue-markdown-next';
import remarkGfm from 'remark-gfm';
import {API, LessonMeta, TrainingModuleMeta} from '@/api/api.ts';
import DataView from "primevue/dataview";
import AccordionContent from "primevue/accordioncontent";
import AccordionPanel from "primevue/accordionpanel";
import Accordion from "primevue/accordion";
import AccordionHeader from "primevue/accordionheader";

class BlogPost {
  public data: GrayMatterFile<string>

  constructor(fileContent: string) {
    this.data = matter(fileContent);
  }
}

const content: Ref<BlogPost | null> = ref(null)
const moduleMeta: Ref<TrainingModuleMeta | null> = ref(null)
const currentLesson: Ref<LessonMeta | null> = ref(null);
const nextLesson: Ref<LessonMeta | null> = ref(null);

function getNextLesson(): LessonMeta {
  return moduleMeta.value.lessons[currentLesson.value?.lessonNumber + 1]
}

function getLesson() {
  const path = router.currentRoute.value.path.split("lesson/")[1].toString()!!;

  API.getModuleMeta(path).then((response) => {
    moduleMeta.value = response;

    // Determine the current lesson
    currentLesson.value = moduleMeta.value.lessons.filter((lesson) => lesson.path == "/" + path)[0]

    if (currentLesson.value == null) {
      currentLesson.value = moduleMeta.value.lessons[0]
    }

    nextLesson.value = moduleMeta.value.lessons[currentLesson.value.lessonNumber + 1]
  }).then(() => {
    API.getAsset(path).then((response) => {
      return response.text()
    }).then((text) => {
      content.value = new BlogPost(text)
    }).catch((error) => {
      console.error("Error loading blog post:", error); // Log the error to the console
      content.value = null; // Set post to null in case of error
    });
  }).catch((error) => {
    console.error("Error loading training module meta:", error); // Log the error to the console
    content.value = null; // Set post to null in case of error
  });
}

function goToLesson(lesson: LessonMeta) {
  content.value = null;
  router.push('/lesson' + lesson.path).then(() => {
    // Scroll to top of the page after navigation
    window.scrollTo({ top: 0, behavior: 'smooth' });
    // Reload the page so that it refreshes the content and "go to next" button.
    window.location.reload();
  });
}

function lessonButtonObject(lesson: LessonMeta) {
  if (currentLesson.value?.lessonNumber > lesson.lessonNumber ) {
    return {
      label: "Review",
      icon: "pi pi-book",
      iconPos: "left",
      class: "p-button-text p-button-sm view-button"
    }
  } else if (currentLesson.value?.lessonNumber == lesson.lessonNumber) {
    return {
      label: "You are here!",
      icon: "pi pi-book",
      iconPos: "left",
      class: "p-button-text p-button-sm view-button"
    }
  } else {
    return {
      label: "Go",
      icon: "pi pi-book",
    }
  }
}

function fixImgSrc(src: string) {
  // Replace images from their relative path in the markdown to the absolute path where assets will be served on the server.
  const newPath = router.currentRoute.value.path.split("lesson/")[1].toString()!!;
  var pathSlice = newPath.split("/");
  pathSlice.pop();

  // Count and pop additional times based on the number of "../" in the URL
  while (src.includes("../")) {
    src = src.replace("../", "");
    pathSlice.pop();
  }

  var hostUrl = 'http://localhost:9000/';
  if (process.env.NODE_ENV === 'production') {
    hostUrl = '/';
  }

  return hostUrl + pathSlice.join("/") + "/" + src.replace("./", "");
}

getLesson();

// Watch for router query changes (filePath) to reload post
watch(
    () => router.currentRoute.value.path,
    (newFilePath) => {
      if (newFilePath) {
        getLesson(); // Re-fetch the blog post when path changes
      }
    }
);
</script>

<template>
  <!-- Display loading skeleton until the post is fetched successfully -->
  <div v-if="!content">
    <Skeleton width="40rem" height="10rem"></Skeleton>
    <Skeleton class="mb-2"></Skeleton>
    <Skeleton width="10rem" class="mb-2"></Skeleton>
    <Skeleton width="5rem" class="mb-2"></Skeleton>
    <Skeleton height="2rem" class="mb-2"></Skeleton>
  </div>
  <div v-else class="blog-container">
    <Card class="mt-4">
      <template #title>
        <h2>{{ moduleMeta.title }}</h2>
        <h4>Lesson {{ currentLesson.lessonNumber }}: {{ currentLesson.title }}</h4>
      </template>
      <template #subtitle>
        <b>Author:</b> Quinn Bast<br/>
        <b>{{ moduleMeta.lastModified.toDateString() }}</b>
      </template>
      <template #content>
        <Accordion value="0">
          <AccordionPanel :value="0">
            <AccordionHeader>Lesson {{ currentLesson.lessonNumber }} of {{moduleMeta.lessons.length}}</AccordionHeader>
            <AccordionContent>
              <DataView :value="moduleMeta.lessons">
                <template #list="slotProps">
                  <div class="w-full">
                    <div
                        v-for="(lesson, index) in slotProps.items"
                        :key="index"
                        class="lesson-row"
                    >
                      <span class="lesson-title">{{ lesson.lessonNumber }}. {{ lesson.title }}</span>

                      <!-- Button with explicit right alignment -->
                      <Button
                          :label="lessonButtonObject(lesson).label"
                          :icon="lessonButtonObject(lesson).icon"
                          iconPos="left"
                          class="p-button-text p-button-sm view-button"
                          @click="goToLesson(lesson)"
                          style="margin-left: auto;"
                      />
                    </div>
                  </div>
                </template>
              </DataView>
            </AccordionContent>
          </AccordionPanel>
        </Accordion>
      </template>
<!--      <template #footer>-->
<!--        <Chip class="chip-tag" v-for="tag in post.data.data.tags" :label="tag" />-->
<!--      </template>-->
    </Card>

    <Markdown :content="content.data.content" :remark-plugins="[remarkGfm]">
      <!-- This lets me override specific HTML elements from the render so I can style them with PrimeVue component library. Very nice. -->
      <template #p="{ children }">
        <div style="padding: 20px;">
          <v-node-renderer :content="children"></v-node-renderer>
        </div>
      </template>
      <template #img="{ src, alt, style }">
        <img :src="fixImgSrc(src)" :alt="alt" :style=style >
      </template>
    </Markdown>
    <div class="navigation-buttons">
      <!-- Back to Blog List Button -->
      <Button
          label="Back to Training Modules"
          icon="pi pi-arrow-left"
          class="p-button-sm p-button-secondary"
          @click="router.push('/training')"
      />

      <!-- Read Next Post Button -->
      <Button
          v-if="currentLesson.lessonNumber != moduleMeta.lessons.length"
          :label="(currentLesson.lessonNumber + 1) + '. ' + getNextLesson().title"
          icon="pi pi-arrow-right"
          iconPos="right"
          class="p-button-sm p-button-secondary"
          @click="goToLesson(getNextLesson())"
      />
    </div>
  </div>
</template>

<style>
.blog-container {
  margin: auto;
  max-width: 740px;
}

.chip-tag {
  background: teal;
  margin: .25rem;
}

:root {
  --primary-color: white;
  --secondary-color: rgb(61, 68, 73);
  --highlight-color: #3282b8;

  --dt-status-available-color: greenyellow;
  --dt-status-away-color: lightsalmon;
  --dt-status-offline-color: lightgray;

  --dt-padding: 12px;
  --dt-padding-s: 6px;
  --dt-padding-xs: 2px;

  --dt-border-radius: 3px;

  --dt-background-color-container: #2a3338;
  --dt-border-color: var(--secondary-color);
  --dt-bg-color: var(--highlight-color);
  --dt-text-color: var(--primary-color);
  --dt-bg-active-button: var(--highlight-color);
  --dt-text-color-button: var(--primary-color);
  --dt-text-color-active-button: var(--primary-color);
  --dt-hover-cell-color: var(--highlight-color);
  --dt-even-row-color: var(--secondary-color);
  --dt-focus-color: var(--highlight-color);
  --dt-input-background-color: var(--secondary-color);
  --dt-input-color: var(--primary-color);
}

code {
  color: teal;
}

th, td {
  padding: var(--dt-padding) var(--dt-padding);
}

th {
  font-weight: bolder;
  text-align: left;
  border-bottom: solid 1px var(--dt-border-color);
}

td {
  border-bottom: solid 1px var(--dt-border-color);
}

tbody tr:hover {
  background-color: var(--dt-hover-cell-color);
}

tbody tr .available::after,
tbody tr .away::after,
tbody tr .offline::after {
  display: inline-block;
  vertical-align: middle;
}

tbody tr .available::after {
  content: "Online";
  color: var(--dt-status-available-color);
}

tbody tr .away::after {
  content: "Away";
  color: var(--dt-status-away-color);
}

tbody tr .offline::after {
  content: "Offline";
  color: var(--dt-status-offline-color);
}

tbody tr .available::before,
tbody tr .away::before,
tbody tr .offline::before {
  content: "";
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 10px;
  border-radius: 50%;
  vertical-align: middle;
}

tbody tr .available::before {
  background-color: var(--dt-status-available-color);
}

tbody tr .away::before {
  background-color: var(--dt-status-away-color);
}

tbody tr .offline::before {
  background-color: var(--dt-status-offline-color);
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  padding: 1rem;
}

.navigation-buttons .p-button {
  font-size: inherit;
  padding: 0.5rem 1rem;
  background-color: #3282b8;
  color: #fff;
  border: none;
  border-radius: 4px;
  transition: background-color 0.2s ease, transform 0.2s ease; /* Smooth effects */
}

.navigation-buttons .p-button:hover {
  background-color: #2c6f9e;
  transform: translateY(-2px);
  cursor: pointer;
}

.navigation-buttons .p-button:active {
  background-color: #255779;
  transform: translateY(0);
}

/* Specific styles for lessons inside accordion */
.lesson-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid var(--surface-border, #dee2e6);
}

.lesson-title {
  flex: 1;
  text-align: left;
  white-space: nowrap;
  white-space: normal;
  overflow: visible;
  padding-right: 0.5rem;
}

/* Mobile-specific styles to prevent button overflow */
@media (max-width: 640px) {
  .lesson-row {
    padding: 0.3rem 0.5rem; /* Reduced padding */
    min-height: 2.25rem; /* Compact but readable height */
  }

  .lesson-title {
    font-size: 0.9rem; /* Slightly smaller text on mobile */
    max-width: 65%; /* Limit title width to ensure space for button */
  }

  /* Make button more compact */
  :deep(.p-button.p-button-sm) {
    padding: 0.2rem 0.4rem;
    min-width: 60px; /* Ensure minimum width for button */
    font-size: 0.85rem;
  }

  /* Change button to show only icon on very small screens */
  @media (max-width: 400px) {
    .view-button .p-button-label {
      display: none;
    }

    .view-button {
      min-width: 32px;
      padding: 0.25rem;
    }

    .lesson-title {
      max-width: 75%; /* Give title more space when button is icon-only */
    }
  }

  /* Reduce unnecessary spacing in accordion */
  :deep(.p-accordion-content) {
    padding: 0.25rem 0;
  }

  :deep(.p-dataview .p-dataview-content) {
    padding: 0;
  }
}

.lesson-row .p-button {
  background-color: #27a5a2; /* Solid green background for the button */
  color: #fff; /* White text for contrast */
  border: none; /* Remove the border for a cleaner look */
  font-weight: bold; /* Bold text for emphasis */
  transition: background-color 0.2s ease, transform 0.2s ease;
}
</style>