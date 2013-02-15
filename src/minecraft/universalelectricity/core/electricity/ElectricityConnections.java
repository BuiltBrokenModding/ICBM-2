package universalelectricity.core.electricity;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

/**
 * Register your block to allow a wire connection with this class. This class manages the visual
 * aspect of a wire connection and does not effect the actual transmission of electricity.
 * 
 * @author Calclavia
 * 
 */
public class ElectricityConnections
{
	private static final HashMap<TileEntity, EnumSet<ForgeDirection>> connectors = new HashMap<TileEntity, EnumSet<ForgeDirection>>();

	/**
	 * Registers a block to allow connection from Universal Electricity wires.
	 * 
	 * @param tileEntity - The TileEntity
	 * @param connectableDirections - Directions that allow connection
	 */
	public static void registerConnector(TileEntity tileEntity, EnumSet<ForgeDirection> connectableDirections)
	{
		connectors.put(tileEntity, connectableDirections);
	}

	public static void unregisterConnector(TileEntity tileEntity)
	{
		connectors.remove(tileEntity);
	}

	public static boolean isConnector(TileEntity tileEntity)
	{
		return connectors.containsKey(tileEntity);
	}

	public static boolean canConnect(TileEntity tileEntity, ForgeDirection side)
	{
		if (isConnector(tileEntity))
		{
			EnumSet<ForgeDirection> enumSet = connectors.get(tileEntity);

			if (enumSet != null)
			{
				return enumSet.contains(side);
			}
		}

		return false;
	}

	/**
	 * Returns the directions in which this block may connect or be connected from.
	 */
	public static EnumSet<ForgeDirection> getDirections(TileEntity tileEntity)
	{
		return connectors.get(tileEntity);
	}

	public static void clearAll()
	{
		connectors.clear();
	}
}
