package icbm.sentry.render;

import icbm.sentry.turret.tiles.TileSentry;
import net.minecraft.tileentity.TileEntity;

public class SentryRenderingHandler extends RenderTurret
{
    private RenderTurret renderer;

    private boolean ticked;

    public SentryRenderingHandler()
    {
        super();
        this.ticked = false;
    }

    @Override
    public void renderTileEntityAt (TileEntity t, double x, double y, double z, float f)
    {
        if (!this.ticked)
            this.renderer = getRendererForTile(t);
        
        this.renderer.renderTileEntityAt(t, x, y, z, f);

    }
    
    

    private RenderTurret getRendererForTile (TileEntity tile)
    {
        TileSentry sentry = null;
        if (tile instanceof TileSentry)
        {
            sentry = (TileSentry) tile;

            switch (sentry.getSentry().getSentryType())
            {
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
