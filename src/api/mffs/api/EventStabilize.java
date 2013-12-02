package mffs.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * The event called when stabilizing a field into solid blocks. Canceling
 * 
 * @author Calclavia
 * 
 */
@Cancelable
public class EventStabilize extends Event
{
	public final World world;
	public final ItemStack itemStack;
	public final int x, y, z;

	public EventStabilize(World world, int x, int y, int z, ItemStack itemStack)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.itemStack = itemStack;
	}
}
