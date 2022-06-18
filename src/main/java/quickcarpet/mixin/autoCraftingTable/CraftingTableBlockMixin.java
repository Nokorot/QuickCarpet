package quickcarpet.mixin.autoCraftingTable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quickcarpet.feature.CraftingTableBlockEntity;
import quickcarpet.utils.mixin.extensions.DynamicBlockEntityProvider;

import javax.annotation.Nullable;

import static quickcarpet.settings.Settings.autoCraftingTable;

@Mixin(CraftingTableBlock.class)
public class CraftingTableBlockMixin extends Block implements DynamicBlockEntityProvider {
    protected CraftingTableBlockMixin(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    public boolean quickcarpet$providesBlockEntity() {
        return autoCraftingTable;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CraftingTableBlockEntity(pos, state);
    }

    @Nullable
    private CraftingTableBlockEntity getBlockEntity(World world, BlockPos pos) {
        if (!quickcarpet$providesBlockEntity()) return null;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CraftingTableBlockEntity) {
            return (CraftingTableBlockEntity) blockEntity;
        }
        return null;
    }

    @Inject(method = "createScreenHandlerFactory", at = @At("HEAD"), cancellable = true)
    private void quickcarpet$autoCraftingTable$onCreateScreenHandler(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<NamedScreenHandlerFactory> cir) {
        CraftingTableBlockEntity blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null) {
            cir.setReturnValue(blockEntity);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorOutput(BlockState blockState) {
        return quickcarpet$providesBlockEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorOutput(BlockState blockState, World world, BlockPos pos) {
        CraftingTableBlockEntity blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null) {
            int filled = 0;
            for (ItemStack stack : blockEntity.inventory) {
                if (!stack.isEmpty()) filled++;
            }
            return (filled * 15) / 9;
        }
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state1, World world, BlockPos pos, BlockState state2, boolean boolean_1) {
        if (state1.getBlock() != state2.getBlock()) {
            CraftingTableBlockEntity blockEntity = getBlockEntity(world, pos);
            if (blockEntity != null) {
                ItemScatterer.spawn(world, pos, blockEntity.inventory);
                if (!blockEntity.output.isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), blockEntity.output);
                }
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state1, world, pos, state2, boolean_1);
        }
    }
}
