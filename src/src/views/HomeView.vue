<script setup lang="ts">
// @ts-nocheck
import { ref } from "vue";

const greeting = ref("Hello!")

function randomizeGreeting() {
  var greetingOptions = [
    "Hello!", "Welcome.", "Bienvenu,", "Howdy!", "Hi there!", "👋", "Hey,", "✌️", "🤙"
  ];
  var optionsToSelect = greetingOptions.filter((option) => option !== greeting.value);
  greeting.value = optionsToSelect[Math.floor(Math.random()*optionsToSelect.length)];
}

setInterval(randomizeGreeting, 4000);
</script>

<template>
  <main class="hub-container gradient">
    <!-- Personal Section -->
    <section class="hub-section">
      <h2 class="section-header">Personal</h2>
      <div class="hub-grid">
        <RouterLink to="/about" class="hub-box">
          <i class="pi pi-info-circle hub-icon"></i>
          <span>About Me</span>
        </RouterLink>
        <RouterLink to="/blog" class="hub-box">
          <i class="pi pi-book hub-icon"></i>
          <span>Blog Posts</span>
        </RouterLink>
      </div>
    </section>

    <!-- Tools Section -->
    <section class="hub-section">
      <h2 class="section-header">Tools</h2>
      <div class="hub-grid">
        <RouterLink to="/flip7" class="hub-box">
          <span>Flip 7 Scoring Tool</span>
        </RouterLink>
        <RouterLink to="/string-replacer" class="hub-box">
          <span>String Replacer</span>
        </RouterLink>
        <RouterLink to="/json-formatter" class="hub-box">
          <span>JSON Formatter</span>
        </RouterLink>
        <RouterLink to="/s2-region-coverer" class="hub-box">
          <span>S2 Region Coverer</span>
        </RouterLink>
        <RouterLink to="/h3-region-coverer" class="hub-box">
          <span>H3 Region Coverer</span>
        </RouterLink>
      </div>
    </section>

    <!-- Technical Section -->
    <section class="hub-section">
      <h2 class="section-header">Technical</h2>
      <div class="hub-grid">
        <RouterLink to="/homelab" class="hub-box">
          <span>Homelab</span>
        </RouterLink>
        <RouterLink to="/training" class="hub-box">
          <span>Training Material</span>
        </RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
/* Full-page gradient background */
.gradient {
  background: linear-gradient(145deg, #1c1c1c, #0f0f0f); /* Dark gray-to-black gradient */
  min-height: 100vh;
  padding: 2rem; /* Maintain breathing room around edges */
  display: flex;
  justify-content: center;
  align-items: center;
  color: #e5e5e5; /* Soft light-gray text for readability */
}

/* Full-width hub container */
.hub-container {
  display: flex;
  flex-direction: column; /* Stack all sections vertically */
  justify-content: space-between; /* Distribute sections evenly */
  width: 100%; /* Take up full width of the available screen */
  max-width: 1400px; /* Limit maximum width */
  padding: 0 1rem; /* Add spacing around the edges */
  box-sizing: border-box; /* Ensure padding doesn’t affect the container size */
}

/* Ensure proper spacing for each section */
.hub-section {
  display: flex;
  flex-direction: column; /* Stack headers and grids vertically */
  text-align: center;
  margin-bottom: 4rem; /* Add space between sections */
}

/* Section header styling */
.section-header {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  color: #d1d1d1; /* Neutral light-gray headers */
  border-bottom: 2px solid #3a3a3a; /* Subtle gray divider for sections */
  padding-bottom: 0.5rem;
}

/* Grid layout for displaying wide boxes */
.hub-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* Two evenly sized columns */
  gap: 2rem; /* Space out items */
  justify-content: center; /* Center grid content */
}

/* Styling for individual boxes */
.hub-box {
  background: #2b2b2b; /* Dark gray background for boxes */
  border-radius: 12px; /* Rounded corners */
  height: 200px; /* Consistent height */
  flex-grow: 1; /* Allow the boxes to grow within the grid */
  display: flex;
  flex-direction: column; /* Stack box content vertically */
  align-items: center; /* Center-align box content */
  justify-content: center;
  text-align: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: #e0e0e0; /* Soft light-gray text */
  text-decoration: none; /* Remove underlines for links */
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.4); /* Soft black shadow for depth */
  width: 45%; /* Make each box take 45% of the grid's width */
  min-width: 250px; /* Prevent shrinking below 250px */
}

/* Add space between the icon and label */
.hub-icon {
  font-size: 3rem; /* Larger icon size for visibility */
  margin-bottom: 1rem; /* Add extra spacing between icon and label */
}

/* Hover effect for buttons */
.hub-box:hover {
  transform: translateY(-5px); /* Subtle lift effect */
  box-shadow: 0px 6px 10px rgba(0, 0, 0, 0.6); /* Darker shadow on hover for depth */
  background: #3a3a3a; /* Slightly lighter gray on hover */
}

/* Mobile responsiveness: Stack boxes in one column */
@media (max-width: 768px) {
  .hub-grid {
    grid-template-columns: 1fr; /* Switch to single column layout */
  }

  .hub-box {
    width: 100%; /* Extend box width to fit the screen */
    height: 180px; /* Adjust height for smaller screens */
  }
}
</style>