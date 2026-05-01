package org.anonymous.sable_tournament;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.anonymous.sable_tournament.registry.ModBlocks;
import org.anonymous.sable_tournament.registry.ModBlockEntities;
import org.anonymous.sable_tournament.registry.ModItems;
import org.slf4j.Logger;

@Mod(SableTournament.MODID)
public class SableTournament {
    public static final String MODID = "sable_tournament";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = 
        CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sable_tournament.main"))
            .icon(() -> ModItems.SIX_WAY_THRUSTER_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // Thrusters and Spinners
                output.accept(ModItems.SIX_WAY_THRUSTER_ITEM.get());
                output.accept(ModItems.SIX_WAY_SPINNER_ITEM.get());
                
                // Gyro
                output.accept(ModItems.GYRO_ITEM.get());
                
                // Aerodynamic
                output.accept(ModItems.TOURNAMENT_FLAP_ITEM.get());
                
                // Explosives
                output.accept(ModItems.EXPLOSIVE_INSTANT_SMALL_ITEM.get());
                output.accept(ModItems.EXPLOSIVE_INSTANT_MEDIUM_ITEM.get());
                output.accept(ModItems.EXPLOSIVE_INSTANT_LARGE_ITEM.get());
            })
            .build());
    
    public SableTournament(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        
        modEventBus.addListener(this::addCreative);
        
        LOGGER.info("Sable Tournament initialized!");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup complete");
    }
    
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.SIX_WAY_THRUSTER_ITEM.get());
            event.accept(ModItems.SIX_WAY_SPINNER_ITEM.get());
            event.accept(ModItems.GYRO_ITEM.get());
        }
    }
}
