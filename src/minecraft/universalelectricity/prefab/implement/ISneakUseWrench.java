package universalelectricity.prefab.implement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface ISneakUseWrench
{
	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ);
}
