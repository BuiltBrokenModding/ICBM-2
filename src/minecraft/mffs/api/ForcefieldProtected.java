package mffs.api;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;



public final class ForcefieldProtected {

	
	/**
	 * Basic API for Check if Block inside a Active Forcefield
	 * 
	 *  world  = the relevant Minecraft World
	 *  x,y,z  = the Block coordinates
	 *  entityPlayer = Player.for Adv. Check or null for Basic Block Inside Ckeck.
	 * 
	 * Adv. Check: 
	 * 
	 * if entityPlayer != null System will check if the projector have a link to Securtiy Station have
	 * "entityPlayer" the right Remove Protected Block (RPB). If player have the right function return true
	 * 
	 *Return:
	 *   
	 *   True:  Block is inside a Active ForceField and if set a Playername, the player is not allowed no remove it..
	 *   
	 *   False: Block is not Protected you can remove it.
	 */

	public static boolean BlockProtected(World world,int x, int y , int z, EntityPlayer entityPlayer) {
		try {

			Method method	= Class.forName("chb.mods.mffs.common.ForceFieldOptions").getMethod("BlockProtected", World.class, Integer.TYPE, Integer.TYPE,Integer.TYPE,EntityPlayer.class);
			return (Boolean)method.invoke(null, world,x,y,z,entityPlayer);
			
		} catch (Exception e) {

			System.err.println("[ModularForceFieldSystem] API Call Fail: ForcefieldProtected.BlockProtected()" + e.getMessage());
			return false;
		}
	}

}
