package com.interfacciabili.benessere;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.interfacciabili.benessere.model.Cliente;
import com.interfacciabili.benessere.model.Dietologo;
import com.interfacciabili.benessere.model.Esperto;

public class DetailHomeFragment extends Fragment {
    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String EXPERT = "EXPERT";
    private static final String CLIENTE = "CLIENTE";
    private static final String TAG_LOG = "DetailHomeFragment";

    private int layoutID;
    private Cliente cliente;
    private Esperto esperto;

    public static DetailHomeFragment newInstance(int layoutID) {
        DetailHomeFragment fragment = new DetailHomeFragment();

        Bundle bundleFragment = new Bundle();
        bundleFragment.putInt(LAYOUT_ID, layoutID);

        fragment.setArguments(bundleFragment);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            layoutID = getArguments().getInt(LAYOUT_ID);

            if (getArguments().containsKey(CLIENTE)) {
                cliente = getArguments().getParcelable(CLIENTE);
                esperto = getArguments().getParcelable(EXPERT);
            } else {
                Log.d(TAG_LOG, "The bundle doesn't contain a client.");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutID, container, false);

        if ((layoutID == R.layout.dettagli_cliente) && (cliente != null)) {
            Toolbar action = (Toolbar) rootView.findViewById(R.id.toolbar_main);
            action.setVisibility(View.GONE);

            TextView tvUsername = rootView.findViewById(R.id.tvDettaglioUsernameCliente);
            tvUsername.setText(cliente.toString());

            ImageView ivFotoCliente = rootView.findViewById(R.id.ivFotoCliente);
            if(cliente.getFotoProfilo()!=null){
                ivFotoCliente.setImageURI(Uri.parse(cliente.getFotoProfilo()));
            }

            Button btnGestisci = rootView.findViewById(R.id.btnGestisciDieta);
            Button btnElimina = rootView.findViewById(R.id.btnElimina);

            btnGestisci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(esperto instanceof Dietologo){
                        Intent intentTo = new Intent(getContext(), InserimentoDieta.class);
                        intentTo.putExtra(CLIENTE, cliente);
                        startActivity(intentTo);
                    } else {
                        Intent intentTo = new Intent(getContext(), InserimentoScheda.class);
                        intentTo.putExtra(CLIENTE, cliente);
                        startActivity(intentTo);
                    }
                }
            });

            btnElimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EliminaClienteDialog ecd = new EliminaClienteDialog();
                    ecd.setCliente(cliente);
                    ecd.setEsperto(esperto);
                    ecd.show(getFragmentManager(), getString(R.string.eliminareCliente));
                }
            });
        } else {
            Log.d(TAG_LOG, "There is not a client.");
        }

        return rootView;
    }

}