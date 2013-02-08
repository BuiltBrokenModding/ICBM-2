package icbm.contraption;

import icbm.api.ICBMTab;
import icbm.explosion.ZhuYaoExplosion;

import java.util.Random;

import net.minecraft.block.BlockButton;

public class BEnNiu extends BlockButton
{
	public BEnNiu(int id)
	{
		super(id, 0, true);
		this.setTickRandomly(true);
		this.setBlockName("glassButton");
		this.setStepSound(soundGlassFootstep);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ZhuYaoExplosion.BLOCK_TEXTURE_FILE);
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
