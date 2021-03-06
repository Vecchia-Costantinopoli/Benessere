package com.interfacciabili.benessere;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.interfacciabili.benessere.control.DatabaseService;
import com.interfacciabili.benessere.model.Cliente;
import com.interfacciabili.benessere.model.Dietologo;

public class HomeDietologo extends AppCompatActivity implements EliminaClienteDialog.EliminaClienteDialogCallback, MasterHomeFragment.MasterHomeFragmentCallback {
    private static final String EXPERT = "EXPERT";
    private static final String CLIENTE = "CLIENTE";

    public DatabaseService databaseService;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatabaseService.LocalBinder localBinder = (DatabaseService.LocalBinder) service;
            databaseService = localBinder.getService();

            if (!landscapeView) {
                updateListview();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public Dietologo dietologo;
    public Cliente clienteCliccato;

    private boolean landscapeView;

    private ListView lvClienti;
    private ArrayAdapter clientAdapter;

    private FragmentManager fragmentManager = null;
    private Fragment detailFragment = null;
    private Fragment masterFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //INTENT DA LOGIN
        Intent intentFromLogin = getIntent();
        Bundle dietologoBundle = intentFromLogin.getExtras();

        if(dietologoBundle!=null && dietologoBundle.containsKey(EXPERT)){
            dietologo = dietologoBundle.getParcelable(EXPERT);
            //Toast.makeText(this, ""+ dietologo, Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CLIENTE)) {
            clienteCliccato = savedInstanceState.getParcelable(CLIENTE);
        }

        setContentView(R.layout.activity_home_dietologo);

        Toolbar homeToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(homeToolbar);
        homeToolbar.setSubtitle(dietologo.getUsername());

        fragmentManager = getSupportFragmentManager();

        /* Verifico l'orientamento del dispositivo per poi impostare di conseguenza l'activity. La variabile "landscapeView" sarà
         *  aggiornata di conseguenza ed utilizzata per scopi successivi.
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // VISUALIZZAZIONE ORIZZONTALE
            landscapeView = true;

            /* Recupero l'intent, verifico se vi è un cliente specifico da visualizzare ed aggiorno il fragment se la condizione è rispettata. Quest'activity, infatti,
             *  potrà essere lanciata da un'applicazione esterna o da un'altra activity come "DettagliCliente".
             */
            Intent intentFrom = getIntent();
            if (intentFrom != null && intentFrom.hasExtra(CLIENTE)) {
                clienteCliccato = intentFrom.getParcelableExtra(CLIENTE);
                dietologo = intentFrom.getParcelableExtra(EXPERT);
                updateClientDetailFragment(R.layout.dettagli_cliente);
            } else {
                updateClientDetailFragment(R.layout.dettagli_cliente_blank);
            }

            updateMasterFragment();
        } else {
            // VISUALIZZAZIONE VERTICALE
            landscapeView = false;

            if (clienteCliccato != null) {
                goToDettagliClienteActivity();
            }

            lvClienti = findViewById(R.id.lvClienti);
            lvClienti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clienteCliccato = (Cliente) parent.getItemAtPosition(position);
                    goToDettagliClienteActivity();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intentDatabaseService = new Intent(this, DatabaseService.class);
        bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);

        if(databaseService != null){
            if(!landscapeView){
                updateListview();
            } else {
                updateMasterFragment();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* Verifico se è stato creato il fragment, indice di una visualizzazione orizzontale. A questo proposito, salvo nell'oggetto Bundle
         *  il cliente da visualizzare per il successivo riavvio dell'activity.
         */
        if (detailFragment != null && clienteCliccato != null) {
            outState.putParcelable(CLIENTE, clienteCliccato);
        } else if ((clienteCliccato == null) && (outState.containsKey(CLIENTE))){
            outState.remove(CLIENTE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseService != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public void updateEliminaClienteDialogCallback() {
        clienteCliccato = null;
        getIntent().removeExtra(CLIENTE);

        updateMasterFragment();
        updateClientDetailFragment(R.layout.dettagli_cliente_blank);
    }

    @Override
    public void updateMasterHomeFragmentCallback(Cliente cliente) {
        clienteCliccato = cliente;
        updateClientDetailFragment(R.layout.dettagli_cliente);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dietologo, menu);

        MenuItem notificationButton = menu.findItem(R.id.notification_badge);

        int nRequests = databaseService.recuperaRichiesteNumeroDieta(dietologo.getUsername());
        if (nRequests > 0) {
            notificationButton.setActionView(R.layout.notification_badge);

            TextView notificationCounter = notificationButton.getActionView().findViewById(R.id.badgeCounter);
            notificationCounter.setText(String.valueOf(nRequests));

        } else {
            notificationButton.setActionView(null);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.notification_badge) {
            goToRichiesteDietologo(item.getActionView());
        }

        if(item.getItemId() == R.id.aggiornaProfiloButton){
            Intent goToAggiornaProfilo = new Intent(HomeDietologo.this, ModificaProfiloDietologo.class);
            goToAggiornaProfilo.putExtra(EXPERT, dietologo);
            startActivity(goToAggiornaProfilo);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToRichiesteDietologo(View v) {
        Intent goToRichiesteDietologo = new Intent(HomeDietologo.this, RichiesteDietologo.class);
        goToRichiesteDietologo.putExtra(EXPERT, dietologo);
        startActivity(goToRichiesteDietologo);
        finish();
    }

    public void updateMasterFragment() {
        masterFragment = MasterHomeFragment.newInstance();

        Bundle bundleFragment = masterFragment.getArguments();
        bundleFragment.putParcelable(EXPERT, dietologo);


        fragmentManager.beginTransaction().replace(R.id.homeMaster, masterFragment).commit();
    }

    public void updateClientDetailFragment(int layoutID) {
        detailFragment = DetailHomeFragment.newInstance(layoutID);
        if (layoutID == R.layout.dettagli_cliente) {
            Bundle bundleFragment = detailFragment.getArguments();
            bundleFragment.putParcelable(EXPERT, dietologo);
            bundleFragment.putParcelable(CLIENTE, clienteCliccato);

        }

        fragmentManager.beginTransaction().replace(R.id.homeDetail, detailFragment).commit();
    }

    private void updateListview() {
        clientAdapter = new ArrayAdapter<Cliente>(HomeDietologo.this, android.R.layout.simple_list_item_1, databaseService.recuperaClientiDiDietologo(dietologo.getUsername()));
        lvClienti.setAdapter(clientAdapter);
    }

    private void goToDettagliClienteActivity() {
        Intent intentTo = new Intent(HomeDietologo.this, DettagliCliente.class);
        intentTo.putExtra(EXPERT, dietologo);
        intentTo.putExtra(CLIENTE, clienteCliccato);
        startActivity(intentTo);
        finish();
    }

    //FLOATING ACTION BUTTON LISTENER
    public void aggiungiCliente(View view) {
        Intent intentTo = new Intent(HomeDietologo.this, RicercaCliente.class);
        intentTo.putExtra(EXPERT, dietologo);
        startActivity(intentTo);
    }
}