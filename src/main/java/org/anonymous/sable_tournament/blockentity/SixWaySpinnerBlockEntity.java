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

public class SixWaySpinnerBlockEntity extends BlockEntity {
    
    // Torque multipliers for each direction (default: 1.0)
    private final double[] torqueMultipliers = new double[6];
    
    // Global multiplier (default: 1.0)
    private double globalMultiplier = 1.0;
    
    // Last signal strengths for change detection
    private final int[] lastSignalStrengths = new int[6];
    
    public SixWaySpinnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIX_WAY_SPINNER_BE.get(), pos, state);
        
        // Initialize all torque multipliers to 1.0
        for (int i = 0; i < 6; i++) {
            torqueMultipliers[i] = 1.0;
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
        // Save torque multipliers for each direction
        for (Direction dir : Direction.values()) {
            String key = "torque_multiplier_" + dir.getName();
            tag.putDouble(key, torqueMultipliers[dir.ordinal()]);
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
        // Load torque multipliers
        for (Direction dir : Direction.values()) {
            String key = "torque_multiplier_" + dir.getName();
            if (tag.contains(key)) {
                torqueMultipliers[dir.ordinal()] = tag.getDouble(key);
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
     * Get torque multiplier for a specific direction
     */
    public double getTorqueMultiplier(Direction direction) {
        return torqueMultipliers[direction.ordinal()];
    }
    
    /**
     * Set torque multiplier for a specific direction
     */
    public void setTorqueMultiplier(Direction direction, double multiplier) {
        torqueMultipliers[direction.ordinal()] = multiplier;
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
     * Calculate torque vector based on direction and value
     * 
     * Rule: When looking FROM outside INTO the block (from the redstone source),
     * the structure should rotate CLOCKWISE around the axis pointing INTO the block.
     * 
     * Using right-hand rule: thumb points in torque direction, fingers curl in rotation direction.
     * For clockwise rotation when looking along an axis, torque vector points AWAY from viewer.
     * 
     * Mapping (torque vector points INTO the block for clockwise rotation):
     * UP    -> Looking down from above, clockwise = torque points DOWN (negative Y)
     * DOWN  -> Looking up from below, clockwise = torque points UP (positive Y)
     * NORTH -> Looking from north side, clockwise = torque points SOUTH (positive Z)
     * SOUTH -> Looking from south side, clockwise = torque points NORTH (negative Z)
     * EAST  -> Looking from east side, clockwise = torque points WEST (negative X)
     * WEST  -> Looking from west side, clockwise = torque points EAST (positive X)
     */
    private Vector3d getTorqueVectorForDirection(Direction direction, double torqueValue) {
        return switch (direction) {
            case UP -> new Vector3d(0.0, -torqueValue, 0.0);      // Clockwise from top view
            case DOWN -> new Vector3d(0.0, torqueValue, 0.0);     // Clockwise from bottom view
            case NORTH -> new Vector3d(0.0, 0.0, torqueValue);    // Clockwise from north side
            case SOUTH -> new Vector3d(0.0, 0.0, -torqueValue);   // Clockwise from south side
            case EAST -> new Vector3d(-torqueValue, 0.0, 0.0);    // Clockwise from east side
            case WEST -> new Vector3d(torqueValue, 0.0, 0.0);     // Clockwise from west side
        };
    }
    
    /**
     * Server tick method - called every game tick
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, SixWaySpinnerBlockEntity blockEntity) {
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
        
        // Check redstone signals from all 6 directions
        for (Direction direction : Direction.values()) {
            int currentSignal = level.getSignal(pos.relative(direction), direction);
            
            // Only apply torque if signal is active
            if (currentSignal > 0) {
                // Calculate total torque: direction_multiplier * global_multiplier * signal_strength
                double torqueMultiplier = blockEntity.getTorqueMultiplier(direction) * blockEntity.globalMultiplier;
                double totalTorque = torqueMultiplier * currentSignal;
                
                // Get torque vector for this direction
                Vector3d torqueVector = blockEntity.getTorqueVectorForDirection(direction, totalTorque);
                
                // Apply angular impulse (torque)
                handle.applyAngularImpulse(torqueVector);
            }
            
            // Update last signal strength
            blockEntity.lastSignalStrengths[direction.ordinal()] = currentSignal;
        }
    }
}
