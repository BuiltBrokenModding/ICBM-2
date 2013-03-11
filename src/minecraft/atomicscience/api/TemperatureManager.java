package atomicscience.api;

import java.util.HashMap;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

public class TemperatureManager
{
	private static final HashMap<Vector3, Float> TEMPERATURE_DATA = new HashMap<Vector3, Float>();

	/**
	 * Sets a heat source emitting from a specific position.
	 */
	public static void setSourceTemperature(Vector3 vector, float celsius)
	{
		TEMPERATURE_DATA.put(vector, celsius);
	}

	/**
	 * Gets the temperature of a specific position.
	 * 
	 * @param vector
	 */
	public static float getTemperature(Vector3 vector)
	{
		float celsius = getSourceTemperature(vector);

		for (int i = 0; i < 6; i++)
		{
			Vector3 checkVector = vector.clone();
			checkVector.modifyPositionFromSide(ForgeDirection.getOrientation(i));

			celsius += getSourceTemperature(checkVector);
		}

		return celsius;
	}

	public static float getSourceTemperature(Vector3 vector)
	{
		return TEMPERATURE_DATA.get(vector);
	}

	public static void clear()
	{
		TEMPERATURE_DATA.clear();
	}
}
