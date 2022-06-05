package quickcarpet.test;

import net.minecraft.block.*;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class DispenserTest {
    private static final BlockPos DISPENSER_POS = new BlockPos(0, 2, 0);
    private static final BlockPos REDSTONE_BLOCK_POS = DISPENSER_POS.up();
    private static final BlockPos FRONT_POS = DISPENSER_POS.east();
    private static final ItemStack WATER_BOTTLE = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);

    private static void dispense(TestContext ctx, ItemStack ...stack) {
        ctx.expectEmptyContainer(DISPENSER_POS);
        DispenserBlockEntity dispenser = ((DispenserBlockEntity) ctx.getBlockEntity(DISPENSER_POS));
        assert dispenser != null;
        for (int i = 0; i < stack.length && i < 9; i++) {
            dispenser.setStack(i, stack[i]);
        }
        ctx.putAndRemoveRedstoneBlock(REDSTONE_BLOCK_POS, 1);
    }

    private static void dispense(TestContext ctx, ItemStack stack, Runnable runnable) {
        dispense(ctx, stack);
        ctx.waitAndRun(4, runnable);
    }

    private static void dispense(TestContext ctx, ItemStack stack, Item after, Block front, Item dispensed, Runnable post) {
        dispense(ctx, stack, () -> {
            ctx.expectBlock(front, FRONT_POS);
            if (after == null || after == Items.AIR) {
                ctx.expectEmptyContainer(DISPENSER_POS);
            } else {
                ctx.expectContainerWith(DISPENSER_POS, after);
            }
            if (dispensed == null || dispensed == Items.AIR) {
                ctx.dontExpectEntity(EntityType.ITEM);
            } else {
                ctx.expectItemAt(dispensed, FRONT_POS, 1);
            }
            if (post != null) {
                post.run();
            } else {
                ctx.complete();
            }
        });
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaPutWaterBucket(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.WATER_BUCKET), Items.BUCKET, Blocks.WATER, null, () -> {
            ctx.setBlockState(FRONT_POS, Blocks.LIGHT_BLUE_STAINED_GLASS);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron")
    public void vanillaPutWaterBucketCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.WATER_BUCKET), null, Blocks.CAULDRON, Items.WATER_BUCKET, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaGetWaterBucket(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER);
        dispense(ctx, new ItemStack(Items.BUCKET), Items.WATER_BUCKET, Blocks.AIR, null, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaPutLavaBucket(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.LAVA_BUCKET), Items.BUCKET, Blocks.LAVA, null, () -> {
            ctx.setBlockState(FRONT_POS, Blocks.ORANGE_STAINED_GLASS);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron")
    public void vanillaPutLavaBucketCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.LAVA_BUCKET), null, Blocks.CAULDRON, Items.LAVA_BUCKET, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaGetLavaBucket(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.LAVA);
        dispense(ctx, new ItemStack(Items.BUCKET), Items.LAVA_BUCKET, Blocks.AIR, null, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaPutPowderSnow(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.POWDER_SNOW_BUCKET), Items.BUCKET, Blocks.POWDER_SNOW, null, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron")
    public void vanillaPutPowderSnowCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.POWDER_SNOW_BUCKET), null, Blocks.CAULDRON, Items.POWDER_SNOW_BUCKET, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron")
    public void vanillaGetWaterBottleCauldron(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
        dispense(ctx, new ItemStack(Items.GLASS_BOTTLE), null, Blocks.WATER_CAULDRON, Items.GLASS_BOTTLE, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 3);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaFireCharge(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.COBBLESTONE);
        dispense(ctx, new ItemStack(Items.FIRE_CHARGE), null, Blocks.COBBLESTONE, null, () -> {
            ctx.expectEntityAround(EntityType.SMALL_FIREBALL, FRONT_POS, 1);
            ctx.killAllEntities();
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaShearVines(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.VINE.getDefaultState().with(VineBlock.WEST, true));
        dispense(ctx, new ItemStack(Items.SHEARS), Items.SHEARS, Blocks.VINE, null, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaGunpowder(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.STONE);
        dispense(ctx, new ItemStack(Items.GUNPOWDER), null, Blocks.STONE, Items.GUNPOWDER, null);
    }

    @GameTest(templateName = "dispenser_only")
    public void vanillaAxe(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.OAK_LOG);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), null, Blocks.OAK_LOG, Items.DIAMOND_AXE, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutLavaBucketCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.LAVA_BUCKET), Items.BUCKET, Blocks.LAVA_CAULDRON, null, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutWaterBottleCauldron1(TestContext ctx) {
        dispense(ctx, WATER_BOTTLE.copy(), Items.GLASS_BOTTLE, Blocks.WATER_CAULDRON, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 1);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutWaterBucketCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.WATER_BUCKET), Items.BUCKET, Blocks.WATER_CAULDRON, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 3);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutWaterBottleCauldron2(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 1));
        dispense(ctx, WATER_BOTTLE.copy(), Items.GLASS_BOTTLE, Blocks.WATER_CAULDRON, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 2);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutWaterBottleCauldron3(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 2));
        dispense(ctx, WATER_BOTTLE.copy(), Items.GLASS_BOTTLE, Blocks.WATER_CAULDRON, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 3);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutWaterBottleCauldron4(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
        dispense(ctx, WATER_BOTTLE.copy(), null, Blocks.WATER_CAULDRON, Items.POTION, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 3);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcGetWaterBottleCauldron(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
        dispense(ctx, new ItemStack(Items.GLASS_BOTTLE), Items.POTION, Blocks.WATER_CAULDRON, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 2);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersPlaceBlocks=all")
    public void qcPutPowderSnow(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.POWDER_SNOW_BUCKET), Items.BUCKET, Blocks.POWDER_SNOW, null, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcPutPowderSnowCauldron(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.POWDER_SNOW_BUCKET), Items.BUCKET, Blocks.POWDER_SNOW_CAULDRON, null, null);
    }

    @GameTest(templateName = "dispenser_with_cauldron", batchId = "rules/dispensersInteractCauldron=true")
    public void qcGetWaterBottleCauldronFull(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
        dispense(ctx,
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2),
            new ItemStack(Items.GLASS_BOTTLE, 2)
        );
        ctx.waitAndRun(4, () -> {
            ctx.expectItemAt(Items.POTION, FRONT_POS, 1);
            ctx.expectBlockProperty(FRONT_POS, LeveledCauldronBlock.LEVEL, 2);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersPlaceBlocks=all")
    public void qcPutBlock1(TestContext ctx) {
        dispense(ctx, new ItemStack(Blocks.LIME_CONCRETE), null, Blocks.LIME_CONCRETE, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersPlaceBlocks=all")
    public void qcPutBlock2(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WATER);
        dispense(ctx, new ItemStack(Blocks.COBBLESTONE_SLAB), null, Blocks.COBBLESTONE_SLAB, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, SlabBlock.WATERLOGGED, true);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersPlaceBlocks=all")
    public void qcPutBlock3(TestContext ctx) {
        dispense(ctx, new ItemStack(Blocks.SEA_PICKLE), null, Blocks.SEA_PICKLE, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, SeaPickleBlock.PICKLES, 1);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersPlaceBlocks=all")
    public void qcPutBlock4(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.SEA_PICKLE);
        dispense(ctx, new ItemStack(Blocks.SEA_PICKLE), null, Blocks.SEA_PICKLE, null, () -> {
            ctx.expectBlockProperty(FRONT_POS, SeaPickleBlock.PICKLES, 2);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/renewableNetherrack=true")
    public void qcFireCharge(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.COBBLESTONE);
        dispense(ctx, new ItemStack(Items.FIRE_CHARGE), null, Blocks.NETHERRACK, null, () -> {
            ctx.dontExpectEntity(EntityType.SMALL_FIREBALL);
            ctx.complete();
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersShearVines=true")
    public void qcShearVines(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.VINE.getDefaultState().with(VineBlock.WEST, true));
        dispense(ctx, new ItemStack(Items.SHEARS), Items.SHEARS, Blocks.AIR, Items.VINE, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersBreakBlocks=normal")
    public void qcGunpowderNormal(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.STONE);
        dispense(ctx, new ItemStack(Items.GUNPOWDER), null, Blocks.AIR, Items.COBBLESTONE, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersBreakBlocks=silk_touch")
    public void qcGunpowderSilk(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.STONE);
        dispense(ctx, new ItemStack(Items.GUNPOWDER), null, Blocks.AIR, Items.STONE, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true")
    public void qcAxe1(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.AIR, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersScrapeCopper=true")
    public void qcAxe2(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.AIR, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true,dispensersScrapeCopper=true")
    public void qcAxe3(TestContext ctx) {
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.AIR, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true")
    public void qcAxeLog1(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.OAK_LOG);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.STRIPPED_OAK_LOG, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true,dispensersScrapeCopper=true")
    public void qcAxeLog2(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.OAK_LOG);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.STRIPPED_OAK_LOG, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersScrapeCopper=true")
    public void qcAxeLog3(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.OAK_LOG);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.OAK_LOG, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true")
    public void qcAxeCopper1(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WAXED_OXIDIZED_COPPER);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.WAXED_OXIDIZED_COPPER, null, null);
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersStripLogs=true,dispensersScrapeCopper=true")
    public void qcAxeCopper2(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WAXED_OXIDIZED_COPPER);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.OXIDIZED_COPPER, null, () -> {
            ((DispenserBlockEntity) ctx.getBlockEntity(DISPENSER_POS)).clear();
            dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.WEATHERED_COPPER, null, null);
        });
    }

    @GameTest(templateName = "dispenser_only", batchId = "rules/dispensersScrapeCopper=true")
    public void qcAxeCopper3(TestContext ctx) {
        ctx.setBlockState(FRONT_POS, Blocks.WAXED_OXIDIZED_COPPER);
        dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.OXIDIZED_COPPER, null, () -> {
            ((DispenserBlockEntity) ctx.getBlockEntity(DISPENSER_POS)).clear();
            dispense(ctx, new ItemStack(Items.DIAMOND_AXE), Items.DIAMOND_AXE, Blocks.WEATHERED_COPPER, null, null);
        });
    }
}
