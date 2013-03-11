package icbm.wanyi;

import icbm.api.ICBMTab;

import java.util.Random;

import net.minecraft.block.BlockButton;

public class BEnNiu extends BlockButton
{
	public BEnNiu(int id)
	{
		super(id, true);
		this.setTickRandomly(true);
		this.setUnlocalizedName("glassButton");
		this.setStepSound(soundGlassFootstep);
		this.setCreativeTab(ICBMTab.INSTANCE);
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
