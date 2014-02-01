package icbm.explosion.model.tiles;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MLeiShePao2 extends ModelBase
{
    // fields
    ModelRenderer Main_Turret_MOVES;
    ModelRenderer Back_Armor_MOVES;
    ModelRenderer Armor_1_MOVES;
    ModelRenderer Armor_2_MOVES;
    ModelRenderer Armor_3_MOVES;
    ModelRenderer Armor_4_MOVES;
    ModelRenderer Armor_5_MOVES;
    ModelRenderer Armor_6_MOVES;
    ModelRenderer Front_Armor_1_MOVES;
    ModelRenderer Front_Armor_2_MOVES;
    ModelRenderer Cannon_MOVES;

    public MLeiShePao2()
    {
        textureWidth = 128;
        textureHeight = 128;

        Main_Turret_MOVES = new ModelRenderer(this, 60, 10);
        Main_Turret_MOVES.addBox(-5F, 0F, -5F, 10, 4, 14);
        Main_Turret_MOVES.setRotationPoint(0F, -5F, 0F);
        Main_Turret_MOVES.setTextureSize(128, 128);
        Main_Turret_MOVES.mirror = true;
        setRotation(Main_Turret_MOVES, 0F, 0F, 0F);
        Back_Armor_MOVES = new ModelRenderer(this, 60, 0);
        Back_Armor_MOVES.addBox(-5F, 5F, 5F, 10, 3, 3);
        Back_Armor_MOVES.setRotationPoint(0F, -3F, 0F);
        Back_Armor_MOVES.setTextureSize(128, 128);
        Back_Armor_MOVES.mirror = true;
        setRotation(Back_Armor_MOVES, 0.7853982F, 0F, 0F);
        Armor_1_MOVES = new ModelRenderer(this, 90, 0);
        Armor_1_MOVES.addBox(3F, 0F, 0F, 4, 4, 3);
        Armor_1_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_1_MOVES.setTextureSize(128, 128);
        Armor_1_MOVES.mirror = true;
        setRotation(Armor_1_MOVES, 0F, 0.7504916F, 0F);
        Armor_2_MOVES = new ModelRenderer(this, 60, 30);
        Armor_2_MOVES.addBox(4F, 0F, -2F, 4, 4, 8);
        Armor_2_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_2_MOVES.setTextureSize(128, 128);
        Armor_2_MOVES.mirror = true;
        setRotation(Armor_2_MOVES, 0F, -0.4363323F, 0F);
        Armor_3_MOVES = new ModelRenderer(this, 90, 30);
        Armor_3_MOVES.addBox(4F, 0F, -1F, 4, 4, 4);
        Armor_3_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_3_MOVES.setTextureSize(128, 128);
        Armor_3_MOVES.mirror = true;
        setRotation(Armor_3_MOVES, 0F, 0.1919862F, 0F);
        Armor_4_MOVES = new ModelRenderer(this, 90, 0);
        Armor_4_MOVES.addBox(3F, 0F, -3F, 4, 4, 3);
        Armor_4_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_4_MOVES.setTextureSize(128, 128);
        Armor_4_MOVES.mirror = true;
        setRotation(Armor_4_MOVES, 0F, 2.391101F, 0F);
        Armor_5_MOVES = new ModelRenderer(this, 90, 30);
        Armor_5_MOVES.addBox(-1F, 0F, 4F, 4, 4, 4);
        Armor_5_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_5_MOVES.setTextureSize(128, 128);
        Armor_5_MOVES.mirror = true;
        setRotation(Armor_5_MOVES, 0F, -1.780236F, 0F);
        Armor_6_MOVES = new ModelRenderer(this, 60, 30);
        Armor_6_MOVES.addBox(-8F, 0F, -2F, 4, 4, 8);
        Armor_6_MOVES.setRotationPoint(0F, -5F, 0F);
        Armor_6_MOVES.setTextureSize(128, 128);
        Armor_6_MOVES.mirror = true;
        setRotation(Armor_6_MOVES, 0F, 0.4363323F, 0F);
        Front_Armor_1_MOVES = new ModelRenderer(this, 110, 0);
        Front_Armor_1_MOVES.addBox(-5F, 3F, -4F, 4, 4, 2);
        Front_Armor_1_MOVES.setRotationPoint(0F, -5F, 0F);
        Front_Armor_1_MOVES.setTextureSize(128, 128);
        Front_Armor_1_MOVES.mirror = true;
        setRotation(Front_Armor_1_MOVES, -0.6457718F, 0F, 0F);
        Front_Armor_2_MOVES = new ModelRenderer(this, 110, 0);
        Front_Armor_2_MOVES.addBox(1F, 3F, -4F, 4, 4, 2);
        Front_Armor_2_MOVES.setRotationPoint(0F, -5F, 0F);
        Front_Armor_2_MOVES.setTextureSize(128, 128);
        Front_Armor_2_MOVES.mirror = true;
        setRotation(Front_Armor_2_MOVES, -0.6457718F, 0F, 0F);
        Cannon_MOVES = new ModelRenderer(this, 60, 50);
        Cannon_MOVES.addBox(-1F, 0F, -22F, 2, 2, 17);
        Cannon_MOVES.setRotationPoint(0F, -4F, 0F);
        Cannon_MOVES.setTextureSize(128, 128);
        Cannon_MOVES.mirror = true;
        setRotation(Cannon_MOVES, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Main_Turret_MOVES.render(f5);
        Back_Armor_MOVES.render(f5);
        Armor_1_MOVES.render(f5);
        Armor_2_MOVES.render(f5);
        Armor_3_MOVES.render(f5);
        Armor_4_MOVES.render(f5);
        Armor_5_MOVES.render(f5);
        Armor_6_MOVES.render(f5);
        Front_Armor_1_MOVES.render(f5);
        Front_Armor_2_MOVES.render(f5);
        Cannon_MOVES.render(f5);
    }

    public void render(float f5)
    {
        Main_Turret_MOVES.render(f5);
        Back_Armor_MOVES.render(f5);
        Armor_1_MOVES.render(f5);
        Armor_2_MOVES.render(f5);
        Armor_3_MOVES.render(f5);
        Armor_4_MOVES.render(f5);
        Armor_5_MOVES.render(f5);
        Armor_6_MOVES.render(f5);
        Front_Armor_1_MOVES.render(f5);
        Front_Armor_2_MOVES.render(f5);
        Cannon_MOVES.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
