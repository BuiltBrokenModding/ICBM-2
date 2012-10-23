package icbm.jiqi;


import java.util.ArrayList;
import java.util.List;

import universalelectricity.core.Vector2;

public class FaSheQiGuanLi
{
	//Static methods
	private static List<TFaSheQi> faSheQi = new ArrayList<TFaSheQi>();
	
	public static void jiaFaSheQi(TFaSheQi launcher)
	{
		qingLiFaSheQi();
		
		if(!faSheQi.contains(launcher))
		{
			faSheQi.add(launcher);
		}
	}
	
	public static List<TFaSheQi> naFaSheQiInArea(Vector2 minVector, Vector2 maxVector)
	{
		qingLiFaSheQi();
		List<TFaSheQi> returnArray = new ArrayList<TFaSheQi>();
		
		for(TFaSheQi launcher : faSheQi)
		{
	        if(launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
	        {
	        	returnArray.add(launcher);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TFaSheQi> getFaSheQi()
	{
		qingLiFaSheQi();
		return faSheQi;
	}
	
	public static void qingLiFaSheQi()
	{
		for(int i = 0; i < faSheQi.size(); i++)
		{
			if(faSheQi.get(i) == null)
			{
				faSheQi.remove(i);
			}
			else if(faSheQi.get(i).isInvalid())
			{
				faSheQi.remove(i);
			}
		}
	}
}
