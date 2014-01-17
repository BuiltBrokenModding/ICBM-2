package icbm.explosion.missile.modular;

import icbm.core.prefab.BlockICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.render.tile.BlockRenderHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.UniversalElectricity;
import universalelectricity.api.vector.Vector3;
import calclavia.components.CalclaviaLoader;
import calclavia.lib.multiblock.link.IBlockActivate;
import calclavia.lib.multiblock.link.IMultiBlock;
import calclavia.lib.multiblock.link.TileMultiBlockPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Multi-block table use to hold a missile prototype while the player is working on the design.
 * 3x1x1 in size
 * 
 * @author DarkGuardsman
 */
public class BlockMissileAssembler extends BlockICBM
{
	public BlockMissileAssembler(int id)
	{
		super(id, "missileAssembler", UniversalElectricity.machine);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType()
	{
		return BlockRenderHandler.ID;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileMissileAssembler();
	}

	/** Called when the block is placed in the world. */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
	{
		super.onBlockPlacedBy(world, z, y, z, entityLiving, itemStack);
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof IMultiBlock)
		{
			CalclaviaLoader.blockMulti.createMultiBlockStructure((IMultiBlock) tileEntity);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity instanceof TileMissileAssembler)
		{
			ForgeDirection s = ((TileMissileAssembler) entity).placedSide;
			Vector3 vec = new Vector3(entity).translate(new Vector3(-s.offsetX, -s.offsetY, -s.offsetZ));

			return Block.blocksList[vec.getBlockID(world)] != null;
		}

		return false;
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y,
	 * z
	 * 
	 * @param rot
	 * @param placeSide
	 */
	public static boolean canPlaceBlockAt(World world, int x, int y, int z, ForgeDirection placeSide, int rot)
	{
		Vector3 pos = new Vector3(x, y, z);
		Block block = Block.blocksList[pos.getBlockID(world)];
		if (block == null || block.isBlockReplaceable(world, x, y, z))
		{
			Vector3[] vecs = TileMissileAssembler.getMultiBlockVectors(placeSide, (byte) rot);
			for (Vector3 vec : vecs)
			{
				block = Block.blocksList[pos.clone().translate(vec).getBlockID(world)];
				if (block != null && !block.isBlockReplaceable(world, x, y, z))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean canRotateBlockTo(World world, int x, int y, int z, ForgeDirection placeSide, int rot)
	{
		Vector3 pos = new Vector3(x, y, z);
		Block block = Block.blocksList[pos.getBlockID(world)];
		if (block == null || block.isBlockReplaceable(world, x, y, z) || block.blockID == ICBMExplosion.blockMissileAssembler.blockID)
		{
			Vector3[] vecs = TileMissileAssembler.getMultiBlockVectors(placeSide, (byte) rot);

			for (Vector3 vec : vecs)
			{
				block = Block.blocksList[pos.clone().translate(vec).getBlockID(world)];
				boolean isNotSubBlock = true;
				if (pos.clone().translate(vec).getTileEntity(world) instanceof TileMultiBlockPart)
				{
					Vector3 main = ((TileMultiBlockPart) pos.clone().translate(vec).getTileEntity(world)).getMainBlock();
					isNotSubBlock = !main.equals(new Vector3(x, y, z));
				}
				if (block != null && !block.isBlockReplaceable(world, x, y, z) && isNotSubBlock)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int s, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		byte rotation = 0;
		ForgeDirection side = ForgeDirection.UP;
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity instanceof TileMissileAssembler)
		{
			rotation = ((TileMissileAssembler) entity).rotationSide;
			side = ((TileMissileAssembler) entity).placedSide;
			if (rotation == 3)
			{
				rotation = 0;
			}
			else
			{
				rotation++;
			}
			if (canRotateBlockTo(world, x, y, z, side, rotation))
			{
				// Due to how multi blocks work we need to save and disable item drops for the
				// tileEntity
				// Then reload the tileEntity nbt into the newly created block&tileEntity
				NBTTagCompound tag = new NBTTagCompound();
				((TileMissileAssembler) entity).rotating = true;

				Vector3[] positions = ((TileMissileAssembler) entity).getMultiBlockVectors();
				((TileMissileAssembler) entity).setRotation(rotation);
				((TileMissileAssembler) entity).writeToNBT(tag);
				for (Vector3 position : positions)
				{
					new Vector3(entity).translate(position).setBlock(entity.worldObj, 0);
				}
				world.setBlock(x, y, z, this.blockID);
				entity = world.getBlockTileEntity(x, y, z);
				((TileMissileAssembler) entity).readFromNBT(tag);

				CalclaviaLoader.blockMulti.createMultiBlockStructure((IMultiBlock) entity);
				((TileMissileAssembler) entity).rotating = false;
				world.markBlockForUpdate(x, y, z);
				return true;

			}
			if (rotation == 3)
			{
				rotation = 0;
			}
			else
			{
				rotation++;
			}
			if (canRotateBlockTo(world, x, y, z, side, rotation))
			{
				// Due to how multi blocks work we need to save and disable item drops for the
				// tileEntity
				// Then reload the tileEntity nbt into the newly created block&tileEntity
				NBTTagCompound tag = new NBTTagCompound();
				((TileMissileAssembler) entity).rotating = true;

				Vector3[] positions = ((TileMissileAssembler) entity).getMultiBlockVectors();
				((TileMissileAssembler) entity).setRotation(rotation);
				((TileMissileAssembler) entity).writeToNBT(tag);
				for (Vector3 position : positions)
				{
					new Vector3(entity).translate(position).setBlock(entity.worldObj, 0);
				}
				world.setBlock(x, y, z, this.blockID);
				entity = world.getBlockTileEntity(x, y, z);
				((TileMissileAssembler) entity).readFromNBT(tag);

				CalclaviaLoader.blockMulti.createMultiBlockStructure((IMultiBlock) entity);
				((TileMissileAssembler) entity).rotating = false;
				world.markBlockForUpdate(x, y, z);
				return true;

			}
		}

		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (!world.isRemote && entity instanceof TileMissileAssembler)
		{
			if (!((TileMissileAssembler) entity).rotating)
			{
				this.dropBlockAsItem_do(world, x, y, z, new ItemStack(ICBMExplosion.blockMissileAssembler, 1, 0));
			}

			CalclaviaLoader.blockMulti.destroyMultiBlockStructure((IMultiBlock) world.getBlockTileEntity(x, y, z));
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	/** Called when the block is right clicked by the player */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof IBlockActivate)
			{
				return ((IBlockActivate) tileEntity).onActivated(player);
			}
		}

		return false;
	}

}
