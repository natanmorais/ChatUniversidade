package br.tiagohm.chatuniversidade.model.entity;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.Date;

public class Conversa implements Serializable, IMessage {

    @Exclude
    public transient String id;
    @SerializedName("usuario")
    public Usuario usuario;
    @SerializedName("texto")
    public String texto;
    @SerializedName("data")
    public long data;

    public Conversa() {
    }

    public Conversa(Usuario usuario, String texto, long data) {
        this.usuario = usuario;
        this.texto = texto;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conversa conversa = (Conversa) o;

        if (data != conversa.data) return false;
        if (!usuario.equals(conversa.usuario)) return false;
        return texto.equals(conversa.texto);

    }

    @Override
    public int hashCode() {
        int result = usuario.hashCode();
        result = 31 * result + texto.hashCode();
        result = 31 * result + (int) (data ^ (data >>> 32));
        return result;
    }

    @Override
    @Exclude
    public String getId() {
        return id;
    }

    @Override
    @Exclude
    public String getText() {
        return usuario.nome + ": " + texto;
    }

    @Override
    @Exclude
    public IUser getUser() {
        return usuario;
    }

    @Override
    @Exclude
    public Date getCreatedAt() {
        return new Date(data);
    }
}
