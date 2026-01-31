<template>
  <div class="gradient">
    <div class="hub-container">
      <!-- Section: JSON Validator and Formatter -->
      <div class="hub-section">
        <h2 class="section-header">JSON Validator & Formatter</h2>

        <!-- Input JSON Field -->
        <div class="input-container">
          <label for="json-input" class="input-label">JSON Input:</label>
          <textarea
              id="json-input"
              v-model="jsonInput"
              placeholder="Enter your JSON string here..."
              rows="10"
              class="input-field"
          ></textarea>
        </div>

        <!-- Validation Message -->
        <p v-if="validationMessage" class="validation-text" :class="{ error: !isValidJson, success: isValidJson }">
          {{ validationMessage }}
        </p>

        <!-- Action Buttons -->
        <div class="button-container">
          <button class="action-button" @click="validateJson">Validate JSON</button>
          <button class="action-button" @click="prettifyJson" :disabled="!isValidJson">Prettify JSON</button>
          <button class="action-button" @click="minifyJson" :disabled="!isValidJson">Minify JSON</button>
        </div>

        <!-- Single Result Section -->
        <div v-if="resultJson" class="output-container">
          <h3 class="output-header">Result:</h3>
          <textarea
              class="output-text"
              v-model="resultJson"
              rows="10"
              readonly
          ></textarea>
          <!-- Copy to Clipboard -->
          <button class="copy-button" @click="copyToClipboard">
            Copy to Clipboard
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      jsonInput: "", // The user's JSON input
      isValidJson: false, // Whether the JSON input is valid
      validationMessage: "", // Message to show validation status
      resultJson: "", // Result after prettifying or minifying
    };
  },
  methods: {
    // Validate the JSON input
    validateJson() {
      try {
        JSON.parse(this.jsonInput); // Try parsing the JSON input
        this.isValidJson = true;
        this.validationMessage = "JSON input is valid!";
      } catch (error) {
        this.isValidJson = false;
        this.validationMessage = "Invalid JSON: " + error.message;
        this.resultJson = ""; // Clear the result if input is invalid
      }
    },
    // Prettify JSON with 2-space indentation
    prettifyJson() {
      if (this.isValidJson) {
        const parsedJson = JSON.parse(this.jsonInput);
        this.resultJson = JSON.stringify(parsedJson, null, 2); // Add indentation
      }
    },
    // Minify JSON (Remove all unnecessary spaces)
    minifyJson() {
      if (this.isValidJson) {
        const parsedJson = JSON.parse(this.jsonInput);
        this.resultJson = JSON.stringify(parsedJson); // Compact the JSON
      }
    },
    // Copy the result to the clipboard
    copyToClipboard() {
      navigator.clipboard.writeText(this.resultJson)
          .then(() => {
            alert("Result copied to clipboard!");
          })
          .catch((error) => {
            console.error("Failed to copy text: ", error);
            alert("Failed to copy text. Please try again.");
          });
    },
  },
};
</script>

<style scoped>
/* Full-page gradient background */
.gradient {
  background: linear-gradient(145deg, #1c1c1c, #0f0f0f);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  color: #e5e5e5;
  font-family: Arial, sans-serif;
}

/* Hub container */
.hub-container {
  width: 100%;
  max-width: 800px;
  padding: 2rem;
  background: #2b2b2b;
  border-radius: 8px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.5);
}

/* Section headers */
.section-header {
  color: #f5f5f5;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  border-bottom: 2px solid #3a3a3a;
  padding-bottom: 0.5rem;
  text-align: center;
}

/* Input container and labels */
.input-container {
  margin-bottom: 1.5rem;
}

.input-label {
  display: block;
  font-size: 1rem;
  color: #cfcfcf;
  margin-bottom: 0.5rem;
}

.input-field {
  width: 100%;
  padding: 0.8rem;
  margin-bottom: 1rem;
  border: 1px solid #555;
  border-radius: 5px;
  background: #1e1e1e;
  color: #f5f5f5;
  font-size: 1rem;
  resize: none;
  box-sizing: border-box;
}

/* Validation Messages */
.validation-text {
  margin-bottom: 1rem;
  font-size: 1rem;
  padding: 0.5rem;
  border-radius: 5px;
}

.validation-text.success {
  background-color: #1d4d32;
  color: #a7f3d0;
}

.validation-text.error {
  background-color: #4d1d1d;
  color: #f3a7a7;
}

/* Action buttons */
.button-container {
  display: flex;
  gap: 1rem;
}

.action-button {
  flex: 1;
  padding: 0.8rem 1rem;
  background: #444;
  border: none;
  border-radius: 5px;
  color: #fff;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  text-align: center;
  transition: background 0.3s ease;
}

.action-button:hover {
  background: #555;
}

.action-button:disabled {
  background: #333;
  cursor: not-allowed;
}

/* Output container */
.output-container {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #222;
  border-radius: 5px;
}

/* Output header */
.output-header {
  color: #f5f5f5;
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}

/* Single Result Text Area */
.output-text {
  width: 100%;
  height: 150px;
  background: #1e1e1e;
  border: 1px solid #555;
  border-radius: 5px;
  color: #e5e5e5;
  font-size: 1rem;
  resize: none;
  padding: 0.8rem;
  box-sizing: border-box;
  overflow: auto;
}

/* Copy button */
.copy-button {
  display: block;
  margin-top: 1rem;
  width: 100%;
  padding: 0.5rem 1rem;
  background: #444;
  border: none;
  border-radius: 5px;
  color: #fff;
  font-size: 0.9rem;
  font-weight: bold;
  cursor: pointer;
  text-align: center;
  transition: background 0.3s ease;
}

.copy-button:hover {
  background: #555;
}
</style>