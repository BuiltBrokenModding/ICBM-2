package icbm.wanyi;

import icbm.api.ITracker;
import icbm.core.di.ItElectricICBM;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItGenZongQi extends ItElectricICBM implements ITracker {
	private static final float YONG_DIAN_LIANG = 0.1f;

	public ItGenZongQi(int id) {
		super(id, "tracker");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		if (par1IconRegister instanceof TextureMap) {
			((TextureMap) par1IconRegister).setTextureEntry(this
					.getUnlocalizedName().replace("item.", ""),
					new TextureGenZhongQi());
			this.itemIcon = ((TextureMap) par1IconRegister)
					.getTextureExtry(this.getUnlocalizedName().replace("item.",
							""));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(itemStack, par2EntityPlayer, par3List, par4);

		Entity trackingEntity = getTrackingEntity(FMLClientHandler.instance()
				.getClient().theWorld, itemStack);

		if (trackingEntity != null) {
			par3List.add("Tracking: " + trackingEntity.getEntityName());
		}
	}

	@Override
	public void setTrackingEntity(ItemStack itemStack, Entity entity) {
		if (itemStack.stackTagCompound == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}

		if (entity != null) {
			itemStack.stackTagCompound.setInteger("trackingEntity",
					entity.entityId);
		}
	}

	@Override
	public Entity getTrackingEntity(World worldObj, ItemStack itemStack) {
		if (worldObj != null) {
			if (itemStack.stackTagCompound != null) {
				int trackingID = itemStack.stackTagCompound
						.getInteger("trackingEntity");
				return worldObj.getEntityByID(trackingID);
			}
		}
		return null;
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
		setTrackingEntity(par1ItemStack, par3EntityPlayer);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(itemStack, par2World, par3Entity, par4, par5);

		if (par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) par3Entity;

			if (player.inventory.getCurrentItem() != null) {
				if (player.inventory.getCurrentItem().itemID == this.itemID) {
					Entity trackingEntity = this.getTrackingEntity(par2World,
							itemStack);

					if (trackingEntity != null) {
						if (this.discharge(itemStack, YONG_DIAN_LIANG, true) < YONG_DIAN_LIANG) {
							this.setTrackingEntity(itemStack, null);
						}
					}
				}
			}
		}
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity. Processed before
	 * damage is done, if return value is true further processing is canceled
	 * and the entity is not attacked.
	 * 
	 * @param itemStack
	 *            The Item being used
	 * @param player
	 *            The player that is attacking
	 * @param entity
	 *            The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	@Override
	public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player,
			Entity entity) {
		if (!player.worldObj.isRemote) {
			if (this.getElectricityStored(itemStack) > YONG_DIAN_LIANG) {
				setTrackingEntity(itemStack, entity);
				player.addChatMessage("Now tracking: " + entity.getEntityName());
				return true;
			} else {
				player.addChatMessage("Tracker out of electricity!");
			}
		}

		return false;
	}

	@Override
	public float getVoltage(ItemStack itemStack) {
		return 20;
	}

	@Override
	public float getMaxElectricityStored(ItemStack itemStack) {
		return 100000;
	}
}
