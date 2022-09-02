package quickcarpet.logging;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.function.Supplier;

public class CommandLogHandler implements LogHandler {
    public final String command;

    public CommandLogHandler(String... args) {
        command = String.join(" ", args);
    }

    @Override
    public void handle(Logger logger, ServerPlayerEntity player, MutableText message, Supplier<Map<String, Object>> commandParams) {
        Map<String, Object> params = commandParams.get();
        String command = this.command;
        for (Map.Entry<String, ?> param : params.entrySet()) {
            String variable = "$" + param.getKey();
            if (!command.contains(variable)) continue;
            command = command.replace(variable, String.valueOf(param.getValue()));
        }
        if (command.contains("$$")) command = command.replace("$$", params.keySet().toString());
        if (command.contains("$text")) {
            command = command.replace("$text",  Text.Serializer.toJson(message));
        }
        player.server.getCommandManager().executeWithPrefix(player.getCommandSource(), command);
    }
}
