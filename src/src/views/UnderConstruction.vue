<template>
  <div class="under-construction">
    <div class="construction-wrapper">
      <h1>🚧 Under Construction 🚧</h1>
      <p>I have plans for this page, but they're not developed yet. Stay tuned!</p>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "UnderConstruction",
};
</script>

<style scoped>
.under-construction {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #121212; /* Dark background */
  color: #e0e0e0; /* Light text for contrast */
  font-family: "Helvetica Neue", Arial, sans-serif;
}

.construction-wrapper {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background-color: #1e1e1e; /* Dark card color */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.6);
}

.construction-wrapper h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #ff6b6b; /* Highlight color */
}

.construction-wrapper p {
  font-size: 1rem;
  color: #b0bec5; /* Subtle contrast color */
}
</style>