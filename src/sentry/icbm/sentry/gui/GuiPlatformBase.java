package icbm.sentry.gui;

import icbm.core.ICBMCore;
import icbm.sentry.CommonProxy;
import icbm.sentry.ICBMSentry;
import icbm.sentry.platform.TileEntityTurretPlatform;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.builtbroken.minecraft.prefab.invgui.GuiBase;
import com.builtbroken.minecraft.prefab.invgui.GuiMachineBase;
import com.builtbroken.minecraft.prefab.invgui.GuiMachineContainer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
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
