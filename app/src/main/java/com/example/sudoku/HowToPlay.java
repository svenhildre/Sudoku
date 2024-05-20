package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HowToPlay extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnSkip;
    private ImageButton btnNext;
    private ImageView imageView1;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasil_oynanir);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Page1Fragment());
        fragments.add(new Page2Fragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = viewPager.getCurrentItem() + 1;
                if (nextItem < adapter.getItemCount()) {
                    viewPager.setCurrentItem(nextItem);
                } else {
                    Intent intent = new Intent(HowToPlay.this, Settings.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HowToPlay.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });
        int mavi = 0xFF005ECC;
        int acik_mavi = 0xFF6EA8FF;

        imageView1  = findViewById(R.id.imageView1);
        imageView2  = findViewById(R.id.imageView2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    imageView1.setBackgroundColor(acik_mavi);
                    imageView2.setBackgroundColor(mavi);
                } else {
                    imageView1.setBackgroundColor(mavi);
                    imageView2.setBackgroundColor(acik_mavi);
                }
            }
        });

    }

}

