package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.TileExplosive;
import icbm.explosion.explosive.blast.BlastEnderman;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.explosion.IExplosiveContainer;
import calclavia.api.mffs.card.ICoordLink;
import calclavia.lib.recipe.RecipeUtility;

public class ExEnder extends Ex
{
    public ExEnder()
    {
        super("ender", 3);
        this.modelName = "missile_ender.tcn";
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {

        if (entityPlayer.inventory.getCurrentItem() != null)
        {
            if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ICoordLink)
            {
                Vector3 link = ((ICoordLink) entityPlayer.inventory.getCurrentItem().getItem()).getLink(entityPlayer.inventory.getCurrentItem());

                if (link != null)
                {
                    TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

                    if (tileEntity instanceof TileExplosive)
                    {
                        link.writeToNBT(((TileExplosive) tileEntity).nbtData);

                        if (!world.isRemote)
                        {
                            entityPlayer.addChatMessage("Synced coordinate with " + this.getExplosiveName());
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer)
    {
        if (entityPlayer.inventory.getCurrentItem() != null)
        {
            if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ICoordLink)
            {
                Vector3 link = ((ICoordLink) entityPlayer.inventory.getCurrentItem().getItem()).getLink(entityPlayer.inventory.getCurrentItem());

                if (link != null)
                {
                    link.writeToNBT(missileObj.nbtData);
                    if (!missileObj.worldObj.isRemote)
                    {
                        entityPlayer.addChatMessage("Synced coordinate with " + this.getMissileName());
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void init()
    {
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "PPP", "PTP", "PPP", 'P', Item.enderPearl, 'T', Explosive.attractive.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        Vector3 teleportTarget = null;

        if (entity instanceof IExplosiveContainer)
        {
            if (((IExplosiveContainer) entity).getTagCompound().hasKey("x") && ((IExplosiveContainer) entity).getTagCompound().hasKey("y") && ((IExplosiveContainer) entity).getTagCompound().hasKey("z"))
            {
                teleportTarget = new Vector3(((IExplosiveContainer) entity).getTagCompound());
            }
        }

        new BlastEnderman(world, entity, x, y, z, 30, teleportTarget).explode();
    }
}
