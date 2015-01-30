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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

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
        if(isServer())
        {
            if (player.isSneaking())
            {
                fireMissile();
            }
        }
        return true;
    }

    EntityMissile missile = null;

    @Override
    public void update()
    {
        super.update();

        if(isServer())
        {
            if (missile == null)
            {
                missile = new EntityMissile(world());
                missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, null));

                missile.setPosition(x() + 0.5, y() + 3, z() + 0.5);
                world().spawnEntityInWorld(missile);
            }
            missile.setPositionAndRotation(x() + 0.5, y() + 3, z() + 0.5, 0, 180);
        }
    }

    public void fireMissile()
    {
        if(missile != null)
        {
            missile.setTarget(new Pos((TileEntity) this).add(Pos.north.multiply(100)), true);
            missile.sourceOfProjectile = new Pos(this);
            missile.motionX = 0;
            missile.motionZ = 0;
            missile.motionY = 2;
            missile.setIntoMotion();
            missile = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.pumpkin.getIcon(0, 0);
    }

    @Override
    public Tile newTile()
    {
        return new TileTestLauncher();
    }
}
