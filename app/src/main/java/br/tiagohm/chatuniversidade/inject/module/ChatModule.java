package br.tiagohm.chatuniversidade.inject.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import dagger.Module;
import dagger.Provides;

@Module
public class ChatModule {

    private Application mApp;

    public ChatModule(Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences() {
        return mApp.getSharedPreferences("Chat", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public ChatManager providesChatManager() {
        return new ChatManager();
    }

    @Provides
    @Singleton
    public Usuario providesUsuario(ChatManager chatManager) {
        return chatManager.getUsuario();
    }
}
