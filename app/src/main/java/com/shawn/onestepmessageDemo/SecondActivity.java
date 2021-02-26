package com.shawn.onestepmessageDemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStore;

import com.shawn.oneStepMessage.EventLiveData;
import com.shawn.oneStepMessage.OSM;
import com.shawn.onestepmessageDemo.viewModel.DemoViewModel;
import com.shawn.onestepmessageDemo.viewModel.bean.Bean;

import java.util.Random;

public class SecondActivity extends AppCompatActivity {

    private Random r = new Random();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView showMessage1 = findViewById(R.id.textView_second_message1);
        TextView showMessage2 = findViewById(R.id.textView_second_message2);

        findViewById(R.id.button_second_message1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OSM.Companion.with(DemoViewModel.class).getMessage1().postEventValue("更改message1了 random = " + r.nextInt());
            }
        });

        findViewById(R.id.button_second_message2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OSM.Companion.with(DemoViewModel.class).getMessage2().postEventValue(new Bean("name" + r.nextInt(),"feature1",r.nextInt(),true));
            }
        });

        OSM.Companion.with(DemoViewModel.class).getMessage1().observeEvent(this, new ViewModelStore(), new EventLiveData.OnChanged<String>() {
            @Override
            public void onChanged(String value) {
                showMessage1.setText(value);
            }
        });

        OSM.Companion.with(DemoViewModel.class).getMessage2().observeEvent(this,new ViewModelStore(), new EventLiveData.OnChanged<Bean>(){

            @Override
            public void onChanged(Bean value) {
                showMessage2.setText(value.toString());
            }
        });
    }
}
