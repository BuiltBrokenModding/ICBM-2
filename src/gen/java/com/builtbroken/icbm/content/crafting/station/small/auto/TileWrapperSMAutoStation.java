//=======================================================
//DISCLAIMER: THIS IS A GENERATED CLASS FILE
//THUS IS PROVIDED 'AS-IS' WITH NO WARRANTY
//FUNCTIONALITY CAN NOT BE GUARANTIED IN ANY WAY 
//USE AT YOUR OWN RISK 
//-------------------------------------------------------
//Built on: Rober
//=======================================================
package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.content.crafting.station.small.auto.TileSMAutoCraft;
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
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class TileWrapperSMAutoStation extends TileEntityWrapper implements IMultiTileHost, IInventoryProvider, ISidedInventory
{
	public TileWrapperSMAutoStation()
	{
		super(new TileSMAutoCraft());
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
        return false;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
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
    
	//============================
	//==Methods:ExternalInventoryWrapped
	//============================


    @Override
    public IInventory getInventory()
    {
        if (tile instanceof IInventoryProvider)
        {
            return ((IInventoryProvider) tile).getInventory();
        }
        return null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).getAccessibleSlotsFromSide(side);
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).canInsertItem(p_102007_1_, p_102007_2_, p_102007_3_);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
    {
        if (getInventory() instanceof ISidedInventory)
        {
            return ((ISidedInventory) getInventory()).canInsertItem(p_102008_1_, p_102008_2_, p_102008_3_);
        }
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        if (getInventory() != null)
        {
            return getInventory().getSizeInventory();
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (getInventory() != null)
        {
            return getInventory().getStackInSlot(slot);
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (getInventory() != null)
        {
            return getInventory().decrStackSize(slot, amount);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (getInventory() != null)
        {
            return getInventory().getStackInSlotOnClosing(slot);
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (getInventory() != null)
        {
            getInventory().setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public String getInventoryName()
    {
        if (getInventory() != null)
        {
            return getInventory().getInventoryName();
        }
        return "inventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        if (getInventory() != null)
        {
            return getInventory().hasCustomInventoryName();
        }
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        if (getInventory() != null)
        {
            return getInventory().getInventoryStackLimit();
        }
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        if (getInventory() != null)
        {
            return getInventory().isUseableByPlayer(player);
        }
        return false;
    }

    @Override
    public void openInventory()
    {
        if (getInventory() != null)
        {
            getInventory().openInventory();
        }
    }

    @Override
    public void closeInventory()
    {
        if (getInventory() != null)
        {
            getInventory().closeInventory();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (getInventory() != null)
        {
            return getInventory().isItemValidForSlot(slot, stack);
        }
        return false;
    }
    
}