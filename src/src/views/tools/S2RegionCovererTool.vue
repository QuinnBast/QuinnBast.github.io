<template>
  <div id="app">
    <!-- Map Container -->
    <div id="map" ref="map"></div>

    <!-- Controls -->
    <div class="controls">
      <h3>S2 Region Coverer</h3>

      <!-- Precision Selector -->
      <label>Precision (S2 Cell Level):</label>
      <input type="range" v-model.number="precision" min="1" max="15" />
      <span>Level {{ precision }}</span>

      <!-- Action Buttons -->
      <button @click="clearMap">Clear Map</button>

      <!-- S2 Cell Output -->
      <h4>Generated S2 Cells:</h4>
      <textarea v-model="s2CellList" readonly rows="10"></textarea>

      <!-- Input S2 Cells -->
      <h4>Input S2 Cell IDs:</h4>
      <textarea v-model="inputCells" placeholder="Enter S2 Cell IDs separated by commas"></textarea>
      <button @click="showInputCells">Show Input Cells on Map</button>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-draw/dist/leaflet.draw.js";
import "leaflet-draw/dist/leaflet.draw.css";
import { s2 } from "s2js"; // Importing s2js for S2 cell operations

export default {
  data() {
    return {
      map: null, // Leaflet map instance
      drawnItems: null, // Group to hold user-drawn shapes
      precision: 10, // S2 cell level
      s2CellList: "", // Text representing S2 cells output
      inputCells: "", // User-provided S2 cells
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
      this.map = L.map(this.$refs.map, {
        center: [0, 0], // Center globally
        zoom: 2, // Initial zoom level
      });

      // Add OpenStreetMap tile layer
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: 'Map data © <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
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
          polygon: true, // Enable polygons
          rectangle: true, // Enable rectangles
          circle: true, // Enable circles
          polyline: false, // Disable polylines
          marker: false, // Disable markers
          circlemarker: false, // Disable circle markers
        },
      });

      this.map.addControl(drawControl);

      // Event Listener: When a new shape is drawn
      this.map.on(L.Draw.Event.CREATED, (e) => {
        const layer = e.layer; // Get the drawn layer
        this.drawnItems.addLayer(layer); // Add to the group
        this.generateS2Cells(layer); // Generate and display S2 cells
      });
    },

    /**
     * Generate S2 cells for a given drawn shape
     */
    generateS2Cells(layer) {
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

      // Ensure we have valid coordinates
      if (!coordinates || coordinates.length === 0) return;

      // Compute S2 cells for the shape
      const coveringCells = this.getCoveringCells(coordinates);
      this.s2CellList = coveringCells.join(", "); // Display S2 tokens in the text area

      // Visualize the computed S2 cells on the map
      this.addS2CellsToMap(coveringCells);
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

    /**
     * Compute S2 cells for the region defined by coordinates
     * @param {Array} coordinates - Array of [lng, lat] pairs
     * @returns {Array} Tokens of computed S2 cells
     */
    getCoveringCells(coordinates) {
      // Convert coordinates to S2 cells at the desired level
      const s2Tokens = [];
      coordinates.forEach(([lng, lat]) => {
        const s2Cell = s2.cellid.fromLatLng({ lat, lng }, this.precision); // Create S2 cell
        s2Tokens.push(s2.cellid.toToken(s2Cell)); // Convert to human-readable token
      });
      return s2Tokens;
    },

    /**
     * Visualize S2 cells on the map as polygons
     * @param {Array} s2CellTokens - List of S2 cell tokens
     */
    addS2CellsToMap(s2CellTokens) {
      s2CellTokens.forEach((token) => {
        const s2Cell = s2.S2Cell.fromToken(token); // Decode token to S2 cell
        const corners = s2Cell.getCornerLatLngs(); // Get the polygon corners
        const latLngs = corners.map(([lat, lng]) => [lat, lng]); // Convert to Leaflet lat/lng array
        L.polygon(latLngs, {
          color: "blue",
          weight: 1,
        }).addTo(this.map); // Add polygon to map
      });
    },

    /**
     * Display user-provided S2 cells from token input
     */
    showInputCells() {
      if (!this.inputCells.trim()) return;
      const tokens = this.inputCells.split(",").map((t) => t.trim());
      this.addS2CellsToMap(tokens); // Visualize tokens on the map
    },

    /**
     * Clears all shapes and S2 overlays from the map
     */
    clearMap() {
      this.s2CellList = ""; // Clear the output
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
.controls {
  position: absolute;
  top: 10px;
  left: 10px;
  background: white;
  max-width: 300px;
  padding: 15px;
  border-radius: 10px;
  z-index: 1000;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}
</style>