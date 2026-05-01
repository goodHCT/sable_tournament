# Sable Tournament

为 Sable 物理引擎打造的航空学竞赛模组，包含六向推进器、旋转器和竞赛襟翼。

## 功能特性

### 六向推进器 (Six-Way Thruster)
- 从6个方向（上下左右前后）接收红石信号
- 每个方向的推力倍数可独立配置（通过 NBT）
- 支持全局倍数调节
- 推力 = 方向倍数 × 全局倍数 × 红石信号强度(0-15)

### 六向旋转器 (Six-Way Spinner)
- 从6个方向接收红石信号控制旋转
- 每个方向的扭矩倍数可独立配置
- 扭矩向量映射：
  - **上/下**: 偏航旋转（绕 Y 轴）
  - **北/南**: 俯仰旋转（绕 X 轴）
  - **东/西**: 翻滚旋转（绕 Z 轴）

### 竞赛襟翼 (Tournament Flap)
- 基于 Simulated 的 Symmetric Sail 设计
- **无视高度**，在任何位置都能提供空气动力学效果
- 只提供阻力，不产生升力（适合用作控制面）
- 可沿任意轴放置（X/Y/Z）
- **阻力系数可通过 NBT 调节**（默认：2.0）
- 升力系数：0.0（对称设计）

## 使用方法

1. 将方块放置在 Sable 子层级结构中
2. 在方块的6个相邻位置放置红石源
3. 红石信号强度决定推力/扭矩大小（0-15）
4. 可通过命令或数据包修改 NBT 参数来调整倍数

## NBT 参数

### 六向推进器
```json
{
  "force_multiplier_north": 1.0,
  "force_multiplier_east": 1.0,
  "force_multiplier_south": 1.0,
  "force_multiplier_west": 1.0,
  "force_multiplier_up": 1.0,
  "force_multiplier_down": 1.0,
  "global_multiplier": 1.0
}
```

### 六向旋转器
```json
{
  "torque_multiplier_north": 1.0,
  "torque_multiplier_east": 1.0,
  "torque_multiplier_south": 1.0,
  "torque_multiplier_west": 1.0,
  "torque_multiplier_up": 1.0,
  "torque_multiplier_down": 1.0,
  "global_multiplier": 1.0
}
```

### 竞赛襟翼
```json
{
  "DragCoefficient": 2.0
}
```

**修改示例**：
```bash
# 将阻力系数设置为 5.0（更高阻力）
/data merge block <x> <y> <z> {DragCoefficient: 5.0}

# 将阻力系数设置为 0.5（更低阻力）
/data merge block <x> <y> <z> {DragCoefficient: 0.5}
```

## 依赖

- Minecraft 1.21.1
- NeoForge 21.1.227+
- Sable 1.0.6+

## 许可证

MIT License
