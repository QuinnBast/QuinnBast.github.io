<template>
  <div class="flip7-scoring dark-theme">
    <h1>Flip7 Scoring Tool</h1>

    <!-- Add Player Section -->
    <div class="add-player">
      <input
          v-model="newPlayerName"
          placeholder="Enter player name"
          @keyup.enter="addPlayer"
          class="input-field"
      />
      <button @click="addPlayer" class="minimal-button">Add Player</button>
    </div>

    <!-- Player List -->
    <div class="player-list">
      <div v-for="(player, index) in players" :key="index" class="player-row">
        <!-- Player Info -->
        <div class="player-info">
          <h2>{{ player.name }}</h2>
          <div class="player-stats">
            <span>Round: <strong>{{ player.round }}</strong></span>
            <span>Busts: <strong>{{ player.busts }}</strong></span>
          </div>
        </div>

        <!-- Player Score -->
        <div class="player-score">
          <strong>{{ player.score }}</strong>
        </div>

        <!-- Player Actions -->
        <div class="player-actions">
          <button @click="openScoreModal(index)" class="minimal-button action-button">Score</button>
          <button @click="bustPlayer(index)" class="minimal-button action-button bust-button">Bust</button>
        </div>
      </div>
    </div>


    <!-- Reset Game Button -->
    <div class="game-controls player-actions">
      <button @click="clearScores" class="minimal-button reset-button">Clear Scores</button>
      <button @click="clearPlayers" class="minimal-button reset-button">Clear Players</button>
    </div>

    <!-- Dim Background -->
    <div class="dim-background" v-if="showScoreModal"></div>

    <!-- Score Popup -->
    <div class="score-popup" v-if="showScoreModal">
      <div class="popup-content">
        <h3>Select Scores for {{ players[currentPlayerIndex]?.name }}</h3>

        <!-- Score Buttons -->
        <div class="score-row">
          <button
              v-for="score in [1, 2, 3, 4, 5, 6]"
              :key="'low-' + score"
              @click="toggleScoreSelection(score)"
              :class="{'selected': selectedScores.includes(score)}"
              class="score-button"
          >
            {{ score }}
          </button>
        </div>
        <div class="score-row">
          <button
              v-for="score in [7, 8, 9, 10, 11, 12]"
              :key="'mid-' + score"
              @click="toggleScoreSelection(score)"
              :class="{'selected': selectedScores.includes(score)}"
              class="score-button"
          >
            {{ score }}
          </button>
        </div>
        <div class="score-row">
          <button
              v-for="modifier in ['x2', '+2', '+4', '+6', '+8', '+10']"
              :key="'modifier-' + modifier"
              @click="toggleScoreSelection(modifier)"
              :class="{'selected': selectedScores.includes(modifier)}"
              class="score-button"
          >
            {{ modifier }}
          </button>
        </div>

        <!-- Confirmation Buttons -->
        <div class="popup-actions">
          <button @click="closeScoreModal" class="minimal-button cancel-button">Cancel</button>

          <div class="bonus-dialog" v-if="hasFlip7()">
            <h3>🎉 Flip 7 Bonus!</h3>
            <p><strong>Score:</strong> {{ getCurrentScore() }}</p>
          </div>
          <!-- Flip 7 Bonus Dialog -->
          <div class="bonus-dialog" v-else>
            <p><strong>Score:</strong> {{ getCurrentScore() }}</p>
          </div>

          <button @click="confirmScore" class="minimal-button confirm-button">Confirm</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // List of players
      players: [],
      // Name for the new player
      newPlayerName: "",
      // State for the score popup modal
      showScoreModal: false,
      showBonusDialog: false,
      currentPlayerIndex: null, // Index of the player being scored
      selectedScores: [], // Array of selected scores/modifiers
      // Available score options
      scoreOptions: [
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, "x2", "+2", "+4", "+6", "+8", "+10"
      ],
    };
  },
  methods: {
    // Add a new player
    addPlayer() {
      if (this.newPlayerName.trim() === "") return;
      this.players.push({
        name: this.newPlayerName,
        score: 0,
        round: 1,
        busts: 0,
      });
      this.newPlayerName = "";
    },
    // Bust a player: increase round and bust count, but don't reset the score
    bustPlayer(index) {
      const player = this.players[index];
      player.round += 1;
      player.busts += 1;
    },
    // Open the score popup modal
    openScoreModal(index) {
      this.currentPlayerIndex = index;
      this.showScoreModal = true;
    },
    // Close the score popup modal
    closeScoreModal() {
      this.showScoreModal = false;
      this.selectedScores = []; // Reset selection on close
    },
    // Toggle the selection of a score or modifier
    toggleScoreSelection(score) {
      const index = this.selectedScores.indexOf(score);
      if (index === -1) {
        this.selectedScores.push(score); // Add to selection
      } else {
        this.selectedScores.splice(index, 1); // Remove from selection
      }
    },
    // Confirm scores for the selected player
    confirmScore() {
      const player = this.players[this.currentPlayerIndex];

      player.score += this.getCurrentScore();
      player.round += 1;

      this.closeScoreModal(); // Close the modal after scoring
    },
    getCurrentScore() {
      // Handle each selected score/modifier
      let scoredCards = [];
      let multiplier = 1;
      let bonusScore = 0;
      let flip7Bonus = 0;

      this.selectedScores.forEach(score => {
        if (score === "x2") {
          multiplier = 2;
        } else if (typeof score === "string" && score.startsWith("+")) {
          const bonus = parseInt(score.replace("+", ""));
          bonusScore += bonus;
        } else {
          scoredCards.push(score);
        }
      });

      if(scoredCards.length === 7) {
        flip7Bonus = 15;
      }

      const scoredCardsSum = scoredCards.reduce((acc, card) => acc + card, 0);
      return (scoredCardsSum * multiplier) + bonusScore + flip7Bonus;
    },
    hasFlip7() {
      const numberCardCount = this.selectedScores.filter(score => typeof score === "number").length;
      return numberCardCount === 7;
    },
    clearScores() {
      this.players.forEach(player => {
        player.score = 0;
        player.round = 1;
        player.busts = 0;
      });
    },
    clearPlayers() {
      this.players = [];
    },
  },
};
</script>

