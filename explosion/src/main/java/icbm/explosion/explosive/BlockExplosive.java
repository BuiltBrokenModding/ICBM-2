package icbm.explosion.explosive;

import icbm.Reference;
import icbm.core.TabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.explosion.entities.EntityExplosive;
import icbm.explosion.render.tile.RenderBombBlock;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;
import calclavia.api.icbm.ICamouflageMaterial;
import calclavia.api.icbm.explosion.ExplosiveType;
import calclavia.api.icbm.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import calclavia.lib.utility.WrenchUtility;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockExplosive extends BlockICBM implements ICamouflageMaterial
{
	public final Icon[] ICON_TOP = new Icon[100];
	public final Icon[] ICON_SIDE = new Icon[100];
	public final Icon[] ICON_BOTTOM = new Icon[100];

	public BlockExplosive(int id)
	{
		super(id, "explosives", Material.tnt);
		setHardness(0.0F);
		setStepSound(soundGrassFootstep);
		setCreativeTab(TabICBM.INSTANCE);
	}

	/** gets the way this piston should face for that entity that placed it. */
	private static byte determineOrientation(World world, int x, int y, int z, EntityLivingBase entityLiving)
	{
		if (entityLiving != null)
		{
			if (MathHelper.abs((float) entityLiving.posX - x) < 2.0F && MathHelper.abs((float) entityLiving.posZ - z) < 2.0F)
			{
				double var5 = entityLiving.posY + 1.82D - entityLiving.yOffset;

				if (var5 - y > 2.0D)
				{
					return 1;
				}

				if (y - var5 > 0.0D)
				{
					return 0;
				}
			}

			int rotation = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			return (byte) (rotation == 0 ? 2 : (rotation == 1 ? 5 : (rotation == 2 ? 3 : (rotation == 3 ? 4 : 0))));
		}
		return 0;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileExplosive)
			{
				if (((TileExplosive) tileEntity).haoMa == Explosive.sMine.getID())
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
			if (tileEntity instanceof TileExplosive)
			{
				if (((TileExplosive) tileEntity).haoMa == Explosive.sMine.getID())
				{
					return AxisAlignedBB.getAABBPool().getAABB(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + 0.2, z + this.maxZ);
				}
			}
		}

		return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}

	/** Called when the block is placed in the world. */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
	{
		((TileExplosive) world.getBlockTileEntity(x, y, z)).haoMa = itemStack.getItemDamage();
		int explosiveID = ((TileExplosive) world.getBlockTileEntity(x, y, z)).haoMa;

		if (!world.isRemote)
		{
			ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, x, y, z, ExplosiveType.BLOCK, ExplosiveRegistry.get(explosiveID));
			MinecraftForge.EVENT_BUS.post(evt);

			if (evt.isCanceled())
			{
				this.dropBlockAsItem(world, x, y, z, explosiveID, 0);
				world.setBlock(x, y, z, 0, 0, 2);
				return;
			}
		}

		world.setBlockMetadataWithNotify(x, y, z, VectorHelper.getOrientationFromSide(ForgeDirection.getOrientation(determineOrientation(world, x, y, z, entityLiving)), ForgeDirection.NORTH).ordinal(), 2);

		if (world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			BlockExplosive.yinZha(world, x, y, z, explosiveID, 0);
		}

		// Check to see if there is fire nearby.
		// If so, then detonate.
		for (byte i = 0; i < 6; i++)
		{
			Vector3 position = new Vector3(x, y, z);
			position.translate(ForgeDirection.getOrientation(i));

			int blockId = position.getBlockID(world);

			if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
			{
				BlockExplosive.yinZha(world, x, y, z, explosiveID, 2);
			}
		}

		if (entityLiving != null)
		{
			FMLLog.fine(entityLiving.getEntityName() + " placed " + ExplosiveRegistry.get(explosiveID).getExplosiveName() + " in: " + x + ", " + y + ", " + z + ".");
		}
	}

	/** Returns the block texture based on the side being looked at. Args: side */
	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		int explosiveID = ((TileExplosive) par1IBlockAccess.getBlockTileEntity(x, y, z)).haoMa;
		return getIcon(side, explosiveID);
	}

	@Override
	public Icon getIcon(int side, int explosiveID)
	{
		if (side == 0)
		{
			return ICON_BOTTOM[explosiveID];
		}
		else if (side == 1)
		{
			return ICON_TOP[explosiveID];
		}

		return ICON_SIDE[explosiveID];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		/** Register every single texture for all explosives. */
		for (Explosive zhaPin : ExplosiveRegistry.getAllZhaPin())
		{
			ICON_TOP[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_top");
			ICON_SIDE[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_side");
			ICON_BOTTOM[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_bottom");
		}
	}

	@SideOnly(Side.CLIENT)
	public Icon getIcon(IconRegister iconRegister, Explosive zhaPin, String suffix)
	{
		String iconName = "explosive_" + zhaPin.getUnlocalizedName() + suffix;

		try
		{
			ResourceLocation resourcelocation = new ResourceLocation(Reference.DOMAIN, Reference.BLOCK_PATH + iconName + ".png");
			InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(resourcelocation).getInputStream();
			BufferedImage bufferedimage = ImageIO.read(inputstream);

			if (bufferedimage != null)
			{
				return iconRegister.registerIcon(Reference.PREFIX + iconName);
			}
		}
		catch (Exception e)
		{
		}

		if (suffix.equals("_bottom"))
		{
			return iconRegister.registerIcon(Reference.PREFIX + "explosive_bottom_" + zhaPin.getTier());
		}

		return iconRegister.registerIcon(Reference.PREFIX + "explosive_base_" + zhaPin.getTier());
	}

	/** Called whenever the block is added into the world. Args: world, x, y, z */
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		super.onBlockAdded(par1World, x, y, z);

		int explosiveID = ((TileExplosive) par1World.getBlockTileEntity(x, y, z)).haoMa;
		par1World.markBlockForRenderUpdate(x, y, z);
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
	 * (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId)
	{
		int explosiveID = ((TileExplosive) world.getBlockTileEntity(x, y, z)).haoMa;

		if (world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			BlockExplosive.yinZha(world, x, y, z, explosiveID, 0);
		}
		else if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
		{
			BlockExplosive.yinZha(world, x, y, z, explosiveID, 2);
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
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileExplosive)
				{
					ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, x, y, z, ExplosiveType.BLOCK, ExplosiveRegistry.get(((TileExplosive) tileEntity).haoMa));
					MinecraftForge.EVENT_BUS.post(evt);

					if (!evt.isCanceled())
					{
						((TileExplosive) tileEntity).exploding = true;
						EntityExplosive eZhaDan = new EntityExplosive(world, new Vector3(x, y, z).add(0.5), ((TileExplosive) tileEntity).haoMa, (byte) world.getBlockMetadata(x, y, z), ((TileExplosive) tileEntity).nbtData);

						switch (causeOfExplosion)
						{
							case 2:
								eZhaDan.setFire(100);
								break;
						}

						world.spawnEntityInWorld(eZhaDan);
						world.setBlockToAir(x, y, z);
					}
				}
			}
		}
	}

	/** Called upon the block being destroyed by an explosion */
	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TileExplosive) world.getBlockTileEntity(x, y, z)).haoMa;
			BlockExplosive.yinZha(world, x, y, z, explosiveID, 1);
		}

		super.onBlockExploded(world, x, y, z, explosion);
	}

	/**
	 * Called upon block activation (left or right click on the block.). The three integers
	 * represent x,y,z of the block.
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (entityPlayer.getCurrentEquippedItem() != null)
		{
			if (entityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID)
			{
				int explosiveID = ((TileExplosive) tileEntity).haoMa;
				BlockExplosive.yinZha(world, x, y, z, explosiveID, 0);
				return true;
			}
			else if (WrenchUtility.isUsableWrench(entityPlayer, entityPlayer.getCurrentEquippedItem(), x, y, z))
			{
				byte change = 3;

				// Reorient the block
				switch (world.getBlockMetadata(x, y, z))
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

				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(change).ordinal(), 3);

				world.notifyBlockChange(x, y, z, this.blockID);
				return true;
			}

		}

		if (tileEntity instanceof TileExplosive)
		{
			return ExplosiveRegistry.get(((TileExplosive) tileEntity).haoMa).onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return RenderBombBlock.ID;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		if (world.getBlockTileEntity(x, y, z) != null)
		{
			int explosiveID = ((TileExplosive) world.getBlockTileEntity(x, y, z)).haoMa;

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
			if (tileEntity instanceof TileExplosive)
			{
				if (!((TileExplosive) tileEntity).exploding)
				{
					int explosiveID = ((TileExplosive) tileEntity).haoMa;
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
		for (Explosive zhaPin : ExplosiveRegistry.getAllZhaPin())
		{
			if (zhaPin.hasBlockForm())
			{
				par3List.add(new ItemStack(par1, 1, zhaPin.getID()));
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileExplosive();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
}
