package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuOyunActivity extends AppCompatActivity {

    private int[][] sudokuBoard; // sudoku tahtasını tutacak 2 boyutlu dizi
    private int[][] solvedSudokuBoard; // çözülmüş sudoku tahtasını tutacak 2 boyutlu dizi
    private TextView[][] cells; // hücreleri tutacak 2 boyutlu dizi
    private int difficultyLevel; // zorluk seviyesi

    private int selectedRow = -1; // seçili satır
    private int selectedCol = -1; // seçili sütun



    private ImageButton settingsButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku_oyun);

        TextView difficultyTextView = findViewById(R.id.difficulty);

        // Get Difficulty level
        Intent intent = getIntent();
        String level = intent.getStringExtra("level");
        if (level.equals("Easy")) {
            difficultyLevel = 1;
            difficultyTextView.setText("Easy");
        } else if (level.equals("Medium")) {
            difficultyLevel = 2;
            difficultyTextView.setText("Medium");
        } else if (level.equals("Hard")) {
            difficultyLevel = 3;
            difficultyTextView.setText("Hard");
        }

        // Define the board and cells
        sudokuBoard = new int[9][9];
        cells = new TextView[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellID = "cell_" + (i + 1) + "_" + (j + 1);
                int resID = getResources().getIdentifier(cellID, "id", getPackageName());
                cells[i][j] = findViewById(resID);

            }
        }
        fillBoard(difficultyLevel);

        settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ayarlarIntent = new Intent(SudokuOyunActivity.this, Ayarlar.class);
                startActivity(ayarlarIntent);
            }
        });

        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SudokuOyunActivity.this, MainActivity.class);
                startActivity(intent); // Geri butonuna tıklandığında MainActivity'e geçiş yapar
                finish(); // SudokuOyunActivity'yi sonlandırır
            }
        });

        Button oneButton = findViewById(R.id.button1);
        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "1"
                cells[selectedRow][selectedCol].setText("1");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 1) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button twoButton = findViewById(R.id.button2);
        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "2"
                cells[selectedRow][selectedCol].setText("2");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 2) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button threeButton = findViewById(R.id.button3);
        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "3"
                cells[selectedRow][selectedCol].setText("3");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 3) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button fourButton = findViewById(R.id.button4);
        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "4"
                cells[selectedRow][selectedCol].setText("4");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 4) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button fiveButton = findViewById(R.id.button5);
        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "5"
                cells[selectedRow][selectedCol].setText("5");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 5) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button sixButton = findViewById(R.id.button6);
        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "6"
                cells[selectedRow][selectedCol].setText("6");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 6) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button sevenButton = findViewById(R.id.button7);
        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Set the selected cell text to "7"
                cells[selectedRow][selectedCol].setText("7");
                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 7) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button eightButton = findViewById(R.id.button8);
        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set the selected cell text to "8"
                cells[selectedRow][selectedCol].setText("8");

                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 8) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        Button nineButton = findViewById(R.id.button9);
        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a cell is selected
                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set the selected cell text to "9"
                cells[selectedRow][selectedCol].setText("9");

                // Check if the solution is correct
                if (solvedSudokuBoard[selectedRow][selectedCol] == 9) {
                    cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                } else {
                    cells[selectedRow][selectedCol].setTextColor(Color.RED);
                }
            }
        });

        // Set OnTouchListener to all cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Save the selected cell row and column
                        selectedRow = row;
                        selectedCol = col;

                        // Change the background color of the selected cell
                        v.setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_selected));

                        // Remove the background color from all other cells
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                if (i != row || j != col) {
                                    cells[i][j].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                                }
                            }
                        }

                        return true;
                    }
                });
            }
        }
    }


    private boolean solveSudoku(int row, int col) {
        if (row == 9) { // tahtanın sonuna ulaştıysak Sudoku çözülmüş demektir
            return true;
        }
        if (col == 9) { // satır tamamlandı, sıradaki satıra geç
            return solveSudoku(row + 1, 0);
        }
        if (sudokuBoard[row][col] != 0) { // bu hücre zaten dolu, sıradaki hücreye geç
            return solveSudoku(row, col + 1);
        }
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i <= 9; i++) { // bu hücreye yerleştirilebilecek sayıları bul
            if (isValid(row, col, i)) {
                choices.add(i);
            }
        }
        if (choices.isEmpty()) { // bu hücreye yerleştirilebilecek hiçbir sayı yok
            return false;
        }
        Collections.shuffle(choices); // seçenekleri karıştır
        for (int choice : choices) { // seçenekleri dene
            sudokuBoard[row][col] = choice;
            if (solveSudoku(row, col + 1)) {
                return true;
            }
        }
        sudokuBoard[row][col] = 0; // hiçbir seçenek işe yaramadı, bu hücreyi boşalt
        return false;
    }

    private boolean isValid(int row, int col, int value) {
        for (int i = 0; i < 9; i++) { // aynı sütundaki sayılarla çakışıp çakışmadığını kontrol et
            if (sudokuBoard[i][col] == value) {
                return false;
            }
        }
        for (int j = 0; j < 9; j++) { // aynı satırdaki sayılarla çakışıp çakışmadığını kontrol et
            if (sudokuBoard[row][j] == value) {
                return false;
            }
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) { // aynı kutudaki sayılarla çakışıp çakışmadığını kontrol et
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (sudokuBoard[i][j] == value) {
                    return false;
                }
            }
        }
        return true;
    }


    private void fillBoard(int difficultyLevel) {
        // create a complete Sudoku board
        solveSudoku(0, 0);

        solvedSudokuBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solvedSudokuBoard[i][j] = sudokuBoard[i][j];
            }
        }
        // remove cells to make it solvable and playable
        int cellsToRemove = 0;
        if (difficultyLevel == 1) {
            cellsToRemove = 50; // easy difficulty, 40 cells removed
        } else if (difficultyLevel == 2) {
            cellsToRemove = 55; // medium difficulty, 50 cells removed
        } else if (difficultyLevel == 3) {
            cellsToRemove = 60; // hard difficulty, 60 cells removed
        }

        Random random = new Random();
        while (cellsToRemove > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            if (sudokuBoard[row][col] != 0) {
                sudokuBoard[row][col] = 0;
                cellsToRemove--;
            }
        }
        // display the board on the screen
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuBoard[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(sudokuBoard[i][j]));
                    cells[i][j].setEnabled(false);
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEnabled(true);
                }
            }
        }
    }
}


