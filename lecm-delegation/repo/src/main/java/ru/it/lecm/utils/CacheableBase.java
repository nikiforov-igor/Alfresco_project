package ru.it.lecm.utils;

import java.util.Date;

abstract public class CacheableBase implements ICacheableEx
{
	final protected Object id;
	protected int weight = 1;
	protected Date expiration = null;
	protected int interval_ms;

	protected CacheableBase(Object id) {
		this(id, -1);
	}

	protected CacheableBase(Object id, int interval_ms) {
		if (id == null)
			throw new IllegalArgumentException("Cache item's key can't be null");
		this.id = id;
		setExpiration(interval_ms);
	}

	protected void setExpiration(int interval_ms) {
		this.interval_ms = interval_ms;
		if (interval_ms < 0)
			expiration = null;
		else
			expiration = new Date(System.currentTimeMillis() + interval_ms);
	}

	public Date getExpirationTime() {
		return expiration;
	}

	public int getSize() {
		return weight;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass()))
			return false;
		return id.equals(((CacheableBase) obj).id);// obj.hashCode() == hashCode();
	}
}
