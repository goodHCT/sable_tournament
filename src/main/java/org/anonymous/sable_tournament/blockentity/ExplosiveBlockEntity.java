package org.anonymous.sable_tournament.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.anonymous.sable_tournament.block.explosive.AbstractExplosiveBlock;
import org.anonymous.sable_tournament.registry.ModBlockEntities;

public class ExplosiveBlockEntity extends BlockEntity {
    private int explosionTicks = 0;

    public ExplosiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPLOSIVE.get(), pos, state);
    }

    public void setExplosionTicks(int ticks) {
        this.explosionTicks = ticks;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(blockEntity instanceof ExplosiveBlockEntity explosiveBE)) return;

        if (explosiveBE.explosionTicks > 0) {
            explosiveBE.explosionTicks--;
            
            if (explosiveBE.explosionTicks == 0) {
                if (state.getBlock() instanceof AbstractExplosiveBlock explosiveBlock) {
                    level.destroyBlock(pos, false);
                    
                    level.explode(
                            null,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            explosiveBlock.getExplosionStrength(),
                            Level.ExplosionInteraction.BLOCK
                    );
                    
                    explosiveBlock.explode(serverLevel, pos);
                }
            }
        }
    }
}
