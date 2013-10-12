package icbm.contraption;

import icbm.contraption.block.TileEntityCamouflage;
import icbm.contraption.block.TileEntityDetector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
    public void preInit()
    {
    }

    public void init()
    {
        GameRegistry.registerTileEntity(TileEntityCamouflage.class, "ICBMYinXin");
        GameRegistry.registerTileEntity(TileEntityDetector.class, "ICBMYinGanQi");
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
