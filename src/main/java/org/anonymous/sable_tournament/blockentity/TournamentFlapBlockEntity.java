package org.anonymous.sable_tournament.blockentity;

import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.anonymous.sable_tournament.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;

public class TournamentFlapBlockEntity extends BlockEntity implements BlockSubLevelLiftProvider {
    
    // Configurable drag coefficient (default: 2.0)
    private double dragCoefficient = 2.0;
    
    public TournamentFlapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOURNAMENT_FLAP.get(), pos, state);
    }
    
    public double getDragCoefficient() {
        return dragCoefficient;
    }
    
    public void setDragCoefficient(double dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
        setChanged();
    }
    
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeData(tag);
    }
    
    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readData(tag);
    }
    
    /**
     * Write NBT data
     */
    private void writeData(CompoundTag tag) {
        tag.putDouble("DragCoefficient", dragCoefficient);
    }
    
    /**
     * Read NBT data
     */
    private void readData(CompoundTag tag) {
        if (tag.contains("DragCoefficient")) {
            dragCoefficient = tag.getDouble("DragCoefficient");
        }
    }
    
    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeData(tag);
        return tag;
    }
    
    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        readData(tag);
    }
    
    @Override
    public float sable$getLiftScalar() {
        return 0.0f; // No lift for symmetric flap
    }
    
    @Override
    public float sable$getParallelDragScalar() {
        return (float) dragCoefficient;
    }
    
    @Override
    public Direction sable$getNormal(BlockState blockState) {
        // Return direction based on axis property
        return Direction.get(Direction.AxisDirection.POSITIVE, blockState.getValue(org.anonymous.sable_tournament.block.TournamentFlapBlock.AXIS));
    }
}
