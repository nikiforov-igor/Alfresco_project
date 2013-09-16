package ru.it.lecm.form;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Collections;

import org.springframework.extensions.config.source.BaseConfigSource;

public class StringConfigSource  extends BaseConfigSource
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