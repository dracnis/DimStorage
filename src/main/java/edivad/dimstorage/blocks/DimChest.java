package edivad.dimstorage.blocks;

import javax.annotation.Nullable;

import edivad.dimstorage.tile.TileEntityDimChest;
import edivad.dimstorage.tile.TileFrequencyOwner;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DimChest extends DimBlockBase {

	public DimChest()
	{
		super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(5.0F));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new TileEntityDimChest();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if(worldIn.isRemote)
			return true;

		TileEntity tile = worldIn.getTileEntity(pos);
		if(!(tile instanceof TileFrequencyOwner) || !(tile instanceof INamedContainerProvider))
			return false;

		TileFrequencyOwner owner = (TileFrequencyOwner) tile;

		return !player.isSneaking() && owner.activate(player, worldIn, pos, handIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileFrequencyOwner)
		{
			((TileFrequencyOwner) tile).onPlaced(placer);
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		return (te instanceof TileEntityDimChest) ? ((TileEntityDimChest) te).getComparatorInput() : 0;
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
	}
}
