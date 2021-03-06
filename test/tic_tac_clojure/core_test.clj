(ns tic-tac-clojure.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-clojure.api :refer :all]))


(deftest valid-move
  (testing "State changes correctly with valid move"
    (let [moves ['(1 1)]]
      (is (= (turn init-state (first moves))
             (assoc init-state
               :moves moves
               :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]]))))))


(deftest invalid-moves
  (testing "What happens when we call turn with an empty move?"
    (is (= (turn init-state '())
           (assoc init-state :status :empty-move))))
  (testing "Trying to move to a cell that was already occupied"
    (let [moves ['(1 1) '(1 1)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves [(first moves)]
               :board [[-1 -1 -1] [-1 \X -1] [-1 -1 -1]]
               :status :already-occupied)))))
  (testing "Trying to move to a cell that is not in the board"
    (is (= (turn init-state '(0 12))
           (assoc init-state :status :out-of-bounds))))
  (testing "Too many moves"
    (let [moves ['(0 0) '(0 1) '(1 1) '(2 2) '(1 2) '(1 0) '(0 2) '(2 0) '(2 1) '(0 0)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves (drop-last moves)
               :board [[\X \O \X] [\O \X \X] [\O \X \O]]
               :status :too-many-moves))))))


(deftest winning
  (testing "X had 3 in a row"
    (let [moves ['(0 0) '(1 0) '(0 1) '(1 1) '(0 2)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves moves
               :board [[\X \X \X] [\O \O -1] [-1 -1 -1]]
               :status :x-won
               :winning-coordinates ['(0 0) '(0 1) '(0 2)])))))
  (testing "O has 3 in a column!"
    (let [moves ['(0 0) '(0 1) '(0 2) '(1 1) '(1 0) '(2 1)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves moves
               :board [[\X \O \X] [\X \O -1] [-1 \O -1]]
               :status :o-won
               :winning-coordinates ['(0 1) '(1 1) '(2 1)])))))
  (testing "X has 3 in the diagonal"
    (let [moves ['(0 0) '(0 1) '(1 1) '(0 2) '(2 2)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves moves
               :board [[\X \O \O] [-1 \X -1] [-1 -1 \X]]
               :status :x-won
               :winning-coordinates ['(0 0) '(1 1) '(2 2)])))))
  (testing "O has 3 in the reverse diagonal"
    (let [moves ['(0 0) '(0 2) '(0 1) '(1 1) '(1 0) '(2 0)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves moves
               :board [[\X \X \O] [\X \O -1] [\O -1 -1]]
               :status :o-won
               :winning-coordinates ['(0 2) '(1 1) '(2 0)]))))))


(deftest draw
  (testing "Game ending in a draw"
    (let [moves ['(0 0) '(0 1) '(1 1) '(2 2) '(1 2) '(1 0) '(0 2) '(2 0) '(2 1)]]
      (is (= (reduce turn init-state moves)
             (assoc init-state
               :moves moves
               :board [[\X \O \X] [\O \X \X] [\O \X \O]]
               :status :draw))))))

