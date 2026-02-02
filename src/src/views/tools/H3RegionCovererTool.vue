<template>
  <div id="app">
    <!-- Map Container -->
    <div id="map" ref="map"></div>

    <!-- Controls -->
    <div class="controls">
      <h3>H3 Region Coverer</h3>

      <!-- Precision Selector -->
      <label>Resolution: {{ resolution }}</label>
      <input onChange="checkToWarn" type="range" v-model.number="resolution" min="0" max="15" />

      <div v-if="resolution >= 5" class="warning">
        <p>&#9888;</p>
        <span>Warning: H3 resolutions above 4 or 5 are computationally expensive. Especially for large polygons.
          Consider reducing the resolution to avoid performance issues, or you risk your browser freezing.</span>
      </div>

      <!-- Action Buttons -->
      <br/>
      <button @click="clearMap">Clear Map</button>

      <!-- H3 Cell Output -->
      <h4>Generated H3 Cells:</h4>
      <textarea v-model="h3CellList" readonly rows="10"></textarea>

      <!-- Input H3 Cells -->
      <h4>Input H3 Cell IDs:</h4>
      <textarea v-model="inputCells" placeholder="Enter H3 Cell IDs separated by commas"></textarea>
      <button @click="showInputCells">Show Input Cells on Map</button>
    </div>
  </div>
</template>

<script lang="ts">
// @ts-nocheck
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-draw/dist/leaflet.draw.js";
import "leaflet-draw/dist/leaflet.draw.css";
import { polygonToCells, cellToVertexes, vertexToLatLng, getResolution } from "h3-js";

export default {
  data() {
    return {
      map: null,
      drawnItems: null,
      resolution: 1,
      h3CellList: "",
      inputCells: "",
    };
  },
  mounted() {
    this.initializeMap(); // Initialize the map on component mount
  },
  methods: {
    /**
     * Initializes the Leaflet map with drawing tools
     */
    initializeMap() {
      this.map = L.map('map', {
        center: [0, 0], // Center globally
        zoom: 2, // Initial zoom level
      });

      // Add OpenStreetMap tile layer
      // L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      //   attribution: 'Map data © <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
      // }).addTo(this.map);

      // L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      //   attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/">CARTO</a>',
      //   subdomains: 'abcd',
      //   maxZoom: 19
      // }).addTo(this.map);

      L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Dark_Gray_Base/MapServer/tile/{z}/{y}/{x}', {
        attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ',
        maxZoom: 16
      }).addTo(this.map);

      // Initialize a Group to store user-drawn items
      this.drawnItems = new L.FeatureGroup();
      this.map.addLayer(this.drawnItems);

      // Configure the drawing controls
      const drawControl = new L.Control.Draw({
        edit: {
          featureGroup: this.drawnItems,
        },
        draw: {
          polygon: true,
          rectangle: false,
          circle: false,
          polyline: false,
          marker: false,
          circlemarker: false,
        },
      });

      this.map.addControl(drawControl);

      // Event Listener: When a new shape is drawn
      this.map.on(L.Draw.Event.CREATED, (e) => {
        const layer = e.layer;
        this.drawnItems.addLayer(layer);
        this.generateH3Cells(layer);
      });
    },
    checkToWarn() {
      if (this.resolution > 5) {
        alert("Warning: H3 resolutions above 5 are computationally expensive. Consider reducing the resolution to avoid performance issues.")
      }
    },

    generateH3Cells(layer) {
      const geoJson = layer.toGeoJSON(); // Convert shape to GeoJSON
      let coordinates;

      // Handle different geometry types
      if (geoJson.geometry.type === "Polygon" || geoJson.geometry.type === "Rectangle") {
        coordinates = geoJson.geometry.coordinates[0]; // Outer polygon ring
      } else if (layer instanceof L.Circle) {
        coordinates = this.approximateCircleToPolygon(layer); // Approximate circle to a polygon
      } else {
        return; // Unsupported shape, exit
      }

      if (!coordinates || coordinates.length === 0) return;

      const coveringCells = this.getCoveringCells(coordinates);
      this.h3CellList = coveringCells.join(", ");
      this.addH3CellsToMap(coveringCells);
    },

    /**
     * Approximates a circle to a polygon by sampling points around the perimeter
     */
    approximateCircleToPolygon(layer) {
      const latLngs = [];
      const center = layer.getLatLng(); // Center of the circle
      const radius = layer.getRadius();

      for (let i = 0; i < 360; i += 10) { // 10-degree increments
        const angle = (i * Math.PI) / 180; // Convert to radians
        const point = L.latLng(
            center.lat + (radius * Math.cos(angle)) / 111320,
            center.lng + (radius * Math.sin(angle)) / (111320 * Math.cos((center.lat * Math.PI) / 180))
        );
        latLngs.push([point.lng, point.lat]); // lng, lat format
      }
      latLngs.push(latLngs[0]); // Close the polygon
      return latLngs;
    },

    getCoveringCells(coordinates) {
      const latLngs = coordinates.map(([lng, lat]) => {
        return [lat, lng]
      });

      return polygonToCells(latLngs, this.resolution)
    },
    radToDegrees(value) {
      return value * (180 / Math.PI);
    },

    addH3CellsToMap(h3CellTokens) {
      cellToVertexes
      h3CellTokens.forEach((h3index) => {
        const vertexIndices = cellToVertexes(h3index);
        const centerLatLng = vertexToLatLng(h3index)
        const vertexAsLatLngs = vertexIndices.map((vertex) => vertexToLatLng(vertex));

        const polygon = L.polygon(vertexAsLatLngs, {
          color: "blue",
          weight: 1,
        }).addTo(this.map);

        polygon.on('click', () => {
          polygon.bindPopup(`
          <div>
            <b>H3 Index:</b> ${h3index}<br/>
            <b>Resolution:</b> ${getResolution(h3index)}<br/>
            <b>Center (Lat, Lng):</b> ${centerLatLng[0].toFixed(6)}, ${centerLatLng[1].toFixed(6)}<br/>
          </div>
          `).openPopup();
        })

      });
    },

    showInputCells() {
      if (!this.inputCells.trim()) return;
      const tokens = this.inputCells.split(",").map((t) => t.trim());
      this.addH3CellsToMap(tokens); // Visualize tokens on the map
    },

    clearMap() {
      this.h3CellList = ""; // Clear the output
      this.inputCells = ""; // Clear the input box
      this.drawnItems.clearLayers(); // Remove user-drawn shapes
      this.map.eachLayer((layer) => {
        if (layer instanceof L.Polygon) this.map.removeLayer(layer); // Remove overlays
      });
    },
  },
};
</script>

