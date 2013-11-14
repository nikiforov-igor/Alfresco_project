package ru.it.lecm.base.config;

import org.springframework.extensions.config.source.BaseConfigSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

public class StringConfigSource extends BaseConfigSource
{
	public StringConfigSource(String config)
	{
		super(Collections.singletonList(config));
	}
	
	@Override
	protected InputStream getInputStream(String sourceString)
	{
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(sourceString.getBytes("UTF-8"));
		} catch (Exception e) {
			
		}
		return in;
   }
}