package com.interfacciabili.benessere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.interfacciabili.benessere.control.DatabaseService;
import com.interfacciabili.benessere.model.Alimento;
import com.interfacciabili.benessere.model.RichiestaDieta;

import static android.content.Context.BIND_AUTO_CREATE;
import static java.lang.Boolean.FALSE;

public class ModificaDietaDialog extends DialogFragment {
    Alimento alimento;
    String utente, utenteNome, utenteCognome;
    String dietologo;
    String porzioneModificaSpinner;
    EditText etAlimentoModifica, etPorzioneModifica;
    Spinner spinnerPorzioneModifica;
    AlertDialog.Builder builder;

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





    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modificadieta, null);
        etAlimentoModifica = view.findViewById(R.id.etAlimentoModifica);


        etPorzioneModifica = view.findViewById(R.id.etPorzioneModifica);

        spinnerPorzioneModifica = view.findViewById(R.id.spinnerPorzioneModifca);
        spinnerPorzioneModifica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                porzioneModificaSpinner = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                porzioneModificaSpinner = (String) parent.getItemAtPosition(0);
            }
        });

        builder.setView(view)
                .setTitle(R.string.modificareAlimento)
                .setMessage(getString(R.string.modificareAlimentoRichiesta) + alimento + "?")
                .setNegativeButton(getString(R.string.annulla), null)
                .setPositiveButton(getString(R.string.inserisci), null);
        return builder.create();
    }

    //Previene che il dialog sia chiuso premendo il bottone ok se l'input e' errato
    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog!=null){
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    if(etAlimentoModifica.getText().toString().length()>0 && etPorzioneModifica.getText().toString().length()>0)
                    {
                        String alimentoModifier = etAlimentoModifica.getText().toString();
                        String porzioneModifier = etPorzioneModifica.getText().toString();
                        RichiestaDieta rd = new RichiestaDieta(utente, utenteNome, utenteCognome, dietologo, alimento.getId(), alimento.getNome(), alimentoModifier, porzioneModifier, porzioneModificaSpinner, FALSE);
                        databaseService.aggiungiRichestaDieta(rd);
                        wantToCloseDialog = true;

                    }else {
                        if(etAlimentoModifica.getText().toString().length()==0){
                            etAlimentoModifica.setError(getString(R.string.inserireAlimento));
                        }
                        if(etPorzioneModifica.getText().toString().length()==0){
                            etPorzioneModifica.setError(getString(R.string.inserirePorzione));
                        }
                    }
                    if(wantToCloseDialog) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    public void setAlimento(Alimento valore){
        alimento = valore;
    }

    public void setUtente(String username, String nome, String cognome){
        utente = username;
        utenteNome = nome;
        utenteCognome = cognome;
    }

    public void setDietologo(String valore){
        dietologo = valore;
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intentDatabaseService = new Intent(getActivity(), DatabaseService.class);
        getActivity().startService(intentDatabaseService);
        getActivity().bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceConnection!= null){
            getActivity().unbindService(serviceConnection);
        }
    }

}
