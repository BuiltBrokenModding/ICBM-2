package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
        if (isServer())
        {
            if (player.isSneaking())
            {
                fireMissile();
            }
        }
        return true;
    }

    @Override
    public void update()
    {
        super.update();
    }

    public void fireMissile()
    {
        EntityMissile missile = new EntityMissile(world());
        missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, null));
        missile.setPositionAndRotation(x() + 0.5, y() + 3, z() + 0.5, 0, 0);
        missile.setTarget(new Pos((TileEntity) this).add(Pos.north.multiply(50)), true);
        missile.sourceOfProjectile = new Pos(this);
        missile.setVelocity(0, 2, 0);
        world().spawnEntityInWorld(missile);
        missile.setIntoMotion();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.pumpkin.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public Tile newTile()
    {
        return new TileTestLauncher();
    }
}
