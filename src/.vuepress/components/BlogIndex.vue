<template>
    <div>
        <b-container>
            <div>
                <h3><b-badge v-for="tag in tags" v-bind:key="tag" pill variant="primary">{{ tag }}<b-icon-plus/></b-badge></h3>
            </div>
        </b-container>

        <h1>Posts</h1>

        <b-card v-for="post in posts"
            :key="post.path"
            :title="post.frontmatter.title"
            class="m-3"
            no-body>
            <b-card-body>
                <router-link :to="post.path"><h4>{{ post.frontmatter.title }}</h4></router-link>
                <h3><b-badge class="m-1" v-for="tag in post.frontmatter.tags" v-bind:key="tag" pill>{{ tag }}</b-badge></h3>
                <p class="card-text">{{ post.body }}</p>
                <p>{{ post.frontmatter.description }}</p>
                <b-button :to="post.path">Read more</b-button>
            </b-card-body>
        </b-card>
    </div>
</template>

<script>
export default {
    computed: {
        posts() {
            return this.$site.pages
                .filter(
                    (page) => page.path.startsWith('/blog/') && !page.frontmatter.blog_index
                )
                .sort(
                    (p1, p2) =>
                        new Date(p2.frontmatter.date) - new Date(p1.frontmatter.date)
                );
        },
        tags() {
            return [... new Set(this.$site.pages.flatMap(page => page.frontmatter.tags))];
        },
        onlyUnique(value, index, array) {
            return array.indexOf(value) === index;
        }
    },
};
</script>