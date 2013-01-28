package icbm.common.jiqi;

import icbm.api.IEMPBlock;
import icbm.api.IExplosive;
import icbm.common.CommonProxy;
import icbm.common.TYinXing;
import icbm.common.ZhuYao;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.implement.IRedstoneProvider;

public class BYinGanQi extends BlockMachine
{
	public BYinGanQi(int id, int texture)
	{
		super("proximityDetector", id, UniversalElectricity.machine, ZhuYao.TAB);
		this.blockIndexInTexture = texture;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int par2)
	{
		return side == 0 ? this.blockIndexInTexture : (side == 1 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 2);
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		par5EntityPlayer.openGui(ZhuYao.instance, CommonProxy.GUI_DETECTOR, par1World, x, y, z);
		return true;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TYinGanQi)
		{
			((TYinGanQi) tileEntity).isInverted = !((TYinGanQi) tileEntity).isInverted;
			return true;
		}

		return false;
	}

	/**
	 * Is this block powering the block on the specified side
	 */
	@Override
	public boolean isProvidingStrongPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneProvider) { return ((IRedstoneProvider) tileEntity).isPoweringTo(ForgeDirection.getOrientation(side)); }

		return false;
	}

	/**
	 * Is this block indirectly powering the block on the specified side
	 */
	@Override
	public boolean isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneProvider) { return ((IRedstoneProvider) tileEntity).isIndirectlyPoweringTo(ForgeDirection.getOrientation(side)); }

		return false;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TYinGanQi();
	}

	/**
	 * Override this if you do not want your machine to be added to the creative menu or if you have
	 * metadata machines and want to add more machines to the creative menu.
	 */
	@Override
	public void addCreativeItems(ArrayList itemList)
	{
		itemList.add(new ItemStack(this));
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.BLOCK_TEXTURE_FILE;
	}
}
