package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MLeiShePao extends ModelBase
{
    // fields
    ModelRenderer Turret_Base;
    ModelRenderer Support_1;
    ModelRenderer Support_2;
    ModelRenderer Support_3;
    ModelRenderer Support_4;
    ModelRenderer Turret_Neck;
    ModelRenderer Turret_Body;

    public MLeiShePao()
    {
        textureWidth = 128;
        textureHeight = 128;

        Turret_Base = new ModelRenderer(this, 0, 0);
        Turret_Base.addBox(0F, 0F, 0F, 14, 14, 14);
        Turret_Base.setRotationPoint(-7F, 10F, -7F);
        Turret_Base.setTextureSize(128, 128);
        Turret_Base.mirror = true;
        setRotation(Turret_Base, 0F, 0F, 0F);
        Support_1 = new ModelRenderer(this, 0, 60);
        Support_1.addBox(0F, 0F, 0F, 2, 6, 2);
        Support_1.setRotationPoint(6F, 18F, 6F);
        Support_1.setTextureSize(128, 128);
        Support_1.mirror = true;
        setRotation(Support_1, 0F, 0F, 0F);
        Support_2 = new ModelRenderer(this, 0, 60);
        Support_2.addBox(0F, 0F, 0F, 2, 6, 2);
        Support_2.setRotationPoint(6F, 18F, -8F);
        Support_2.setTextureSize(128, 128);
        Support_2.mirror = true;
        setRotation(Support_2, 0F, 0F, 0F);
        Support_3 = new ModelRenderer(this, 0, 60);
        Support_3.addBox(0F, 0F, 0F, 2, 6, 2);
        Support_3.setRotationPoint(-8F, 18F, -8F);
        Support_3.setTextureSize(128, 128);
        Support_3.mirror = true;
        setRotation(Support_3, 0F, 0F, 0F);
        Support_4 = new ModelRenderer(this, 0, 60);
        Support_4.addBox(0F, 0F, 0F, 2, 6, 2);
        Support_4.setRotationPoint(-8F, 18F, 6F);
        Support_4.setTextureSize(128, 128);
        Support_4.mirror = true;
        setRotation(Support_4, 0F, 0F, 0F);
        Turret_Neck = new ModelRenderer(this, 10, 60);
        Turret_Neck.addBox(-1F, 0F, -1F, 2, 1, 2);
        Turret_Neck.setRotationPoint(0F, -1F, 0F);
        Turret_Neck.setTextureSize(128, 128);
        Turret_Neck.mirror = true;
        setRotation(Turret_Neck, 0F, 0F, 0F);
        Turret_Body = new ModelRenderer(this, 0, 30);
        Turret_Body.addBox(0F, 0F, 0F, 10, 11, 10);
        Turret_Body.setRotationPoint(-5F, 0F, -5F);
        Turret_Body.setTextureSize(128, 128);
        Turret_Body.mirror = true;
        setRotation(Turret_Body, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Turret_Base.render(f5);
        Support_1.render(f5);
        Support_2.render(f5);
        Support_3.render(f5);
        Support_4.render(f5);
        Turret_Neck.render(f5);
        Turret_Body.render(f5);
    }

    public void render(float f5)
    {
        Turret_Body.render(f5);
        Turret_Base.render(f5);
        Support_1.render(f5);
        Support_2.render(f5);
        Support_3.render(f5);
        Support_4.render(f5);
        Turret_Neck.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
