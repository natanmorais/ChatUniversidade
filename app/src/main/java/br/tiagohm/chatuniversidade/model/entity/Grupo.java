package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Grupo implements Serializable {

    @Exclude
    public String id;
    @SerializedName("admin")
    public Usuario admin;
    @SerializedName("instituicao")
    public String instituicao;
    @SerializedName("nome")
    public String nome;
    @SerializedName("tipo")
    public int tipo;
    @SerializedName("usuarios")
    public HashMap<String, Usuario> usuarios = new HashMap<>();

    public Grupo() {
    }

    public Grupo(Usuario admin, String instituicao, String nome, int tipo) {
        this.admin = admin;
        this.instituicao = instituicao;
        this.nome = nome;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id='" + id + '\'' +
                ", admin=" + admin +
                ", instituicao='" + instituicao + '\'' +
                ", nome='" + nome + '\'' +
                ", tipo=" + tipo +
                ", usuarios=" + usuarios +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grupo grupo = (Grupo) o;

        if (tipo != grupo.tipo) return false;
        if (!admin.equals(grupo.admin)) return false;
        if (!instituicao.equals(grupo.instituicao)) return false;
        return nome.equals(grupo.nome);

    }

    @Override
    public int hashCode() {
        int result = admin.hashCode();
        result = 31 * result + instituicao.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + tipo;
        return result;
    }
}
