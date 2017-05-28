package br.tiagohm.chatuniversidade.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Representa um usuário logado dentro do sistema.
 */
public class Usuario implements Serializable {

    @SerializedName("instituicao")
    public String instituicao;
    @SerializedName("nome")
    public String nome;
    @SerializedName("tipo")
    public int tipo;
    @SerializedName("matricula")
    public String matricula;
    @SerializedName("email")
    public String email;

    public Usuario() {
    }

    public Usuario(String instituicao, String nome, int tipo, String matricula,
                   String email) {
        this.instituicao = instituicao;
        this.nome = nome;
        this.tipo = tipo;
        this.matricula = matricula;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Usuario {" +
                "instituicao='" + instituicao + '\'' +
                ", nome='" + nome + '\'' +
                ", tipo=" + tipo +
                ", matricula='" + matricula + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}