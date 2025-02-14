package quickcarpet.mixin.fillLimit;

import net.minecraft.server.command.CloneCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import quickcarpet.settings.Settings;

@Mixin(CloneCommand.class)
public class CloneCommandMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private static int quickcarpet$fillLimit(int old) {
        return Settings.fillLimit;
    }
}
