<template>
    <b-container>
        <b-card
            :key="this.$page.key"
            :title="this.$page.title"
            class="m-2">
            <b-card-body>
                <p><b>Author:</b> Quinn Bast</p>
                <p><b>Date:</b> {{ new Date(this.$page.frontmatter.date).toLocaleString() }}</p>
                <h3><b-badge class="m-1" v-for="tag in this.$page.frontmatter.tags" v-bind:key="tag" pill variant="success">{{ tag }}</b-badge></h3>
            </b-card-body>
        </b-card>
    </b-container>
</template>

<script>
export default {
}
</script>

<style>

</style>