package com.example.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UpdateAppearance;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private boolean useDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Check if the user has selected a dark theme from previous session
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int mavi = 0xFF5599FF;

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/kalam.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Gluten-Medium.ttf");

        TextView klasikTextView = findViewById(R.id.klasik);
        TextView sudokuTextView = findViewById(R.id.sudoku);
        klasikTextView.setTypeface(typeface1);
        sudokuTextView.setTypeface(typeface2);


        SwitchCompat switchCompat = findViewById(R.id.switchcompat1);
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

        Button newGame = findViewById(R.id.new_game_button);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Popup göster
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
                        Intent intent = new Intent(MainActivity.this, SudokuOyunActivity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                        finish();

                        dialog.dismiss(); // Popup'ı kapat
                    }
                });

                Button btnMedium = view.findViewById(R.id.btnMedium);
                btnMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String level = "Medium";
                        Intent intent = new Intent(MainActivity.this, SudokuOyunActivity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                        finish();

                        dialog.dismiss(); // Popup'ı kapat
                    }
                });

                Button btnHard = view.findViewById(R.id.btnHard);
                btnHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String level = "Hard";
                        Intent intent = new Intent(MainActivity.this, SudokuOyunActivity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                        finish();

                        dialog.dismiss(); // Popup'ı kapat
                    }
                });
            }
        });
    }
    private void saveDarkThemeState(boolean darkTheme) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
    }
}
