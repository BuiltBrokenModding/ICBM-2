package icbm.core;

import icbm.core.blocks.TileProximityDetector;
import icbm.core.entity.EntityFlyingBlock;
import icbm.core.entity.EntityFragments;
import icbm.core.entity.RenderEntityBlock;
import icbm.core.entity.RenderShrapnel;
import icbm.core.gui.GuiFrequency;
import icbm.core.gui.GuiProximityDetector;
import icbm.core.tiles.GuiBox;
import icbm.core.tiles.TileBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import resonant.api.items.IItemFrequency;
import cpw.mods.fml.client.registry.RenderingRegistry;
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
    public void init()
    {
        super.init();
        RenderingRegistry.registerEntityRenderingHandler(EntityFlyingBlock.class, new RenderEntityBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityFragments.class, new RenderShrapnel());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileBox)
        {
            return new GuiBox(entityPlayer, (TileBox) tileEntity);
        }
        else if (tileEntity instanceof TileProximityDetector)
        {
            return new GuiProximityDetector((TileProximityDetector) tileEntity);
        }
        else if (entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new GuiFrequency(entityPlayer, entityPlayer.inventory.getCurrentItem());
        }

        return null;
    }
}
