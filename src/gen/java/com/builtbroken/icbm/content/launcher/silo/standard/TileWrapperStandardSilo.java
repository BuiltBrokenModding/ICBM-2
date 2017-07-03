//=======================================================
//DISCLAIMER: THIS IS A GENERATED CLASS FILE
//THUS IS PROVIDED 'AS-IS' WITH NO WARRANTY
//FUNCTIONALITY CAN NOT BE GUARANTIED IN ANY WAY 
//USE AT YOUR OWN RISK 
//-------------------------------------------------------
//Built on: Rober
//=======================================================
package com.builtbroken.icbm.content.launcher.silo.standard;

import com.builtbroken.icbm.content.launcher.silo.standard.TileStandardSilo;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.BlockBase;
import com.builtbroken.mc.framework.logic.wrapper.TileEntityWrapper;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import java.util.List;

public class TileWrapperStandardSilo extends TileEntityWrapper implements IMultiTileHost
{
	public TileWrapperStandardSilo()
	{
		super(new TileStandardSilo());
	}

	//============================
	//==Methods:MultiBlockWrapped
	//============================


    private List[] _getMultiTileListeners()
    {
        if (!(getBlockType() instanceof BlockBase))
        {
            return new List[]{getListeners("multiblock")};
        }
        return new List[]{getListeners("multiblock"), ((BlockBase) getBlockType()).listeners.get("multiblock")};
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            ((IMultiTileHost) getTileNode()).onMultiTileAdded(tileMulti);
        }
        for (List<ITileEventListener> list : _getMultiTileListeners())
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if (listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (listener.isValidForTile())
                        {
                            ((IMultiTileHost) listener).onMultiTileAdded(tileMulti);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (isServer())
        {
            if (getTileNode() instanceof IMultiTileHost)
            {
                return ((IMultiTileHost) getTileNode()).onMultiTileBroken(tileMulti, source, harvest);
            }
            for (List<ITileEventListener> list : _getMultiTileListeners())
            {
                if (list != null && !list.isEmpty())
                {
                    boolean b = false;
                    for (ITileEventListener listener : list)
                    {
                        if (listener instanceof IMultiTileHost)
                        {
                            if (listener instanceof IBlockListener)
                            {
                                ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                            }
                            if (listener.isValidForTile())
                            {
                                if (((IMultiTileHost) listener).onMultiTileBroken(tileMulti, source, harvest))
                                {
                                    b = true;
                                }
                            }
                        }
                    }
                    return b;
                }
            }
        }
        return false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {
        if (isServer())
        {
            if (getTileNode() instanceof IMultiTileHost)
            {
                ((IMultiTileHost) getTileNode()).onTileInvalidate(tileMulti);
            }
            for (List<ITileEventListener> list : _getMultiTileListeners())
            {
                if (list != null && !list.isEmpty())
                {
                    for (ITileEventListener listener : list)
                    {
                        if (listener instanceof IMultiTileHost)
                        {
                            if (listener instanceof IBlockListener)
                            {
                                ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                            }
                            if (listener.isValidForTile())
                            {
                                ((IMultiTileHost) listener).onTileInvalidate(tileMulti);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        boolean b = false;
        if (getTileNode() instanceof IMultiTileHost)
        {
            b = ((IMultiTileHost) getTileNode()).onMultiTileActivated(tile, player, side, xHit, yHit, zHit);
        }
        for (List<ITileEventListener> list : _getMultiTileListeners())
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if (listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (listener.isValidForTile())
                        {
                            if (((IMultiTileHost) listener).onMultiTileActivated(tile, player, side, xHit, yHit, zHit))
                            {
                                b = true;
                            }
                        }
                    }
                }
            }
        }
        return b;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            ((IMultiTileHost) getTileNode()).onMultiTileClicked(tile, player);
        }
        for (List<ITileEventListener> list : _getMultiTileListeners())
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if (listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (listener.isValidForTile())
                        {
                            ((IMultiTileHost) listener).onMultiTileClicked(tile, player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        if (getTileNode() instanceof IMultiTileHost)
        {
            HashMap<IPos3D, String> map = ((IMultiTileHost) getTileNode()).getLayoutOfMultiBlock();
            if (map != null && !map.isEmpty())
            {
                return map;
            }
        }
        for (List<ITileEventListener> list : _getMultiTileListeners())
        {
            if (list != null && !list.isEmpty())
            {
                for (ITileEventListener listener : list)
                {
                    if (listener instanceof IMultiTileHost)
                    {
                        if (listener instanceof IBlockListener)
                        {
                            ((IBlockListener) listener).inject(world(), xi(), yi(), zi());
                        }
                        if (listener.isValidForTile())
                        {
                            HashMap<IPos3D, String> map = ((IMultiTileHost) listener).getLayoutOfMultiBlock();
                            if (map != null && !map.isEmpty())
                            {
                                return map;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
}