package icbm.contraption.block;

import icbm.Reference;
import icbm.core.CreativeTabICBM;

import java.util.Random;

import net.minecraft.block.BlockButton;
import net.minecraft.client.renderer.texture.IconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGlassButton extends BlockButton
{
	public BlockGlassButton(int id)
	{
		super(id, true);
		this.setTickRandomly(true);
		this.setUnlocalizedName(Reference.PREFIX + "glassButton");
		this.setStepSound(soundGlassFootstep);
		this.setCreativeTab(CreativeTabICBM.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", ""));
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
