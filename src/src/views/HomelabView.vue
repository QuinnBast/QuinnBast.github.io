<template>
  <div class="homelab-page">
    <h1 class="page-title">Homelab</h1>
    <div class="hub-container gradient">
      <!-- Public Section -->
      <section class="hub-section">
        <h2 class="section-header">Public Links</h2>
        <div class="hub-grid">
          <a href="https://pihole.quinnbast.ca/admin" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>PiHole</span>
          </a>
          <a href="https://calibre.quinnbast.ca" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Calibre</span>
          </a>
          <a href="https://reader.quinnbast.ca" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Readarr</span>
          </a>
        </div>
      </section>


      <!-- Internal Links -->
      <section class="hub-section">
        <h2 class="section-header">Internal Links</h2>

        <!-- Info Box -->
        <div class="info-box blue-info">
          <p>
            ℹ️ <strong>Note:</strong> These links are accessible only from my <strong>local network</strong>.
            If you're outside my network, none of these will work.
          </p>
        </div>

        <div class="hub-grid">
          <a href="http://172.16.1.1:4073/admin/login.php" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>PiHole</span>
          </a>
          <a href="http://172.16.1.223:8096/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Jellyfin</span>
          </a>
          <a href="http://172.16.1.223:5056/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Jellyseer</span>
          </a>
          <a href="http://172.16.1.223:7878/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Radarr</span>
          </a>
          <a href="http://172.16.1.223:8989/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Sonarr</span>
          </a>
          <a href="http://172.16.1.223:9696/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Prowlarr</span>
          </a>
          <a href="http://172.16.1.223:8777/accounts/login/?next=/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Paperless-ngx</span>
          </a>
          <a href="http://172.16.1.223:9090/query" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Prometheus</span>
          </a>
          <a href="http://172.16.1.223:3340/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Grafana</span>
          </a>
          <a href="http://172.16.1.223:9705/#home" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Huntarr</span>
          </a>
          <a href="https://172.16.1.223:8181/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Calibre</span>
          </a>
          <a href="http://172.16.1.223:8083/" target="_blank" rel="noopener noreferrer" class="hub-box">
            <span>Calibre-Web</span>
          </a>
        </div>
      </section>
    </div>
  </div>
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
  grid-template-columns: repeat(3, 1fr); /* Two evenly sized columns */
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
/* Info Box */
.info-box {
  padding: 15px 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  font-size: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

/* Blue Themed Info Box Style */
.blue-info {
  background-color: #134a71; /* Light blue background */
  color: #bcd1f1; /* Dark blue text */
  border: 1px solid #0f4167; /* Subtle blue border */
}

.blue-info strong {
  color: #5c94e8; /* Strong text color (dark blue) */
}

.blue-info p {
  margin: 0;
  line-height: 1.5;
}

/* Links Section */
.link-container {
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  justify-content: center;
  align-items: center;
}

.link-box {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  padding: 20px;
  background-color: var(--card-background); /* Matches theme for boxes */
  color: var(--text-color-primary); /* Text color according to theme */
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s; /* Animations for hover */
}

.link-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.4);
}

.link-box h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.link-box p {
  font-size: 1rem;
  color: var(--text-color-secondary); /* Subtle text color for content */
}
</style>