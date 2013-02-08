package icbm.contraption;

import icbm.api.ICBMTab;
import icbm.api.IEMPBlock;
import icbm.api.IExplosive;
import icbm.explosion.ZhuYao;
import icbm.explosion.render.RBYinXing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.BlockMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BYinXing extends BlockMachine implements IEMPBlock
{
	public BYinXing(int id, int texture)
	{
		super("camouflage", id, Material.cloth);
		this.blockIndexInTexture = texture;
		this.setHardness(0.3F);
		this.setResistance(1F);
		this.setStepSound(this.soundClothFootstep);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ZhuYao.BLOCK_TEXTURE_FILE);
	}

	@Override
	public void onEMP(World world, Vector3 position, IExplosive empExplosive)
	{
		TileEntity tileEntity = position.getTileEntity(world);

		if (tileEntity instanceof TYinXing)
		{
			((TYinXing) tileEntity).setFangGe(0, 0);
			((TYinXing) tileEntity).setQing(false);
			world.markBlockForRenderUpdate(position.intX(), position.intY(), position.intZ());
		}
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z,
	 * side
	 */
	@Override
	public int getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity t = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				TYinXing tileEntity = (TYinXing) t;

				if (tileEntity.getQing(ForgeDirection.getOrientation(side)))
				{
					return Block.glass.blockIndexInTexture;
				}

				Block block = Block.blocksList[tileEntity.getJiaHaoMa()];

				if (block != null)
				{
					try
					{
						return Block.blocksList[tileEntity.getJiaHaoMa()].getBlockTextureFromSideAndMetadata(side, tileEntity.getJiaMetadata());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return this.blockIndexInTexture;
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		try
		{
			if (par5EntityPlayer.getCurrentEquippedItem() != null)
			{
				if (par5EntityPlayer.getCurrentEquippedItem().itemID < Block.blocksList.length)
				{
					Block block = Block.blocksList[par5EntityPlayer.getCurrentEquippedItem().itemID];

					if (block != null)
					{
						if (block.isOpaqueCube() && (block.getRenderType() == 0 || block.getRenderType() == 31))
						{
							((TYinXing) par1World.getBlockTileEntity(x, y, z)).setFangGe(block.blockID, par5EntityPlayer.getCurrentEquippedItem().getItemDamage());
							par1World.markBlockForRenderUpdate(x, y, z);
							return true;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				((TYinXing) par1World.getBlockTileEntity(x, y, z)).setQing(ForgeDirection.getOrientation(side));
				par1World.markBlockForRenderUpdate(x, y, z);
			}
		}

		return true;
	}

	@Override
	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				((TYinXing) par1World.getBlockTileEntity(x, y, z)).setYing();
			}
		}

		return true;
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color.
	 * Note only called when first determining what to render.
	 */
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int x, int y, int z)
	{
		try
		{
			TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

			if (tileEntity instanceof TYinXing)
			{
				int haoMa = ((TYinXing) tileEntity).getJiaHaoMa();

				if (haoMa < Block.blocksList.length)
				{
					Block block = Block.blocksList[haoMa];

					if (block != null)
					{
						if (block == Block.grass)
						{
							int redColor = 0;
							int greenColor = 0;
							int blueColork = 0;

							for (int izy = -1; izy <= 1; izy++)
							{
								for (int ix = -1; ix <= 1; ix++)
								{
									int grassColor = par1IBlockAccess.getBiomeGenForCoords(x + ix, z + izy).getBiomeGrassColor();
									redColor += ((grassColor & 0xFF0000) >> 16);
									greenColor += ((grassColor & 0xFF00) >> 8);
									blueColork += (grassColor & 0xFF);
								}
							}

							return (redColor / 9 & 0xFF) << 16 | (greenColor / 9 & 0xFF) << 8 | blueColork / 9 & 0xFF;
						}

						return block.colorMultiplier(par1IBlockAccess, x, y, x);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 16777215;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		TileEntity t = par1World.getBlockTileEntity(x, y, z);

		if (t != null)
		{
			if (t instanceof TYinXing)
			{
				if (((TYinXing) t).getYing())
				{
					return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
				}
			}
		}

		return null;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return RBYinXing.ID;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TYinXing();
	}
}
