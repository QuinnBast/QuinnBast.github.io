const { description } = require('../../package')
const util = require('util')

const getConfig = require("vuepress-bar");
const { nav, sidebar } = getConfig("src/", {
  maxLevel: 4,
  filter: (meta) => meta.title !== undefined
});

module.exports = {
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#title
   */
  title: 'Queue Bee Co.',
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#description
   */
  description: description,

  /**
   * Extra tags to be injected to the page HTML `<head>`
   *
   * ref：https://v1.vuepress.vuejs.org/config/#head
   */
  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
  ],

  /**
   * Theme configuration, here is the default theme configuration for VuePress.
   *
   * ref：https://v1.vuepress.vuejs.org/theme/default-theme-config.html
   */
  themeConfig: {
    repo: 'QuinnBast/QuinnBast.github.io',
    editLinks: false,
    docsDir: '',
    lastUpdated: true,
    nav: [
      {
        text: 'Projects',
        link: '/projects/',
      },
      {
        text: 'Blog',
        link: '/blog/'
      },
      // {
      //   text: 'Donate',
      //   link: '/donate/'
      // },
    ],
    searchPlaceholder: 'Search...',
    displayAllHeaders: true,
    lastUpdated: 'Last Updated',
    nextLinks: true,
    prevLinks: true,
    sidebarDepth: 4,
    sidebar: sidebar,
  },

  /**
   * Apply plugins，ref：https://v1.vuepress.vuejs.org/zh/plugin/
   */
  plugins: [
    '@vuepress/plugin-back-to-top',
    '@vuepress/plugin-medium-zoom',
  ]
}