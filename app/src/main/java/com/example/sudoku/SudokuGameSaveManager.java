package com.example.sudoku;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class SudokuGameSaveManager {
    private static final String PREFS_NAME = "SudokuGameSave";
    private static final String KEY_SUDOKU_BOARD = "sudoku_board";
    private static final String KEY_SOLVED_SUDOKU_BOARD = "solved_sudoku_board";
    private static final String KEY_DIFFICULTY_LEVEL = "difficulty_level";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveSudokuGame(Context context, int[][] sudokuBoard, int[][] solvedSudokuBoard, int difficultyLevel) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        JSONArray sudokuBoardJsonArray = new JSONArray();
        JSONArray solvedSudokuBoardJsonArray = new JSONArray();

        // Convert sudokuBoard to JSONArray
        for (int i = 0; i < 9; i++) {
            JSONArray rowJsonArray = new JSONArray();
            for (int j = 0; j < 9; j++) {
                rowJsonArray.put(sudokuBoard[i][j]);
            }
            sudokuBoardJsonArray.put(rowJsonArray);
        }

        // Convert solvedSudokuBoard to JSONArray
        for (int i = 0; i < 9; i++) {
            JSONArray rowJsonArray = new JSONArray();
            for (int j = 0; j < 9; j++) {
                rowJsonArray.put(solvedSudokuBoard[i][j]);
            }
            solvedSudokuBoardJsonArray.put(rowJsonArray);
        }

        editor.putString(KEY_SUDOKU_BOARD, sudokuBoardJsonArray.toString());
        editor.putString(KEY_SOLVED_SUDOKU_BOARD, solvedSudokuBoardJsonArray.toString());
        editor.putInt(KEY_DIFFICULTY_LEVEL, difficultyLevel);

        editor.apply();
    }

    public static SudokuGameLoadResult loadSudokuGame(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);

        if (!sharedPreferences.contains(KEY_SUDOKU_BOARD)) {
            return new SudokuGameLoadResult(false, null, null, 0);
        }

        try {
            String sudokuBoardJsonString = sharedPreferences.getString(KEY_SUDOKU_BOARD, null);
            String solvedSudokuBoardJsonString = sharedPreferences.getString(KEY_SOLVED_SUDOKU_BOARD, null);
            int difficultyLevel = sharedPreferences.getInt(KEY_DIFFICULTY_LEVEL, 0);

            if (sudokuBoardJsonString != null && solvedSudokuBoardJsonString != null) {
                JSONArray sudokuBoardJsonArray = new JSONArray(sudokuBoardJsonString);
                JSONArray solvedSudokuBoardJsonArray = new JSONArray(solvedSudokuBoardJsonString);

                int[][] sudokuBoard = new int[9][9];
                int[][] solvedSudokuBoard = new int[9][9];

                // Convert sudokuBoard JSONArray to int[][]
                for (int i = 0; i < 9; i++) {
                    JSONArray rowJsonArray = sudokuBoardJsonArray.getJSONArray(i);
                    for (int j = 0; j < 9; j++) {
                        sudokuBoard[i][j] = rowJsonArray.getInt(j);
                    }
                }

                // Convert solvedSudokuBoard JSONArray to int[][]
                for (int i = 0; i < 9; i++) {
                    JSONArray rowJsonArray = solvedSudokuBoardJsonArray.getJSONArray(i);
                    for (int j = 0; j < 9; j++) {
                        solvedSudokuBoard[i][j] = rowJsonArray.getInt(j);
                    }
                }

                return new SudokuGameLoadResult(true, sudokuBoard, solvedSudokuBoard, difficultyLevel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new SudokuGameLoadResult(false, null, null, 0);
    }



    public static void deleteSavedSudokuGame(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}

