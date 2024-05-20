package com.example.sudoku;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
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

import java.util.Timer;
import java.util.TimerTask;

public class SudokuGameActivity extends AppCompatActivity {

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
    private int mistakeCount = 0;
    private TextView mistakeLimit;
    private boolean isPopupOpen = false; // popup ekranın açık/kapalı durumunu takip etmek için bir değişken oluşturuyoruz

    private ImageButton settingsButton;
    private ImageButton pauseButton;
    private SudokuGameSaveManager gameSaveManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku_oyun);
        TextView difficultyTextView = findViewById(R.id.difficulty);
        timeCounter = findViewById(R.id.zaman);
        timer = new Timer();
        mistakeLimit = findViewById(R.id.mistake_limit);
        gameSaveManager = new SudokuGameSaveManager();

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
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("switch5State", false)) {
                mistakeLimit.setText("Mistakes: 0/3");
                mistakeCount = 0;
            } else {
                mistakeLimit.setText("Mistakes: -/-");
                mistakeCount = 3; // Mistake limit is disabled when switch is off, but mistake count is set to 3 initially to prevent any mistakes before switching on the limit
            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Sudoku", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Sudoku notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        ImageButton erase = findViewById(R.id.erase);
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRow != -1 && selectedCol != -1) {
                    cells[selectedRow][selectedCol].setText("");
                    cells[selectedRow][selectedCol].setBackground(getResources().getDrawable(R.drawable.sudoku_border_cell));
                }
            }
        });


        pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                saveSudokuGame();
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuGameActivity.this);
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
                        if (sharedPreferences.getBoolean("switch4State", false)) {
                            startTimer();
                        } else {
                            timeCounter.setText("--:--");
                        }
                    }
                });
            }
        });

        settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ayarlarIntent = new Intent(SudokuGameActivity.this, Settings.class);
                saveSudokuGame();
                startActivity(ayarlarIntent);
            }
        });
        ImageButton checkWinButton = findViewById(R.id.checkmark);
        checkWinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkWin()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SudokuGameActivity.this);
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
                            Intent intent = new Intent(SudokuGameActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    int remainingCount = countRemainingCells();
                    Toast.makeText(getApplicationContext(), "Not a valid solution yet. Remaining cells: " + remainingCount, Toast.LENGTH_SHORT).show();


                }
            }
        });


        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuGameActivity.this);
                builder.setMessage("Are you sure you want to go back to the main screen?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SudokuGameActivity.this, MainActivity.class);
                        saveSudokuGame();
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
                        cells[selectedRow][selectedCol].setTextColor(getResources().getColor(R.color.numbers));
                        makeMistake();
                    }
                    updateRemainingCounts(); // Update remaining counts after placing a number
                    saveSudokuGame();
                    if (checkWin()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SudokuGameActivity.this);
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
                                Intent intent = new Intent(SudokuGameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    updateRemainingCounts();
                }
            });
        }


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
                        updateRemainingCounts(); // Update remaining counts after selecting a cell
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
        loadSudokuGame();

    }
    private void loadSudokuGame() {
        SudokuGameLoadResult loadResult = gameSaveManager.loadSudokuGame(this);
        if (loadResult.isGameLoaded()) {
            sudokuBoard = loadResult.getSudokuBoard();
            solvedSudokuBoard = loadResult.getSolvedSudokuBoard();
            difficultyLevel = loadResult.getDifficultyLevel();
        } else {
            fillBoard(difficultyLevel);
        }

        SudokuUIManager uiManager = new SudokuUIManager(this, cells);
        uiManager.displayBoard(sudokuBoard, solvedSudokuBoard);
    }

    public void saveSudokuGame() {
        SudokuUIManager uiManager = new SudokuUIManager(this, cells);
        sudokuBoard = uiManager.getBoardState();
        gameSaveManager.saveSudokuGame(this, sudokuBoard, solvedSudokuBoard, difficultyLevel);
    }
    private void makeMistake() {
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        mistakeCount += 1;
        if (sharedPreferences.getBoolean("switch5State", false)) {
            if (mistakeCount >= 3) {
                gameSaveManager.deleteSavedSudokuGame(SudokuGameActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuGameActivity.this);
                builder.setMessage("Game over. You made too many mistakes.")
                        .setCancelable(false)
                        .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                gameSaveManager.deleteSavedSudokuGame(SudokuGameActivity.this);
                                fillBoard(difficultyLevel);
                                mistakeLimit.setText("Mistakes: 0/3");
                                mistakeCount = 0;
                            }
                        })
                        .setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(SudokuGameActivity.this, MainActivity.class);
                                gameSaveManager.deleteSavedSudokuGame(SudokuGameActivity.this);
                                startActivity(intent);
                                finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                mistakeLimit.setText("Mistakes: " + mistakeCount + "/3");
            }
        } else {
            mistakeLimit.setText("Mistakes: -/-");
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
            isTimerRunning = true;
        }
    }
    private void updateTimerText() {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timeCounter.setText(timeString);
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
    private int countRemainingCells() {
        int remainingCount = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellText = cells[i][j].getText().toString().trim();
                if (cellText.isEmpty() || !TextUtils.isDigitsOnly(cellText)) {
                    remainingCount++;
                } else {
                    int cellValue = Integer.parseInt(cellText);
                    if (cellValue != solvedSudokuBoard[i][j]) {
                        remainingCount++;
                    }
                }
            }
        }

        return remainingCount;
    }

    private void fillBoard(int difficultyLevel) {
        SudokuSolver solver = new SudokuSolver();
        sudokuBoard = solver.generateSudoku(difficultyLevel);

        solvedSudokuBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solvedSudokuBoard[i][j] = sudokuBoard[i][j];
            }
        }

        int cellsToRemove = 0;
        if (difficultyLevel == 1) {
            cellsToRemove = 45; // easy difficulty, 45 cells removed
        } else if (difficultyLevel == 2) {
            cellsToRemove = 50; // medium difficulty, 50 cells removed
        } else if (difficultyLevel == 3) {
            cellsToRemove = 55; // hard difficulty, 55 cells removed
        }

        SudokuGenerator generator = new SudokuGenerator();
        sudokuBoard = generator.generatePlayableSudoku(sudokuBoard, cellsToRemove);

        SudokuUIManager uiManager = new SudokuUIManager(this, cells);
        uiManager.displayBoard(sudokuBoard, solvedSudokuBoard);
    }

    private void updateRemainingCounts() {
        TextView[] remainingTextViews = new TextView[9];
        remainingTextViews[0] = findViewById(R.id.kalan_rakam_1);
        remainingTextViews[1] = findViewById(R.id.kalan_rakam_2);
        remainingTextViews[2] = findViewById(R.id.kalan_rakam_3);
        remainingTextViews[3] = findViewById(R.id.kalan_rakam_4);
        remainingTextViews[4] = findViewById(R.id.kalan_rakam_5);
        remainingTextViews[5] = findViewById(R.id.kalan_rakam_6);
        remainingTextViews[6] = findViewById(R.id.kalan_rakam_7);
        remainingTextViews[7] = findViewById(R.id.kalan_rakam_8);
        remainingTextViews[8] = findViewById(R.id.kalan_rakam_9);

        int[] remainingCounts = new int[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = sudokuBoard[i][j];
                if (value >= 1 && value <= 9) {
                    remainingCounts[value - 1]++;
                }
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        boolean switch7State = sharedPreferences.getBoolean("switch7State", true);

        for (int i = 0; i < 9; i++) {
            int remainingCount = 9 - remainingCounts[i];
            TextView textView = remainingTextViews[i];
            textView.setVisibility(switch7State ? View.VISIBLE : View.GONE);
            textView.setText(String.valueOf(remainingCount));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSudokuGame();
        pauseTimer();
    }
    @Override
    protected void onRestart() {
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        super.onRestart();
        saveSudokuGame();
        updateRemainingCounts();
        if (sharedPreferences.getBoolean("switch4State", false)) {
            startTimer();
        } else {
            timeCounter.setText("--:--");
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back to the main screen?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SudokuGameActivity.this, MainActivity.class);
                saveSudokuGame();
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


