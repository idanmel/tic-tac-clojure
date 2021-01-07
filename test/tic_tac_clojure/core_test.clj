(ns tic-tac-clojure.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-clojure.core :refer :all]))

(def init-state2
  {:players "XO"
   :moves []
   :board [[-1 -1 -1]
           [-1 -1 -1]
           [-1 -1 -1]]
   :status :ok})

(deftest empty-move
  (testing "What happens when we call turn with an empty move?"
    (is (= (turn init-state2 '())
           (assoc init-state2 :status :empty-move)))))

(deftest valid-move
  (testing "State changes correctly with valid move"
    (is (= (turn init-state2 '(1 1))
           (assoc init-state2
             :moves ['(1 1)]
             :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]])))))

(deftest invalid-moves
  (testing "Trying to move to a cell that was already occupied"
    (is (= (reduce turn init-state2 ['(1 1) '(1 1)])
           (assoc init-state2
             :moves ['(1 1)]
             :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]]
             :status :already-occupied))))
  (testing "Trying to move to a cell that is not in the board"
    (is (= (turn init-state2 '(0 12))
           (assoc init-state2 :status :out-of-bounds)))))


