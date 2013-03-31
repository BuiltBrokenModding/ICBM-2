package icbm.zhapin.fx;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSpawner
{
	public static void spawnParticle(String name, World world, Vector3 position, float red, float green, float blue, float scale, double distance)
	{
		if (name == "smoke")
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXYan(world, position, red, green, blue, scale, distance));
		}
		else if (name == "portal")
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXWan(world, position, red, green, blue, scale, distance));
		}
		else if (name == "antimatter")
		{
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXFanWuSu(world, position, red, green, blue, scale, distance));
		}
	}

	public static void spawnParticle(String name, World world, Vector3 position, float red, float green, float blue)
	{
		spawnParticle(name, world, position, red, green, blue, 5F, 2F);
	}

	public static void spawnParticle(String name, World world, Vector3 position)
	{
		spawnParticle(name, world, position, 0.2f, 0.2f, 0.2f);
	}

}
