package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;

/**
 * Prefab for all the workstation to use for making implementation easier
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2015.
 */
public class TileAbstractWorkstation extends TileModuleMachine implements IMultiTileHost
{
    private boolean _destroyingStructure = false;
    protected boolean rotating = false;

    public TileAbstractWorkstation(String name, Material material)
    {
        super(name, material);
    }

    @Override
    public void onRemove(Block block, int par6)
    {
        super.onRemove(block, par6);
        breakDownStructure(true);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos(this));
            if (getLayoutOfMultiBlock().containsKey(pos))
            {
                breakDownStructure(harvest);
                return true;
            }
        }
        return false;
    }

    private void breakDownStructure(boolean b)
    {
        if (!_destroyingStructure)
        {
            _destroyingStructure = true;
            MultiBlockHelper.destroyMultiBlockStructure(this, b);
            _destroyingStructure = false;
        }
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        return false;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        return null;
    }
}
