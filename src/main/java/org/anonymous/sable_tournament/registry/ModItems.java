package org.anonymous.sable_tournament.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.anonymous.sable_tournament.SableTournament;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SableTournament.MODID);
    
    // Six-Way Thruster Item with tooltip
    public static final DeferredItem<BlockItem> SIX_WAY_THRUSTER_ITEM = 
        ITEMS.register("six_way_thruster", () -> new BlockItem(ModBlocks.SIX_WAY_THRUSTER.get(), 
            new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.sable_tournament.six_way_thruster.tooltip1")
                    .withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.translatable("item.sable_tournament.six_way_thruster.tooltip2")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.sable_tournament.six_way_thruster.tooltip3")
                    .withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, context, tooltip, flag);
            }
        });
    
    // Six-Way Spinner Item with tooltip
    public static final DeferredItem<BlockItem> SIX_WAY_SPINNER_ITEM = 
        ITEMS.register("six_way_spinner", () -> new BlockItem(ModBlocks.SIX_WAY_SPINNER.get(), 
            new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.sable_tournament.six_way_spinner.tooltip1")
                    .withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.translatable("item.sable_tournament.six_way_spinner.tooltip2")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.sable_tournament.six_way_spinner.tooltip3")
                    .withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, context, tooltip, flag);
            }
        });
    
    // Tournament Flap Item (with custom model and tooltip)
    public static final DeferredItem<BlockItem> TOURNAMENT_FLAP_ITEM = 
        ITEMS.register("tournament_flap", () -> new BlockItem(ModBlocks.TOURNAMENT_FLAP.get(), 
            new Item.Properties()) {
            @Override
            public String getDescriptionId() {
                return "block." + SableTournament.MODID + ".tournament_flap";
            }
            
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.sable_tournament.tournament_flap.tooltip1")
                    .withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.translatable("item.sable_tournament.tournament_flap.tooltip2")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.sable_tournament.tournament_flap.tooltip3")
                    .withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, context, tooltip, flag);
            }
        });
    
    // Gyro Item with tooltip
    public static final DeferredItem<BlockItem> GYRO_ITEM = 
        ITEMS.register("gyro", () -> new BlockItem(ModBlocks.GYRO.get(), 
            new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("item.sable_tournament.gyro.tooltip1")
                    .withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.translatable("item.sable_tournament.gyro.tooltip2")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.sable_tournament.gyro.tooltip3")
                    .withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, context, tooltip, flag);
            }
        });
    
    // Instant Explosive Small Item
    public static final DeferredItem<BlockItem> EXPLOSIVE_INSTANT_SMALL_ITEM = 
        ITEMS.registerSimpleBlockItem("explosive_instant_small", ModBlocks.EXPLOSIVE_INSTANT_SMALL);
    
    // Instant Explosive Medium Item
    public static final DeferredItem<BlockItem> EXPLOSIVE_INSTANT_MEDIUM_ITEM = 
        ITEMS.registerSimpleBlockItem("explosive_instant_medium", ModBlocks.EXPLOSIVE_INSTANT_MEDIUM);
    
    // Instant Explosive Large Item
    public static final DeferredItem<BlockItem> EXPLOSIVE_INSTANT_LARGE_ITEM = 
        ITEMS.registerSimpleBlockItem("explosive_instant_large", ModBlocks.EXPLOSIVE_INSTANT_LARGE);
}
