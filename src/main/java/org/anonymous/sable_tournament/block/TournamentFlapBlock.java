package org.anonymous.sable_tournament.block;

import com.mojang.serialization.MapCodec;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.anonymous.sable_tournament.blockentity.TournamentFlapBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class TournamentFlapBlock extends BaseEntityBlock implements BlockSubLevelLiftProvider {
    
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    
    // Thread-local to pass drag coefficient from contributeLiftAndDrag to getParallelDragScalar
    private static final ThreadLocal<Float> THREAD_DRAG = ThreadLocal.withInitial(() -> null);
    
    public static final MapCodec<TournamentFlapBlock> CODEC = simpleCodec(TournamentFlapBlock::new);
    
    @Override
    public MapCodec<TournamentFlapBlock> codec() {
        return CODEC;
    }
    
    public TournamentFlapBlock(Properties properties) {
        super(properties);
        // Set default state with Y axis (horizontal)
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AXIS);
    }
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getNearestLookingDirection().getAxis());
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Box shape changes based on axis orientation
        // The flap is 4 pixels thick (from y=6 to y=10 in default orientation)
        return switch (state.getValue(AXIS)) {
            case Y -> Shapes.box(0.0, 0.375, 0.0, 1.0, 0.625, 1.0);  // Horizontal: flat panel
            case X -> Shapes.box(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);  // Vertical along X (thin in X)
            case Z -> Shapes.box(0.0, 0.0, 0.375, 1.0, 1.0, 0.625);  // Vertical along Z (thin in Z)
        };
    }
    
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, 0);
    }
    
    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
        } else {
            this.bounce(entity);
        }
    }
    
    private void bounce(Entity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.y < 0.0D) {
            double bounceFactor = 0.26F;
            entity.setDeltaMovement(velocity.x, -velocity.y * bounceFactor, velocity.z);
        }
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public float sable$getLiftScalar() {
        return 0.0f; // No lift for symmetric flap
    }
    
    @Override
    public float sable$getParallelDragScalar() {
        Float drag = THREAD_DRAG.get();
        return drag != null ? drag : 1.75f;
    }
    
    @Override
    public void sable$contributeLiftAndDrag(@NotNull LiftProviderContext ctx, @NotNull ServerSubLevel subLevel,
                                            @NotNull Pose3d localPose, double timeStep,
                                            @NotNull Vector3dc linearVelocity, @NotNull Vector3dc angularVelocity,
                                            @NotNull Vector3d linearImpulse, @NotNull Vector3d angularImpulse,
                                            @Nullable LiftProviderGroup group) {
        // Get drag coefficient from BlockEntity
        BlockEntity blockEntity = subLevel.getLevel().getBlockEntity(ctx.pos());
        float dragScalar = 1.75f;
        
        if (blockEntity instanceof TournamentFlapBlockEntity flapEntity) {
            dragScalar = (float) flapEntity.getDragCoefficient();
        }
        
        // Set thread-local drag coefficient
        THREAD_DRAG.set(dragScalar);
        try {
            // Call parent implementation - it will use our ThreadLocal value
            BlockSubLevelLiftProvider.super.sable$contributeLiftAndDrag(
                ctx, subLevel, localPose, timeStep, linearVelocity, angularVelocity, linearImpulse, angularImpulse, group
            );
        } finally {
            // Clean up thread-local
            THREAD_DRAG.remove();
        }
    }
    
    @Override
    public @NotNull Direction sable$getNormal(BlockState blockState) {
        // Return direction based on axis
        return Direction.get(Direction.AxisDirection.POSITIVE, blockState.getValue(AXIS));
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TournamentFlapBlockEntity(pos, state);
    }
}
