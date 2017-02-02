package com.builtbroken.icbm.content.debug;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 1/18/2015.
 */
public class TileRotationTest extends TileMissileContainer
{
    public TileRotationTest()
    {
        super("TestRotation", Material.anvil);
        this.renderTileEntity = true;
        this.isOpaque = false;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 1);
    }

    EntityMissile missile = null;

    @Override
    public void update()
    {
        super.update();

        if(isClient())
        {
            if (missile == null)
            {
                missile = new EntityMissile(world());
                missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, (ItemStack)null));
                missile.setIntoMotion();
                missile.setPosition(x() + 0.5, y() + 3, z() + 0.5);
                world().spawnEntityInWorld(missile);
            }

            EntityPlayer player = worldObj.getClosestPlayer(x() + 0.5, y() + 3, z() + 0.5, 100);
            if (player != null)
            {
                missile.setTarget(new Pos(player), false);
            }
            else
            {
                missile.setTarget(new Pos(this).add(0.5, 3, 0.5), false);
            }


            EulerAngle angle = new Pos(missile).toEulerAngle(missile.target_pos);

            missile.rotationYaw = -(float)angle.yaw() - 180;
            missile.rotationPitch = -(float)angle.pitch();
            missile.setPosition(x() + 0.5, y() + 3, z() + 0.5);
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if(missile != null)
        {
            RenderUtility.renderFloatingText("Yaw: " + missile.rotationYaw, pos.add(0.5, 3, 0.5));
            RenderUtility.renderFloatingText("Pitch: " + missile.rotationPitch, pos.add(0.5, 4, 0.5));
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.dark_oak_stairs.getIcon(0, 0);
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
        return new TileRotationTest();
    }
}
