package org.anonymous.sable_tournament.blockentity;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
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
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class GyroBlockEntity extends BlockEntity {
    
    private int powerTop = 0;
    
    // Eureka稳定参数（去除质量和速度影响）
    private static final double STABILIZATION_TORQUE_CONSTANT = 7.5;
    private static final double DEAD_ZONE = 0.01;
    
    private boolean physicsDirty = true;
    
    public GyroBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GYRO.get(), pos, state);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, GyroBlockEntity blockEntity) {
        if (level.isClientSide) return;
        
        blockEntity.updateRedstonePower(level, pos);
        
        // 每tick都执行稳定，不需要physicsDirty标志
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.applyStabilization(serverLevel);
        }
    }
    
    private void updateRedstonePower(Level level, BlockPos pos) {
        int powerUp = level.getSignal(pos.relative(Direction.UP), Direction.UP);
        this.powerTop = powerUp;
    }
    
    /**
     * 应用Eureka风格的水平稳定
     * 基于船只当前上方向与世界坐标上方向的夹角计算稳定力矩
     */
    private void applyStabilization(ServerLevel serverLevel) {
        SubLevel subLevel = Sable.HELPER.getContaining(serverLevel, worldPosition);
        if (!(subLevel instanceof ServerSubLevel serverSubLevel)) return;
        
        RigidBodyHandle handle = RigidBodyHandle.of(serverSubLevel);
        if (handle == null) return;
        
        // 获取当前姿态
        Quaterniond orientation = serverSubLevel.logicalPose().orientation();
        
        // 计算船只当前的上方向向量
        Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
        orientation.transform(shipUp);
        
        // 世界坐标系的上方向
        Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
        
        // 计算夹角
        double angleBetween = shipUp.angle(worldUp);
        
        // 如果夹角很小，不需要稳定
        if (angleBetween < DEAD_ZONE) {
            return;
        }
        
        // 计算理想角加速度：需要旋转的轴和角度
        // 叉乘得到旋转轴
        Vector3d stabilizationRotationAxis = shipUp.cross(worldUp, new Vector3d()).normalize();
        
        // 理想角加速度 = 旋转轴 * 夹角 * 稳定常数（P控制器）
        Vector3d idealAngularAcceleration = stabilizationRotationAxis.mul(angleBetween * STABILIZATION_TORQUE_CONSTANT, new Vector3d());
        
        // 减去当前角速度的X和Z分量（只稳定俯仰和翻滚，不影响Y轴转弯）
        Vector3d currentOmega = handle.getAngularVelocity(new Vector3d());
        idealAngularAcceleration.sub(currentOmega.x(), 0.0, currentOmega.z());
        
        // 红石信号控制：顶部信号越强，稳定力度越弱
        // powerTop = 0: 完全稳定 (controlStrength = 1.0)
        // powerTop = 15: 完全关闭 (controlStrength = 0.0)
        double controlStrength = 1.0 - (powerTop / 15.0);
        
        if (controlStrength < 0.01) {
            return;
        }
        
        // 根据红石信号强度缩放
        idealAngularAcceleration.mul(controlStrength);
        
        // 直接施加角速度修正（简化版本，不通过力矩）
        handle.addLinearAndAngularVelocity(new Vector3d(), idealAngularAcceleration);
    }
    
    public void markPhysicsDirty() {
        this.physicsDirty = true;
    }
    
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("PowerTop", powerTop);
    }
    
    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PowerTop")) {
            this.powerTop = tag.getInt("PowerTop");
        }
    }
    
    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }
    
    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }
}
