package com.interfacciabili.benessere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.interfacciabili.benessere.control.DatabaseService;

import static android.content.Context.BIND_AUTO_CREATE;

public class InserisciClienteDialog extends AppCompatDialogFragment {
    private static final String TAG_LOG = "InserisciClienteDialog";

    TextView tvMessaggioInserisciCliente;
    String usernameCliente, usernameExpert;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_inserisci_cliente, null);

        tvMessaggioInserisciCliente = view.findViewById(R.id.tvMessaggioInserisciCliente);
        tvMessaggioInserisciCliente.append(usernameCliente +  "?");

        if (usernameExpert != null) {
            builder.setView(view)
                    .setTitle("Inserisci cliente")
                    .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setPositiveButton("Inserisci", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseService.aggiungiClienteADietologo(usernameCliente, usernameExpert);
                            getActivity().finish();
                            dismiss();
                        }
                    });
            return builder.create();
        } else {
            Log.d(TAG_LOG, "There is not an expert.");
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intentDatabaseService = new Intent(getActivity(), DatabaseService.class);
        getActivity().bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);
    }

    public void setUsernameCliente(String username){
        usernameCliente = username;
    }

    public void setUsernameExpert(String username){
        usernameExpert = username;
    }
}





