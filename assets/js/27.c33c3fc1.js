(window.webpackJsonp=window.webpackJsonp||[]).push([[27],{321:function(t,e,s){"use strict";s.r(e);s(92);var a={data:()=>({selectedTags:[],availableTags:[],showPosts:[]}),methods:{selectTag(t){this.selectedTags.push(t.currentTarget.innerText),this.availableTags=this.availableTags.filter(e=>e!==t.currentTarget.innerText)},removeTag(t){this.selectedTags=this.selectedTags.filter(e=>e!==t.currentTarget.innerText),this.availableTags.push(t.currentTarget.innerText)},sortPostsByDate:(t,e)=>new Date(e.frontmatter.date)-new Date(t.frontmatter.date),filterIfEnabled(t){return 0!=this.selectedTags.length?t.filter():t},getPostsToShow(){return this.allPostsSorted.filter(t=>0!=this.selectedTags.length&&null!=t.frontmatter.tags?t.frontmatter.tags.some(t=>this.selectedTags.includes(t)):t)}},mounted(){this.availableTags=this.allPostTags},computed:{allPostsSorted(){return this.$site.pages.filter(t=>t.path.startsWith("/blog/")&&!t.frontmatter.blog_index).sort(this.sortPostsByDate)},allPostTags(){return[...new Set(this.allPostsSorted.flatMap(t=>t.frontmatter.tags))].filter(t=>null!=t)}}},r=s(16),l=Object(r.a)(a,(function(){var t=this,e=t._self._c;return e("div",[e("b-container",[e("div",[e("h4",[e("b-icon-filter"),t._v("Filter by Tag")],1),t._v(" "),e("h3",t._l(t.selectedTags,(function(s){return e("b-badge",{key:s,staticClass:"m-1",attrs:{pill:"",variant:"secondary"},on:{click:t.removeTag}},[t._v(t._s(s)),e("b-icon-x")],1)})),1),t._v(" "),e("h3",t._l(t.availableTags,(function(s){return e("b-badge",{key:s,staticClass:"m-1",attrs:{pill:"",variant:"success"},on:{click:t.selectTag}},[t._v(t._s(s)),e("b-icon-plus")],1)})),1)])]),t._v(" "),e("h1",[t._v("Posts")]),t._v(" "),t._l(t.getPostsToShow(),(function(s){return e("b-card",{key:s.path,staticClass:"m-3",attrs:{title:s.frontmatter.title,"no-body":""}},[e("b-card-body",[e("router-link",{attrs:{to:s.path}},[e("h4",[t._v(t._s(s.frontmatter.title))])]),t._v(" "),e("h3",t._l(s.frontmatter.tags,(function(s){return e("b-badge",{key:s,staticClass:"m-1",attrs:{pill:""}},[t._v(t._s(s))])})),1),t._v(" "),e("h6",[t._v(t._s(new Date(s.frontmatter.date).toDateString()))]),t._v(" "),e("p",{staticClass:"card-text"},[t._v(t._s(s.body))]),t._v(" "),e("p",[t._v(t._s(s.frontmatter.description))]),t._v(" "),e("b-button",{attrs:{to:s.path}},[t._v("Read more")])],1)],1)}))],2)}),[],!1,null,null,null);e.default=l.exports}}]);