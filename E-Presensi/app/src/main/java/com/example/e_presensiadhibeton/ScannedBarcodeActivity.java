package com.example.e_presensiadhibeton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.e_presensiadhibeton.model.ModelAbsen;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ScannedBarcodeActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    SurfaceView surfaceView;
    Button btnDatang, btnPulang;
    String intentData = "", jam = "", tgl = "", status = "";

    DateTimeFormatter formattertime = DateTimeFormatter.ofPattern("h:mm a");
    DateTimeFormatter formatterdate = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        btnDatang = findViewById(R.id.btnDatang);
        btnPulang = findViewById(R.id.btnPulang);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Kehadiran");

        btnDatang.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    //GetTanggal
                    LocalDate today = LocalDate.now();
                    tgl = today.format(formatterdate);

                    //GetJam
                    LocalTime now = LocalTime.now();
                    LocalTime current = now.minusHours(2);
                    jam = current.format(formattertime);

                    checkDatang(jam);
                    checkDay();

                    String id = myRef.push().getKey();
                    ModelAbsen absen = new ModelAbsen(tgl, jam, "Datang", status, intentData);
                    myRef.child(id).setValue(absen);
                    Toast.makeText(ScannedBarcodeActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(ScannedBarcodeActivity.this, Homepage.class)
//                            .putExtra("tanggal", tgl)
//                            .putExtra("waktu", jam)
//                            .putExtra("absen", "Datang")
//                            .putExtra("status", status)
//                            .putExtra("keterangan", intentData));
                    finish();
                } else {
                    Toast.makeText(ScannedBarcodeActivity.this, "Barcode tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPulang.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    //GetTanggal
                    LocalDate today = LocalDate.now();
                    tgl = today.format(formatterdate);

                    //GetJam
                    LocalTime now = LocalTime.now();
                    LocalTime current = now.minusHours(2);
                    jam = current.format(formattertime);

                    checkPulang(jam);
                    checkDay();

                    String id = myRef.push().getKey();
                    ModelAbsen absen = new ModelAbsen(tgl, jam, "Pulang", status, intentData);
                    myRef.child(id).setValue(absen);
                    Toast.makeText(ScannedBarcodeActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(ScannedBarcodeActivity.this, Homepage.class)
//                            .putExtra("tanggal", tgl)
//                            .putExtra("waktu", jam)
//                            .putExtra("absen", "Pulang")
//                            .putExtra("status", status)
//                            .putExtra("keterangan", intentData));
                    finish();
                } else {
                    Toast.makeText(ScannedBarcodeActivity.this, "Barcode tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(ScannedBarcodeActivity.this, "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            Toast.makeText(ScannedBarcodeActivity.this, "Barcode scanned!", Toast.LENGTH_SHORT).show();
                            cameraSource.stop();
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void checkDatang(String checkTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        LocalTime startLocalTime = LocalTime.parse("9:00 AM", formatter);
        LocalTime endLocalTime = LocalTime.parse("5:00 PM", formatter);
        LocalTime checkLocalTime = LocalTime.parse(checkTime, formatter);

        boolean isInBetween = false;
        if (endLocalTime.isAfter(startLocalTime)) {
            if (startLocalTime.isBefore(checkLocalTime) && endLocalTime.isAfter(checkLocalTime)) {
                isInBetween = true;
            }
        } else if (checkLocalTime.isAfter(startLocalTime) || checkLocalTime.isBefore(endLocalTime)) {
            isInBetween = true;
        }

        if (isInBetween) {
            status = "Hadir";
        } else {
            status = "Tidak hadir";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void checkPulang(String checkTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        LocalTime startLocalTime = LocalTime.parse("9:00 AM", formatter);
        LocalTime endLocalTime = LocalTime.parse("5:00 PM", formatter);
        LocalTime checkLocalTime = LocalTime.parse(checkTime, formatter);

        boolean isInBetween = false;
        if (endLocalTime.isAfter(startLocalTime)) {
            if (startLocalTime.isBefore(checkLocalTime) && endLocalTime.isAfter(checkLocalTime)) {
                isInBetween = true;
            }
        } else if (checkLocalTime.isAfter(startLocalTime) || checkLocalTime.isBefore(endLocalTime)) {
            isInBetween = true;
        }

        if (isInBetween) {
            status = "Didalam Jam";
        } else {
            status = "Diluar Jam";
        }
    }

    protected void checkDay(){

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (Calendar.SATURDAY == dayOfWeek) {
            status = "Libur Kerja";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            status = "Libur Kerja";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}
