package icbm.sentry.turret.block;

import calclavia.lib.access.AccessGroup;
import calclavia.lib.access.AccessUser;
import calclavia.lib.access.Nodes;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.block.BlockAdvanced;
import calclavia.lib.prefab.item.ItemBlockSaved;
import calclavia.lib.utility.inventory.InventoryUtility;
import calclavia.lib.utility.nbt.SaveManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.core.TabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.SentryRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map.Entry;

import universalelectricity.api.vector.Vector3;

/**
 * Block turret is a class used by all turrets. Each type of turret will have a different tile
 * entity.
 * 
 * @author Calclavia, tgame14
 */
public class BlockTurret extends BlockICBM
{
	public BlockTurret(int id)
	{
		super(id, "turret");
		this.setCreativeTab(TabICBM.INSTANCE);
		this.setHardness(100f);
		this.setResistance(50f);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);

		if (ent instanceof TileTurret)
		{
			Entity fakeEntity = ((TileTurret) ent).getFakeEntity();

			if (fakeEntity != null)
			{
				this.setBlockBounds(.2f, 0, .2f, .8f, .4f, .8f);
			}
			else
			{
				this.setBlockBounds(.2f, 0, .2f, .8f, .8f, .8f);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(Reference.PREFIX + "machine");
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IBlockActivate)
		{
			return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
		}

		return false;
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		/**
		 * Checks the TileEntity if it can activate. If not, then try to activate the turret
		 * platform below it.
		 */
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IBlockActivate)
		{
			return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
		}

		int id = world.getBlockId(x, y - 1, z);
		Block block = Block.blocksList[id];

		if (block instanceof BlockAdvanced)
		{
			return ((BlockAdvanced) block).onMachineActivated(world, x, y - 1, z, entityPlayer, side, hitX, hitY, hitZ);
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileTurret)
		{
			if (!this.canBlockStay(world, x, y, z))
			{
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 1);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileTurret();
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

	@Override
	public int damageDropped(int metadata)
	{
		return 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return world.getBlockId(x, y - 1, z) == ICBMSentry.blockPlatform.blockID;
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativeTab, List list)
	{
		for (Class<? extends Sentry> sentry : SentryRegistry.getSentryMap().values())
			list.add(SentryRegistry.getItemStack(sentry));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		ItemBlockSaved.dropBlockWithNBT(this, world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileTurret)
		{
			TileTurret tile = (TileTurret) te;

			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;

				tile.getAccessProfile().getOwnerGroup().addMemeber(new AccessUser(player.username));
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return ItemBlockSaved.getItemStackWithNBT(this, world, x, y, z);
	}

	/* to cancel the vanilla method of dropping the itemEntity */
	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack stack)
	{

	}

	@Override
	public int getRenderType()
	{
		return -1;
	}
}
