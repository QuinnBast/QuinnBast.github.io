<template>
    <div class="full">
        <div class="name center">
            <Transition appear name="fade" mode="out-in"><span :key="greeting">{{ greeting }}</span></Transition><br/><span>I'm Quinn</span>
            
        </div>
        <div class="p-2 center">
            <b-button class="m-4" to="/projects"><h1><b-icon-award/>&nbsp;Accomplishments</h1></b-button>
            <b-button class="m-4" to="/blog"><h1><b-icon-book/>&nbsp;Read my Blog</h1></b-button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            greeting: "Hello!"
        }
    },
    methods: {
        randomizeGreeting() {
            var greetingOptions = [
                "Hello!", "Welcome.", "Bienvenu,", "Howdy!", "Hi there!", "👋", "Hey,", "✌️", "🤙"
            ];
            var optionsToSelect = greetingOptions.filter((option) => option !== this.greeting);
            this.greeting = optionsToSelect[Math.floor(Math.random()*optionsToSelect.length)];
        },
    },
    mounted() {
        setInterval(this.randomizeGreeting, 4000);
    }
}
</script>

<style>
.theme-default-content:has(.full) {
    max-width: 100%;
}

.name {
    font-size: 10vw;
}

.center {
   text-align: center; 
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>