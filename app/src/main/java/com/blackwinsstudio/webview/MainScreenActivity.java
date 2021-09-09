package com.blackwinsstudio.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity {
    Button FillMenuButton, ExitButton;
    TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Button AppMainButton = (Button) findViewById(R.id.appmainbutton);
        FillMenuButton = (Button) findViewById (R.id.fillmenubutton);
        ExitButton = (Button) findViewById (R.id.exitbutton);
        myText = (TextView)findViewById (R.id.myText);
        AppMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Opensastaamart();
            }
        });

        FillMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               OpenFillMenu();
            }


        });

        myText.setOnLongClickListener (new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View view) {
                FillMenuButton.setVisibility (View.VISIBLE);
                ExitButton.setVisibility (View.VISIBLE);
                return false;
            }
        });
    }

    public void OpenFillMenu() {
        Intent intent = new Intent(this, fillMenuscreen.class);
        startActivity(intent);
    }

    public void Opensastaamart(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra ("page", "mainscreen");
        startActivity(intent);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}