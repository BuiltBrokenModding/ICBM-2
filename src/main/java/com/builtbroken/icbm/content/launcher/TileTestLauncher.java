package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
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
        if(player.isSneaking())
        {
            fireMissile();
        }
        else
        {

        }
        return true;
    }

    public void fireMissile()
    {
        //Create Missile
        EntityMissile missile = new EntityMissile(world());
        missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, ExplosiveRegistry.get("TNT")));

        //Set position and rotation
        missile.rotationPitch = -90;
        missile.setPosition(x() + 0.5, y() + 3, z() + 0.5);

        //Set target point
        missile.setTarget(new Pos((TileEntity)this).add(Pos.north.multiply(100)), true);

        //Spawn and start moving
        world().spawnEntityInWorld(missile);
        missile.setIntoMotion();
    }

    @Override
    public Tile newTile()
    {
        return new TileTestLauncher();
    }
}
