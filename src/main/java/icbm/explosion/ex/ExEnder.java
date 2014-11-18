package icbm.explosion.ex;

import icbm.Settings;
import icbm.content.entity.EntityMissile;
import icbm.explosion.Explosive;
import icbm.content.tile.ex.TileExplosive;
import icbm.explosion.blast.BlastEnderman;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonant.api.explosion.IExplosiveContainer;
import resonant.api.mffs.card.ICoordLink;
import resonant.lib.recipe.RecipeUtility;
import resonant.lib.transform.vector.Vector3;

public class ExEnder extends Explosion
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
                    TileEntity tileEntity = world.getTileEntity(x, y, z);

                    if (tileEntity instanceof TileExplosive)
                    {
                        link.writeNBT(((TileExplosive) tileEntity).nbtData);

                        if (!world.isRemote)
                        {
                            entityPlayer.addChatMessage(new ChatComponentText("Synced coordinate with " + this.getExplosiveName()));
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
                    link.writeNBT(missileObj.nbtData);
                    if (!missileObj.worldObj.isRemote)
                    {
                        entityPlayer.addChatMessage(new ChatComponentText("Synced coordinate with " + this.getMissileName()));
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
        RecipeUtility.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "PPP", "PTP", "PPP", 'P', Items.ender_pearl, 'T', Explosive.attractive.getItemStack() }), this.getUnlocalizedName(), Settings.CONFIGURATION, true);
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
