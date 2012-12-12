package ru.it.lecm.utils.cache;

import java.util.Date;

public interface ICacheableEx extends ICacheable
{
	public Date getExpirationTime();
	public int getSize();
}
