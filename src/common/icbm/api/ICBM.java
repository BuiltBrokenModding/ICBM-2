package icbm.api;

import java.lang.reflect.Method;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLLog;

/**
 * A class containing general things you might want to reference to in your mod in ICBM. Check if
 * these variables are null because they might possibly be!
 * 
 * @author Calclavia
 * 
 */
public class ICBM
{
	/**
	 * A reference to the explosive TNT block instance.
	 */
	public static Block blockExplosive;
	public static Class explosionManager;

	/**
	 * Creates an ICBM explosion anywhere in this specific position.
	 * 
	 * @param worldObj - The world
	 * @param x - X position
	 * @param y - Y position
	 * @param z - Z position
	 * @param entity - The entity causing this explosion. Can be null if not specified.
	 * @param explosiveID - The ID of the explosive
	 */
	public static void createExplosion(World worldObj, double x, double y, double z, Entity entity, int explosiveID)
	{
		try
		{
			Method m = explosionManager.getMethod("createExplosion", World.class, Double.class, Double.class, Double.class, Entity.class, Integer.class);
			m.invoke(null, worldObj, x, y, z, entity, explosiveID);
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to create ICBM explosive.");
		}
	}
}
