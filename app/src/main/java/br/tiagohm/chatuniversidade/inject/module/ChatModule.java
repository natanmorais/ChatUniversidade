package br.tiagohm.chatuniversidade.inject.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

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
}
