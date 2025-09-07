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
import { blogItems } from '@/utils/blog-post-list';

const router = useRouter()

class BlogPost {
  public data: GrayMatterFile<string>
  public filePath: string

  constructor(filePath: string, fileContent: string) {
    this.filePath = filePath
    this.data = matter(fileContent);
  }
}

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

    <div class="blog-layout" v-if="blogPosts.length != 0">

      <!-- Sidebar for Navigation and Tag Filters -->
      <aside class="sidebar">
        <PanelMenu :model="navPathItems" multiple />

        <div class="filter-container">
          <h4><i class="pi pi-filter" /> Filter by Tag</h4>
          <div class="tags">
            <div v-for="tag in tags" :key="tag" class="tag-checkbox">
              <Checkbox v-model="selectedTags" :inputId="tag" :value="tag" />
              <label :for="tag">{{ tag }}</label>
            </div>
          </div>
        </div>
      </aside>

      <!-- Blog Posts Section -->
      <section class="blog-grid">
        <Card
            v-for="post in blogsSorted"
            :key="post.filePath"
            class="blog-card"
        >
          <template #title>
            <h3>{{ post.data.data.title }}</h3>
          </template>

          <template #content>
            <p class="excerpt">{{ post.data.data.description }}</p>
            <p class="date">{{ new Date(post.data.data.date).toLocaleDateString() }}</p>
          </template>

          <template #footer>
            <Button
                label="Read Post"
                icon="pi pi-book"
                iconPos="left"
                class="p-button-text p-button-sm"
                @click="() => router.push('/article?filePath=' + post.filePath)"
            />
          </template>
        </Card>
      </section>

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
/* Blog Container */
.blog-container {
  padding: 2rem;
  max-width: 1200px;
  margin: auto;
}

.blog-card .p-button {
  background-color: #27a5a2; /* Solid green background for the button */
  color: #fff; /* White text for contrast */
  border: none; /* Remove the border for a cleaner look */
  font-weight: bold; /* Bold text for emphasis */
  transition: background-color 0.2s ease, transform 0.2s ease;
}


.title {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

/* Main Layout (Sidebar + Blog Posts Grid) */
.blog-layout {
  display: grid;
  grid-template-columns: 300px 1fr; /* Sidebar and Content */
  gap: 2rem;
}

.sidebar {
  background-color: #1e1e1e;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  height: fit-content;
}

.filter-container {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #272727;
  border: 1px solid #333;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3); /* Soft shadow for elevation */
}


.tags {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-container h4 {
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
  color: #ddd;
}


.tag-checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tag-checkbox .p-checkbox {
  --checkbox-border-radius: 4px;
}

/* Blog Cards Section */
.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); /* Flexible grid */
  gap: 2rem;
}

.blog-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.blog-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.excerpt {
  margin: 1rem 0;
  color: #555;
}

.date {
  font-size: 0.9rem;
  color: #888;
}

/* Responsive Adjustments */
@media (max-width: 1024px) {
  .blog-layout {
    grid-template-columns: 1fr; /* Stack sidebar and content */
  }

  .sidebar {
    margin-bottom: 2rem;
  }

  .filter-container {
    display: none; /* Completely hide the tag filter */
  }
}

</style>