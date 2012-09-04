package icbm.models;

import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelCruiseLauncherRail extends ModelBase
{
	//fields
    ModelRenderer LAUNCH_RAIL_1_ELEVATES;
    ModelRenderer LAUNCH_RAIL_2_ELEVATES;
    ModelRenderer ELEVATION_MECHANISM_ELEVATES;
    ModelRenderer PROPULSION_SHIELDING_ELEVATES;
    ModelRenderer TURRET_MAIN_BODY_ROTATES;
  
  public ModelCruiseLauncherRail()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      LAUNCH_RAIL_1_ELEVATES = new ModelRenderer(this, 0, 71);
      LAUNCH_RAIL_1_ELEVATES.addBox(-5F, -1F, -21F, 4, 1, 28);
      LAUNCH_RAIL_1_ELEVATES.setRotationPoint(0F, 18F, 0F);
      LAUNCH_RAIL_1_ELEVATES.setTextureSize(128, 128);
      LAUNCH_RAIL_1_ELEVATES.mirror = true;
      setRotation(LAUNCH_RAIL_1_ELEVATES, 0F, 0F, 0.7853982F);
      LAUNCH_RAIL_2_ELEVATES = new ModelRenderer(this, 0, 71);
      LAUNCH_RAIL_2_ELEVATES.addBox(-5F, 0F, -21F, 4, 1, 28);
      LAUNCH_RAIL_2_ELEVATES.setRotationPoint(0F, 18F, 0F);
      LAUNCH_RAIL_2_ELEVATES.setTextureSize(128, 128);
      LAUNCH_RAIL_2_ELEVATES.mirror = true;
      setRotation(LAUNCH_RAIL_2_ELEVATES, 0F, 0F, 2.356194F);
      ELEVATION_MECHANISM_ELEVATES = new ModelRenderer(this, 0, 52);
      ELEVATION_MECHANISM_ELEVATES.addBox(-1F, -2F, -3F, 2, 4, 13);
      ELEVATION_MECHANISM_ELEVATES.setRotationPoint(0F, 18F, 0F);
      ELEVATION_MECHANISM_ELEVATES.setTextureSize(128, 128);
      ELEVATION_MECHANISM_ELEVATES.mirror = true;
      setRotation(ELEVATION_MECHANISM_ELEVATES, 0F, 0F, 0F);
      PROPULSION_SHIELDING_ELEVATES = new ModelRenderer(this, 0, 5);
      PROPULSION_SHIELDING_ELEVATES.addBox(-2F, -6F, 10F, 4, 8, 1);
      PROPULSION_SHIELDING_ELEVATES.setRotationPoint(0F, 18F, 0F);
      PROPULSION_SHIELDING_ELEVATES.setTextureSize(128, 128);
      PROPULSION_SHIELDING_ELEVATES.mirror = true;
      setRotation(PROPULSION_SHIELDING_ELEVATES, 0F, 0F, 0F);
      TURRET_MAIN_BODY_ROTATES = new ModelRenderer(this, 0, 38);
      TURRET_MAIN_BODY_ROTATES.addBox(-3F, 0F, -5F, 6, 3, 10);
      TURRET_MAIN_BODY_ROTATES.setRotationPoint(0F, 18F, 0F);
      TURRET_MAIN_BODY_ROTATES.setTextureSize(128, 128);
      TURRET_MAIN_BODY_ROTATES.mirror = true;
      setRotation(TURRET_MAIN_BODY_ROTATES, 0F, 0F, 0F);
  }
  
  public void render(float f5)
  {
    LAUNCH_RAIL_1_ELEVATES.render(f5);
    LAUNCH_RAIL_2_ELEVATES.render(f5);
    ELEVATION_MECHANISM_ELEVATES.render(f5);
    PROPULSION_SHIELDING_ELEVATES.render(f5);
    TURRET_MAIN_BODY_ROTATES.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }

}
