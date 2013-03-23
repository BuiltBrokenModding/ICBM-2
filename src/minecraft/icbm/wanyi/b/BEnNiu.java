package icbm.wanyi.b;

import icbm.api.ICBMTab;
import icbm.core.ZhuYao;

import java.util.Random;

import net.minecraft.block.BlockButton;
import net.minecraft.client.renderer.texture.IconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BEnNiu extends BlockButton
{
	public BEnNiu(int id)
	{
		super(id, true);
		this.setTickRandomly(true);
		this.setUnlocalizedName(ZhuYao.PREFIX + "glassButton");
		this.setStepSound(soundGlassFootstep);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(this.getUnlocalizedName2());
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
}
