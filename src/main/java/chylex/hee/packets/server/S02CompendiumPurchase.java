package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.AbstractServerPacket;

public class S02CompendiumPurchase extends AbstractServerPacket{
	private boolean isFragment;
	private int id;
	
	public S02CompendiumPurchase(){}
	
	public S02CompendiumPurchase(KnowledgeObject<?> object){
		this.isFragment = false;
		this.id = object.globalID;
	}
	
	public S02CompendiumPurchase(KnowledgeFragment fragment){
		this.isFragment = true;
		this.id = fragment.globalID;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeBoolean(isFragment).writeShort(id);
	}

	@Override
	public void read(ByteBuf buffer){
		isFragment = buffer.readBoolean();
		id = buffer.readShort();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		CompendiumFile file = CompendiumEvents.getPlayerData(player);
		
		if (isFragment){
			KnowledgeFragment fragment = KnowledgeFragment.fromID(id);
			if (fragment != null)file.tryPurchaseFragment(player,fragment);
		}
		else{
			KnowledgeObject<?> object = KnowledgeObject.fromID(id);
			if (object != null)file.tryPurchaseObject(player,object);
		}
		PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.RESTORE_COMPENDIUM_PAUSE));
	}
}
