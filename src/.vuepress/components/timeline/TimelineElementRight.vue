<template>
    <div class="timeline-container right">
        <div class="content-side-left">
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