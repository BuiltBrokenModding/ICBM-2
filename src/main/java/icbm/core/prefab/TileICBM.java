package icbm.core.prefab;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import calclavia.lib.prefab.tile.IPlayerUsing;
import calclavia.lib.prefab.tile.TileElectrical;

/**
 * @author Calclavia
 * 
 */
public abstract class TileICBM extends TileElectrical implements IPlayerUsing
{
	public final HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	@Override
	public HashSet<EntityPlayer> getPlayersUsing()
	{
		return this.playersUsing;
	}

}
