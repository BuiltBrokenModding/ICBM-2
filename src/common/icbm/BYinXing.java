package icbm;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.BlockMachine;

public class BYinXing extends BlockMachine
{
	protected BYinXing(int id)
	{
		super("camouflage", id, Material.cloth);
		this.setHardness(0.3F);
		this.setResistance(1F);
		this.setStepSound(this.soundClothFootstep);
		this.setCreativeTab(ZhuYao.TAB);
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z,
	 * side
	 */
	@Override
	public int getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		try
		{
			return Block.blocksList[((TYinXin) par1IBlockAccess.getBlockTileEntity(x, y, z)).getFakeBlock()].getBlockTexture(par1IBlockAccess, x, y, z, side);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (par5EntityPlayer.getCurrentEquippedItem() != null)
		{
			Block block = Block.blocksList[par5EntityPlayer.getCurrentEquippedItem().itemID];

			if (block != null)
			{
				if ((block.getRenderType() == 0 || block.getRenderType() == 31) && block.blockID <= 145)
				{
					((TYinXin) par1World.getBlockTileEntity(x, y, z)).setFakeBlock(block.blockID);
					par1World.markBlockForRenderUpdate(x, y, z);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color.
	 * Note only called when first determining what to render.
	 */
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int x, int y, int z)
	{
		try
		{
			return Block.blocksList[((TYinXin) par1IBlockAccess.getBlockTileEntity(x, y, z)).getFakeBlock()].colorMultiplier(par1IBlockAccess, x, y, x);
		}
		catch (Exception e)
		{
			return 16777215;
		}
	}

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
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TYinXin();
	}
}
