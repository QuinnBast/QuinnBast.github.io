<script setup lang="ts">
import {type Ref, ref, computed} from "vue";
import { default as matter } from 'gray-matter'
import type {GrayMatterFile} from "gray-matter";
import Card from "primevue/card";
import Chip from "primevue/chip";
import Skeleton from 'primevue/skeleton';
import router from "@/router";
import { Markdown, VNodeRenderer } from 'vue-markdown-next';
import remarkGfm from 'remark-gfm';

class BlogPost {
  public data: GrayMatterFile<string>

  constructor(fileContent: string) {
    this.data = matter(fileContent);
  }
}

// List item that holds a list of all of the blog
const blogItems = [
    // Kubernetes
    "articles/kubernetes/resources-stuck-terminating.md",
    "articles/kubernetes/understanding-requests-and-limits.md",
    "articles/kubernetes/highly-available-disks.md",
    "articles/kubernetes/highly-available-control-plane.md",

    // Game def
    "articles/game-dev/subterfuge/choosing-a-game-engine.md",

    // Frontend
    "articles/frontend/personal-website.md",
];

const post: Ref<BlogPost | null> = ref(null)

function getBlogPost() {
  const path = router.currentRoute.value.query.filePath?.toString()!!;

  fetchBlogData(path).then((blogPost) => {
    post.value = blogPost;
  });
}

function fetchBlogData(blogLink: string): Promise<BlogPost> {
  return fetch(blogLink).then((response) => {
    return response.text().then((text) => {
      return new BlogPost(text);
    })
  })
}

getBlogPost();
</script>

<template>
  <div class="blog-container" v-if="post != null">
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
    <br/>
    <br/>
  </div>
  <div v-else>
    <Skeleton width="40rem" height="10rem"></Skeleton>
    <Skeleton class="mb-2"></Skeleton>
    <Skeleton width="10rem" class="mb-2"></Skeleton>
    <Skeleton width="5rem" class="mb-2"></Skeleton>
    <Skeleton height="2rem" class="mb-2"></Skeleton>
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

</style>