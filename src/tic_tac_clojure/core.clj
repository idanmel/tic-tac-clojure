(ns tic-tac-clojure.core)

(defn player-symbol
  "Returns the player symbol to be placed on the board"
  [state]
  (let [{:keys [players moves]} state]
    (->> players
         cycle
         (take (inc (count moves)))
         last)))


(defn already-occupied?
  "Returns true when trying to play a cell that was already played before"
  [state move]
  (let [{:keys [board]} state]
    (char? (get-in board move))))


(defn out-of-bounds?
  "Returns true when trying to move out of bounds"
  [state move]
  (nil? (get-in state move)))


(defn valid-move?
  "It's a valid move if there's an empty space there"
  [state move]
  (let [{:keys [board]} state]
    (= -1 (get-in board move))))


(defn won?
  "One of these spaces is known to be occupied.
  There's a winner if all the cells are occupied
  by the same player"
  [board cells]
  (= 1 (->> cells                                           ; ['(0 0) '(1 1) '(2 2)]
            (map #(get-in board %))                         ; [\X \X \X]
            set                                             ; #{\X}
            count)))                                        ; 1


(defn get-winning-coordinates
  "Return the winning coordinates"
  [board [row-number col-number]]
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


(defn win-status
  "Return the winning status"
  [board coordinates]
  (cond
    (= \X (get-in board (first coordinates))) :x-won
    (= \O (get-in board (first coordinates))) :o-won
    :else nil))


(defn board-count
  "Count the number of cells in the board"
  [board]
  (reduce + (map count board)))


(defn draw-status
  "Count the number of moves made and equal it to the number
  of places on the board"
  [board moves]
  (when (= (inc (count moves)) (board-count board)) :draw))


(defn place-on-board
  "Place the move on the board and return the appropriate status"
  [state move]
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


(defn turn
  "This is the only function that should be used from outside this namespace...
  Given a state and the next move, returns the next state"
  [state move]
  (let [{:keys [board moves]} state]
    (cond
      (empty? move) (assoc state :status :empty-move)
      (= (count moves) (board-count board)) (assoc state :status :too-many-moves)
      (valid-move? state move) (place-on-board state move)
      (already-occupied? state move) (assoc state :status :already-occupied)
      (out-of-bounds? state move) (assoc state :status :out-of-bounds))))
