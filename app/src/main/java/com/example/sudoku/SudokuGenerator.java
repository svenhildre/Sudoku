package com.example.sudoku;

import java.util.Random;

public class SudokuGenerator {

    public int[][] generatePlayableSudoku(int[][] sudokuBoard, int cellsToRemove) {
        Random random = new Random();
        while (cellsToRemove > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            if (sudokuBoard[row][col] != 0) {
                sudokuBoard[row][col] = 0;
                cellsToRemove--;
            }
        }
        return sudokuBoard;
    }
}
