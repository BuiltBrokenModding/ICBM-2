package icbm.zhapin.zhapin;

import icbm.api.ICBMTab;
import icbm.api.ICamouflageMaterial;
import icbm.core.ZhuYao;
import icbm.core.di.BICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.render.RHZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.implement.IToolConfigurator;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BZhaDan extends BICBM implements ICamouflageMaterial
{
	public static final List<Icon> ICON_TOP = new ArrayList<Icon>();
	public static final List<Icon> ICON_SIDE = new ArrayList<Icon>();
	public static final List<Icon> ICON_BOTTOM = new ArrayList<Icon>();

	public BZhaDan(int id)
	{
		super(id, "explosives", Material.tnt);
		this.setHardness(0.0F);
		this.setStepSound(soundGrassFootstep);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}

	/**
	 * gets the way this piston should face for that entity that placed it.
	 */
	private static byte determineOrientation(World world, int x, int y, int z, EntityLiving entityLiving)
	{
		if (MathHelper.abs((float) entityLiving.posX - (float) x) < 2.0F && MathHelper.abs((float) entityLiving.posZ - (float) z) < 2.0F)
		{
			double var5 = entityLiving.posY + 1.82D - (double) entityLiving.yOffset;

			if (var5 - (double) y > 2.0D)
			{
				return 1;
			}

			if ((double) y - var5 > 0.0D)
			{
				return 0;
			}
		}

		int rotation = MathHelper.floor_double((double) (entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return (byte) (rotation == 0 ? 2 : (rotation == 1 ? 5 : (rotation == 2 ? 3 : (rotation == 3 ? 4 : 0))));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TZhaDan)
			{
				if (((TZhaDan) tileEntity).haoMa == ZhaPin.diLei.getID())
				{
					this.setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
					return;
				}
			}
		}

		this.setBlockBounds(0, 0, 0, 1f, 1f, 1f);
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0, 0, 0, 1f, 1f, 1f);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after
	 * the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TZhaDan)
			{
				if (((TZhaDan) tileEntity).haoMa == ZhaPin.diLei.getID())
				{
					return AxisAlignedBB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + 0.2, (double) z + this.maxZ);
				}
			}
		}

		return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack itemStack)
	{
		int explosiveID = ((TZhaDan) world.getBlockTileEntity(x, y, z)).haoMa;

		if (!world.isRemote)
		{
			if (ZhuYaoZhaPin.shiBaoHu(world, new Vector3(x, y, z), ZhaPinType.ZHA_DAN, explosiveID))
			{
				this.dropBlockAsItem(world, x, y, z, explosiveID, 0);
				world.setBlockAndMetadataWithNotify(x, y, z, 0, 0, 2);
				return;
			}
		}

		world.setBlockMetadataWithNotify(x, y, z, VectorHelper.getOrientationFromSide(ForgeDirection.getOrientation(determineOrientation(world, x, y, z, entityLiving)), ForgeDirection.NORTH).ordinal(), 2);

		if (world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			BZhaDan.yinZha(world, x, y, z, explosiveID, 0);
		}

		// Check to see if there is fire nearby.
		// If so, then detonate.
		for (byte i = 0; i < 6; i++)
		{
			Vector3 position = new Vector3(x, y, z);
			position.modifyPositionFromSide(ForgeDirection.getOrientation(i));

			int blockId = position.getBlockID(world);

			if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
			{
				BZhaDan.yinZha(world, x, y, z, explosiveID, 2);
			}
		}

		FMLLog.fine(entityLiving.getEntityName() + " placed " + ZhaPin.list[explosiveID].getExplosiveName() + " in: " + x + ", " + y + ", " + z + ".");
	}

	/**
	 * Returns the block texture based on the side being looked at. Args: side
	 */
	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		int explosiveID = ((TZhaDan) par1IBlockAccess.getBlockTileEntity(x, y, z)).haoMa;
		return this.getBlockTextureFromSideAndMetadata(side, explosiveID);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int explosiveID)
	{
		if (side == 0)
		{
			return ICON_BOTTOM.get(explosiveID);
		}
		else if (side == 1)
		{
			return ICON_TOP.get(explosiveID);
		}

		return ICON_SIDE.get(explosiveID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void func_94332_a(IconRegister iconRegister)
	{
		/**
		 * Register every single texture for all explosives.
		 */
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			this.ICON_TOP.add(this.getIcon(iconRegister, i, "_top"));
			this.ICON_SIDE.add(this.getIcon(iconRegister, i, "_side"));
			this.ICON_BOTTOM.add(this.getIcon(iconRegister, i, "_bottom"));
		}
	}

	public Icon getIcon(IconRegister iconRegister, int i, String suffix)
	{
		ITexturePack itexturepack = Minecraft.getMinecraft().texturePackList.getSelectedTexturePack();
		String iconName = "explosive_" + ZhaPin.list[i].getUnlocalizedName() + suffix;
		String path = "/mods/" + ZhuYao.PREFIX.replace(":", "") + "/textures/blocks/" + iconName + ".png";
		try
		{
			BufferedImage bufferedimage = ImageIO.read(itexturepack.getResourceAsStream(path));
			return iconRegister.func_94245_a(ZhuYao.PREFIX + iconName);
		}
		catch (Exception e)
		{
		}

		if (suffix.equals("_bottom"))
		{
			return iconRegister.func_94245_a(ZhuYao.PREFIX + "explosive_bottom_" + ZhaPin.list[i].getTier());
		}

		return iconRegister.func_94245_a(ZhuYao.PREFIX + "explosive_base_" + ZhaPin.list[i].getTier());
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		super.onBlockAdded(par1World, x, y, z);

		int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).haoMa;
		par1World.markBlockForRenderUpdate(x, y, z);
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
	 * (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int blockId)
	{
		int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).haoMa;

		if ((blockId > 0 && Block.blocksList[blockId].canProvidePower() && par1World.isBlockIndirectlyGettingPowered(x, y, z)))
		{
			BZhaDan.yinZha(par1World, x, y, z, explosiveID, 0);
		}
		else if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
		{
			BZhaDan.yinZha(par1World, x, y, z, explosiveID, 2);
		}
	}

	/*
	 * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
	 * 1, exploded, 2 burned)
	 */
	public static void yinZha(World world, int x, int y, int z, int explosiveID, int causeOfExplosion)
	{
		if (!world.isRemote)
		{
			if (!ZhuYaoZhaPin.shiBaoHu(world, new Vector3(x, y, z), ZhaPinType.ZHA_DAN, explosiveID))
			{
				TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
				if (tileEntity != null)
				{
					if (tileEntity instanceof TZhaDan)
					{
						((TZhaDan) tileEntity).exploding = true;
						ZhaPin.list[explosiveID].spawnZhaDan(world, new Vector3(x, y, z), ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)), (byte) causeOfExplosion);
						world.setBlockAndMetadataWithNotify(x, y, z, 0, 0, 2);
					}
				}
			}
		}
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World par1World, int x, int y, int z, Explosion explosion)
	{
		if (par1World.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TZhaDan) par1World.getBlockTileEntity(x, y, z)).haoMa;
			BZhaDan.yinZha(par1World, x, y, z, explosiveID, 1);
		}
	}

	/**
	 * Called upon block activation (left or right click on the block.). The three integers
	 * represent x,y,z of the block.
	 */
	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (par5EntityPlayer.getCurrentEquippedItem() != null)
		{
			if (par5EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID)
			{
				int explosiveID = ((TZhaDan) tileEntity).haoMa;
				BZhaDan.yinZha(par1World, x, y, z, explosiveID, 0);
				return true;
			}
			else if (par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof IToolConfigurator)
			{
				byte change = 3;

				// Reorient the block
				switch (par1World.getBlockMetadata(x, y, z))
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

				par1World.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(change).ordinal(), 3);

				par1World.notifyBlockChange(x, y, z, this.blockID);
				return true;
			}

		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return RHZhaPin.ID;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TZhaDan) world.getBlockTileEntity(x, y, z)).haoMa;

			return new ItemStack(this.blockID, 1, explosiveID);
		}

		return null;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TZhaDan)
			{
				if (!((TZhaDan) tileEntity).exploding)
				{
					int explosiveID = ((TZhaDan) tileEntity).haoMa;
					int id = idDropped(world.getBlockMetadata(x, y, z), world.rand, 0);

					this.dropBlockAsItem_do(world, x, y, z, new ItemStack(id, 1, explosiveID));
				}
			}
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}

		par3List.add(new ItemStack(this, 1, ZhaPin.diLei.getID()));
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TZhaDan();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
}
