package ru.it.lecm.utils.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache<TItem extends ICacheable>
{
	final static int DEFAULT_CAPACITY = 24;

	private int capacity = DEFAULT_CAPACITY;

	// (?) (11/07/08, RuSA) make it synchronizedMap/synchronizedList.
	protected final Map<TItem, Object> entries;
	protected final Map<ICacheable, TItem> keyIndex;

	// TODO: исопльзование линейного массива в данном случае плохо - надо список с быстрым удалением начала и быстрым поиском
	// protected final List<TItem> history = new ArrayList<TItem>();

	private int hitCount = 0;
	private int missCount = 0;
	private int pushCount = 0;

	private static long gCACHE_ID = 0; // сквозной счётчик для автоматической идентификации кешей. 
	private final long internalId; // 1,2...

	public Cache()
	{
		this(DEFAULT_CAPACITY);
	}

	public Cache (int acapacity)
	{
		this.internalId = ++gCACHE_ID;
		if (acapacity < DEFAULT_CAPACITY) acapacity = DEFAULT_CAPACITY;
		this.capacity = acapacity;
		final int mapSize = (capacity * 4 + 3) / 3;		// assuming load factor 0.75
		entries = new HashMap<TItem, Object>(mapSize);
		keyIndex = new HashMap<ICacheable, TItem>(mapSize);
	}

	synchronized public int getCapacity ()
	{
		return capacity;
	}

	synchronized public int getSize ()
	{
		return entries.size();
	}

	synchronized public void setCapacity (int capacity)
	{
		this.capacity = capacity;
		// while (history.size() > capacity) remove ( history.get(0));
		while (entries.size() > capacity) remove ( entries.keySet().iterator().next());
	}

	synchronized public boolean contains(TItem key) 
	{
		return entries.containsKey (key);
	}

	synchronized public Object get(TItem key) throws Exception
	{
		if (entries.containsKey (key))
		{
			hitCount++;
			// history.remove(key);
			// history.add(key);
			return entries.get(key);
		}
		missCount++;
		return put(key);
	}

	/**
	 * Поместить элемент в кеш
	 * @param key
	 * @return key.value()
	 * @throws Exception
	 */
	synchronized public Object put(TItem key) throws Exception
	{
		// if (entries.containsKey (key)) history.remove(key);
		final Object value = key.getValue();
		// history.add(key);
		entries.put(key, value);
		keyIndex.put( key, key );
		push();
		return value;
	}

	synchronized public void remove (TItem key)
	{
		if (!entries.containsKey (key))
			return;
		// history.remove(key);
		entries.remove(key);
		keyIndex.remove( key );
	}

	/**
	 * Выбросить устраевшие элементы, если нет места (ограничивается capacity)
	 */
	synchronized protected void push()
	{
		while (getSize() > capacity) {
			pushCount++;
			// (2011/07/08, skashanski) Заплатка : может быть пустая history и entries
			TItem key = null;

			// if (history.size() > 0) key = history.remove(0);
			if((key == null) && (entries.size() > 0))
				key = entries.keySet().iterator().next();

			if (key == null)
				return;

			entries.remove(key);
			keyIndex.remove( key );
			remove(key);
		}
	}

	public int getHitCount () {
		return hitCount;
	}

	public int getMissCount() {
		return missCount;
	}

	public int getPushCount()
	{
		return pushCount;
	}

	/**
	 * @return unique in the applicaton Id, sequentially/automatically assigned for the instance.
	 */
	public long getInternalId() {
		return this.internalId;
	}
}