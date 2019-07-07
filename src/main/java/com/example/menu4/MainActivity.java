package com.example.menu4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.ar.sceneform.ux.ArFragment;

public class MainActivity extends AppCompatActivity {

    private ArFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
    }
}