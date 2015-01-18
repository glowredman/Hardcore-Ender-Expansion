package chylex.hee.render.projectile;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.projectile.EntityProjectileCurse;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.curse.CurseType;

@SideOnly(Side.CLIENT)
public class RenderProjectileCurse extends RenderProjectileBase{
	private ItemStack curseItem = new ItemStack(ItemList.curse);
	
	public RenderProjectileCurse(RenderManager renderManager, RenderItem renderItem){
		super(renderManager,renderItem);
	}

	@Override
	protected void render(Entity entity){
		EntityProjectileCurse curse = (EntityProjectileCurse)entity;
		CurseType type = curse.getType();
		if (type == null)return;
		
		int col = type.getColor(0);
		GL11.glColor3f(((col>>16)&255)/255F,((col>>8)&255)/255F,(col&255)/255F);
		
		curseItem.setItemDamage(type.damage);
		renderItem.renderItemModel(curseItem);
		
		col = type.getColor(1);
		GL11.glColor3f(((col>>16)&255)/255F,((col>>8)&255)/255F,(col&255)/255F);
		renderIcon(ItemList.curse.getIconFromDamageForRenderPass(type.damage,1));
	}
}