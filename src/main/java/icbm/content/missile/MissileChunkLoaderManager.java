package icbm.content.missile;

import icbm.IChunkLoadHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import resonant.lib.transform.vector.Vector3;

import java.util.List;

/**
 * Created by robert on 11/19/2014.
 */
public class MissileChunkLoaderManager implements ForgeChunkManager.LoadingCallback
{
    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        for (ForgeChunkManager.Ticket ticket : tickets)
        {
            if (ticket.getEntity() instanceof IChunkLoadHandler)
            {
                ((IChunkLoadHandler) ticket.getEntity()).chunkLoaderInit(ticket);
            } else
            {
                if (ticket.getModData() != null)
                {
                    Vector3 position = new Vector3(ticket.getModData());

                    TileEntity tileEntity = position.getTileEntity(ticket.world);

                    if (tileEntity instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                    }
                }
            }
        }
    }
}
