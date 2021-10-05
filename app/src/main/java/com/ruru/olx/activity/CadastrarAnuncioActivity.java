package com.ruru.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ruru.olx.R;
import com.ruru.olx.helper.ConfiguracaoFirebase;
import com.ruru.olx.helper.Permissoes;
import com.ruru.olx.model.Anuncio;
import com.santalu.maskedittext.MaskEditText;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog dialog;

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Anuncio anuncio;

    private Spinner spinnerEstado, spinnerCategoria;

    private ImageView imagem1, imagem2, imagem3;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();

    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        Permissoes.validarPermissoes(permissoes, this, 1);

        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //configura localidade
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);

        carregarDadosSpinner();
    }

    private void carregarDadosSpinner(){
        //spinner estados
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        //spinner categorias
        String[] categoria = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoria);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            //recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configura imagem no ImageView
            if(requestCode == 0){
                imagem1.setImageURI(imagemSelecionada);
            }else if(requestCode == 1){
                imagem2.setImageURI(imagemSelecionada);
            }else if(requestCode == 2){
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void validarDadosAnuncio(View view){
        anuncio = configurarAnuncio();
        String fone = "";
        String valor = String.valueOf(campoValor.getRawValue());
        if(campoTelefone.getRawText() != null){
            fone = campoTelefone.getRawText().toString();
        }

        if (listaFotosRecuperadas.size() != 0){
            if(!anuncio.getEstado().isEmpty()){
                if(!anuncio.getCategoria().isEmpty()){
                    if(!anuncio.getTitulo().isEmpty()){
                        if(!valor.isEmpty() && !valor.equals("0")){
                            if(!anuncio.getTelefone().isEmpty() && fone.length() >= 10){
                                if(!anuncio.getDescricao().isEmpty()){
                                    salvarAnuncio();
                                }else{
                                    exibirMensagemErro("Preencha a descrição!");
                                }
                            }else{
                                exibirMensagemErro("Preencha o número de telefone, digite ao menos 10 números!");
                            }
                        }else{
                            exibirMensagemErro("Preencha o valor!");
                        }
                    }else{
                        exibirMensagemErro("Preencha o título");
                    }
                }else{
                    exibirMensagemErro("Selecione a categoria!");
                }
            }else{
                exibirMensagemErro("Selecione o estado!");
            }
        }else{
            exibirMensagemErro("Selecione ao menos uma foto!");
        }
    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio(){

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Salvando Anúncio").setCancelable(false).build();
        dialog.show();

        //salvar as imagens no storage
        for(int i=0; i<listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador){
        //criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);

        //fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        String urlConvertida = url.toString();
                        listaURLFotos.add(urlConvertida);

                        if(totalFotos == listaURLFotos.size()){
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload!");
                Log.i("INFO", "Falha ao fazer upload: "+e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio(){
        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }
}