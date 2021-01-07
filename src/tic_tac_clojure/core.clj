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


(defn won? [board cells]
  (= 1 (count (set (map #(get-in board %) cells)))))

(map #(get-in [[-1 -1 \X] [-1 -1 -1] [-1 -1 -1]] %) ['(0 4) '(1 2) '(2 2)])

(defn get-winning-coordinates [board [row-number col-number]]
  (let [row (map #(conj [row-number] %) (range 3))
        col (map #(conj '() col-number %) (range 3))]
    (cond
      (won? board row) row
      (won? board col) col)))

(defn win-status [board coordinates]
  (cond
    (= \X (get-in board (first coordinates))) :x-won
    (= \O (get-in board (first coordinates))) :o-won
    :else nil))

(defn draw-status [board moves]
  (= (inc (count moves)) (reduce + (map count board))))

(defn place-on-board [state move]
  (let [{:keys [board moves]} state
        new-board (assoc-in board move (player-symbol state))
        winning-coordinates (get-winning-coordinates new-board move)]
    (assoc state :moves (conj moves move)
                 :board new-board
                 :winning-coordinates winning-coordinates
                 :status (or (win-status new-board winning-coordinates) (draw-status board moves) :ok))))


(defn turn [state move]
  "Given a state and the next move, returns the next state"
  (cond
    (empty? move) (assoc state :status :empty-move)
    (valid-move? state move) (place-on-board state move)
    (already-occupied? state move) (assoc state :status :already-occupied)
    (out-of-bounds? state move) (assoc state :status :out-of-bounds)))
