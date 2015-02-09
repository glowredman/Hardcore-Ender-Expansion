package chylex.hee.render;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockList.BlockData;
import chylex.hee.block.state.IAbstractState;
import chylex.hee.item.IMultiModel;
import chylex.hee.item.ItemList;
import chylex.hee.system.logging.Log;

@SideOnly(Side.CLIENT)
public class ItemRenderRegistry{
	public static void registerAll(RenderItem renderItem){
		ItemModelMesher mesher = renderItem.getItemModelMesher();
		
		for(Entry<String,BlockData> data:BlockList.getBlockEntries())registerBlock(mesher,data);
		for(Entry<String,Item> data:ItemList.getItemEntries())registerItem(mesher,data);
		
		loadBlockModels();
	}
	
	private static void registerBlock(ItemModelMesher mesher, Entry<String,BlockData> blockEntry){
		Block block = blockEntry.getValue().block;
		
		if (block instanceof IAbstractState){
			IAbstractState state = (IAbstractState)block;
			
			IProperty prop = ((IAbstractState)block).getPropertyArray()[0];
			
			for(Object value:prop.getAllowedValues()){
				int intval = value instanceof Integer ? ((Integer)value).intValue() : value instanceof Enum ? ((Enum)value).ordinal() : -1;
				
				if (intval == -1)Log.debug("Registering complex block location failed: $0/$1, $2=$3",blockEntry.getKey(),block,prop.getName(),value);
				else{
					Log.debug("Registering complex block location: $0/$1, $2=$3",blockEntry.getKey(),block,prop.getName(),intval);
					mesher.register(Item.getItemFromBlock(block),intval,new ModelResourceLocation(blockEntry.getKey(),"inventory"));
				}
			}
		}
		else{
			Log.debug("Registering simple block location: $0/$1",blockEntry.getKey(),block);
			mesher.register(Item.getItemFromBlock(block),0,new ModelResourceLocation(blockEntry.getKey(),"inventory"));
		}
	}
	
	private static void registerItem(ItemModelMesher mesher, Entry<String,Item> itemEntry){
		Item item = itemEntry.getValue();
		
		if (item instanceof IMultiModel){
			IMultiModel multi = (IMultiModel)item;
			int a = -1;
			
			for(String model:multi.getModels()){
				if (model.startsWith("^"))model = "hardcoreenderexpansion:"+model.substring(1);
				
				Log.debug("Registering complex item location: $0/$1, $2",itemEntry.getKey(),item,model);
				mesher.register(item,++a,new ModelResourceLocation(model,"inventory"));
			}
		}
		else{
			Log.debug("Registering simple item location: $0/$1",itemEntry.getKey(),item);
			mesher.register(item,0,new ModelResourceLocation(itemEntry.getKey(),"inventory"));
		}
	}
	
	private static void loadBlockModels(){
		Set<String> models = new HashSet<>();
		
		for(Entry<String,BlockData> data:BlockList.getBlockEntries()){
			Block block = data.getValue().block;
			
			if (block instanceof IAbstractState){
				IAbstractState state = (IAbstractState)block;
				
				models.clear();
				Collection<?> values = state.getPropertyArray()[0].getAllowedValues();
				String propName = state.getPropertyArray()[0].getName();
				
				for(Object value:values)models.add(new StringBuilder().append(data.getKey()).append('#').append(propName).append('=').append(value).toString());
				Log.debug("Registering $0 models: $1",data.getKey(),models);
				
				// TODO ModelBakery.addVariantName(Item.getItemFromBlock(block),models.toArray(new String[models.size()]));
			}
		}
	}
}
