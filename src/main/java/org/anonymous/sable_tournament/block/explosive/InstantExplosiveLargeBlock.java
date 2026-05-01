package org.anonymous.sable_tournament.block.explosive;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import org.anonymous.sable_tournament.registry.ModBlocks;

public class InstantExplosiveLargeBlock extends AbstractExplosiveBlock {
    public static final MapCodec<InstantExplosiveLargeBlock> CODEC = simpleCodec(properties -> new InstantExplosiveLargeBlock());

    @Override
    protected MapCodec<? extends AbstractExplosiveBlock> codec() {
        return CODEC;
    }

    public InstantExplosiveLargeBlock() {
        super();
    }

    @Override
    public void explode(ServerLevel level, BlockPos pos) {
        level.explode(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                getExplosionStrength(),
                Level.ExplosionInteraction.BLOCK
        );

        int range = (int) (getExplosionStrength() / 2.0) + 1;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.hasChunkAt(checkPos)) {
                        var blockState = level.getBlockState(checkPos);
                        var block = blockState.getBlock();

                        if (block == Blocks.TNT ||
                                block == ModBlocks.EXPLOSIVE_INSTANT_SMALL.get() ||
                                block == ModBlocks.EXPLOSIVE_INSTANT_MEDIUM.get() ||
                                block == ModBlocks.EXPLOSIVE_INSTANT_LARGE.get()) {

                            if (block instanceof AbstractExplosiveBlock explosiveBlock) {
                                if (level.getBlockEntity(checkPos) == null) {
                                    explosiveBlock.explode(level, checkPos);
                                }
                            } else if (block == Blocks.TNT) {
                                if (blockState.hasProperty(TntBlock.UNSTABLE)) {
                                    level.setBlock(checkPos, Blocks.TNT.defaultBlockState().setValue(TntBlock.UNSTABLE, true), 3);
                                } else {
                                    ((TntBlock) block).onCaughtFire(blockState, level, checkPos, null, null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getExplosionStrength() {
        return 7.0F;
    }
}
