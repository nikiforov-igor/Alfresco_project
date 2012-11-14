package ru.it.lecm.utils;

import java.util.ArrayList;
import java.util.Date;

public class CacheEx<TItem extends ICacheableEx> extends Cache<TItem>
{
	private ArrayList<TItem> expiring = new ArrayList<TItem>();
	private int size = 0;
	private int expiredCounter = 0;

	public CacheEx() {
		super();
	}

	public CacheEx(int capacity)
	{
		super(capacity);
	}

	private void expireItems()
	{
		final Date now = new Date();
		// remove beginning expired ICacheableEx ... 
		while (expiring.size() > 0)
		{
			final TItem item = expiring.get(0);
			if ( !item.getExpirationTime().before(now) ) {
				break;
			}
			remove(item);
			expiredCounter++;
		}
	}

	/**
	 * Условный размер кеша. Удобен для ограничения кеша из списков элементов,
	 * для контроля суммарного размера всех списков.
	 * @return размер в условных единицах: обычно каждый элемент имеет размер 
	 * 1 единица, но может быть и более, например, когда элементы являются списками. 
	 */
	@Override
	synchronized public int getSize() {
		return size;
	}

	@Override
	synchronized public boolean contains(TItem key) 
	{
		expireItems();
		return super.contains(key);
	}

	@Override
	public synchronized Object get(TItem key) throws Exception
	{
		expireItems();
		return super.get(key);
	}

	@Override
	public synchronized Object put(TItem key) throws Exception
	{
		expireItems();
		//(2011/07/08, skashanski) : меняем размер кэша так как во время get мог попасть неправильный размер 
		//когда делаем get(Key) может попасть размер 1 например но размер в кэше считаеться как ICacheableEx) key).getSize()
		//поэтому перед заменой имеет смыысл уменьшить размер  
		if ( entries.containsKey( key ) ) {
			int asize = 1;
			final ICacheableEx keyWhichIsCurrentlyInCache = (ICacheableEx) keyIndex.get( key );
			if (keyWhichIsCurrentlyInCache != null && keyWhichIsCurrentlyInCache.getSize() > 1)
				asize = keyWhichIsCurrentlyInCache.getSize();
			size -= asize;
		}

		final Object value = super.put(key);
		final int itemSize = key.getSize();
		if (itemSize > 1) {
			size += (itemSize > 1 ? itemSize : 1);
			push();
		} else
			size++;
		final Date expTime = key.getExpirationTime();
		if (expTime != null) {
			int i = expiring.size();
			while (i > 0 && expTime.before(expiring.get(i - 1).getExpirationTime()))
				i--;
			expiring.add(i, key);
		}
		return value;
	}

	@Override
	public synchronized void remove(TItem key)
	{
		expiring.remove(key);
		super.remove(key);
		final int itemSize = key.getSize();
		final int freed = (itemSize > 1) ? itemSize: 1;
		size -= freed;
	}

	public int getExpiredCounter() {
		return expiredCounter;
	}
}
