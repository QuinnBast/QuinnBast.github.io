<script setup lang="ts">
// @ts-nocheck
import {type Ref, ref, computed } from "vue";
import { default as matter } from 'gray-matter'
import type {GrayMatterFile} from "gray-matter";
import Card from "primevue/card";
import Button from "primevue/button";
import Checkbox from "primevue/checkbox";
import PanelMenu from 'primevue/panelmenu';
import Skeleton from 'primevue/skeleton';
import type {MenuItem} from "primevue/menuitem";
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()

class BlogPost {
  public data: GrayMatterFile<string>
  public filePath: string

  constructor(filePath: string, fileContent: string) {
    this.filePath = filePath
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

const blogPosts: Ref<Array<BlogPost>> = ref([])
const tags: Ref<Array<string>> = ref([])
const selectedTags: Ref<Array<string>> = ref([])

const navPathItems: Ref<Array<MenuItem>> = ref([])

function getBlogPosts() {
  blogItems.map((url) => {
    fetchBlogData(url).then((blogPost) => {
      blogPosts.value.push(blogPost);
      addToPathList(blogPost)
      blogPost.data.data.tags.forEach((tag: string) => {
        if(!tags.value.includes(tag)) {
          tags.value.push(tag)
        }
      })
    })
  })
}

function fetchBlogData(blogLink: string): Promise<BlogPost> {
  return fetch(blogLink).then((response) => {
    return response.text().then((text) => {
      return new BlogPost(blogLink, text);
    })
  })
}

function addToPathList(blogPost: BlogPost) {

  const blogPostPath = blogPost.data.data.path.split("/")
  blogPostPath.push(blogPost.data.data.title)

  var curentNavPathItems = navPathItems.value

  blogPostPath.forEach((path: string) => {
    var item = curentNavPathItems.find((item) => item.label === path)
    if(item != undefined) {
      // The path already exists. Update to the new item list and continue walking.
      curentNavPathItems = item.items!!;
    } else {
      var isArticle = path == blogPost.data.data.title;
      var navPath = {
        label: path,
        icon: isArticle ? "pi pi-book" : "pi pi-folder",
        items: isArticle ? undefined : [],
        command: () => {
          if (isArticle) {
            router.push('/article?filePath=' + blogPost.filePath);
          }
        }
      }
      // Push the current path.
      curentNavPathItems.push(navPath)
      // Update to the latest item.
      if(!isArticle) {
        curentNavPathItems = navPath.items!!;
      }
    }
  })
}

const blogsSorted = computed(() => {
  return blogPosts.value
      .filter((item) => {
        if(selectedTags.value.length > 0) {
          return item.data.data.tags.some( tag => selectedTags.value.includes(tag))
        } else {
          return true
        }
      })
      .sort((first, second) => { return new Date(second.data.data.date) - new Date(first.data.data.date) });
})

getBlogPosts();
</script>

<template>
  <div class="blog-container">
    <h1>Blog</h1>

    <div class="flex-container" v-if="blogPosts.length != 0">

      <div class="flex-menu">
        <div class="card flex justify-center path-finder">
          <PanelMenu :model="navPathItems" multiple class="w-full md:w-80">
          </PanelMenu>
        </div>

        <h4><i class="pi pi-filter"/>Filter by Tag</h4>
        <h5>
          <div v-for="tag in tags" :key="tag" class="checkbox flex items-center gap-2">
            <Checkbox class="pad-check" v-model="selectedTags" :inputId="tag" name="tag" :value="tag" />
            <label class="label-pad" :for="tag">{{ tag }}</label>
          </div>
        </h5>
      </div>

      <div class="flex-post-list">

        <h1>Posts</h1>

        <Card class="mt-4" v-for="post in blogsSorted">
          <template #title><h1>{{ post.data.data.title }} {{ post.filename }}</h1></template>
          <template #subtitle><b>{{ new Date(post.data.data.date).toDateString() }}</b></template>
          <template #content>{{ post.data.data.description }}</template>
          <template #footer>
            <Button class="m-4" asChild v-slot="slotProps">
              <RouterLink :to="'/article?filePath=' + post.filePath" :class="slotProps.class + ' m-4'"><h4><i class="pi pi-book"/>&nbsp;Read more</h4></RouterLink>
            </Button>
          </template>
        </Card>
      </div>
    </div>
    <div v-else>
      <Skeleton width="40rem" height="10rem"></Skeleton>
      <Skeleton class="mb-2"></Skeleton>
      <Skeleton width="10rem" class="mb-2"></Skeleton>
      <Skeleton width="5rem" class="mb-2"></Skeleton>
      <Skeleton height="2rem" class="mb-2"></Skeleton>
    </div>
  </div>
</template>

<style scoped>
.flex-container {
  display: flex;
}

.flex-menu {
  margin: 2rem;
  flex-grow: 2;
  max-width: 600px;
  min-width: 400px;
}

.path-finder {
  margin: 2rem;
}

.flex-post-list {
  flex-grow: 3;
}

.checkbox {
  margin: 4px;
}

.blog-container {
  margin: auto;
  max-width: 1240px;
}

.label-pad {
  padding: 6px;
}
</style>