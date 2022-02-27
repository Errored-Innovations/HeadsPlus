package io.github.thatsmusic99.headsplus.util.prompts;

import io.github.thatsmusic99.headsplus.config.MessagesManager;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatPrompt extends StringPrompt {


    @NotNull
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return MessagesManager.get().getString("commands.heads.chat-input", (Player) conversationContext.getForWhom());
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData("term", s);
        return END_OF_CONVERSATION;
    }
}
