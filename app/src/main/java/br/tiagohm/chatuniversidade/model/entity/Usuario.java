package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um usuário logado dentro do sistema.
 */
public class Usuario implements Serializable, IUser {

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
    @SerializedName("grupos")
    public List<Grupo> grupos = new ArrayList<>(0);

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

    @Override
    @Exclude
    public String getId() {
        return email;
    }

    @Override
    @Exclude
    public String getName() {
        return nome;
    }

    @Override
    @Exclude
    public String getAvatar() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        if (tipo != usuario.tipo) return false;
        if (!instituicao.equals(usuario.instituicao)) return false;
        if (!nome.equals(usuario.nome)) return false;
        if (!matricula.equals(usuario.matricula)) return false;
        return email.equals(usuario.email);

    }

    @Override
    public int hashCode() {
        int result = instituicao.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + tipo;
        result = 31 * result + matricula.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}