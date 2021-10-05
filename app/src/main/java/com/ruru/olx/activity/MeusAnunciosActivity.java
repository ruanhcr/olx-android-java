package com.ruru.olx.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ruru.olx.R;
import com.ruru.olx.adapter.AdapterAnuncios;
import com.ruru.olx.databinding.ActivityMeusAnunciosBinding;
import com.ruru.olx.helper.ConfiguracaoFirebase;
import com.ruru.olx.helper.RecyclerItemClickListener;
import com.ruru.olx.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeusAnunciosBinding binding;
    private RecyclerView recyclerMeusAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios").child(ConfiguracaoFirebase.getIdUsuario());
        recyclerMeusAnuncios = findViewById(R.id.recyclerMeusAnuncios);

        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
        recyclerMeusAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerMeusAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerMeusAnuncios.setAdapter(adapterAnuncios);

        recuperarAnuncios();

        recyclerMeusAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerMeusAnuncios,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Anuncio anuncioSelecionado = anuncios.get(position);
                        anuncioSelecionado.remover();
                        adapterAnuncios.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
    }

    private void recuperarAnuncios(){
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Anúncios").setCancelable(false).build();
        dialog.show();
        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncios.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}