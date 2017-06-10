package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Convite implements Serializable {

    @Exclude
    public transient String id;
    @SerializedName("grupo")
    public Grupo grupo;
    @SerializedName("remetente")
    public String remetente;
    @SerializedName("destinatario")
    public String destinatario;

    public Convite() {
    }

    public Convite(Grupo grupo, String remetente, String destinatario) {
        this.grupo = grupo;
        this.remetente = remetente;
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "Convite {" +
                ", remetente='" + remetente + '\'' +
                ", destinatário='" + destinatario + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Convite convite = (Convite) o;

        if (!grupo.equals(convite.grupo)) return false;
        if (!remetente.equals(convite.remetente)) return false;
        return destinatario.equals(convite.destinatario);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + grupo.hashCode();
        result = 31 * result + remetente.hashCode();
        result = 31 * result + destinatario.hashCode();
        return result;
    }
}
