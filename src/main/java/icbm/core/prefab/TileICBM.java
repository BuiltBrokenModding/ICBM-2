package icbm.core.prefab;

import java.util.HashSet;

import universalelectricity.api.UniversalElectricity;
import universalelectricity.api.electricity.IVoltageInput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.prefab.tile.IPlayerUsing;
import calclavia.lib.prefab.tile.TileElectrical;

/**
 * @author Calclavia
 * 
 */
public abstract class TileICBM extends TileElectrical implements IPlayerUsing, IVoltageInput
{
	public final HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	@Override
	public long getVoltageInput(ForgeDirection direction)
	{
		return UniversalElectricity.DEFAULT_VOLTAGE * 2;
	}

	@Override
	public void onWrongVoltage(ForgeDirection direction, long voltage)
	{

	}

	@Override
	public HashSet<EntityPlayer> getPlayersUsing()
	{
		return this.playersUsing;
	}

}
