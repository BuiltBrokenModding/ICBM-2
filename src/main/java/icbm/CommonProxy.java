package icbm;

import cpw.mods.fml.common.registry.GameRegistry;
import icbm.content.tile.assembler.ContainerMissileTable;
import icbm.content.entity.EntityMissile;
import icbm.content.tile.ex.TileExplosive;
import icbm.content.tile.emptower.TileEMPTower;
import icbm.content.tile.assembler.TileMissileAssembler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import resonant.api.items.IItemFrequency;
import resonant.lib.gui.ContainerDummy;
import resonant.lib.prefab.AbstractProxy;
import resonant.lib.transform.vector.Vector3;

import java.util.List;

public class CommonProxy extends AbstractProxy
{
    @Override
    public void init()
    {
        super.init();
       //GameRegistry.registerTileEntity(TileLauncherBase.class, "ICBMFaSheDi");
        //GameRegistry.registerTileEntity(TileLauncherScreen.class, "ICBMFaSheShiMuo");
        //GameRegistry.registerTileEntity(TileLauncherFrame.class, "ICBMFaSheJia");
        //GameRegistry.registerTileEntity(TileRadarStation.class, "ICBMLeiDaTai");
        GameRegistry.registerTileEntity(TileEMPTower.class, "ICBMDianCiQi");
        GameRegistry.registerTileEntity(TileExplosive.class, "ICBMZhaDan");
        GameRegistry.registerTileEntity(TileMissileAssembler.class, "ICBMMissileTable");
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new ContainerDummy(player, tileEntity);
        }
        else if (tileEntity instanceof TileMissileAssembler)
        {
            return new ContainerMissileTable(player.inventory, (TileMissileAssembler) tileEntity);
        }
        return null;
    }

    public boolean isGaoQing()
    {
        return false;
    }

    public void spawnParticle(String name, World world, Vector3 position, float scale, double distance)
    {
        this.spawnParticle(name, world, position, 0, 0, 0, scale, distance);
    }

    public void spawnParticle(String name, World world, Vector3 position, double motionX, double motionY, double motionZ, float scale, double distance)
    {
        this.spawnParticle(name, world, position, motionX, motionY, motionZ, 1, 1, 1, scale, distance);
    }

    public void spawnParticle(String name, World world, Vector3 position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
    {

    }

    public IUpdatePlayerListBox getDaoDanShengYin(EntityMissile eDaoDan)
    {
        return null;
    }

    public int getParticleSetting()
    {
        return -1;
    }

    public List<Entity> getEntityFXs()
    {
        return null;
    }

    public void spawnShock(World world, Vector3 position, Vector3 target)
    {

    }

    public void spawnShock(World world, Vector3 startVec, Vector3 targetVec, int duration)
    {
        // TODO Auto-generated method stub

    }
}
