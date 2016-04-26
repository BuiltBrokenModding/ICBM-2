package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.RenderMissile;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;

/**
 * Simple display table to test to make sure missiles are rendering correctly
 * Later will be changed to only render micro and small missiles
 * Created by robert on 12/31/2014.
 */
public class TileMissileDisplay extends TileMissileContainer
{
    EntityMissile missile;

    public TileMissileDisplay()
    {
        super("missileDisplay", Material.circuits);
        this.renderTileEntity = true;
        this.isOpaque = false;
        this.bounds = new Cube(0, 0, 0, 1, .4, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        //Use clay texture for breaking animation
        return Blocks.hardened_clay.getIcon(0, 0);
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
        return new TileMissileDisplay();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if (missile != null)
        {
            RenderMissile.INSTANCE.doRender(missile, pos.x(), pos.y(), pos.z(), 0.0F, 1.0F); //1.0F
        }
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
    }

    @Override
    public void update()
    {
        super.update();
        if (isClient())
        {
            if (missile == null)
            {
                if (getMissile() != null)
                {
                    missile = new EntityMissile(world());
                    missile.setMissile(getMissile());
                    missile.setPosition(xCoord + 0.5, yCoord + 1, zCoord + 0.5);
                }
            }
            else
            {
                if (getMissile() == null)
                {
                    missile = null;
                }
                else if (ticks % 20 == 0)
                {
                    missile.motionX = missile.motionY = missile.motionZ = 0;
                    missile.rotationYaw = 90;
                    missile.rotationYaw = missile.rotationYaw % 360.0F;
                    missile.rotationPitch = 0;
                    missile.rotationPitch = missile.rotationPitch % 360.0F;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }
}
