//=======================================================
//DISCLAIMER: THIS IS A GENERATED CLASS FILE
//THUS IS PROVIDED 'AS-IS' WITH NO WARRANTY
//FUNCTIONALITY CAN NOT BE GUARANTIED IN ANY WAY 
//USE AT YOUR OWN RISK 
//-------------------------------------------------------
//Built on: Rober
//=======================================================
package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.content.launcher.controller.local.TileLocalController;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.logic.wrapper.TileEntityWrapper;

public class TileWrapperLocalController extends TileEntityWrapper
{
	public TileWrapperLocalController()
	{
		super(new TileLocalController());
	}

	//Fields from Empty
	public static final String TEST_FIELD = "DATA";
	;
	//============================
	//==Methods:Empty
	//============================


    @Override
    public String getClassDisplayName()
    {
        return "TileEntityWrapper(Empty)";
    }
    
}