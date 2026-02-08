<template>
  <div class="training-modules-page">
    <h1 class="page-title">Training Material</h1>
    <p class="page-description">
      <p>Below is various training modules that I have created over the years.</p>
      <p>Each module contains a series of lessons that can be completed in-order or viewed on demand.</p>
    </p>

    <!-- Content Section -->
    <div v-if="trainingModules.length > 0" class="modules-grid">
      <!-- Training Module Card -->
      <Card
          v-for="module in trainingModules"
          :key="module.title"
          class="blog-card"
      >
        <template #title>
          <h3>{{ module.title }}</h3>
        </template>

        <template #content>
          <p class="date">Last Updated: {{ module.lastModified.toLocaleDateString() }}</p>
          <p class="excerpt">{{ module.description }}</p>
          <Accordion value="0">
            <AccordionPanel :value="0">
              <AccordionHeader>{{module.lessons.length}} Lessons</AccordionHeader>
              <AccordionContent>
                <DataView :value="module.lessons">
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
                            label="Go"
                            icon="pi pi-book"
                            iconPos="left"
                            class="p-button-text p-button-sm view-button"
                            @click="() => router.push('/lesson' + module.path + '\?lesson=' + lesson.path)"
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

        <template #footer>
          <Button
              label="Start Training"
              icon="pi pi-book"
              iconPos="left"
              class="p-button-text p-button-sm"
              @click="() => router.push('/lesson' + module.path)"
          />
        </template>
      </Card>
    </div>

    <!-- Skeleton Loader Section -->
    <div v-else class="skeleton-loader">
      <Skeleton class="mb-4" width="70%" height="2rem" />
      <Skeleton class="mb-4" width="100%" height="1.5rem" />
      <Skeleton class="mb-4" width="85%" height="1.5rem" />
      <Skeleton width="50%" height="1.5rem" />
    </div>
  </div>
</template>

<script setup lang="ts">
// @ts-nocheck
import { ref } from "vue";
import { useRouter } from "vue-router";
import Card from "primevue/card";
import Button from "primevue/button";
import Skeleton from "primevue/skeleton";
import Accordion from 'primevue/accordion';
import AccordionPanel from 'primevue/accordionpanel';
import AccordionHeader from 'primevue/accordionheader';
import AccordionContent from 'primevue/accordioncontent';
import DataView from 'primevue/dataview';
import {API, TrainingModuleMeta} from "@/api/api.ts";

const router = useRouter();
const trainingModules = ref<TrainingModuleMeta[]>([]);

/**
 * Fetches training modules data from the API
 */
function getTrainingModules() {
  API.getTrainingModules().then((modules) => {
    trainingModules.value = modules;
  });
}

getTrainingModules();
</script>

<style scoped>
:deep(.p-card),
:deep(.p-card-body),
:deep(.p-card-content) {
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden; /* Prevent overflow */
}

:deep(.p-accordion),
:deep(.p-accordion-tab),
:deep(.p-accordion-content) {
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.training-modules-page {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 1rem;
  font-family: Arial, sans-serif;
  color: #eaeaea;
  text-align: center;
  background-color: #1e1e1e;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  border-radius: 8px;
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  color: #ffffff;
  font-weight: bold;
}

.page-description {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: #bbbbbb;
}

/* Grid Layout */
.modules-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
  margin-top: 2rem;
}

/* Skeleton Loader */
.skeleton-loader {
  max-width: 800px;
  margin: 0 auto;
  padding: 1rem;
}

/* Module Card */
.module-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.module-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
  padding: 1rem 1.5rem;
  color: #555;
  border-bottom: 1px solid #ddd;
  background: #f4f4f4;
  border-radius: 8px 8px 0 0;
}

.module-title {
  font-size: 1.4rem;
  font-weight: bold;
  color: #007ad9;
}

.module-date {
  font-size: 0.85rem;
  color: #999;
  white-space: nowrap;
}

.module-content {
  padding: 1rem 1.5rem;
  font-size: 1rem;
  color: #333;
}

.module-footer {
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: flex-end;
}

.excerpt {
  margin: 1rem 0;
  color: #7c7c7c;
}

/* View Module Button */
.view-module-button {
  background-color: #007ad9; /* PrimeVue blue */
  color: white;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
}

.view-module-button:hover {
  background-color: #005bb5; /* Darker blue hover */
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .module-date {
    margin-top: 0.5rem;
  }

  .modules-grid {
    grid-template-columns: 1fr; /* Stack cards vertically */
  }
}

.blog-card .p-button {
  background-color: #27a5a2; /* Solid green background for the button */
  color: #fff; /* White text for contrast */
  border: none; /* Remove the border for a cleaner look */
  font-weight: bold; /* Bold text for emphasis */
  transition: background-color 0.2s ease, transform 0.2s ease;
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

</style>