<template>
    <div class="timeline-container left">
        <div class="content-side-right">
            <slot name="side"></slot>
        </div>
        <b-card :title="title">
            <slot>{{ content }}</slot>
        </b-card>
    </div>
</template>

<script>
export default {
    props: {
        title: String,
        content: String,
    }
}
</script>

<style>

</style>