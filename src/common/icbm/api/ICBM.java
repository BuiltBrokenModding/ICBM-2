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
	public static Block explosiveBlock;
	public static Class explosionManager;

	/**
	 * Some texture file directory references.
	 */
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
	public static final String BLOCK_TEXTURE_FILE = TEXTURE_FILE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_FILE = TEXTURE_FILE_PATH + "items.png";
	public static final String TRACKER_TEXTURE_FILE = TEXTURE_FILE_PATH + "tracker.png";

	/**
	 * Returns an ItemStack of the explosive with the explosive block.
	 * 
	 * @param explosiveID
	 * @return
	 */
	public static ItemStack getExplosiveBlock(int explosiveID)
	{
		return new ItemStack(explosiveBlock, 1, explosiveID);
	}

	/**
	 * Creates an ICBM explosion anywhere in this specific position.
	 * 
	 * @param worldObj
	 *            - The world
	 * @param x
	 *            - X position
	 * @param y
	 *            - Y position
	 * @param z
	 *            - Z position
	 * @param entity
	 *            - The entity causing this explosion. Can be null if not specified.
	 * @param explosiveID
	 *            - The ID of the explosive
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
