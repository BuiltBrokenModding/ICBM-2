package icbm.gangshao.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRailgun extends ModelBase
{
	// fields
	ModelRenderer BASE;
	ModelRenderer NECK;

	ModelRenderer SUPPORT_1_ROTATES;
	ModelRenderer MAIN_TURRET_ROTATES;
	ModelRenderer BATTERY_PACK_ROTATES;
	ModelRenderer MAIN_CANNON_ROTATES;
	ModelRenderer NOZZLE_ROTATES;
	ModelRenderer SUPPORT_2_ROTATES;
	ModelRenderer SUPPORT_PLATFORM_ROTATES;

	public ModelRailgun()
	{
		textureWidth = 128;
		textureHeight = 128;

		BASE = new ModelRenderer(this, 0, 0);
		BASE.addBox(0F, 0F, 0F, 10, 4, 10);
		BASE.setRotationPoint(-5F, 20F, -5F);
		BASE.setTextureSize(128, 128);
		BASE.mirror = true;
		setRotation(BASE, 0F, 0F, 0F);
		NECK = new ModelRenderer(this, 0, 19);
		NECK.addBox(0F, 0F, 0F, 6, 2, 6);
		NECK.setRotationPoint(-3F, 18F, -3F);
		NECK.setTextureSize(128, 128);
		NECK.mirror = true;
		setRotation(NECK, 0F, 0F, 0F);
		SUPPORT_1_ROTATES = new ModelRenderer(this, 41, 10);
		SUPPORT_1_ROTATES.addBox(3F, 0F, -2F, 1, 7, 4);
		SUPPORT_1_ROTATES.setRotationPoint(0F, 10F, 0F);
		SUPPORT_1_ROTATES.setTextureSize(128, 128);
		SUPPORT_1_ROTATES.mirror = true;
		setRotation(SUPPORT_1_ROTATES, 0F, 0F, 0F);
		MAIN_TURRET_ROTATES = new ModelRenderer(this, 75, 0);
		MAIN_TURRET_ROTATES.addBox(-3F, -3F, -5F, 6, 6, 12);
		MAIN_TURRET_ROTATES.setRotationPoint(0F, 10F, 0F);
		MAIN_TURRET_ROTATES.setTextureSize(128, 128);
		MAIN_TURRET_ROTATES.mirror = true;
		setRotation(MAIN_TURRET_ROTATES, 0F, 0F, 0F);
		BATTERY_PACK_ROTATES = new ModelRenderer(this, 53, 10);
		BATTERY_PACK_ROTATES.addBox(-4F, -4F, 2F, 4, 4, 6);
		BATTERY_PACK_ROTATES.setRotationPoint(0F, 10F, 0F);
		BATTERY_PACK_ROTATES.setTextureSize(128, 128);
		BATTERY_PACK_ROTATES.mirror = true;
		setRotation(BATTERY_PACK_ROTATES, 0F, 0F, 0F);
		MAIN_CANNON_ROTATES = new ModelRenderer(this, 41, 22);
		MAIN_CANNON_ROTATES.addBox(-1F, -2F, -15F, 2, 2, 10);
		MAIN_CANNON_ROTATES.setRotationPoint(0F, 10F, 0F);
		MAIN_CANNON_ROTATES.setTextureSize(128, 128);
		MAIN_CANNON_ROTATES.mirror = true;
		setRotation(MAIN_CANNON_ROTATES, 0F, 0F, 0F);
		NOZZLE_ROTATES = new ModelRenderer(this, 66, 22);
		NOZZLE_ROTATES.addBox(-1F, -2F, -19F, 2, 3, 4);
		NOZZLE_ROTATES.setRotationPoint(0F, 10F, 0F);
		NOZZLE_ROTATES.setTextureSize(128, 128);
		NOZZLE_ROTATES.mirror = true;
		setRotation(NOZZLE_ROTATES, 0F, 0F, 0F);
		SUPPORT_2_ROTATES = new ModelRenderer(this, 41, 10);
		SUPPORT_2_ROTATES.addBox(-4F, 0F, -2F, 1, 7, 4);
		SUPPORT_2_ROTATES.setRotationPoint(0F, 10F, 0F);
		SUPPORT_2_ROTATES.setTextureSize(128, 128);
		SUPPORT_2_ROTATES.mirror = true;
		setRotation(SUPPORT_2_ROTATES, 0F, 0F, 0F);
		SUPPORT_PLATFORM_ROTATES = new ModelRenderer(this, 41, 0);
		SUPPORT_PLATFORM_ROTATES.addBox(-4F, 0F, -4F, 8, 1, 8);
		SUPPORT_PLATFORM_ROTATES.setRotationPoint(0F, 17F, 0F);
		SUPPORT_PLATFORM_ROTATES.setTextureSize(128, 128);
		SUPPORT_PLATFORM_ROTATES.mirror = true;
		setRotation(SUPPORT_PLATFORM_ROTATES, 0F, 0F, 0F);
	}

	public void render(float rotationYaw, float rotationPitch, float f5)
	{
		BASE.render(f5);
		NECK.render(f5);

		SUPPORT_1_ROTATES.rotateAngleY = rotationYaw;
		SUPPORT_1_ROTATES.render(f5);
		SUPPORT_2_ROTATES.rotateAngleY = rotationYaw;
		SUPPORT_2_ROTATES.render(f5);
		SUPPORT_PLATFORM_ROTATES.rotateAngleY = rotationYaw;
		SUPPORT_PLATFORM_ROTATES.render(f5);

		// Pitch Rotatable
		MAIN_TURRET_ROTATES.rotateAngleY = rotationYaw;
		MAIN_TURRET_ROTATES.rotateAngleX = rotationPitch;
		MAIN_TURRET_ROTATES.render(f5);
		BATTERY_PACK_ROTATES.rotateAngleY = rotationYaw;
		BATTERY_PACK_ROTATES.rotateAngleX = rotationPitch;
		BATTERY_PACK_ROTATES.render(f5);
		MAIN_CANNON_ROTATES.rotateAngleY = rotationYaw;
		MAIN_CANNON_ROTATES.rotateAngleX = rotationPitch;
		MAIN_CANNON_ROTATES.render(f5);
		NOZZLE_ROTATES.rotateAngleY = rotationYaw;
		NOZZLE_ROTATES.rotateAngleX = rotationPitch;
		NOZZLE_ROTATES.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}