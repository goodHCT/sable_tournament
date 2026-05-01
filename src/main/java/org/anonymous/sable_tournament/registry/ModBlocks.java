package org.anonymous.sable_tournament.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.anonymous.sable_tournament.SableTournament;
import org.anonymous.sable_tournament.block.GyroBlock;
import org.anonymous.sable_tournament.block.SixWaySpinnerBlock;
import org.anonymous.sable_tournament.block.SixWayThrusterBlock;
import org.anonymous.sable_tournament.block.TournamentFlapBlock;
import org.anonymous.sable_tournament.block.explosive.InstantExplosiveLargeBlock;
import org.anonymous.sable_tournament.block.explosive.InstantExplosiveMediumBlock;
import org.anonymous.sable_tournament.block.explosive.InstantExplosiveSmallBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SableTournament.MODID);
    
    // Six-Way Thruster Block
    public static final DeferredBlock<SixWayThrusterBlock> SIX_WAY_THRUSTER = 
        BLOCKS.register("six_way_thruster", () -> new SixWayThrusterBlock(
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .noOcclusion()
        ));
    
    // Six-Way Spinner Block
    public static final DeferredBlock<SixWaySpinnerBlock> SIX_WAY_SPINNER = 
        BLOCKS.register("six_way_spinner", () -> new SixWaySpinnerBlock(
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .noOcclusion()
        ));
    
    // Tournament Flap Block (configurable aerodynamic wing)
    public static final DeferredBlock<TournamentFlapBlock> TOURNAMENT_FLAP = 
        BLOCKS.register("tournament_flap", () -> new TournamentFlapBlock(
            BlockBehaviour.Properties.of()
                .strength(2.0F, 4.0F)
                .noOcclusion()
        ));
    
    // Gyro Block (stabilizes ship orientation using redstone control)
    public static final DeferredBlock<GyroBlock> GYRO = 
        BLOCKS.register("gyro", () -> new GyroBlock(
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .noOcclusion()
        ));
    
    // Instant Explosive Small
    public static final DeferredBlock<InstantExplosiveSmallBlock> EXPLOSIVE_INSTANT_SMALL = 
        BLOCKS.register("explosive_instant_small", InstantExplosiveSmallBlock::new);
    
    // Instant Explosive Medium
    public static final DeferredBlock<InstantExplosiveMediumBlock> EXPLOSIVE_INSTANT_MEDIUM = 
        BLOCKS.register("explosive_instant_medium", InstantExplosiveMediumBlock::new);
    
    // Instant Explosive Large
    public static final DeferredBlock<InstantExplosiveLargeBlock> EXPLOSIVE_INSTANT_LARGE = 
        BLOCKS.register("explosive_instant_large", InstantExplosiveLargeBlock::new);
}
