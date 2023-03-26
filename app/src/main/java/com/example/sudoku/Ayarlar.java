package com.example.sudoku;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

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

        CompoundButton[] switches = {switch1, switch2, switch3, switch4, switch5, switch6, switch7};
        int textColorLight = isLightMode() ? Color.parseColor("#FF5599FF") : Color.parseColor("#FFC328");
        int textColorDark = isLightMode() ? Color.BLACK : Color.WHITE;

        for (CompoundButton switchBtn : switches) {
            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setTextColor(textColorLight);
                    } else {
                        buttonView.setTextColor(textColorDark);
                    }
                }
            });
        }

        for (CompoundButton switchButton : Arrays.asList(switch1, switch2, switch3, switch4, switch5, switch6, switch7)) {
            switchButton.setChecked(!isLightMode());
            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        switchButton.setTextColor(isLightMode() ? blue : Color.parseColor("#FFC328"));
                    } else {
                        switchButton.setTextColor(isLightMode() ? Color.BLACK : Color.WHITE);
                    }
                }
            });
        }
    }

    private boolean isLightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_NO;
    }

}