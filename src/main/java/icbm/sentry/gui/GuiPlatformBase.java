package icbm.sentry.gui;

import icbm.core.ICBMCore;
import icbm.sentry.ICBMSentry;
import icbm.sentry.platform.TileEntityTurretPlatform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import com.builtbroken.minecraft.prefab.invgui.GuiMachineContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A base class for gun platforms
 * 
 * @author Darkguardsman */
@SideOnly(Side.CLIENT)
public abstract class GuiPlatformBase extends GuiMachineContainer
{
    public GuiPlatformBase(InventoryPlayer player, Container container, TileEntityTurretPlatform tileEntity)
    {
        super(ICBMSentry.instance, container, player, tileEntity);
        TEXTURE = new ResourceLocation(ICBMCore.DOMAIN, ICBMCore.GUI_PATH + "gui_platform_terminal.png");

    }
}
