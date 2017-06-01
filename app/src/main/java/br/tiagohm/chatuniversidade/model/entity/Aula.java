package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 01/06/17.
 */
public class Aula {

    @Exclude
    public transient String id;
    @SerializedName("titulo")
    public String titulo;
    @SerializedName("conteudo")
    public String conteudo;

    public Aula() {}

    public Aula(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return "Aula {" +
                "titulo=" + titulo +
                ", conteudo='" + conteudo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aula aula = (Aula) o;

        if (!titulo.equals(aula.titulo)) return false;
        return conteudo.equals(aula.conteudo);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + titulo.hashCode();
        result = 31 * result + conteudo.hashCode();
        return result;
    }
}
