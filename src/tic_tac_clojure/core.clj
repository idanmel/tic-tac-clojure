(ns tic-tac-clojure.core
  (:require [clojure.string :as str]))


(defn split [regex txt]
  (str/split txt regex))

(defn parse-input [txt]
  (try
    (->> txt
         str/trim
         (split #"\s+")
         (mapv #(Integer/parseInt %)))
    (catch Exception e :number-format-exception)))


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


(defn turn [state move]
  "Given a state and the next move, returns the next state"
  (let [{:keys [moves board]} state]
    (cond
      (empty? move) (assoc state :status :empty-move)
      (valid-move? state move) (assoc state :moves (conj moves move)
                                    :board (assoc-in board move (player-symbol state)))
      (already-occupied? state move) (assoc state :status :already-occupied)
      (out-of-bounds? state move) (assoc state :status :out-of-bounds))))

(defn game-ended? [state]
  (let [{:keys [moves]} state]
    (if (= 3 (count moves))
      true
      false)))

;(defn -main
;  []
;  (loop [state init-state]
;    (println state)
;    (if (game-ended? state)
;      (println "game ended")
;      (recur (turn state (move-it state))))) )
;