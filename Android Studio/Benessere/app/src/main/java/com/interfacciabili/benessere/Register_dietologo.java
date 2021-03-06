package com.interfacciabili.benessere;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.interfacciabili.benessere.control.DatabaseService;
import com.interfacciabili.benessere.model.Dietologo;

public class Register_dietologo extends AppCompatActivity {
    String sesso = null;

    public DatabaseService databaseService;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatabaseService.LocalBinder localBinder = (DatabaseService.LocalBinder) service;
            databaseService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dietologo);

        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etMail = (EditText) findViewById(R.id.etEmail);
        EditText etNome = (EditText) findViewById(R.id.etNome);
        EditText etCognome = (EditText) findViewById(R.id.etCognome);
        EditText etEta = (EditText) findViewById(R.id.etEta);
        EditText etStudio = (EditText) findViewById(R.id.etStudio);

        TextView tvGenere = (TextView) findViewById(R.id.tvGenere);

        RadioButton rbMaschio = (RadioButton) findViewById(R.id.rbMaschio);
        RadioButton rbFemmina = (RadioButton) findViewById(R.id.rbFemmina);
        RadioButton rbAltro = (RadioButton) findViewById(R.id.rbAltro);

        Button bennessereRegisterButton = (Button) findViewById(R.id.btnRegBenessere);
        bennessereRegisterButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errato = false;


                if (etUsername.getText().toString().isEmpty()){
                    etUsername.setError(getString(R.string.erroreUsername));
                    errato = true;
                }

                if (etPassword.getText().toString().isEmpty()){
                    etPassword.setError(getString(R.string.errorePassword));
                    errato = true;
                }

                if (etMail.getText().toString().isEmpty()){
                    etMail.setError(getString(R.string.erroreEmail));
                    errato = true;
                }

                if (etNome.getText().toString().isEmpty()){
                    etNome.setError(getString(R.string.erroreNome));
                    errato = true;
                }

                if (etCognome.getText().toString().isEmpty()){
                    etCognome.setError(getString(R.string.erroreCognome));
                    errato = true;
                }

                if (etEta.getText().toString().isEmpty()){
                    etEta.setError(getString(R.string.erroreEta));
                    errato = true;
                }

                if (rbMaschio.isChecked()) {
                    sesso = "Maschio";
                } else if (rbFemmina.isChecked()){
                    sesso = "Femmina";
                } else if (rbAltro.isChecked()){
                    sesso = "Altro";
                }

                if (etEta.getText().toString().isEmpty()){
                    etEta.setError(getString(R.string.erroreEta));
                    errato = true;
                }

                if (sesso == null) {
                    tvGenere.setTextColor(getColor(R.color.simplyRed));
                    tvGenere.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);

                    rbMaschio.setTextColor(getColor(R.color.simplyRed));
                    rbMaschio.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    rbFemmina.setTextColor(getColor(R.color.simplyRed));
                    rbFemmina.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    rbAltro.setTextColor(getColor(R.color.simplyRed));
                    rbAltro.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                }

                if (etStudio.getText().toString().isEmpty()){
                    etStudio.setError(getString(R.string.erroreStudio));
                    errato = true;
                }

                if (!errato) {
                    boolean isDietologistUsernameInDatabase = databaseService.isDietologistUsernameInDatabase(etUsername.getText().toString());

                    if (isDietologistUsernameInDatabase){
                        etUsername.setError(getString(R.string.erroreUsernameEsistente));
                    } else {
                        Dietologo dietologo = new Dietologo(
                                etUsername.getText().toString(),
                                etPassword.getText().toString(),
                                etMail.getText().toString(),
                                etNome.getText().toString(),
                                etCognome.getText().toString(),
                                sesso,
                                Integer.parseInt(etEta.getText().toString()),
                                etStudio.getText().toString()
                        );

                        if (databaseService.aggiungiDietologo(dietologo)) {
                            Intent intent = new Intent(Register_dietologo.this, com.interfacciabili.benessere.HomeDietologo.class);
                            intent.putExtra("EXPERT", dietologo);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.toastErroreRegistrazione), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intentDatabaseService = new Intent(this, DatabaseService.class);
        bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);
    }
}