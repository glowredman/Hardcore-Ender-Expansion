package chylex.hee.system.collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;

public class WeightedList<T extends IWeightProvider> extends ArrayList<T>{
	private static final long serialVersionUID = -382485527777212023L;
	
	protected int totalWeight;
	
	public WeightedList(T...weightedItems){
		for(T item:weightedItems)add(item);
	}

	public WeightedList(WeightedList<T> weightedItemCollection){
		for(T item:weightedItemCollection)add(item);
	}

	@Override
	public boolean add(T obj){
		boolean b = super.add(obj);
		recalculateWeight();
		return b;
	}
	
	public WeightedList<T> addAll(T[] objArray){
		for(T obj:objArray)super.add(obj);
		recalculateWeight();
		return this;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> collection){
		boolean b = super.addAll(collection);
		recalculateWeight();
		return b;
	}
	
	@Override
	public T remove(int index){
		T is = super.remove(index);
		recalculateWeight();
		return is;
	}
	
	@Override
	public boolean remove(Object o){
		boolean b = super.remove(o);
		recalculateWeight();
		return b;
	}
	
	public void recalculateWeight(){
		totalWeight = 0;
		for(T obj:this)totalWeight += obj.getWeight();
	}
	
	public int getTotalWeight(){
		return totalWeight;
	}
	
	public T getRandomItem(Random rand){
		if (totalWeight == 0)return null;
		int i = rand.nextInt(totalWeight);
		
		for(T obj:this){
			i -= obj.getWeight();
			if (i < 0)return obj;
		}
		
		return null;
	}
	
	public Optional<T> tryGetRandomItem(Random rand){
		return Optional.ofNullable(getRandomItem(rand));
	}
	
	public T removeRandomItem(Random rand){
		T item = getRandomItem(rand);
		this.remove(item);
		return item;
	}
}