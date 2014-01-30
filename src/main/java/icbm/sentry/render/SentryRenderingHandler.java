package icbm.sentry.render;

import icbm.sentry.turret.tiles.TileSentry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class SentryRenderingHandler extends TileEntitySpecialRenderer
{
    private RenderTurret renderer;


    public SentryRenderingHandler()
    {
        super();
        this.renderer = null;
    }

    @Override
    public void renderTileEntityAt (TileEntity t, double x, double y, double z, float f)
    {
        if(renderer == null) {
            getRendererForTile(t);
            return;
        }
        
        renderer.renderTileEntityAt(t, x, y, z, f);

    }
    
    

    private RenderTurret getRendererForTile (TileEntity tile)
    {       
        //System.out.println("rend");
        
        
        TileSentry sentry = null;
        if (tile instanceof TileSentry)
        {
            
            sentry = (TileSentry) tile;
            if (sentry == null)
                System.out.println("tile is null");
            if (sentry.getClientSentryType() == null)
                System.out.println("type is null");
            
            //System.out.println(sentry.getClientSentryType());
                
            
            switch (sentry.getClientSentryType())
            {
            case VOID:
                return null;
            case AA:
                return new RenderAAGun();
            case CLASSIC:
                return new RenderGunTurret();
            case LASER:
                return new RenderLaserTurret();
            case RAILGUN:
                return new RenderLaserTurret();
            default:
                break;
            }
            
        }
        return null;
    }
}
