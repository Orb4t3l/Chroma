package com.orbital.chroma.block;

import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DyeingTableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART  = EnumProperty.create("part", Part.class);

    private static final int TABLE_BOTTOM_Y = 6;
    private static final int TABLE_THICKNESS = 2;
    private static final int LAYER_GAP = 5;
    
    private static final int TABLE_TOP_Y = TABLE_BOTTOM_Y + TABLE_THICKNESS;
    private static final int SECOND_LAYER_BOTTOM_Y = TABLE_TOP_Y + LAYER_GAP;
    private static final int SECOND_LAYER_TOP_Y = SECOND_LAYER_BOTTOM_Y + TABLE_THICKNESS;

    private static final VoxelShape TABLE_BOTTOM_LAYER = Block.box(0, TABLE_BOTTOM_Y, 0, 16, TABLE_TOP_Y, 16);
    private static final VoxelShape TABLE_TOP_LAYER = Block.box(0, SECOND_LAYER_BOTTOM_Y, 0, 16, SECOND_LAYER_TOP_Y, 16);
    private static final VoxelShape TABLE_TOP = Shapes.or(TABLE_BOTTOM_LAYER, TABLE_TOP_LAYER);
    
    private static final VoxelShape LEG_NW = Block.box(0, 0, 0, 2, TABLE_BOTTOM_Y, 2);
    private static final VoxelShape LEG_NE = Block.box(14, 0, 0, 16, TABLE_BOTTOM_Y, 2);
    private static final VoxelShape LEG_SW = Block.box(0, 0, 14, 2, TABLE_BOTTOM_Y, 16);
    private static final VoxelShape LEG_SE = Block.box(14, 0, 14, 16, TABLE_BOTTOM_Y, 16);

    private static final VoxelShape SHAPE_CONTROLLER_NORTH = Shapes.or(TABLE_TOP, LEG_NE, LEG_SE);
    private static final VoxelShape SHAPE_CONTROLLER_EAST = Shapes.or(TABLE_TOP, LEG_SE, LEG_SW);
    private static final VoxelShape SHAPE_CONTROLLER_SOUTH = Shapes.or(TABLE_TOP, LEG_SW, LEG_NW);
    private static final VoxelShape SHAPE_CONTROLLER_WEST = Shapes.or(TABLE_TOP, LEG_NW, LEG_NE);

    private static final VoxelShape SHAPE_EXTENSION_NORTH = Shapes.or(TABLE_TOP, LEG_NW, LEG_SW);
    private static final VoxelShape SHAPE_EXTENSION_EAST = Shapes.or(TABLE_TOP, LEG_NE, LEG_NW);
    private static final VoxelShape SHAPE_EXTENSION_SOUTH = Shapes.or(TABLE_TOP, LEG_SE, LEG_NE);
    private static final VoxelShape SHAPE_EXTENSION_WEST = Shapes.or(TABLE_TOP, LEG_SW, LEG_SE);

    public DyeingTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.CONTROLLER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockPos extensionPos = ctx.getClickedPos().relative(facing.getCounterClockWise());
        if (!ctx.getLevel().getBlockState(extensionPos).canBeReplaced(ctx)) return null;
        return defaultBlockState().setValue(FACING, facing).setValue(PART, Part.CONTROLLER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            net.minecraft.world.entity.LivingEntity placer,
                            net.minecraft.world.item.ItemStack stack) {
        Direction facing = state.getValue(FACING);
        level.setBlock(pos.relative(facing.getCounterClockWise()),
                state.setValue(PART, Part.EXTENSION), 3);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Direction facing = state.getValue(FACING);
        Part part = state.getValue(PART);
        
        if (part == Part.CONTROLLER) {
            return switch (facing) {
                case NORTH -> SHAPE_CONTROLLER_NORTH;
                case EAST -> SHAPE_CONTROLLER_EAST;
                case SOUTH -> SHAPE_CONTROLLER_SOUTH;
                case WEST -> SHAPE_CONTROLLER_WEST;
                default -> TABLE_TOP;
            };
        } else {
            return switch (facing) {
                case NORTH -> SHAPE_EXTENSION_NORTH;
                case EAST -> SHAPE_EXTENSION_EAST;
                case SOUTH -> SHAPE_EXTENSION_SOUTH;
                case WEST -> SHAPE_EXTENSION_WEST;
                default -> TABLE_TOP;
            };
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.CONTROLLER ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            Direction facing = state.getValue(FACING);
            BlockPos other = state.getValue(PART) == Part.CONTROLLER
                    ? pos.relative(facing.getCounterClockWise())
                    : pos.relative(facing.getClockWise());
            if (level.getBlockState(other).is(this)) level.removeBlock(other, false);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos controller = state.getValue(PART) == Part.CONTROLLER
                ? pos : pos.relative(state.getValue(FACING).getClockWise());
        BlockEntity be = level.getBlockEntity(controller);
        if (be instanceof MenuProvider mp) {
            net.minecraftforge.network.NetworkHooks.openScreen(
                    (net.minecraft.server.level.ServerPlayer) player, mp, controller);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.CONTROLLER
                ? new DyeingTableBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) { return null; }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos,
                                  PathComputationType type) { return false; }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.BLOCK; }

    public enum Part implements net.minecraft.util.StringRepresentable {
        CONTROLLER("controller"), EXTENSION("extension");
        private final String name;
        Part(String n) { name = n; }
        @Override public String getSerializedName() { return name; }
    }
}