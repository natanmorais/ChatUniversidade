package br.tiagohm.chatuniversidade.common;

import android.app.Application;

import br.tiagohm.chatuniversidade.inject.component.ChatComponent;
import br.tiagohm.chatuniversidade.inject.component.DaggerChatComponent;
import br.tiagohm.chatuniversidade.inject.module.ChatModule;

public class App extends Application {

    private static ChatComponent mChatComponent;

    public static ChatComponent getChatComponent() {
        return mChatComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mChatComponent = DaggerChatComponent.builder()
                .chatModule(new ChatModule(this))
                .build();
    }
}
