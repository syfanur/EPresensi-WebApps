package com.example.adhibeton;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HalamanSelfie extends AppCompatActivity {
TextView rLokasiabsen;
String mLokasi="";
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_selfie);
        rLokasiabsen=(TextView)findViewById(R.id.rLokasi);
        mLokasi = HalamanSelfie.this.getIntent().getStringExtra("lokasiAbsen");
    }

    public void MoveToFaceDetectDatang(View view) {
        Intent home = new Intent(HalamanSelfie.this, FaceDetectDatang.class);
        home.putExtra("lokasiAbsenDatang", mLokasi);
        startActivity(home);

    }


    public void MoveToFaceDetectPulang(View view) {
        Intent intent = new Intent(HalamanSelfie.this, FaceDetectPulang.class);
       intent.putExtra("lokasiAbsenPulang", mLokasi);
        startActivity(intent);
    }
}
