package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by robert on 1/18/2015.
 */
public class TileTestLauncher extends TileMissileContainer implements ILauncher
{
    public TileTestLauncher()
    {
        super("TestLauncher", Material.anvil);
        this.addInventoryModule(1);
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.arrow)
        {
            if(player.isSneaking())
            {
                fireMissile();
            }
            else
            {

            }
            return true;
        }
        return super.onPlayerRightClick(player, side, hit);
    }

    public void fireMissile()
    {
        EntityMissile missile = new EntityMissile(world());
        missile.setPosition(x(), y() + 3, z());
        missile.setTarget(new Pos((TileEntity)this).add(Pos.north.multiply(100)), true);
        world().spawnEntityInWorld(missile);
        missile.setIntoMotion();
    }

    @Override
    public Tile newTile()
    {
        return new TileTestLauncher();
    }
}
