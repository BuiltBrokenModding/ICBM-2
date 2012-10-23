package icbm;

import net.minecraft.src.World;
import universalelectricity.core.Vector3;
import cpw.mods.fml.client.FMLClientHandler;

public class ParticleSpawner {

	public static void spawnParticle(String name, World world, Vector3 position, float red, float green, float blue, float scale, double distance)
	{
		if(name == "smoke")
		{
			SmokeFX effect = new SmokeFX(world, position, red, green, blue, scale, distance);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(effect);
		}
		else if(name == "portal")
		{
			PortalFX effect = new PortalFX(world, position, red, green, blue, scale, distance);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(effect);
		}
	}

	public static void spawnParticle(String name, World world, Vector3 position, float red, float green, float blue)
	{
		spawnParticle(name, world, position, red, green, blue, 5F, 2F);
	}

	public static void spawnParticle(String name, World world, Vector3 position)
	{
		spawnParticle(name, world, position, 0, 0, 0);
	}

}
