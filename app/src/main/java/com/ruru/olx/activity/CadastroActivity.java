package com.ruru.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.ruru.olx.R;
import com.ruru.olx.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        
        inicializarComponentes();
        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                
                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        //verificar o estado do switch
                        if(tipoAcesso.isChecked()){//cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        //direcionar para a tela principal do app
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                    }else{
                                        String excesao = "";
                                        try{
                                            throw task.getException();
                                        }catch(FirebaseAuthWeakPasswordException e){
                                            excesao = "Digite uma senha mais forte!";
                                        }catch(FirebaseAuthInvalidCredentialsException e){
                                            excesao = "Digite um e-mail válido!";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            excesao = "Essa conta já foi cadastrada!";
                                        }catch(Exception e){
                                            excesao = "ao cadastrar usuário!" + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(CadastroActivity.this, "Erro: " + excesao, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{//login
                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(CadastroActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                                        //enviar para a tela principal
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                    }else{
                                        Toast.makeText(CadastroActivity.this, "Erro ao fazer login: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "Peencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
    }
}