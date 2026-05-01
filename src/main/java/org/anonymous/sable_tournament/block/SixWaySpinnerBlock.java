package org.anonymous.sable_tournament.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.anonymous.sable_tournament.blockentity.SixWaySpinnerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SixWaySpinnerBlock extends BaseEntityBlock {
    
    public static final MapCodec<SixWaySpinnerBlock> CODEC = simpleCodec(SixWaySpinnerBlock::new);
    
    @Override
    public MapCodec<SixWaySpinnerBlock> codec() {
        return CODEC;
    }
    
    public SixWaySpinnerBlock(Properties properties) {
        super(properties);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SixWaySpinnerBlockEntity(pos, state);
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
        return createTickerHelper(type, org.anonymous.sable_tournament.registry.ModBlockEntities.SIX_WAY_SPINNER_BE.get(), 
            SixWaySpinnerBlockEntity::serverTick);
    }
}
