package com.example.sudoku;

import android.content.Context;
import android.widget.TextView;

public class SudokuUIManager {

    private TextView[][] cells;
    private Context context;

    public SudokuUIManager(Context context, TextView[][] cells) {
        this.context = context;
        this.cells = cells;
    }

    public void displayBoard(int[][] sudokuBoard, int[][] solvedSudokuBoard) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuBoard[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(sudokuBoard[i][j]));
                    cells[i][j].setEnabled(false);
                    cells[i][j].setTextColor(context.getResources().getColor(R.color.pop_up));
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEnabled(true);
                    cells[i][j].setTextColor(context.getResources().getColor(R.color.pop_up));
                }
            }
        }

    }
    public int[][] getBoardState() {
        int[][] boardState = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellText = cells[i][j].getText().toString();
                if (!cellText.isEmpty()) {
                    int value = Integer.parseInt(cellText);
                    boardState[i][j] = value;
                }
            }
        }
        return boardState;
    }
}
