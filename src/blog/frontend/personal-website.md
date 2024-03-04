---
title: "Using VuePress to create a Personal Website"
date: 2023-11-25 18:21
description: "Exploring my options for creating a website. My experience using VuePress for static content generation using markdown files."
tags:
- Vue
- Javascript
- Frontend
---

<PageHeader/>

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
Ultimately, I wanted to be able to manage my blog postings through markdown files, as markdown can easily convey information while being extremely simple to manage.

Additionally, I didn't want to use a CMS (Content Management System) or other server, as I knew that using Github I could host static pages for free
and didnt want to pay to host a server.

I am fairly familiar and comfortable with Vue.js so I wanted to create my website using Vue.
Initially, I was interested in using the [vue-markdown-render](https://www.npmjs.com/package/vue-markdown-render) library.
While this option worked well during prototypes, it ended up getting complex, as writing my readme files would need to be done directly in `.vue` files:

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

Additionally, this markdown rendering would happen on the fly, meaning that my website content would not be statically rendered in advance. Not having static pages would harm my website's SEO. [While Google does support scraping SPA (Single Page Apps) for SEO purposes](https://netpeak.net/blog/spa-seo-how-to-create-seo-friendly-single-page-app-website/), many other browsers will not index SPA's, and thus reduce my presence on the web.

I needed a better solution.

# Introducing VuePress

While researching a bit more, I came across [this blog post](https://michaelnthiessen.com/rebuilding-blog-in-vue) which weighed out various options for blogging with Vue.
I found that [VuePress](https://vuepress.vuejs.org/) was a static site generator which is backed by markdown files, however, it still allows custom Vue components to be used. This was ideal, as promoting my video-game releases, and accepting donations were things that I would have to create custom components for anyway.

Getting started with VuePress was pretty easy, I just created a folder for my website, and ran their project bootstrapper:

```bash
cd PersonalWebsite
npm create vuepress-site
```

Once the installation was complete, I could run the following commands to get a static website hosted at `localhost:8080`:

```bash
cd docs
npm install
npm run dev
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

And, as you can see from this website, I have navigation links in the navbar!

# Writing markdown

Once I had the navigation links configured, the next step was to add some markdown files to test my blog. First, I created two directories, `blog` and `projects` to the top level of my repository and added some sample markdown files into each:

```bash
mkdir blog
touch blog/test.md
mkdir projects
touch projects/test.md
```

However, after some trial and error, I realized, that VuePress will only register content relative to the `src` directory.
Because I had created my directories at the repository root, VuePress was not detecting my content, and thus I was getting 404 pages when attempting to view my files.
After moving my directories into the `src` folder, (eg. `./src/blog/`), and re-running `npm run dev`, I was able to see my example markdown files that I had created rendered as static pages, confirming that markdown rending works!

# Using Bootstrap-Vue with VuePress

While rendering markdown is nice, I wanted to try out something more custom.
My website will need to have custom components for my donation page and project advertising, so my next step was to try to render Vue components in my markdown.

Firstly, I am fairly familiar with [Bootstrap](https://bootstrap-vue.org/docs/components/button#component-reference)'s CSS components, so for any custom Vue components that I created, I also wanted to be able to use bootstrap-vue. Luckily, this wasn't too hard to setup. First I had to install bootstrap:

```bash
npm install bootstrap bootstrap-vue
```

Next, I needed to configure VuePress to load the bootstrap library.
First, I needed to 'enhance' VuePress by changing `/src/.vuepress/enhanceApp.js` to load bootstrap-vue as I normally would in any other Vue project:

```js
// src/.vuepress/enhanceApp.js
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";


export default ({Vue, options, router, siteData}) => {
  Vue.use(BootstrapVue);
  Vue.use(IconsPlugin);
}
```

Finally, I needed to import bootstrap's CSS files. VuePress has a few [special path URLs](https://vuepress.vuejs.org/guide/assets.html) that you can use to indicate that files should be loaded from specific locations. Specifically, the `~` character indicates that we should load a webpack module from the `node_modules` directory. Using this, we can import the bootstrap CSS files in the `src/.vuepress/styles/index.styl` file:

```js
@require '~bootstrap/dist/css/bootstrap.css'
@require '~bootstrap-vue/dist/bootstrap-vue.css'
```

# Creating custom Vue components

Now that I had everything I needed, I could finally start testing out creating a custom component.
I created a new file named `BlogIndex.vue` in the `src/.vuepress/components/` directory [as documented](https://vuepress.vuejs.org/guide/using-vue.html#using-components). I created my component and also made use of the `b-card` bootstrap-vue components to ensure that Bootstrap was working.

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

Once the component was created, I rendered this component in one of my markdown files:

```
# sample-file.md

This is my blog:

<BlogIndex/>
```

By including my custom component, I was able to see my content rendered on the page with the bootstrap styling applied!
After prototying, I was fairly confident that VuePress would be flexible enough for my needs and created this blog post!

# Automating Deployment

One last thing that I wanted was the ability to automatically deploy my website to Github pages.
Luckily, VuePress has documentation on [how to deploy to GitHub pages](https://vuepress.vuejs.org/guide/deploy.html#github-pages-and-github-actions).
While I have deployed to Github pages [in the past](https://github.com/Subterfuge-Revived/Remake-Website/blob/master/.github/workflows/BuildWebsite.yml), VuePress has a custom Github Action that you can use which does everything all in one-go!

By making a deployment github action workflow at `.github/workflows/vuepress-deploy.yml`, and setting my access token, I was able to also successfully automate deployment of my website!

```yaml
name: Build and Deploy
on:
  push:
    branches: [ master ]
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
        TARGET_REPO: QuinnBast/QuinnBast.github.io
        TARGET_BRANCH: gh_pages
        BUILD_SCRIPT: yarn && yarn build
        BUILD_DIR: src/.vuepress/dist
```

Now any updates to my github repository will automatically deploy to [my github website](https://quinnbast.github.io/)!