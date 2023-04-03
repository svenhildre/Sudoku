package com.example.sudoku;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class Ayarlar extends AppCompatActivity {

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
                finish(); // Geri butonuna tıklandığında Ayarlar aktivitesini sonlandırır
            }
        });

        ImageButton nasilOynanirButton = findViewById(R.id.forward);
        nasilOynanirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ayarlar.this, NasilOynanirActivity.class);
                startActivity(intent);
                finish();
            }
        });

        int blue = 0xFF5599FF;

        // SharedPreferences nesnesi oluşturuluyor
        SharedPreferences sharedPreferences = getSharedPreferences("SwitchState", MODE_PRIVATE);

// Switchlerin son durumu alınıyor
        boolean switch1State = sharedPreferences.getBoolean("switch1State", false);
        boolean switch2State = sharedPreferences.getBoolean("switch2State", false);
        boolean switch3State = sharedPreferences.getBoolean("switch3State", false);
        boolean switch4State = sharedPreferences.getBoolean("switch4State", false);
        boolean switch5State = sharedPreferences.getBoolean("switch5State", false);
        boolean switch6State = sharedPreferences.getBoolean("switch6State", false);
        boolean switch7State = sharedPreferences.getBoolean("switch7State", false);

// Switchlerin durumlarına göre metin rengi ayarlanıyor
        int textColorLight = isLightMode() ? blue : Color.parseColor("#FFC328");
        int textColorDark = isLightMode() ? Color.BLACK : Color.WHITE;

        switch1.setChecked(switch1State);
        switch1.setTextColor(switch1State ? textColorLight : textColorDark);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch1State", isChecked).apply();
            }
        });

        switch2.setChecked(switch2State);
        switch2.setTextColor(switch2State ? textColorLight : textColorDark);
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch2State", isChecked).apply();
            }
        });

        switch3.setChecked(switch3State);
        switch3.setTextColor(switch3State ? textColorLight : textColorDark);
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch3State", isChecked).apply();
            }
        });

        switch4.setChecked(switch4State);
        switch4.setTextColor(switch4State ? textColorLight : textColorDark);
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch4State", isChecked).apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                builder.setMessage("Changes will apply after starting a new game!")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                // Ekran kapansın ama yeni oyuna başlamasın
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        switch5.setChecked(switch5State);
        switch5.setTextColor(switch5State ? textColorLight : textColorDark);
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch5State", isChecked).apply();
            }
        });

        switch6.setChecked(switch6State);
        switch6.setTextColor(switch6State ? textColorLight : textColorDark);
        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch6State", isChecked).apply();
            }
        });

        switch7.setChecked(switch7State);
        switch7.setTextColor(switch7State ? textColorLight : textColorDark);
        switch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? textColorLight : textColorDark);
                sharedPreferences.edit().putBoolean("switch7State", isChecked).apply();
            }
        });
    }

    private boolean isLightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_NO;
    }

}