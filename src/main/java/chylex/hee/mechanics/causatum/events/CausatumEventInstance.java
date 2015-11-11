package chylex.hee.mechanics.causatum.events;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.game.save.types.player.CausatumFile;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.world.util.Range;

public abstract class CausatumEventInstance{
	public enum EventTypes{
		STAGE_ADVANCE_TO_ENDERMAN_KILLED(null); // TODO
		
		public final short requiredLevel;
		private final Progress minProgress, maxProgress;
		private final BiFunction<EventTypes,EntityPlayer,CausatumEventInstance> eventConstructor;
		
		private EventTypes(BiFunction<EventTypes,EntityPlayer,CausatumEventInstance> eventConstructor){
			this.requiredLevel = -1;
			this.minProgress = this.maxProgress = null;
			this.eventConstructor = eventConstructor;
		}
		
		private EventTypes(int requiredLevel, Progress minProgress, Progress maxProgress, BiFunction<EventTypes,EntityPlayer,CausatumEventInstance> eventConstructor){
			this.requiredLevel = (short)requiredLevel;
			this.minProgress = minProgress;
			this.maxProgress = maxProgress;
			this.eventConstructor = eventConstructor;
		}
		
		public boolean isRandomEvent(){
			return requiredLevel >= 0;
		}
		
		public boolean canTrigger(Progress currentProgress, int level){
			return (minProgress == null || maxProgress == null || new Range(minProgress.ordinal(),maxProgress.ordinal()).in(currentProgress.ordinal())) && (requiredLevel == -1 || level >= requiredLevel);
		}
		
		public CausatumEventInstance createEvent(EntityPlayer player){
			return eventConstructor.apply(this,player);
		}
	}
	
	public enum EventState{
		WAITING, STARTED, SATISFIED, FINISHED
	}
	
	private final EventTypes eventType;
	private final String playerID;
	private @Nullable EntityPlayer player;
	private @Nonnull EventState state = EventState.WAITING, prevState = state;
	
	CausatumEventInstance(EventTypes eventType, EntityPlayer player){
		this.eventType = eventType;
		this.playerID = PlayerDataHandler.getID(player);
		this.player = player;
	}
	
	protected final @Nullable EntityPlayer getPlayer(){
		return player;
	}
	
	protected final void updateState(@Nonnull EventState state){
		if (state == prevState)return;
		
		this.state = state;
		if (state == EventState.SATISFIED)SaveData.player(playerID,CausatumFile.class).finishEvent(eventType);
	}
	
	public EventState updateEvent(){
		if (player == null){
			ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
			if (manager != null)player = ((List<EntityPlayer>)manager.playerEntityList).stream().filter(entity -> playerID.equals(PlayerDataHandler.getID(entity))).findFirst().orElse(null);
		}
		
		// TODO
		
		return state;
	}
	
	protected abstract void onUpdate();
	protected abstract void onPlayerDisconnected();
	protected abstract void onPlayerChangedDimension();
}