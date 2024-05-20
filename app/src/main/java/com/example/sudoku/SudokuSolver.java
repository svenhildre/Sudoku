package com.example.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuSolver {

    public int[][] generateSudoku(int difficultyLevel) {
        int[][] sudokuBoard = new int[9][9];
        solveSudoku(sudokuBoard, 0, 0);
        return sudokuBoard;
    }

    private boolean solveSudoku(int[][] board, int row, int col) {
        if (row == 9) {
            return true;
        }
        if (col == 9) {
            return solveSudoku(board, row + 1, 0);
        }
        if (board[row][col] != 0) {
            return solveSudoku(board, row, col + 1);
        }
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (isValid(board, row, col, i)) {
                choices.add(i);
            }
        }
        if (choices.isEmpty()) {
            return false;
        }
        Collections.shuffle(choices);
        for (int choice : choices) {
            board[row][col] = choice;
            if (solveSudoku(board, row, col + 1)) {
                return true;
            }
        }
        board[row][col] = 0;
        return false;
    }

    private boolean isValid(int[][] board, int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == value) {
                return false;
            }
        }
        for (int j = 0; j < 9; j++) {
            if (board[row][j] == value) {
                return false;
            }
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == value) {
                    return false;
                }
            }
        }
        return true;
    }
}
