package icbm;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import universalelectricity.Vector3;

public class ParticleSpawner {

	public static void spawnParticle(String name, World world, Vector3 position, float red, float green, float blue, float scale, float distance)
	{
		if(name == "smoke")
		{
			SmokeFX effect = new SmokeFX(world, position, red, green, blue, scale, distance);
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
