(ns tic-tac-clojure.api
  (:require [tic-tac-clojure.impl.utils :as utils]))


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
      (= (count moves) (utils/board-count board)) (assoc state :status :too-many-moves)
      (utils/valid-move? state move) (utils/place-on-board state move)
      (utils/already-occupied? state move) (assoc state :status :already-occupied)
      (utils/out-of-bounds? state move) (assoc state :status :out-of-bounds))))
