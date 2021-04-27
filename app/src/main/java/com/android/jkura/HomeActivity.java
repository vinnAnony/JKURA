package com.android.jkura;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AnimatedPieView animatedPieView = findViewById(R.id.pie_chart);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)// Starting angle offset
                .addData(new SimplePieInfo(30, getResources().getColor(R.color.pieBlue), "Number 1"))//Data (bean that implements the IPieInfo interface)
                .addData(new SimplePieInfo(18, getResources().getColor(R.color.pieYellow), "Number 2"))
                .addData(new SimplePieInfo(35, getResources().getColor(R.color.pieOthers), "Others"))
                .duration(2000)
        .drawText(true)
        .textSize(30);// draw pie animation duration

        animatedPieView.applyConfig(config);
        animatedPieView.start();
    }
}