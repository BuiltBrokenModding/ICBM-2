package icbm;

import java.util.List;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockFence;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMobType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class BBuoLiPan extends ICBMBlock
{
	/**
	 * The mob type that can trigger this pressure plate.
	 */
	private EnumMobType triggerMobType;

	protected BBuoLiPan(int par1, int par2)
	{
		super(par1, par2, Material.glass);
		this.triggerMobType = EnumMobType.mobs;
		this.setTickRandomly(true);
		float var5 = 0.0625F;
		this.setBlockBounds(var5, 0.0F, var5, 1.0F - var5, 0.03125F, 1.0F - var5);
		this.setHardness(0.5F);
		this.setResistance(1F);
		this.setBlockName("glassPressurePlate");
		this.setCreativeTab(ZhuYao.TAB);
	}

	/**
	 * The glass pressure plate will not yield itself once broken.
	 * 
	 * @param par1Random
	 * @return 0
	 */
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate()
	{
		return 20;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after
	 * the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the
	 * shared face of two adjacent blocks and also whether the player can attach torches, redstone
	 * wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs,
	 * buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return true;
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y,
	 * z
	 */
	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	{
		return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
	 * (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
	{
		boolean var6 = false;

		if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4)))
		{
			var6 = true;
		}

		if (var6)
		{
			this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
			par1World.setBlockWithNotify(par2, par3, par4, 0);
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		if (!par1World.isRemote)
		{
			if (par1World.getBlockMetadata(par2, par3, par4) != 0)
			{
				this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
			}
		}
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world,
	 * x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if (!par1World.isRemote)
		{
			if (par1World.getBlockMetadata(par2, par3, par4) != 1)
			{
				this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
			}
		}
	}

	/**
	 * Checks if there are mobs on the plate. If a mob is on the plate and it is off, it turns it
	 * on, and vice versa.
	 */
	private void setStateIfMobInteractsWithPlate(World par1World, int par2, int par3, int par4)
	{
		boolean var5 = par1World.getBlockMetadata(par2, par3, par4) == 1;
		boolean var6 = false;
		float var7 = 0.125F;
		List var8 = null;

		if (this.triggerMobType == EnumMobType.everything)
		{
			var8 = par1World.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) ((float) par2 + var7), (double) par3, (double) ((float) par4 + var7), (double) ((float) (par2 + 1) - var7), (double) par3 + 0.25D, (double) ((float) (par4 + 1) - var7)));
		}

		if (this.triggerMobType == EnumMobType.mobs)
		{
			var8 = par1World.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) ((float) par2 + var7), (double) par3, (double) ((float) par4 + var7), (double) ((float) (par2 + 1) - var7), (double) par3 + 0.25D, (double) ((float) (par4 + 1) - var7)));
		}

		if (this.triggerMobType == EnumMobType.players)
		{
			var8 = par1World.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) ((float) par2 + var7), (double) par3, (double) ((float) par4 + var7), (double) ((float) (par2 + 1) - var7), (double) par3 + 0.25D, (double) ((float) (par4 + 1) - var7)));
		}

		if (!var8.isEmpty())
		{
			var6 = true;
		}

		if (var6 && !var5)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
			par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
			par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
			par1World.playSoundEffect((double) par2 + 0.5D, (double) par3 + 0.1D, (double) par4 + 0.5D, "random.click", 0.3F, 0.6F);
		}

		if (!var6 && var5)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
			par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
			par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
			par1World.playSoundEffect((double) par2 + 0.5D, (double) par3 + 0.1D, (double) par4 + 0.5D, "random.click", 0.3F, 0.5F);
		}

		if (var6)
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
		}
	}

	/**
	 * ejects contained items into the world, and notifies neighbours of an update, as appropriate
	 */
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		if (par6 > 0)
		{
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
			par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
		}

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		boolean var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1;
		float var6 = 0.0625F;

		if (var5)
		{
			this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.03125F, 1.0F - var6);
		}
		else
		{
			this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.0625F, 1.0F - var6);
		}
	}

	/**
	 * Is this block powering the block on the specified side
	 */
	@Override
	public boolean isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return par1IBlockAccess.getBlockMetadata(par2, par3, par4) > 0;
	}

	/**
	 * Is this block indirectly powering the block on the specified side
	 */
	@Override
	public boolean isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 0 ? false : par5 == 1;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its
	 * state.
	 */
	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender()
	{
		float var1 = 0.5F;
		float var2 = 0.125F;
		float var3 = 0.5F;
		this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
	}

	@Override
	public int getMobilityFlag()
	{
		return 1;
	}
}
