package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SudokuOyunActivity extends AppCompatActivity {

    private int[][] sudokuBoard; // sudoku tahtasını tutacak 2 boyutlu dizi
    private int[][] solvedSudokuBoard; // çözülmüş sudoku tahtasını tutacak 2 boyutlu dizi
    private TextView[][] cells; // hücreleri tutacak 2 boyutlu dizi
    private int difficultyLevel; // zorluk seviyesi

    private int selectedRow = -1; // seçili satır
    private int selectedCol = -1; // seçili sütun
    private Timer timer;
    private TextView timeCounter;
    private boolean isTimerRunning = false;
    private long startTime;
    private long elapsedTime;
    private boolean isPopupOpen = false; // popup ekranın açık/kapalı durumunu takip etmek için bir değişken oluşturuyoruz

    private ImageButton settingsButton;
    private ImageButton pauseButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku_oyun);
        TextView difficultyTextView = findViewById(R.id.difficulty);
        timeCounter = findViewById(R.id.zaman);
        timer = new Timer();

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

        pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuOyunActivity.this);
                View view = getLayoutInflater().inflate(R.layout.pause_pop_up, null);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                isPopupOpen = true;
                dialog.show();
                Window window = dialog.getWindow();
                window.getDecorView().setClipToOutline(true);
                window.getDecorView().setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        int radius = 175;
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isPopupOpen = false;
                            startTimer();
                    }
                });
            }
        });

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
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuOyunActivity.this);
                builder.setMessage("Are you sure you want to go back to the main screen?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SudokuOyunActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        // Set OnClickListeners to all number buttons
        for (int i = 1; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            final int number = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if a cell is selected
                    if (selectedRow == -1 || selectedCol == -1) {
                        Toast.makeText(getApplicationContext(), "Please select a cell first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Set the selected cell text to the clicked number
                    cells[selectedRow][selectedCol].setText(String.valueOf(number));
                    // Check if the solution is correct
                    if (solvedSudokuBoard[selectedRow][selectedCol] == number) {
                        cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                    } else {
                        cells[selectedRow][selectedCol].setTextColor(Color.RED);
                    }
                    if (checkWin()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SudokuOyunActivity.this);
                        builder.setMessage("Congratulations, you won!");
                        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fillBoard(difficultyLevel); // Start a new game with the same difficulty level
                            }
                        });
                        builder.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SudokuOyunActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }

        // SharedPreferences nesnesi oluşturuluyor
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        // Set OnTouchListener to all cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final int row = i;
                final int col = j;
                final int boxRow = (row / 3) * 3;
                final int boxCol = (col / 3) * 3;
                cells[i][j].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Change background color to all cells to default
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                cells[i][j].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                            }
                        }
                        // Save the selected cell row and column
                        selectedRow = row;
                        selectedCol = col;
                        // Change the background color of the selected cell
                        v.setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_selected));
                        // Change the background color from cells in the same row or column
                        for (int i = 0; i < 9; i++) {
                            if (i != row) {
                                cells[i][col].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_row_selected));
                            }
                            if (i != col) {
                                cells[row][i].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_row_selected));
                            }
                        }
                        // Change the background color from cells in the same 3x3 box
                        int boxRowStart = (row / 3) * 3;
                        int boxColStart = (col / 3) * 3;
                        int boxRowEnd = boxRowStart + 2;
                        int boxColEnd = boxColStart + 2;
                        for (int i = boxRowStart; i <= boxRowEnd; i++) {
                            for (int j = boxColStart; j <= boxColEnd; j++) {
                                if (i != row || j != col) {
                                    cells[i][j].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_area_selected));
                                }
                            }
                        }
                        // Get switch6 state from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
                        boolean switch6State = sharedPreferences.getBoolean("switch6State", false);
                        // Change the background color from cells in the same row or column based on switch6State
                        for (int i = 0; i < 9; i++) {
                            if (i != row) {
                                if (switch6State) {
                                    cells[i][col].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_row_selected));
                                } else {
                                    cells[i][col].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                                }
                            }
                            if (i != col) {
                                if (switch6State) {
                                    cells[row][i].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_row_selected));
                                } else {
                                    cells[row][i].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                                }
                            }
                        }
                        // Change the background color from cells in the same 3x3 box based on switch6State
                        for (int i = boxRowStart; i <= boxRowEnd; i++) {
                            for (int j = boxColStart; j <= boxColEnd; j++) {
                                if (i != row || j != col) {
                                    if (switch6State) {
                                        cells[i][j].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell_area_selected));
                                    } else {
                                        cells[i][j].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                                    }
                                }
                            }
                        }
                        return true;
                    }
                });
            }
        }
        boolean switch4State = sharedPreferences.getBoolean("switch4State", false);
        if (switch4State) {
            startTimer();
        } else {
            timeCounter.setText("--:--");
        }
    }
    private void startTimer() {
        if (elapsedTime == 0) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerText();
                    }
                });
            }
        }, 0, 1000);
        isTimerRunning = true;
    }
    private void pauseTimer() {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
        }
    }
    private void updateTimerText() {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timeCounter.setText(timeString);
    }
    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer();
    }
    private boolean checkWin() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellText = cells[i][j].getText().toString().trim();
                if (cellText.isEmpty() || !TextUtils.isDigitsOnly(cellText)) {
                    return false; // If any cell is empty or has non-numeric text, not a win yet
                }
                int cellValue = Integer.parseInt(cellText);
                if (cellValue != solvedSudokuBoard[i][j]) {
                    return false; // If any cell value is different from solution value, not a win yet
                }
            }
        }
        return true; // If all cells are filled correctly, it's a win
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
            cellsToRemove = 45; // easy difficulty, 45 cells removed
        } else if (difficultyLevel == 2) {
            cellsToRemove = 50; // medium difficulty, 50 cells removed
        } else if (difficultyLevel == 3) {
            cellsToRemove = 55; // hard difficulty, 55 cells removed
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
                    cells[i][j].setTextColor(getResources().getColor(R.color.pop_up));
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEnabled(true);
                    cells[i][j].setTextColor(getResources().getColor(R.color.pop_up));
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back to the main screen?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SudokuOyunActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


