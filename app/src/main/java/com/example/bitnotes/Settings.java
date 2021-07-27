package com.example.bitnotes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import maes.tech.intentanim.CustomIntent;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("");

    }

    @Override
    public void finish(){
        super.finish();
        CustomIntent.customType(this, "bottom-to-up");
    }

}