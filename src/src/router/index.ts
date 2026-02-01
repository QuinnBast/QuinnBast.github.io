import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import BlogListView from '../views/BlogListView.vue'
import IndividualBlogPostView from '../views/IndividualBlogPostView.vue'
import FlipSevenScoringTool from '../views/tools/FlipSevenScoringTool.vue'
import StringReplaceTool from '../views/tools/StringReplaceTool.vue'
import JsonFormatTool from '../views/tools/JsonFormatTool.vue'
import S2RegionCovererTool from '../views/tools/S2RegionCovererTool.vue'
import H3RegionCovererTool from '../views/tools/H3RegionCovererTool.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/blog',
      name: 'blog',
      component: BlogListView,
    },
    {
      path: '/article',
      name: 'article',
      component: IndividualBlogPostView,
    },
    {
      path: '/flip7',
      name: 'flip7',
      component: FlipSevenScoringTool,
    },
    {
      path: '/string-replacer',
      name: 'stringReplace',
      component: StringReplaceTool,
    },
    {
      path: '/json-formatter',
      name: 'jsonFormat',
      component: JsonFormatTool,
    },
    {
      path: '/s2-region-coverer',
      name: 's2Coverer',
      component: S2RegionCovererTool,
    },
    {
      path: '/h3-region-coverer',
      name: 'h3Coverer',
      component: H3RegionCovererTool,
    },
  ],
})

export default router
