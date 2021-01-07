(ns tic-tac-clojure.core
  (:require [clojure.string :as str]))

(def init-state
  {:players "XO"
   :moves []
   :board [[-1 -1 -1]
           [-1 -1 -1]
           [-1 -1 -1]]})

(defn split [regex txt]
  (str/split txt regex))

(defn parse-input [txt]
  (try
    (->> txt
         str/trim
         (split #"\s+")
         (mapv #(Integer/parseInt %)))
    (catch Exception e :number-format-exception)))


(defn check-valid-move [state move]
  (let [{:keys [board]} state
        cell (get-in board move)]
    (case cell
      nil :out-of-range
      "X" :place-occupied
      "O" :place-occupied
      -1 move
      :something-went-wrong)))


(defn get-move [state input]
  (let [move (parse-input input)]
    (case move
      :number-format-exception :number-format-exception
      (check-valid-move state move))))


(defn player-symbol [state]
  "Returns the player symbol to be placed on the board"
  (let [{:keys [players moves]} state]
    (->> players
         cycle
         (take (inc (count moves)))
         last)))

(defn turn [state move]
  "Given a state and the next move, returns the next state"
  (let [{:keys [players moves board]} state]
    (assoc state :moves (conj moves move)
                 :board (assoc-in board move (player-symbol state)))))
    ;{:players players
    ; :moves   (conj moves move)
    ; :board   (assoc-in board move (player-symbol state))}))


(defn move-it [state]
  (let [input (read-line)
        move (get-move state input)]
    (if (coll? move)
      move
      (move-it state))))

(defn game-ended? [state]
  (let [{:keys [moves]} state]
    (if (= 3 (count moves))
      true
      false)))

(defn -main
  []
  (loop [state init-state]
    (println state)
    (if (game-ended? state)
      (println "game ended")
      (recur (turn state (move-it state))))) )
