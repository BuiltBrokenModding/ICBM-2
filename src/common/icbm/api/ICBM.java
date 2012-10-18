package icbm.api;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

/**
 * A class containing general things you might want to reference to in your mod in ICBM.
 * Check if these variables are null because they might possibly be!
 * @author Calclavia
 *
 */
public class ICBM
{
	/**
	 * A reference to the explosive TNT block instance.
	 */
	public static Block explosiveBlock;
	public static Object explosionManager;
	
	/**
	 * Some texture file directory references.
	 */
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
	public static final String BLOCK_TEXTURE_FILE = TEXTURE_FILE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_FILE = TEXTURE_FILE_PATH + "items.png";
	public static final String TRACKER_TEXTURE_FILE = TEXTURE_FILE_PATH + "tracker.png";
	
	public static ItemStack getExplosiveBlock(int explosiveID)
	{
		return new ItemStack(explosiveBlock, 1, explosiveID);
	}
	
	public static void createExplosion(int )
}
