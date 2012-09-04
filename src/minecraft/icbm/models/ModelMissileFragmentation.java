
package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelMissileFragmentation extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
    ModelRenderer Shape12;
    ModelRenderer Shape13;
  
  public ModelMissileFragmentation()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      Shape1 = new ModelRenderer(this, 0, 0);
      Shape1.addBox(0F, 0F, 0F, 6, 40, 6);
      Shape1.setRotationPoint(-3F, -16F, -3F);
      Shape1.setTextureSize(128, 128);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 25, 0);
      Shape2.addBox(0F, 0F, 0F, 2, 5, 18);
      Shape2.setRotationPoint(-1F, 19F, -9F);
      Shape2.setTextureSize(128, 128);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new ModelRenderer(this, 60, 40);
      Shape3.addBox(0F, 0F, 0F, 18, 5, 2);
      Shape3.setRotationPoint(-9F, 19F, -1F);
      Shape3.setTextureSize(128, 128);
      Shape3.mirror = true;
      setRotation(Shape3, 0F, 0F, 0F);
      Shape4 = new ModelRenderer(this, 0, 65);
      Shape4.addBox(0F, 0F, 0F, 10, 4, 2);
      Shape4.setRotationPoint(-5F, -13F, -1F);
      Shape4.setTextureSize(128, 128);
      Shape4.mirror = true;
      setRotation(Shape4, 0F, 0F, 0F);
      Shape5 = new ModelRenderer(this, 0, 50);
      Shape5.addBox(0F, 0F, 0F, 2, 4, 10);
      Shape5.setRotationPoint(-1F, -13F, -5F);
      Shape5.setTextureSize(128, 128);
      Shape5.mirror = true;
      setRotation(Shape5, 0F, 0F, 0F);
      Shape6 = new ModelRenderer(this, 25, 30);
      Shape6.addBox(0F, 0F, 0F, 8, 3, 8);
      Shape6.setRotationPoint(-4F, -19F, -4F);
      Shape6.setTextureSize(128, 128);
      Shape6.mirror = true;
      setRotation(Shape6, 0F, 0F, 0F);
      Shape7 = new ModelRenderer(this, 25, 45);
      Shape7.addBox(0F, 0F, 0F, 4, 2, 4);
      Shape7.setRotationPoint(-2F, -21F, -2F);
      Shape7.setTextureSize(128, 128);
      Shape7.mirror = true;
      setRotation(Shape7, 0F, 0F, 0F);
      Shape8 = new ModelRenderer(this, 25, 65);
      Shape8.addBox(0F, 0F, 0F, 6, 2, 1);
      Shape8.setRotationPoint(-3F, -18F, -5F);
      Shape8.setTextureSize(128, 128);
      Shape8.mirror = true;
      setRotation(Shape8, 0F, 0F, 0F);
      Shape9 = new ModelRenderer(this, 25, 55);
      Shape9.addBox(0F, 0F, 0F, 1, 2, 6);
      Shape9.setRotationPoint(4F, -18F, -3F);
      Shape9.setTextureSize(128, 128);
      Shape9.mirror = true;
      setRotation(Shape9, 0F, 0F, 0F);
      Shape10 = new ModelRenderer(this, 25, 65);
      Shape10.addBox(0F, 0F, 0F, 6, 2, 1);
      Shape10.setRotationPoint(-3F, -18F, 4F);
      Shape10.setTextureSize(128, 128);
      Shape10.mirror = true;
      setRotation(Shape10, 0F, 0F, 0F);
      Shape11 = new ModelRenderer(this, 25, 55);
      Shape11.addBox(0F, 0F, 0F, 1, 2, 6);
      Shape11.setRotationPoint(-5F, -18F, -3F);
      Shape11.setTextureSize(128, 128);
      Shape11.mirror = true;
      setRotation(Shape11, 0F, 0F, 0F);
      Shape12 = new ModelRenderer(this, 80, 0);
      Shape12.addBox(0F, 0F, 0F, 10, 9, 10);
      Shape12.setRotationPoint(-5F, 15F, -5F);
      Shape12.setTextureSize(128, 128);
      Shape12.mirror = true;
      setRotation(Shape12, 0F, 0F, 0F);
      Shape13 = new ModelRenderer(this, 80, 20);
      Shape13.addBox(0F, 0F, 0F, 8, 4, 8);
      Shape13.setRotationPoint(-4F, 11F, -4F);
      Shape13.setTextureSize(128, 128);
      Shape13.mirror = true;
      setRotation(Shape13, 0F, 0F, 0F);
  }
  
  @Override
public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Shape1.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
    Shape4.render(f5);
    Shape5.render(f5);
    Shape6.render(f5);
    Shape7.render(f5);
    Shape8.render(f5);
    Shape9.render(f5);
    Shape10.render(f5);
    Shape11.render(f5);
    Shape12.render(f5);
    Shape13.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  @Override
public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }

}
