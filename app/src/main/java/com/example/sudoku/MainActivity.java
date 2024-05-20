package com.example.sudoku;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private boolean useDarkTheme;
    private Button settingsButton, continueButton;
    private SudokuGameSaveManager gameSaveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameSaveManager = new SudokuGameSaveManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                subscribeToFirebaseTopics();
            }
        } else {
            subscribeToFirebaseTopics();
        }

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/kalam.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Gluten-Medium.ttf");

        TextView klasikTextView = findViewById(R.id.klasik);
        TextView sudokuTextView = findViewById(R.id.sudoku);
        klasikTextView.setTypeface(typeface1);
        sudokuTextView.setTypeface(typeface2);

        SwitchCompat switchCompat = findViewById(R.id.mode_switch);
        switchCompat.setChecked(useDarkTheme);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useDarkTheme = isChecked;
                saveDarkThemeState(useDarkTheme);
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ayarlarIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(ayarlarIntent);
            }
        });

        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SudokuGameLoadResult loadResult = gameSaveManager.loadSudokuGame(MainActivity.this);
                if (loadResult.isGameLoaded()) {
                    Intent intent = new Intent(MainActivity.this, SudokuGameActivity.class);
                    intent.putExtra("level", getDifficultyLevelString(loadResult.getDifficultyLevel()));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No saved game found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button newGame = findViewById(R.id.new_game_button);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.popup_seviye, null);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();

                Button btnKolay = view.findViewById(R.id.btnEasy);
                btnKolay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String level = "Easy";
                        Intent intent = new Intent(MainActivity.this, SudokuGameActivity.class);
                        intent.putExtra("level", level);
                        gameSaveManager.deleteSavedSudokuGame(MainActivity.this);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });

                Button btnMedium = view.findViewById(R.id.btnMedium);
                btnMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String level = "Medium";
                        Intent intent = new Intent(MainActivity.this, SudokuGameActivity.class);
                        intent.putExtra("level", level);
                        gameSaveManager.deleteSavedSudokuGame(MainActivity.this);
                        startActivity(intent);
                        finish();

                        dialog.dismiss();
                    }
                });

                Button btnHard = view.findViewById(R.id.btnHard);
                btnHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String level = "Hard";
                        Intent intent = new Intent(MainActivity.this, SudokuGameActivity.class);
                        intent.putExtra("level", level);
                        gameSaveManager.deleteSavedSudokuGame(MainActivity.this);
                        startActivity(intent);
                        finish();

                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void subscribeToFirebaseTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();
                subscribeToFirebaseTopics();
            } else {
                Toast.makeText(this, "Notifications Off", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveDarkThemeState(boolean darkTheme) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
    }
    private String getDifficultyLevelString(int difficultyLevel) {
        if (difficultyLevel == 1) {
            return "Easy";
        } else if (difficultyLevel == 2) {
            return "Medium";
        } else if (difficultyLevel == 3) {
            return "Hard";
        }
        return "";
    }
}
