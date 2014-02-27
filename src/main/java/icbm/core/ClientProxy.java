package icbm.core;

import icbm.api.IItemFrequency;
import icbm.contraption.block.TileProximityDetector;
import icbm.contraption.gui.GuiDectector;
import icbm.contraption.gui.GuiTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileProximityDetector)
        {
            return new GuiDectector((TileProximityDetector) tileEntity);
        }
        else if (entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new GuiTracker(entityPlayer, entityPlayer.inventory.getCurrentItem());
        }

        return null;
    }
}