<style scoped>
/* General Dark Theme */
.flip7-scoring {
  font-family: 'Arial', sans-serif;
  max-width: 750px;
  margin: 20px auto;
  text-align: center;
  padding: 20px;
  background: #121212;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.5);
  color: #eaeaea;
}

h1 {
  font-size: 20px;
  margin-bottom: 15px;
}

/* Add Player Section */
.add-player {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.add-player .input-field {
  flex: 1;
  margin-right: 10px;
  padding: 8px;
  font-size: 14px;
  border: none;
  background-color: #1e1e1e;
  color: #ffffff;
  border-radius: 5px;
}

/* Player List */
.player-list {
  margin-top: 10px;
}

.player-row {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: #1e1e1e;
  border-radius: 5px;
  margin-bottom: 8px;
}

.player-info h2 {
  font-size: 16px;
}

.player-stats {
  margin-top: 5px;
  font-size: 12px;
  display: flex;
  gap: 10px;
}

.player-actions {
  display: flex;
  gap: 10px;
}

/* Dim Background */
.dim-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1;
}

/* Score Popup */
.score-popup {
  position: fixed;
  bottom: 20%;
  left: 50%;
  transform: translateX(-50%);
  background: #1e1e1e;
  max-width: 400px;
  width: 90%;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.6);
  z-index: 2;
}

.popup-content h3 {
  margin-bottom: 15px;
  font-size: 16px;
}

/* Score Buttons */
.score-row {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.score-button {
  flex: 1;
  height: 50px; /* Taller buttons */
  margin: 2px;
  font-size: 14px;
  border: 1px solid #333;
  background-color: #121212;
  color: #fff;
  border-radius: 5px;
  transition: 0.2s ease;
  cursor: pointer;
}

.score-button:hover, .score-button.selected {
  background-color: #007bff;
  color: white;
}

.score-button.selected {
  border: 2px solid white;
}

/* Confirmation Buttons */
.popup-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.minimal-button {
  padding: 10px 20px;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.minimal-button:hover {
  background-color: #0056b3;
}

.confirm-button {
  background-color: #007bff;
}

.cancel-button {
  background-color: #555;
}

/* Flip 7 Bonus Dialog */
.bonus-dialog {
  background: #1e1e1e;
  color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
  z-index: 10;
}

.bonus-dialog h3 {
  font-size: 20px;
  margin-bottom: 10px;
}

.bonus-dialog p {
  font-size: 16px;
  margin: 5px 0;
}

.game-controls {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.reset-button {
  background-color: #a03535;
  color: #fff;
  font-size: 16px;
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.reset-button:hover {
  background-color: #dc3545;
}
.bust-button {
  background-color: #a03535;
}

.bust-button:hover {
  background-color: #dc3545;
}
.action-button {
  font-size: 14px;
  height: 50px;
  background: #333;
  color: #fff;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.action-button:hover {
  background-color: #0056b3;
}
/* Actions Column */
.player-actions {
  display: flex;
  gap: 10px;
}
/* Player Score Column */
.player-score {
  font-size: 24px; /* Bigger, prominent score */
  font-weight: bold;
  text-align: center;
  background: #1e1e1e;
  color: #ffd700; /* Gold for emphasis */
  border: 2px solid #404040; /* Border to match the button style */
  border-radius: 5px;
  height: 50px; /* Match "Score"/"Bust" buttons */
  width: 50px; /* Square for symmetry */
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>