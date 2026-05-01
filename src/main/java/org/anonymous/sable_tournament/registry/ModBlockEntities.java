package org.anonymous.sable_tournament.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.anonymous.sable_tournament.SableTournament;
import org.anonymous.sable_tournament.blockentity.GyroBlockEntity;
import org.anonymous.sable_tournament.blockentity.SixWaySpinnerBlockEntity;
import org.anonymous.sable_tournament.blockentity.SixWayThrusterBlockEntity;
import org.anonymous.sable_tournament.blockentity.TournamentFlapBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SableTournament.MODID);
    
    // Six-Way Thruster Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SixWayThrusterBlockEntity>> SIX_WAY_THRUSTER_BE = 
        BLOCK_ENTITIES.register("six_way_thruster", () -> 
            BlockEntityType.Builder.of(SixWayThrusterBlockEntity::new, ModBlocks.SIX_WAY_THRUSTER.get()).build(null));
    
    // Six-Way Spinner Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SixWaySpinnerBlockEntity>> SIX_WAY_SPINNER_BE = 
        BLOCK_ENTITIES.register("six_way_spinner", () -> 
            BlockEntityType.Builder.of(SixWaySpinnerBlockEntity::new, ModBlocks.SIX_WAY_SPINNER.get()).build(null));
    
    // Tournament Flap Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TournamentFlapBlockEntity>> TOURNAMENT_FLAP = 
        BLOCK_ENTITIES.register("tournament_flap", () -> 
            BlockEntityType.Builder.of(TournamentFlapBlockEntity::new, ModBlocks.TOURNAMENT_FLAP.get()).build(null));
    
    // Gyro Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GyroBlockEntity>> GYRO = 
        BLOCK_ENTITIES.register("gyro", () -> 
            BlockEntityType.Builder.of(GyroBlockEntity::new, ModBlocks.GYRO.get())
                .build(null));
    
    // Explosive Block Entity (shared by all explosive types)
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<org.anonymous.sable_tournament.blockentity.ExplosiveBlockEntity>> EXPLOSIVE = 
        BLOCK_ENTITIES.register("explosive", () -> 
            BlockEntityType.Builder.of(
                org.anonymous.sable_tournament.blockentity.ExplosiveBlockEntity::new,
                ModBlocks.EXPLOSIVE_INSTANT_SMALL.get(),
                ModBlocks.EXPLOSIVE_INSTANT_MEDIUM.get(),
                ModBlocks.EXPLOSIVE_INSTANT_LARGE.get()
            ).build(null));
}
