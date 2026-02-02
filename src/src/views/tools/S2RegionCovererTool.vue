<template>
  <div id="app">
    <!-- Map Container -->
    <div id="map" ref="map"></div>

    <!-- Controls -->
    <div class="controls">
      <h3>S2 Region Coverer</h3>

      <!-- Precision Selector -->
      <label>Precision (S2 Cell Level): {{ precision }}</label>
      <input type="range" v-model.number="precision" min="1" max="30" />

      <!-- Max Cells Selector -->
      <label>Maximum Covering Cells: {{ maxCells }}</label>
      <input type="range" v-model.number="maxCells" min="1" max="400" /><!-- Max Cells Selector -->

      <!-- Cover only inside or not -->
      <label>
        <input type="checkbox" v-model="coverInterior" />
        Only Cover Interior
      </label>

      <!-- Action Buttons -->
      <br/>
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

<script lang="ts">
// @ts-nocheck
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
      maxCells: 12,
      coverInterior: false,
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
      const regionCoverer = new s2.RegionCoverer({ maxLevel: this.precision, maxCells: this.maxCells });
      const s2Points = coordinates.map(([lng, lat]) => {
        const latLng = new s2.LatLng.fromDegrees(lat, lng);
        return new s2.Point.fromLatLng(latLng);
      });

      const loop = new s2.Loop(s2Points);
      loop.normalize();
      const polygon = new s2.Polygon([loop]);

      let coveringCells = [];
      if(this.coverInterior) {
        coveringCells = regionCoverer.interiorCovering(polygon);
      } else {
        coveringCells = regionCoverer.covering(polygon);
      }

      return coveringCells.map((cell) => {
        return "" + s2.cellid.toToken(cell);
      });
    },
    radToDegrees(value) {
      return value * (180 / Math.PI);
    },

    /**
     * Visualize S2 cells on the map as polygons
     * @param {Array} s2CellTokens - List of S2 cell tokens
     */
    addS2CellsToMap(s2CellTokens) {
      s2CellTokens.forEach((token) => {
        const s2id = s2.cellid.fromToken(token);
        const s2Cell = new s2.Cell.fromCellID(s2id);

        const corners = [
          [s2Cell.latitude(0, 0), s2Cell.longitude(0, 0)],
          [s2Cell.latitude(1, 0), s2Cell.longitude(1, 0)],
          [s2Cell.latitude(1, 1), s2Cell.longitude(1, 1)],
          [s2Cell.latitude(0, 1), s2Cell.longitude(0, 1)],
        ];

        const cornersInDegrees = corners.map((corner) => {
          return [
            this.radToDegrees(corner[0]),
            this.radToDegrees(corner[1]),
          ]
        })

        const polygon = L.polygon(cornersInDegrees, {
          color: "blue",
          weight: 1,
        }).addTo(this.map);

        polygon.on('click', () => {
          const centerLatLng = s2.LatLng.fromPoint(s2Cell.center());
          polygon.bindPopup(`
          <div>
            <b>Token:</b> ${s2.cellid.toToken(s2Cell.id)}<br/>
            <b>Cell ID:</b> ${s2Cell.id}<br/>
            <b>Level:</b> ${s2Cell.level}<br/>
            <b>Center (Lat, Lng):</b> ${this.radToDegrees(centerLatLng.lat).toFixed(6)}, ${this.radToDegrees(centerLatLng.lng).toFixed(6)}<br/>
          </div>
          `).openPopup();
        })

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
</style>