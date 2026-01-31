<template>
  <div class="gradient">
    <div class="hub-container">
      <!-- Section: String Replacement Tool -->
      <div class="hub-section">
        <h2 class="section-header">String Replacement Tool</h2>

        <!-- Input Fields -->
        <div class="input-container">
          <label for="input-text" class="input-label">Text to Process:</label>
          <textarea
              id="input-text"
              v-model="inputText"
              placeholder="Enter the text here..."
              rows="5"
              class="input-field"
          ></textarea>

          <label for="text-to-find" class="input-label">Text to Find:</label>
          <input
              id="text-to-find"
              v-model="textToFind"
              type="text"
              placeholder="Enter the text to find..."
              class="input-field"
          />

          <label for="replacement-text" class="input-label">Replacement Text:</label>
          <input
              id="replacement-text"
              v-model="replacementText"
              type="text"
              placeholder="Enter the replacement text..."
              class="input-field"
          />
        </div>

        <!-- Replace Button -->
        <button class="action-button" @click="replaceText">Replace</button>

        <!-- Output Result -->
        <div class="output-container">
          <h3 class="output-header">Result:</h3>
          <div class="output-scrollable">
            <p class="output-text" ref="outputText">{{ resultText }}</p>
          </div>

          <!-- Copy to Clipboard Button -->
          <button v-if="resultText" class="copy-button" @click="copyToClipboard">
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
      inputText: "", // Input text
      textToFind: "", // Text to find
      replacementText: "", // Replacement text
      resultText: "", // Text after replacement
    };
  },
  methods: {
    replaceText() {
      if (!this.textToFind) {
        alert("Please provide the text to find.");
        return;
      }

      // Replace all occurrences globally and case-sensitively
      const regex = new RegExp(this.textToFind, "g");
      this.resultText = this.inputText.replace(regex, this.replacementText);
    },
    copyToClipboard() {
      // Get the text to copy
      const textToCopy = this.resultText;

      // Use the Clipboard API to programmatically copy the text
      navigator.clipboard.writeText(textToCopy)
          .then(() => {
            alert("Result copied to clipboard!");
          })
          .catch((err) => {
            console.error("Failed to copy text: ", err);
            alert("Failed to copy text. Please copy it manually.");
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
  margin-bottom: 1.5rem;
  border: 1px solid #555;
  border-radius: 5px;
  background: #1e1e1e;
  color: #f5f5f5;
  font-size: 1rem;
  resize: none;
  box-sizing: border-box;
}

/* Action button */
.action-button {
  display: block;
  width: 100%;
  padding: 0.8rem 1rem;
  background: #444;
  border: none;
  border-radius: 5px;
  color: #fff;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  text-align: center;
  margin-bottom: 1rem;
  transition: background 0.3s ease;
}

.action-button:hover {
  background: #555;
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

/* Scrollable output text container */
.output-scrollable {
  max-height: 200px; /* Limit the height to make the content scrollable */
  overflow-y: auto; /* Allow vertical scrolling if the text exceeds the height */
  padding: 1rem;
  background: #1e1e1e;
  border-radius: 5px;
  box-shadow: inset 0px 2px 4px rgba(0, 0, 0, 0.5);
  margin-bottom: 1rem; /* Adds spacing between the text and the copy button */
}

/* Selectable output text */
.output-text {
  color: #e5e5e5;
  font-size: 1rem;
  white-space: pre-wrap; /* Preserve multiline formatting */
  user-select: text; /* Allow text selection */
  word-break: break-word; /* Handle long words gracefully */
}

/* Copy button */
.copy-button {
  display: block;
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