package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Aula implements Serializable {

    @Exclude
    public String id;
    @SerializedName("titulo")
    public String titulo;
    @SerializedName("conteudo")
    public String conteudo;
    @SerializedName("data")
    public long data;

    public Aula() {
    }

    public Aula(String titulo, String conteudo, long data) {
        this.data = data;
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return "Aula {" +
                "data=" + data +
                "titulo=" + titulo +
                ", conteudo='" + conteudo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aula aula = (Aula) o;

        if (data != aula.data) return false;
        if (id != null ? !id.equals(aula.id) : aula.id != null) return false;
        if (!titulo.equals(aula.titulo)) return false;
        return conteudo.equals(aula.conteudo);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + titulo.hashCode();
        result = 31 * result + conteudo.hashCode();
        result = 31 * result + (int) (data ^ (data >>> 32));
        return result;
    }
}
