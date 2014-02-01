package icbm.explosion.missile.ex;

import calclavia.lib.recipe.RecipeUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.api.explosion.IExplosiveContainer;
import icbm.core.Settings;
import icbm.core.prefab.render.ModelICBM;
import icbm.explosion.explosive.blast.BlastEnderman;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.TileExplosive;
import icbm.explosion.missile.missile.EntityMissile;
import icbm.explosion.missile.missile.Missile;
import icbm.explosion.model.missiles.MMWan;
import mffs.api.card.ICoordLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.api.vector.Vector3;

public class ExEnder extends Missile
{
    public ExEnder(String mingZi, int tier)
    {
        super(mingZi, tier);
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

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMWan();
    }

}
