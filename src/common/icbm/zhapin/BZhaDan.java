package icbm.zhapin;

import icbm.BaoHu;
import icbm.ZhuYao;
import icbm.api.ICBM;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import buildcraft.api.tools.IToolWrench;

public class BZhaDan extends BlockContainer
{
	public BZhaDan(int par1, int par2)
	{
		super(par1, par2, Material.tnt);
		this.setHardness(0.0F);
		this.setBlockName("Explosives");
		this.setStepSound(soundGrassFootstep);
		this.setRequiresSelfNotify();
		this.setCreativeTab(ZhuYao.TAB);
	}

	/**
	 * gets the way this piston should face for
	 * that entity that placed it.
	 */
	private static byte determineOrientation(World world, int x, int y, int z, EntityLiving entityLiving)
	{
		if (MathHelper.abs((float) entityLiving.posX - (float) x) < 2.0F && MathHelper.abs((float) entityLiving.posZ - (float) z) < 2.0F)
		{
			double var5 = entityLiving.posY + 1.82D - (double) entityLiving.yOffset;

			if (var5 - (double) y > 2.0D) { return 1; }

			if ((double) y - var5 > 0.0D) { return 0; }
		}

		int rotation = MathHelper.floor_double((double) (entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return (byte) (rotation == 0 ? 2 : (rotation == 1 ? 5 : (rotation == 2 ? 3 : (rotation == 3 ? 4 : 0))));
	}

	/**
	 * Called when the block is placed in the
	 * world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).explosiveID;

		if (!par1World.isRemote)
		{
			if (!BaoHu.nengDanBaoHu(par1World, new Vector2(x, z)))
			{
				this.dropBlockAsItem(par1World, x, y, z, explosiveID, 0);
				par1World.setBlockWithNotify(x, y, z, 0);
				return;
			}
		}

		par1World.setBlockMetadata(x, y, z, Vector3.getOrientationFromSide(ForgeDirection.getOrientation(determineOrientation(par1World, x, y, z, par5EntityLiving)), ForgeDirection.NORTH).ordinal());

		if (par1World.isBlockIndirectlyGettingPowered(x, y, z))
		{
			BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 0);
		}

		// Check to see if there is fire nearby.
		// If so, then detonate.
		for (byte i = 0; i < 6; i++)
		{
			Vector3 position = new Vector3(x, y, z);
			position.modifyPositionFromSide(ForgeDirection.getOrientation(i));

			int blockId = position.getBlockID(par1World);

			if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
			{
				BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 2);
			}
		}
	}

	/**
	 * Returns the block texture based on the side
	 * being looked at. Args: side
	 */
	@Override
	public int getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		int explosiveID = ((TZhaDan) par1IBlockAccess.getBlockTileEntity(x, y, z)).explosiveID;

		return this.getBlockTextureFromSideAndMetadata(side, explosiveID);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int explosiveID)
	{
		// Get the tier of the explosive and find
		// the row of it
		int displacement = 0;

		if (ZhaPin.list[explosiveID].getTier() == 1)
		{
			displacement = -1;
		}
		else if(ZhaPin.list[explosiveID].getTier() == 3)
		{
			displacement = 1;
		}
		else if (ZhaPin.list[explosiveID].getTier() == 4)
		{
			displacement = 2;
		}

		int rowPrefix = 16 + 16 * (ZhaPin.list[explosiveID].getTier() + displacement);

		int columnPrefix = explosiveID;

		switch (ZhaPin.list[explosiveID].getTier())
		{
			case 2:
				columnPrefix -= ZhaPin.MAX_TIER_ONE;
				break;
			case 3:
				columnPrefix -= ZhaPin.MAX_TIER_TWO;
				break;
			case 4:
				columnPrefix -= ZhaPin.MAX_TIER_THREE;
				break;
		}

		columnPrefix *= 3;

		return side == 0 ? rowPrefix + columnPrefix : (side == 1 ? rowPrefix + columnPrefix + 1 : rowPrefix + columnPrefix + 2);
	}

	/**
	 * Called whenever the block is added into the
	 * world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		super.onBlockAdded(par1World, x, y, z);

		int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).explosiveID;
	}

	/**
	 * Lets the block know when one of its
	 * neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int blockId)
	{
		int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).explosiveID;

		if ((blockId > 0 && Block.blocksList[blockId].canProvidePower() && par1World.isBlockIndirectlyGettingPowered(x, y, z)))
		{
			BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 0);
		}
		else if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
		{
			BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 2);
		}
	}

	/*
	 * Called to detonate the TNT. Args: world, x,
	 * y, z, metaData, CauseOfExplosion (0,
	 * intentional, 1, exploded, 2 burned)
	 */
	public static void detonateTNT(World par1World, int x, int y, int z, int metadata, int causeOfExplosion)
	{
		if (!par1World.isRemote)
		{
			if (BaoHu.nengDanBaoHu(par1World, new Vector2(x, z)))
			{
				ZhaPin.list[metadata].spawnZhaDan(par1World, new Vector3(x, y, z), ForgeDirection.getOrientation(par1World.getBlockMetadata(x, y, z)), (byte) causeOfExplosion);
				par1World.setBlockWithNotify(x, y, z, 0);
			}
		}
	}

	/**
	 * Called upon the block being destroyed by an
	 * explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World par1World, int x, int y, int z)
	{
		if (par1World.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).explosiveID;
			BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 1);
		}
	}

	/**
	 * Called upon block activation (left or right
	 * click on the block.). The three integers
	 * represent x,y,z of the block.
	 */
	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (par5EntityPlayer.getCurrentEquippedItem() != null)
		{
			if (par5EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex)
			{
				int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).explosiveID;
				BZhaDan.detonateTNT(par1World, x, y, z, explosiveID, 0);
				return true;
			}
			else if (par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench)
			{
				byte change = 3;

				// Reorient the block
				switch (((IRotatable) par1World.getBlockTileEntity(x, y, z)).getDirection().ordinal())
				{
					case 0:
						change = 2;
						break;
					case 2:
						change = 5;
						break;
					case 5:
						change = 3;
						break;
					case 3:
						change = 4;
						break;
					case 4:
						change = 1;
						break;
					case 1:
						change = 0;
						break;
				}

				par1World.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(change).ordinal());

				par1World.notifyBlockChange(x, y, z, this.blockID);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getTextureFile()
	{
		return ICBM.BLOCK_TEXTURE_FILE;
	}

	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TZhaDan) world.getBlockTileEntity(x, y, z)).explosiveID;

			return new ItemStack(this.blockID, 1, explosiveID);
		}

		return null;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		if (world.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TZhaDan) world.getBlockTileEntity(x, y, z)).explosiveID;

			int count = quantityDropped(metadata, fortune, world.rand);
			for (int i = 0; i < count; i++)
			{
				int id = idDropped(metadata, world.rand, 0);
				if (id > 0)
				{
					ret.add(new ItemStack(id, 1, explosiveID));
				}
			}
		}

		return ret;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ZhaPin.MAX_EXPLOSIVE_ID; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TZhaDan();
	}
}
