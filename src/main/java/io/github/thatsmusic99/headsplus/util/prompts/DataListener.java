package io.github.thatsmusic99.headsplus.util.prompts;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DataListener extends StringPrompt {
    private static final List<String> types = Arrays.asList("id", "texture", "displayname", "price", "section");
    private final Set<String> sections = ConfigCustomHeads.get().sections.keySet();
    private final String message;
    private final int type;

    public DataListener(int id, String message) {
        this.type = id;
        if (id == 4) {
            message = message.replaceAll("\\{sections}", Arrays.toString(sections.toArray()));
        }
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
        HeadsPlusMessagesManager messages = HeadsPlusMessagesManager.get();
        Conversable user = context.getForWhom();
        if (s.equalsIgnoreCase("cancel")) {
            user.sendRawMessage(messages.getString("commands.addhead.cancelled", (CommandSender) user));
            context.setSessionData("cancel", true);
            return END_OF_CONVERSATION;
        }
        String currentType = types.get(type);
        int neW = type + 1;
        if (currentType.equalsIgnoreCase("texture")) {
            if (s.equalsIgnoreCase("done")) {
                context.setSessionData(currentType, context.getSessionData(currentType));
                String fullTexture = String.valueOf(context.getSessionData("texture"));
                if (CachedValues.BASE64_PATTERN.matcher(fullTexture).matches()) {
                    context.setSessionData("encode", false);
                } else if (CachedValues.MINECRAFT_TEXTURES_PATTERN.matcher(fullTexture).matches()) {
                    context.setSessionData("encode", true);
                } else {
                    context.setSessionData("texture", null);
                    user.sendRawMessage(messages.getString("commands.addhead.bad-texture"));
                    return new DataListener(type, messages.getString("commands.addhead." + currentType, (CommandSender) user));
                }
                return new DataListener(neW, messages.getString("commands.addhead." + types.get(neW), (CommandSender) user));
            } else if (context.getSessionData("texture") != null) {
                context.setSessionData("texture", context.getSessionData("texture") + s);
                return new DataListener(type, messages.getString("commands.addhead." + currentType, (CommandSender) user));
            } else {
                context.setSessionData(currentType, s);
                return new DataListener(type, messages.getString("commands.addhead." + currentType, (CommandSender) user));
            }

        } else if (currentType.equalsIgnoreCase("price")) {
            if (!CachedValues.DOUBLE_PATTERN.matcher(s).matches() && !s.equalsIgnoreCase("default")) {
                user.sendRawMessage(messages.getString("commands.errors.invalid-args", (CommandSender) user));
                return new DataListener(type, messages.getString("commands.addhead." + currentType));
            }
        } else if (currentType.equalsIgnoreCase("id")) {
            if (ConfigCustomHeads.get().headsCache.containsKey(s)) {
                user.sendRawMessage(messages.getString("commands.addhead.id-taken", (CommandSender) user).replaceAll("\\{id}", s));
                return new DataListener(type, messages.getString("commands.addhead." + currentType, (CommandSender) user));
            }
        } else if (currentType.equalsIgnoreCase("section")) {
            if (!sections.contains(s)) {
                user.sendRawMessage(messages.getString("commands.errors.invalid-args", (CommandSender) user));
                return new DataListener(type, messages.getString("commands.addhead." + currentType));
            }
        }
        context.setSessionData(currentType, s);
        if (type == 4) {
            return END_OF_CONVERSATION;
        }
        return new DataListener(neW, messages.getString("commands.addhead." + types.get(neW), (CommandSender) user));
    }
}
