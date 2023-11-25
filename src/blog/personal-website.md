---
title: "Creating a personal website"
date: 2023-11-25
description: "Exploring my options for creating a website. Using VuePress for static generation and sharing my experiences along the way."
tags:
- Vue
- Javascript
- Frontend
---

# Creating a personal website

I set out on an adventure to create a personal website to get some visibility to the world.

Specifically, I wanted my website to contain a number of pages:

1. A technical Blog - because who doesn't want to start up a blog?
2. A website to advertise any projects that I release.
3. Provide a place to accept donations if a genrous soul wants to support me.

This post outlines how I ended up creating this blog, and how you can quickly setup your own blog too!

# Weighing the Options

While I initially wanted the website to be a completely custom website, being able to manage a blog was becoming a daunting task.
Managing content, tags, metadata, timestamps, and figuring out how to cohesively present my blog posts was a heavy lift for something I just wanted to get done in a day.
Ultimately, I wanted to be able to manage by blog postings through markdown files, as markdown can easily convey information while being extremely simple to manage.

I also didn't want to use a CMS (Content Management System) or other backend server, as I knew that I could host static pages for free on Github Pages and didn't want to pay to host a backend server.

Initially, I was interested in using [vue-markdown-render](https://www.npmjs.com/package/vue-markdown-render), as this component looked pretty useful for rendering markdown. While this option would have worked well, it got a bit complex, as writing my readme files would need to be done directly in `.vue` files:

```vue
<template>
  <div>
    <vue-markdown :source="src">
  </div>
</template>

<script lang="ts">
import VueMarkdown from 'vue-markdown-render'

export default {
  components: { VueMarkdown },
  data() {
      return {
          content: `
# This is my markdown

The file spacing is not ideal, I have to manually un-tab each new line I write...
Additionally, this file is now a mix of Vue.js and markdown.
Not to mention, I can't easily track metadata like tags, modification dates, etc.
          `
      }
  }
})
</script>
```

Additionally, this markdown rendering would happen on the fly, meaning that my website content would not be statically rendered, Javascript would be generating my blog posts on-demand. [While Google supports SPA (Single Page Apps)](https://netpeak.net/blog/spa-seo-how-to-create-seo-friendly-single-page-app-website/) and has a majority of the market share, many other browsers will not index SPA's, and reduce my presence on the web.

# Introducing VuePress

While researching a bit more, I came across [this blog post](https://michaelnthiessen.com/rebuilding-blog-in-vue) which weighed out various options for blogging with Vue.
I found that [VuePress](https://vuepress.vuejs.org/) was a static site generator which is backed by markdown files, however, it still allows custom Vue components to be used. This was ideal, as promoting my video-game releases, and accepting donations were things that I would have to create custom components for anyway.

Getting started with VuePress was pretty easy, I just created a folder for my website, and ran their project bootstrapper:

```bash
cd PersonalWebsite
npm create vuepress-site
```

Once the installation was complete, I could run the following commands to get a static website hosted at `localhost:8080`:

```
cd docs
npm install
npm run dev
```

Personally, I didn't like the `docs` folder, as it was just a top level folder doing nothing, so I moved everything to the repository top level:

```
mv ./docs/ .
```

# Configuring my Blog

Once I had the basics running, I wanted to change some things away from the defaults.
Specifically, I wanted to change up the homepage and set some different navigation links.

To do this, I modified `src/.vuepress/config.js` to customize the homepage, and set the navbar:

```js
// src/.vuepress/config.js

module.exports = {
  title: 'Queue Bee Co.',
  themeConfig: {
    nav: [
      {
        text: 'Projects',
        link: '/projects/',
      },
      {
        text: 'Blog',
        link: '/blog/'
      },
      {
        text: 'Donate',
        link: '/donate/'
      }
    ],
  }
}
```

And, as you can see, it added navigation links to the website!

# Writing markdown

Once I had added the navigation links configured, the next step was to add some markdown files to test my blog. I initially tried adding a `blog` and `projects` directory to the top level of my project and added some sample markdown files into eeach directory.:

```bash
mkdir blog
touch blog/test.md
mkdir projects
touch projects/test.md
```

However, after some trial and error, I realized, that VuePress's relative links start within the `src` directory.
After moving my directories into the `src` folder, (eg. `./src/blog/`), and re-running `npm run dev`, I was able to see the sample markdown files that I had created on the website, confirming that markdown rending works!

# Using Bootstrap-Vue with VuePress

Finally, I wanted to look into using custom Vue components to see how they worked as I will definitely need to create custom components for my donation page and project advertising.

I am fairly familiar with [Bootstrap](https://bootstrap-vue.org/docs/components/button#component-reference)'s CSS components, so for any custom Vue components that I created, I also wanted to be able to use bootstrap-vue. Luckily, this wasn't too hard to setup. First I had to install bootstrap:

```bash
npm install bootstrap bootstrap-vue
```

And then, I needed to configure VuePress to load the bootstrap components. To do this, I needed to modify two files.

First, I needed to 'enhance' VuePress by changing `/src/.vuepress/enhanceApp.js` to load bootstrap-vue as I normally would in any other Vue project:

```js
// src/.vuepress/enhanceApp.js
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";


export default ({Vue, options, router, siteData}) => {
  Vue.use(BootstrapVue);
  Vue.use(IconsPlugin);
}
```

And finally, I needed to import bootstrap's CSS files. VuePress has a few [special path URLs](https://vuepress.vuejs.org/guide/assets.html) that you can use to indicate that files should be loaded from specific locations. Specifically, the `~` character indicates that we should load a webpack module from the `node_modules` directory. Using this, we can import the bootstrap CSS files in the `src/.vuepress/styles/index.styl` file:

```
@require '~bootstrap/dist/css/bootstrap.css'
@require '~bootstrap-vue/dist/bootstrap-vue.css'
```

# Creating custom Vue components

Once this was done, I created a custom `.vue` component, `BlogIndex.vue` in the `src/.vuepress/components/` directory [as documented](https://vuepress.vuejs.org/guide/using-vue.html#using-components). My Vue component made use of some bootstrap-vue components:

```vue
<template>
    <div>
        <b-card title="Hello World">
            <p>This is a blog post!</p>
        </b-card>
    </div>
</template>

<script>
export default {};
</script>
```

And, finally, I rendered this component in one of my markdown files. Changing one of my temporary markdown files to include the custom Vue component, I was able to see my component with bootstrap styling applied get rendered to my website!

```
# sample-file.md

This is my blog:

<BlogIndex/>
```

After prototying, I was fairly confident that VuePress would be flexible enough for my needs and created this blog post!

# Automating Deployment

Because I am using Github pages, I also looked at the VuePress documentation for [how to deploy to GitHub pages](https://vuepress.vuejs.org/guide/deploy.html#github-pages-and-github-actions). I have deployed to Github pages [in the past](https://github.com/Subterfuge-Revived/Remake-Website/blob/master/.github/workflows/BuildWebsite.yml), but VuePress has a custom Github Action that you can import that does it all in one-go!

By just making a deployment github action and setting my access token, I was able to also successfully automate uploads to my github!

```yaml
name: Build and Deploy
on:
  push:
    branches: [ main ]
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout
      uses: actions/checkout@master

    - name: vuepress-deploy
      uses: jenkey2011/vuepress-deploy@master
      env:
        ACCESS_TOKEN: ${{ secrets.ACCESS_TOKEN }}
        TARGET_REPO: QuinnBast/repo
        TARGET_BRANCH: main
        BUILD_SCRIPT: yarn && yarn docs:build
        BUILD_DIR: docs/.vuepress/dist
```

Now any updates to my github repository will 