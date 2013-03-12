package icbm.wanyi;

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
	public void func_94332_a(IconRegister par1IconRegister)
	{
		this.field_94336_cN = par1IconRegister.func_94245_a(this.func_94330_A());
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
