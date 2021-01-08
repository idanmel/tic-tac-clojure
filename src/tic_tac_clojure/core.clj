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
  "It's a valid move if there's an empty space there"
  (let [{:keys [board]} state]
    (= -1 (get-in board move))))


(defn won? [board cells]
  "One of these spaces is known to be occupied.
  There's a winner if all the cells are occupied
  by the same player"
  (= 1 (->> cells                                           ; ['(0 0) '(1 1) '(2 2)]
            (map #(get-in board %))                         ; [\X \X \X]
            set                                             ; #{\X}
            count)))                                        ; 1


(defn get-winning-coordinates [board [row-number col-number]]
  "Return the winning coordinates"
  (let [row (map #(conj [row-number] %) (range 3))
        col (map #(conj '() col-number %) (range 3))
        diagonal ['(0 0) '(1 1) '(2 2)]
        reverse-diagonal ['(0 2) '(1 1) '(2 0)]]
    (cond
      (won? board row) row
      (won? board col) col
      (won? board diagonal) diagonal
      (won? board reverse-diagonal) reverse-diagonal
      :else nil)))


(defn win-status [board coordinates]
  "Return the winning status"
  (cond
    (= \X (get-in board (first coordinates))) :x-won
    (= \O (get-in board (first coordinates))) :o-won
    :else nil))


(defn board-count [board]
  "Count the number of cells in the board"
  (reduce + (map count board)))


(defn draw-status [board moves]
  "Count the number of moves made and equal it to the number
  of places on the board"
  (if (= (inc (count moves)) (board-count board))
    :draw
    nil))


(defn place-on-board [state move]
  "Place the move on the board and return the appropriate status"
  (let [{:keys [board moves]} state
        new-board (assoc-in board move (player-symbol state))
        winning-coordinates (get-winning-coordinates new-board move)]
    (assoc state :moves (conj moves move)
                 :board new-board
                 :winning-coordinates winning-coordinates
                 :status (or (win-status new-board winning-coordinates)
                             (draw-status board moves)
                             :ok))))

(def init-state
  "An example for how the initial board state should look like"
  {:players "XO"
   :moves []
   :board [[-1 -1 -1]
           [-1 -1 -1]
           [-1 -1 -1]]
   :status :ok
   :winning-coordinates nil})


(defn turn [state move]
  "This is the only function that should be used from outside this namespace...
  Given a state and the next move, returns the next state"
  (let [{:keys [board moves]} state]
    (cond
      (empty? move) (assoc state :status :empty-move)
      (= (count moves) (board-count board)) (assoc state :status :too-many-moves)
      (valid-move? state move) (place-on-board state move)
      (already-occupied? state move) (assoc state :status :already-occupied)
      (out-of-bounds? state move) (assoc state :status :out-of-bounds))))
