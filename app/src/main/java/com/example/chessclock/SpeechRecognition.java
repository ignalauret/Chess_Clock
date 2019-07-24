package com.example.chessclock;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.chessclock.Constants.SPEECH_REQUEST_CODE;

public class SpeechRecognition extends AppCompatActivity {

    private TextView mTextResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_recognition);

        mTextResult = findViewById(R.id.resultText);
    }


    public void getSpeechToText(View view){

        Intent getSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        getSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(getIntent().resolveActivity(getPackageManager()) != null){
            startActivityForResult(getSpeech, SPEECH_REQUEST_CODE);
        } else {
            mTextResult.setText("We're sorry, your device doesn't support Speech Input.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SPEECH_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> speechResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTextResult.setText(speechResult.get(0));
                } else {
                    mTextResult.setText("I can't understand you");
                }
                break;

            default:
                Log.d("onActivityResult","Can't handle this request");
                break;
        }
    }
}
