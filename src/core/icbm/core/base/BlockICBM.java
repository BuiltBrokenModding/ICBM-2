package icbm.core.base;

import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.implement.IRedstoneProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.block.BlockTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockICBM extends BlockTile
{
	protected Icon iconTop, iconSide, iconBottom;
	protected boolean requireSidedTextures = false;

	public BlockICBM(int id, String name, Material material)
	{
		super(ICBMConfiguration.CONFIGURATION.getBlock(name, id).getInt(id), material);
		this.setUnlocalizedName(ICBMCore.PREFIX + name);
		this.setCreativeTab(CreativeTabICBM.INSTANCE);
		this.setTextureName(ICBMCore.PREFIX + name);
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);

		if (this.requireSidedTextures)
		{
			this.iconTop = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_top");
			this.iconSide = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_side");
			this.iconBottom = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_bottom");
		}
	}

	/** Is this block powering the block on the specified side */
	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneProvider)
		{
			return ((IRedstoneProvider) tileEntity).isPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
		}

		return 0;
	}

	/** Is this block indirectly powering the block on the specified side */
	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneProvider)
		{
			return ((IRedstoneProvider) tileEntity).isIndirectlyPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
		}

		return 0;
	}
}
