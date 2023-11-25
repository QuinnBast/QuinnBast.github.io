/**
 * Client app enhancement file.
 *
 * https://v1.vuepress.vuejs.org/guide/basic-config.html#app-level-enhancements
 */
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";


export default ({Vue, options, router, siteData}) => {
  Vue.use(BootstrapVue);
  Vue.use(IconsPlugin);
}
