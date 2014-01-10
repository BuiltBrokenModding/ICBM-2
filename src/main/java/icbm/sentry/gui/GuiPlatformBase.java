package icbm.sentry.gui;

import icbm.core.prefab.render.GuiICBM;
import icbm.sentry.platform.TileTurretPlatform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author Calclavia
 * 
 */
public abstract class GuiPlatformBase extends GuiICBM
{
	public static final int MAX_BUTTON_ID = 0;

	protected TileTurretPlatform tileEntity;
	protected EntityPlayer entityPlayer;

	public GuiPlatformBase(InventoryPlayer inventory, TileTurretPlatform tileEntity)
	{
		this.entityPlayer = inventory.player;
		this.tileEntity = tileEntity;
	}

}
