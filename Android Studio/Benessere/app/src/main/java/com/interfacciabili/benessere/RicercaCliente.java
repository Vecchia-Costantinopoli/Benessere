package com.interfacciabili.benessere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.interfacciabili.benessere.control.DietDBHelper;
import com.interfacciabili.benessere.model.Cliente;

public class RicercaCliente extends AppCompatActivity {

    TextView etName;
    Button btnAggiungi;
    ListView lvRicercaClienti;
    ArrayAdapter clientAdapter;
    DietDBHelper dietDBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_cliente);


        dietDBH = new DietDBHelper(RicercaCliente.this);

        btnAggiungi = findViewById(R.id.btnCerca);
        etName = findViewById(R.id.etName);
        lvRicercaClienti = findViewById(R.id.lvRicercaClienti);
        lvRicercaClienti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cliente clienteCliccato = (Cliente) parent.getItemAtPosition(position);
                InserisciClienteDialog icd = new InserisciClienteDialog();
                icd.setDietologo("Dietologo1");
                icd.setUsername(clienteCliccato.getUsername());
                icd.show(getSupportFragmentManager(), "Inserisci cliente");
            }
        });

    }

    public void cercaCliente(View v){
        if(!etName.getText().toString().isEmpty()){
            clientAdapter = new ArrayAdapter<Cliente>(RicercaCliente.this,
                    android.R.layout.simple_list_item_1,
                    dietDBH.recuperaClientiSenzaDietologo(etName.getText().toString()));
            lvRicercaClienti.setAdapter(clientAdapter);
        }
    }

}