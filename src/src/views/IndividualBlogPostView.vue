<script setup lang="ts">
import {type Ref, ref, computed, watch} from "vue";
import { default as matter } from 'gray-matter'
import type {GrayMatterFile} from "gray-matter";
import Button from "primevue/button";
import Card from "primevue/card";
import Chip from "primevue/chip";
import Skeleton from 'primevue/skeleton';
import router from "@/router";
import { Markdown, VNodeRenderer } from 'vue-markdown-next';
import remarkGfm from 'remark-gfm';
import { blogItems } from '@/utils/blog-post-list';

class BlogPost {
  public data: GrayMatterFile<string>

  constructor(fileContent: string) {
    this.data = matter(fileContent);
  }
}

const post: Ref<BlogPost | null> = ref(null)
const nextPostPath: Ref<string | null> = ref(null);

function getBlogPost() {
  const path = router.currentRoute.value.query.filePath?.toString()!!;

  fetchBlogData(path).then((blogPost) => {
    post.value = blogPost;

    // Calculate the next post
    const currentIndex = blogItems.indexOf(path);
    if (currentIndex >= 0 && currentIndex < blogItems.length - 1) {
      nextPostPath.value = blogItems[currentIndex + 1]; // Next post in the list
    } else {
      nextPostPath.value = blogItems[0];
    }

  })
  .catch((error) => {
    console.error("Error loading blog post:", error); // Log the error to the console
    post.value = null; // Set post to null in case of error
  });
}

function fetchBlogData(blogLink: string): Promise<BlogPost> {
  return fetch(blogLink).then((response) => {
    return response.text().then((text) => {
      return new BlogPost(text);
    })
  })
}

function goToNextPost() {
  if (nextPostPath.value) {
    post.value = null;
    router.push('/article?filePath=' + nextPostPath.value).then(() => {
      // Scroll to top of the page after navigation
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
}

getBlogPost();

// Watch for router query changes (filePath) to reload post
watch(
    () => router.currentRoute.value.query.filePath,
    (newFilePath) => {
      if (newFilePath) {
        getBlogPost(); // Re-fetch the blog post when path changes
      }
    }
);
</script>

<template>
  <!-- Display loading skeleton until the post is fetched successfully -->
  <div v-if="!post">
    <Skeleton width="40rem" height="10rem"></Skeleton>
    <Skeleton class="mb-2"></Skeleton>
    <Skeleton width="10rem" class="mb-2"></Skeleton>
    <Skeleton width="5rem" class="mb-2"></Skeleton>
    <Skeleton height="2rem" class="mb-2"></Skeleton>
  </div>
  <div v-else class="blog-container">
    <Card class="mt-4">
      <template #title><h2>{{ post.data.data.title }}</h2></template>
      <template #subtitle>
        <b>Author:</b> Quinn Bast<br/>
        <b>{{ new Date(post.data.data.date).toDateString() }}</b>
      </template>
      <template #content>{{ post.data.data.description }}</template>
      <template #footer>
        <Chip class="chip-tag" v-for="tag in post.data.data.tags" :label="tag" />
      </template>
    </Card>

    <Markdown :content="post.data.content" :remark-plugins="[remarkGfm]">
      <!-- This lets me override specific HTML elements from the render so I can style them with PrimeVue component library. Very nice. -->
      <template #p="{ children }">
        <div style="padding: 20px;">
          <v-node-renderer :content="children"></v-node-renderer>
        </div>
      </template>
    </Markdown>
    <div class="navigation-buttons">
      <!-- Back to Blog List Button -->
      <Button
          label="Back to Blog List"
          icon="pi pi-arrow-left"
          class="p-button-sm p-button-secondary"
          @click="router.push('/blog')"
      />

      <!-- Read Next Post Button -->
      <Button
          v-if="nextPostPath"
          label="Read Next Post"
          icon="pi pi-arrow-right"
          iconPos="right"
          class="p-button-sm p-button-secondary"
          @click="goToNextPost"
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

</style>