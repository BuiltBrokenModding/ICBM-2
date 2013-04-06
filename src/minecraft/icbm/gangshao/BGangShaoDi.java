package icbm.gangshao;

import icbm.core.di.BICBM;
import net.minecraft.block.material.Material;

public class BGangShaoDi extends BICBM
{
	public BGangShaoDi(int id, String name, Material material)
	{
		super(id, name, material);
		this.requireSidedTextures = true;
	}
}
