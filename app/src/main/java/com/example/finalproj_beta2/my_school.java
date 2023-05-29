package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class my_school extends AppCompatActivity {



    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * In this activity an ADMIN can view the students, teachers in his school.
     * He can remove any one of them and promote/demote any teacher.
     * He can Also view the SCHOOL_ID and SCHOOL_SECRET codes.
     */

    String get_school_id, get_school_secret, get_school_name;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    myViewPagerAdapter myViewPagerAdapter;
    TextView name_tv,codes_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_school);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        myViewPagerAdapter = new myViewPagerAdapter(this);
        viewPager.setAdapter(myViewPagerAdapter);

        name_tv = findViewById(R.id.name);
        codes_tv = findViewById(R.id.codes);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

                                               Intent intent = getIntent();

        get_school_id = intent.getStringExtra("school_id");
        get_school_secret = intent.getStringExtra("school_secret");
        get_school_name = intent.getStringExtra("school_name");
        name_tv.setText("School name: " + get_school_name);
        codes_tv.setText("School code: " + get_school_id + ", School secret: " + get_school_secret);
    }

    // this transfers the user back to the profile activity.
    public void home_screen(View view) {
        finish();
    }
}