package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Instituicao implements Serializable {

    @Exclude
    public String id;
    @SerializedName("sigla")
    public String sigla;
    @SerializedName("nome")
    public String nome;
    @SerializedName("endereco")
    public String endereco;
    @SerializedName("telefone")
    public String telefone;
    @SerializedName("email")
    public String email;

    public Instituicao() {
    }

    public Instituicao(String sigla, String nome, String endereco, String telefone, String email) {
        this.sigla = sigla;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instituicao that = (Instituicao) o;

        if (!sigla.equals(that.sigla)) return false;
        if (!nome.equals(that.nome)) return false;
        if (!endereco.equals(that.endereco)) return false;
        if (!telefone.equals(that.telefone)) return false;
        return email.equals(that.email);

    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + sigla.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + endereco.hashCode();
        result = 31 * result + telefone.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return nome;
    }
}
