package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 08/06/17.
 */
public class Convite {

    @Exclude
    public transient String id;
    @SerializedName("nomeGrupo")
    public String nomeGrupo;
    @SerializedName("remetente")
    public String remetente;
    @SerializedName("destinatario")
    public String destinatario;

    public Convite() {}

    public Convite(String pNomeGrupo, Usuario remetente, String destinatario){
        this.nomeGrupo = pNomeGrupo;
        this.remetente = remetente.email;
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "Convite {" +
                "nomeGrupo=" + nomeGrupo +
                ", remetente='" + remetente + '\'' +
                ", destinatário='" + destinatario + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Convite convite = (Convite) o;

        if (!nomeGrupo.equals(convite.nomeGrupo)) return false;
        if (!remetente.equals(convite.remetente)) return false;
        return destinatario.equals(convite.destinatario);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + nomeGrupo.hashCode();
        result = 31 * result + remetente.hashCode();
        result = 31 * result + destinatario.hashCode();
        return result;
    }
}
