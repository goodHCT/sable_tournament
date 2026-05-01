package org.anonymous.sable_tournament.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.anonymous.sable_tournament.blockentity.GyroBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GyroBlock extends BaseEntityBlock {
    
    public static final MapCodec<GyroBlock> CODEC = simpleCodec(GyroBlock::new);
    
    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    
    @Override
    public MapCodec<GyroBlock> codec() {
        return CODEC;
    }
    
    public GyroBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        
        if (level.isClientSide) return;
        
        // 当陀螺仪放置时，通知Sable子系统更新
        notifySubLevelUpdate(level, pos);
    }
    
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        
        if (level.isClientSide()) return;
        
        // 当陀螺仪被破坏时，通知Sable子系统更新
        notifySubLevelUpdate((Level) level, pos);
    }
    
    private void notifySubLevelUpdate(Level level, BlockPos pos) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // 触发物理系统更新
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GyroBlockEntity gyroBE) {
                gyroBE.markPhysicsDirty();
            }
        }
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, org.anonymous.sable_tournament.registry.ModBlockEntities.GYRO.get(), 
            GyroBlockEntity::tick);
    }
    
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, 0);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new GyroBlockEntity(pos, state);
    }
}
