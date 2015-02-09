package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.proxy.ModCommonProxy;

public class ItemEssence extends Item implements IMultiModel{
	public ItemEssence(){
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(EssenceType essenceType:EssenceType.values()){
			if (essenceType == EssenceType.INVALID)continue;
			list.add(new ItemStack(item,1,essenceType.getItemDamage()));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		EssenceType essenceType = EssenceType.getById(is.getItemDamage()+1);
		if (ModCommonProxy.hardcoreEnderbacon && essenceType == EssenceType.DRAGON)return "item.essence.dragon.bacon";
		return "item.essence."+(essenceType == null ? "invalid" : essenceType.essenceNameLowercase);
	}
	
	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player){
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is){
		return EnumRarity.UNCOMMON;
	}
	
	@Override
	public String[] getModels(){
		return new String[]{
			"^dragon_essence",
			"^fiery_essence",
			"^spectral_essence"
		};
	}
}