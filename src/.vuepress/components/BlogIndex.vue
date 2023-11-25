<template>
    <div>
        <b-container>
            <div>
                <h4><b-icon-filter/>Filter by Tag</h4>
                <h3><b-badge @click="removeTag" v-for="tag in selectedTags" v-bind:key="tag" pill variant="secondary">{{ tag }}<b-icon-x/></b-badge></h3>
                <h3><b-badge @click="selectTag" v-for="tag in availableTags" v-bind:key="tag" pill variant="success">{{ tag }}<b-icon-plus/></b-badge></h3>
            </div>
        </b-container>

        <h1>Posts</h1>

        <b-card v-for="post in getPostsToShow()"
            :key="post.path"
            :title="post.frontmatter.title"
            class="m-3"
            no-body>
            <b-card-body>
                <router-link :to="post.path"><h4>{{ post.frontmatter.title }}</h4></router-link>
                <h3><b-badge class="m-1" v-for="tag in post.frontmatter.tags" v-bind:key="tag" pill>{{ tag }}</b-badge></h3>
                <h6>{{ new Date(post.frontmatter.date).toDateString() }}</h6>
                <p class="card-text">{{ post.body }}</p>
                <p>{{ post.frontmatter.description }}</p>
                <b-button :to="post.path">Read more</b-button>
            </b-card-body>
        </b-card>
    </div>
</template>

<script>
export default {
    data() {
        return {
            selectedTags: [],
            availableTags: [],
            showPosts: []
        };
    },
    methods: {
        selectTag(e) {
            this.selectedTags.push(e.currentTarget.innerText);
            this.availableTags = this.availableTags.filter((tag) => tag !== e.currentTarget.innerText);
            
        },
        removeTag(e) {
            this.selectedTags = this.selectedTags.filter((tag) => tag !== e.currentTarget.innerText);
            this.availableTags.push(e.currentTarget.innerText);
        },
        sortPostsByDate(p1, p2) {
            return new Date(p2.frontmatter.date) - new Date(p1.frontmatter.date);
        },
        filterIfEnabled(list) {
            if(this.selectedTags.length != 0) {
                return list.filter()
            } else {
                return list;
            }
        },
        getPostsToShow() {
            return this.allPostsSorted.filter((post) => {
                if(this.selectedTags.length != 0) {
                    return post.frontmatter.tags.some((tag) => this.selectedTags.includes(tag));
                } else {
                    return post;
                }
            });
        },
    },
    mounted() {
        this.availableTags = this.allPostTags;
    },
    computed: {
        allPostsSorted() {
            return this.$site.pages
                .filter((page) => page.path.startsWith('/blog/') && !page.frontmatter.blog_index)
                .sort(this.sortPostsByDate);
        },
        allPostTags() {
            return [... new Set(this.allPostsSorted.flatMap(page => page.frontmatter.tags))].filter((tag) => tag != null);
        },
    },
};
</script>