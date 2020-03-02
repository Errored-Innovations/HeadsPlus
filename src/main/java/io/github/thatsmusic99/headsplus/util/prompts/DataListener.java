package io.github.thatsmusic99.headsplus.util.prompts;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DataListener extends StringPrompt {
    private static final List<String> types = Arrays.asList("id", "texture", "encode", "displayname", "price", "section");
    private String message;
    private int type;

    public DataListener(int id, String message) {
        this.type = id;
        this.message = message;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return message;
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String s) {
        assert s != null;
        String currentType = types.get(type);
        int neW = type + 1;
        if (currentType.equalsIgnoreCase("texture")) {
            if (s.equalsIgnoreCase("done")) {
                context.setSessionData(currentType, context.getSessionData(currentType) + s);
                return new DataListener(neW, types.get(neW));
            } if (context.getSessionData("texture") != null) {
                context.setSessionData("texture", context.getSessionData("texture") + s);
                return new DataListener(neW, types.get(neW));
            } else {
                context.setSessionData(currentType, s);
                return new DataListener(type, currentType);
            }

        } else if (currentType.equalsIgnoreCase("price")) {
            Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
            if (!pattern.matcher(s).matches()) {
                context.getForWhom().sendRawMessage("Invalid input");
                return new DataListener(type, currentType);
            }
        } else if (currentType.equalsIgnoreCase("id")) {
            if (HeadsPlus.getInstance().getHeadsXConfig().headsCache.containsKey(s)) {
                return new DataListener(type, currentType);
            }
        }
        context.setSessionData(currentType, s);
        if (type == 5) {
            return END_OF_CONVERSATION;
        }
        return new DataListener(neW, types.get(neW));
    }
}
