package com.danielogbuti.androideatitserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonSignIn,buttonSignUp;

    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        slogan = (TextView)findViewById(R.id.sloganTextView);

        //change the font of the text
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/blacc.ttf");
        slogan.setTypeface(face);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });
    }
}
