package com.example.sudoku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayarlar);

        Switch switch1 = findViewById(R.id.sound_effects);
        Switch switch2 = findViewById(R.id.vibration);
        Switch switch3 = findViewById(R.id.notification);
        Switch switch4 = findViewById(R.id.time_switch);
        Switch switch5 = findViewById(R.id.mistake_switch);
        Switch switch6 = findViewById(R.id.highlight_areas_switch);
        Switch switch7 = findViewById(R.id.number_left_switch);

        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton nasilOynanirButton = findViewById(R.id.forward);
        nasilOynanirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, HowToPlay.class);
                startActivity(intent);
                finish();
            }
        });

        int blue = 0xFF5599FF;

        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);

        boolean switch1State = sharedPreferences.getBoolean("switch1State", false);
        boolean switch2State = sharedPreferences.getBoolean("switch2State", false);
        boolean switch3State = sharedPreferences.getBoolean("switch3State", false);
        boolean switch4State = sharedPreferences.getBoolean("switch4State", false);
        boolean switch5State = sharedPreferences.getBoolean("switch5State", false);
        boolean switch6State = sharedPreferences.getBoolean("switch6State", false);
        boolean switch7State = sharedPreferences.getBoolean("switch7State", false);

        int textColorLight = isLightMode() ? blue : Color.parseColor("#FFC328");
        int textColorDark = isLightMode() ? Color.BLACK : Color.WHITE;

        setupSwitch(switch1, "switch1State", switch1State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch2, "switch2State", switch2State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch3, "switch3State", switch3State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch4, "switch4State", switch4State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch5, "switch5State", switch5State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch6, "switch6State", switch6State, textColorLight, textColorDark, sharedPreferences);
        setupSwitch(switch7, "switch7State", switch7State, textColorLight, textColorDark, sharedPreferences);

        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
            sharedPreferences.edit().putBoolean("switch3State", isChecked).apply();

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("all")
                        .addOnCompleteListener(task -> {
                            String msg = task.isSuccessful() ? "Notifications On" : "Failed to turn on Notifications";
                            Toast.makeText(Settings.this, msg, Toast.LENGTH_SHORT).show();
                        });
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                        .addOnCompleteListener(task -> {
                            String msg = task.isSuccessful() ? "Notifications Off" : "Failed to turn off Notifications";
                            Toast.makeText(Settings.this, msg, Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void setupSwitch(Switch switchView, String key, boolean state, int textColorLight, int textColorDark, SharedPreferences sharedPreferences) {
        switchView.setChecked(state);
        switchView.setTextColor(state ? textColorLight : textColorDark);
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
            sharedPreferences.edit().putBoolean(key, isChecked).apply();
        });
    }

    private boolean isLightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_NO;
    }
}
