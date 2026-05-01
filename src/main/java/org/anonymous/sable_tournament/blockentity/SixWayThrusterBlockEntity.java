package org.anonymous.sable_tournament.blockentity;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.anonymous.sable_tournament.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class SixWayThrusterBlockEntity extends BlockEntity {
    
    // Force multipliers for each direction (default: 1.0)
    private final double[] forceMultipliers = new double[6];
    
    // Global multiplier (default: 1.0)
    private double globalMultiplier = 1.0;
    
    // Last signal strengths for change detection
    private final int[] lastSignalStrengths = new int[6];
    
    public SixWayThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIX_WAY_THRUSTER_BE.get(), pos, state);
        
        // Initialize all force multipliers to 1.0
        for (int i = 0; i < 6; i++) {
            forceMultipliers[i] = 1.0;
            lastSignalStrengths[i] = 0;
        }
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
        // Save force multipliers for each direction
        for (Direction dir : Direction.values()) {
            String key = "force_multiplier_" + dir.getName();
            tag.putDouble(key, forceMultipliers[dir.ordinal()]);
        }
        
        // Save global multiplier
        tag.putDouble("global_multiplier", globalMultiplier);
        
        // Save last signal strengths
        for (Direction dir : Direction.values()) {
            String key = "last_signal_" + dir.getName();
            tag.putInt(key, lastSignalStrengths[dir.ordinal()]);
        }
    }
    
    /**
     * Read NBT data
     */
    private void readData(CompoundTag tag) {
        // Load force multipliers
        for (Direction dir : Direction.values()) {
            String key = "force_multiplier_" + dir.getName();
            if (tag.contains(key)) {
                forceMultipliers[dir.ordinal()] = tag.getDouble(key);
            }
        }
        
        // Load global multiplier
        if (tag.contains("global_multiplier")) {
            globalMultiplier = tag.getDouble("global_multiplier");
        }
        
        // Load last signal strengths
        for (Direction dir : Direction.values()) {
            String key = "last_signal_" + dir.getName();
            if (tag.contains(key)) {
                lastSignalStrengths[dir.ordinal()] = tag.getInt(key);
            }
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
    
    /**
     * Get force multiplier for a specific direction
     */
    public double getForceMultiplier(Direction direction) {
        return forceMultipliers[direction.ordinal()];
    }
    
    /**
     * Set force multiplier for a specific direction
     */
    public void setForceMultiplier(Direction direction, double multiplier) {
        forceMultipliers[direction.ordinal()] = multiplier;
        setChanged();
    }
    
    /**
     * Get global multiplier
     */
    public double getGlobalMultiplier() {
        return globalMultiplier;
    }
    
    /**
     * Set global multiplier
     */
    public void setGlobalMultiplier(double multiplier) {
        this.globalMultiplier = multiplier;
        setChanged();
    }
    
    /**
     * Server tick method - called every game tick
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, SixWayThrusterBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Get the containing sub-level
        ServerSubLevel subLevel = (ServerSubLevel) Sable.HELPER.getContaining(serverLevel, pos);
        if (subLevel == null) {
            return;
        }
        
        // Get rigid body handle using the correct API
        RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
        if (handle == null || !handle.isValid()) {
            return;
        }
        
        // Calculate center position in world space
        Vector3d blockWorldPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        
        // Check redstone signals from all 6 directions
        for (Direction direction : Direction.values()) {
            int currentSignal = level.getSignal(pos.relative(direction), direction);
            
            // Only apply force if signal is active
            if (currentSignal > 0) {
                // Calculate total thrust: direction_multiplier * global_multiplier * signal_strength
                double thrustMultiplier = blockEntity.getForceMultiplier(direction) * blockEntity.globalMultiplier;
                double totalThrust = thrustMultiplier * currentSignal;
                
                // Convert direction to normalized vector
                Vector3d forceVector = new Vector3d(
                    direction.getStepX() * totalThrust,
                    direction.getStepY() * totalThrust,
                    direction.getStepZ() * totalThrust
                );
                
                // Apply impulse at block position
                handle.applyImpulseAtPoint(blockWorldPos, forceVector);
            }
            
            // Update last signal strength
            blockEntity.lastSignalStrengths[direction.ordinal()] = currentSignal;
        }
    }
}
