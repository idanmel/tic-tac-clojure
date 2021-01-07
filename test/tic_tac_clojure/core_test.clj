(ns tic-tac-clojure.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-clojure.core :refer :all]))

(def init-state
  {:players "XO"
   :moves []
   :board [[-1 -1 -1]
           [-1 -1 -1]
           [-1 -1 -1]]
   :status :ok
   :winning-coordinates nil})

(deftest empty-move
  (testing "What happens when we call turn with an empty move?"
    (is (= (turn init-state '())
           (assoc init-state :status :empty-move)))))

(deftest valid-move
  (testing "State changes correctly with valid move"
    (is (= (turn init-state '(1 1))
           (assoc init-state
             :moves ['(1 1)]
             :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]])))))

(deftest invalid-moves
  (testing "Trying to move to a cell that was already occupied"
    (is (= (reduce turn init-state ['(1 1) '(1 1)])
           (assoc init-state
             :moves ['(1 1)]
             :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]]
             :status :already-occupied))))
  (testing "Trying to move to a cell that is not in the board"
    (is (= (turn init-state '(0 12))
           (assoc init-state :status :out-of-bounds)))))


(deftest winning
  (testing "X had 3 in a row"
    (is (= (reduce turn init-state ['(0 0) '(1 0) '(0 1) '(1 1) '(0 2)])
           (assoc init-state
             :moves ['(0 0) '(1 0) '(0 1) '(1 1) '(0 2)]
             :board [[\X \X \X] [\O \O -1] [-1 -1 -1]]
             :status :x-won
             :winning-coordinates ['(0 0) '(0 1) '(0 2)]))))
  (testing "O has 3 in a column!"
    (is (= (reduce turn init-state ['(0 0) '(0 1) '(0 2) '(1 1) '(1 0) '(2 1)])
           (assoc init-state
             :moves ['(0 0) '(0 1) '(0 2) '(1 1) '(1 0) '(2 1)]
             :board [[\X \O \X] [\X \O -1] [-1 \O -1]]
             :status :o-won
             :winning-coordinates ['(0 1) '(1 1) '(2 1)]))))
  (testing "X has 3 in the diagonal"
    (is (= (reduce turn init-state ['(0 0) '(0 1) '(1 1) '(0 2) '(2 2)])
           (assoc init-state
             :moves ['(0 0) '(0 1) '(1 1) '(0 2) '(2 2)]
             :board [[\X \O \O] [-1 \X -1] [-1 -1 \X]]
             :status :x-won
             :winning-coordinates ['(0 0) '(1 1) '(2 2)]))))
  (testing "O has 3 in the reverse diagonal"
    (is (= (reduce turn init-state ['(0 0) '(0 2) '(0 1) '(1 1) '(1 0) '(2 0)])
           (assoc init-state
             :moves ['(0 0) '(0 2) '(0 1) '(1 1) '(1 0) '(2 0)]
             :board [[\X \X \O] [\X \O -1] [\O -1 -1]]
             :status :o-won
             :winning-coordinates ['(0 2) '(1 1) '(2 0)])))))



(deftest draw
  (testing "Game ending in a draw"
    (is (= (reduce turn init-state ['(0 0) '(0 1) '(1 1) '(2 2) '(1 2) '(1 0) '(0 2) '(2 0) '(2 1)])
           (assoc init-state
             :moves ['(0 0) '(0 1) '(1 1) '(2 2) '(1 2) '(1 0) '(0 2) '(2 0) '(2 1)]
             :board [[\X \O \X] [\O \X \X] [\O \X \O]]
             :status :draw)))))