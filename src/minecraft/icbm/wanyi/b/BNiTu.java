package icbm.wanyi.b;

import icbm.core.di.BICBM;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BNiTu extends BICBM
{
	private Icon iconCompact, iconReinforced;

	public BNiTu(int id)
	{
		super(id, "concrete", Material.rock);
		this.setHardness(1.8f);
		this.setResistance(50);
		this.setStepSound(soundMetalFootstep);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		switch (metadata)
		{
			case 1:
				return this.iconCompact;
			case 2:
				return this.iconReinforced;
		}

		return this.blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);

		this.iconCompact = iconRegister.registerIcon(this.getUnlocalizedName2() + "Compact");
		this.iconReinforced = iconRegister.registerIcon(this.getUnlocalizedName2() + "Reinforced");

	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
	{
		int metadata = world.getBlockMetadata(x, y, z);

		switch (metadata)
		{
			case 1:
				return 38;
			case 2:
				return 48;
		}

		return this.getExplosionResistance(par1Entity);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 3; i++)
		{
			par3List.add(new ItemStack(par1, 1, i));
		}
	}
}