<style>
/* Style for map and controls */
#map {
  height: 100vh; /* Full screen height */
}
/* Dark-themed styling for the control box */
.controls {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(20, 20, 20, 0.9); /* Darker background with slight transparency */
  border: 1px solid #333; /* Subtle dark border for separation */
  max-width: 300px;
  padding: 15px;
  border-radius: 10px;
  z-index: 1000;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.7); /* Subtle shadow effect */
  font-family: Arial, sans-serif; /* Clean sans-serif font */
  color: white; /* Ensure all text inside is white */
}

/* Ensure labels, headings, and main text contrast well */
.controls h3,
.controls h4,
.controls label {
  color: white; /* White for readability against dark background */
  margin: 10px 0;
  font-weight: bold;
  font-size: 14px;
}

/* Styled spans for better visibility */
.controls span {
  color: #00ffcc; /* Slightly standout text color for values (light cyan) */
  font-weight: bold; /* Highlights the current state/value */
  margin-left: 5px;
  font-size: 13px;
}

/* Style sliders/inputs for the dark theme */
.controls input[type="range"] {
  width: 100%;
  margin-bottom: 10px; /* Spacing after sliders */
  background: transparent; /* Matches the dark theme */
}

/* Textareas styled for a dark theme */
.controls textarea {
  width: 100%;
  resize: none;
  margin: 10px 0;
  padding: 8px;
  font-family: Arial, sans-serif;
  border: 1px solid #444; /* Darker border for better integration */
  border-radius: 5px;
  background: #222; /* Dark background */
  color: white; /* White text for readability */
  font-size: 13px; /* Consistent text size */
}

/* Style buttons for a dark and minimalistic theme */
.controls button {
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #007bff; /* Blue for contrast */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease; /* Smooth hover effect */
}

.controls button:hover {
  background-color: #0056b3; /* Darker blue for hover */
}

/* Style for the map container - ensure dark theme surroundings */
#map {
  height: 100vh; /* Full viewport height */
  background: #121212; /* Dark background beyond bounds */
}

.warning {
  display: flex;
  align-items: center;
  background-color: #fff3cd; /* Light yellow background */
  color: #856404; /* Dark brown text for readability on yellow */
  padding: 10px 15px;
  border: 1px solid #ffeeba; /* Slightly darker yellow border */
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  margin: 15px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
}

.warning p {
  font-size: 18px;
  margin-right: 10px;
  color: #856404; /* Same text color for consistency */
}

.warning span {
  flex: 1; /* Allow warning text to take up the rest of the space */
  color: #856404; /* Dark brown text for readability on yellow */
}

</style>