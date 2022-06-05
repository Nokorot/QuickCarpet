package quickcarpet.logging;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import quickcarpet.QuickCarpetServer;
import quickcarpet.utils.Translations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static quickcarpet.utils.Messenger.t;

@FunctionalInterface
public interface LogHandler {
    MapCodec<LogHandler> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
        Codec.STRING.fieldOf("name").forGetter(h -> LogHandlers.getCreatorName(LogHandlers.getCreator(h))),
        Codec.STRING.listOf().optionalFieldOf("extra").forGetter(h -> Optional.ofNullable(h.getExtraArgs()))
    ).apply(it, (name, extra) -> LogHandlers.createHandler(name, extra.map(strings -> strings.toArray(new String[0])).orElseGet(() -> new String[0]))));

    LogHandler CHAT = (logger, player, message, commandParams) -> player.sendMessage(t("chat.type.announcement", logger.getDisplayName(), Translations.translate(message, player)), false);

    LogHandler HUD = new LogHandler() {
        @Override
        public void handle(Logger logger, ServerPlayerEntity player, MutableText message, Supplier<Map<String, Object>> commandParams) {
           HUDController.addMessage(player, message);
        }

        @Override
        public void onRemovePlayer(String playerName) {
            ServerPlayerEntity player = QuickCarpetServer.getMinecraftServer().getPlayerManager().getPlayer(playerName);
            if (player != null)
                HUDController.clearPlayerHUD(player);
        }
    };

    LogHandler ACTION_BAR = (logger, player, message, commandParams) -> player.networkHandler.sendPacket(new OverlayMessageS2CPacket(message));

    @FunctionalInterface
    interface LogHandlerCreator {
        LogHandler create(String... extraArgs);

        default boolean usesExtraArgs() {
            return false;
        }
    }

    void handle(Logger logger, ServerPlayerEntity player, MutableText message, Supplier<Map<String, Object>> commandParams);
    default void onAddPlayer(String playerName) {}
    default void onRemovePlayer(String playerName) {}
    default List<String> getExtraArgs() {
        return null;
    }
    default LogHandlerCreator getCreator() {
        return null;
    }
}
