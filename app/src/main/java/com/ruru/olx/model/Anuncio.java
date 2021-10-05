package com.ruru.olx.model;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.ruru.olx.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {
    private String idAnuncio, estado, categoria, titulo, valor, telefone, descricao;
    private List<String> fotos;

    public Anuncio() {

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public void salvar(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios");
        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);
        salvarAnuncioPublico();
    }

    public void salvarAnuncioPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");
        anuncioRef.child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }

    public void remover(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child(idUsuario)
                .child(getIdAnuncio());
        anuncioRef.removeValue();
        removerAnuncioPublico();
    }

    public void removerAnuncioPublico(){
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());
        anuncioRef.removeValue();
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
