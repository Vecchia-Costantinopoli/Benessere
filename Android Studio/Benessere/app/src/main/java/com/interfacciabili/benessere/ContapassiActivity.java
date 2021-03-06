package com.interfacciabili.benessere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class ContapassiActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private long steps;
    private Switch switchStepCounter;
    private TextView textViewStepCounter, tvAttivaContapassi;
    private int ACTIVITY_PERMISSION_CODE = 1;
    private String PASSI = "PASSI";
    private ImageView ivScarpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contapassi);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Switch switchStepCounter = (Switch) findViewById(R.id.switchStepCounter);
        textViewStepCounter = (TextView) findViewById(R.id.textViewStepCounter);
        textViewStepCounter.setText(String.valueOf(steps));
        tvAttivaContapassi = findViewById(R.id.tvAttivaContapassi);

        ivScarpa = findViewById(R.id.ivScarpa);
        Animation animation_run  = AnimationUtils.loadAnimation(this,R.anim.movestartend);
        ivScarpa.startAnimation(animation_run);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestActivityRecongnition();
        }

        switchStepCounter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvAttivaContapassi.setText(R.string.disattiva);

                    if (savedInstanceState != null) {
                        if (savedInstanceState.containsKey(PASSI)) {
                            steps = savedInstanceState.getLong(PASSI);
                            textViewStepCounter.setText(String.valueOf(steps));

                            savedInstanceState.remove(PASSI);
                        }
                    } else {
                        steps = 0;
                    }

                    controllaSensore();
                } else {
                    tvAttivaContapassi.setText(R.string.attiva);
                    if(sensorManager!=null){
                        sensorManager.unregisterListener(ContapassiActivity.this);
                    }

                }
            }
        });
    }

    private void requestActivityRecongnition() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)){

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.richiestaPermesso))
                    .setMessage(R.string.permessoAttivitaContapassi)
                    .setPositiveButton(getString(R.string.richiestaPermessoConsenti), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ContapassiActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton(getString(R.string.richiestaPermessoRifiuta), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(ContapassiActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_PERMISSION_CODE);
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            textViewStepCounter.setText(String.valueOf(steps));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PASSI, steps);
    }

    public void controllaSensore(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager!=null){
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_STEP_DETECTOR);

                if (sensorList.size() > 0) {
                    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                    sensorManager.registerListener(ContapassiActivity.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.sensoreAssente))
                            .setMessage(getString(R.string.richiestaSensoreContapassi))
                            .setPositiveButton("OK", null)
                            .show();
                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}