package org.anonymous.sable_tournament.block.explosive;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.Explosion;
import org.anonymous.sable_tournament.blockentity.ExplosiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractExplosiveBlock extends BaseEntityBlock {
    // Abstract classes don't need a codec - subclasses will define their own
    
    public AbstractExplosiveBlock() {
        super(Properties.of()
                .mapColor(MapColor.SAND)
                .sound(SoundType.GRAVEL)
                .strength(2.0f, 2.0f));
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.POWER));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (!(level instanceof ServerLevel serverLevel)) return;

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            ignite(serverLevel, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExplosiveBlockEntity(pos, state);
    }

    public abstract void explode(ServerLevel level, BlockPos pos);

    public int explosionTicks() {
        return 0;
    }

    public void explodeTick(ServerLevel level, BlockPos pos) {
    }

    public final void ignite(ServerLevel level, BlockPos pos) {
        if (explosionTicks() > 0) {
            explodeTick(level, pos);
            try {
                ExplosiveBlockEntity be = (ExplosiveBlockEntity) level.getBlockEntity(pos);
                if (be != null) {
                    be.setExplosionTicks(explosionTicks());
                }
            } catch (Exception ignored) {
            }
        } else {
            level.destroyBlock(pos, false);

            level.explode(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    getExplosionStrength(),
                    Level.ExplosionInteraction.BLOCK
            );
            explode(level, pos);
        }
    }

    public float getExplosionStrength() {
        return 4.0F;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (levelB, posB, stateB, t) -> {
            ExplosiveBlockEntity.tick(levelB, posB, stateB, t);
        };
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!(level instanceof ServerLevel serverLevel)) return;

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            ignite(serverLevel, pos);
        }
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (level instanceof ServerLevel serverLevel) {
            ignite(serverLevel, pos);
        }
    }
}
