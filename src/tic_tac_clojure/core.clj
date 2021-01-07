(ns tic-tac-clojure.core)

(defn player-symbol [state]
  "Returns the player symbol to be placed on the board"
  (let [{:keys [players moves]} state]
    (->> players
         cycle
         (take (inc (count moves)))
         last)))


(defn already-occupied? [state move]
  "Returns true when trying to play a cell that was already played before"
  (let [{:keys [board]} state]
    (char? (get-in board move))))


(defn out-of-bounds? [state move]
  "Returns true when trying to move out of bounds"
  (nil? (get-in state move)))


(defn valid-move? [state move]
  (let [{:keys [board]} state]
    (= -1 (get-in board move))))


(defn place-on-board [state move]
  (let [{:keys [board moves]} state]
    (assoc state :moves (conj moves move)
                 :board (assoc-in board move (player-symbol state)))))


(defn turn [state move]
  "Given a state and the next move, returns the next state"
  (cond
    (empty? move) (assoc state :status :empty-move)
    (valid-move? state move) (place-on-board state move)
    (already-occupied? state move) (assoc state :status :already-occupied)
    (out-of-bounds? state move) (assoc state :status :out-of-bounds)))
