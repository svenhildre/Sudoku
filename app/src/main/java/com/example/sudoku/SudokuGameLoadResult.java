package com.example.sudoku;

public class SudokuGameLoadResult {
    private boolean gameLoaded;
    private int[][] sudokuBoard;
    private int[][] solvedSudokuBoard;
    private int difficultyLevel;

    public SudokuGameLoadResult(boolean gameLoaded, int[][] sudokuBoard, int[][] solvedSudokuBoard, int difficultyLevel) {
        this.gameLoaded = gameLoaded;
        this.sudokuBoard = sudokuBoard;
        this.solvedSudokuBoard = solvedSudokuBoard;
        this.difficultyLevel = difficultyLevel;
    }

    public boolean isGameLoaded() {
        return gameLoaded;
    }

    public int[][] getSudokuBoard() {
        return sudokuBoard;
    }

    public int[][] getSolvedSudokuBoard() {
        return solvedSudokuBoard;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }
}
