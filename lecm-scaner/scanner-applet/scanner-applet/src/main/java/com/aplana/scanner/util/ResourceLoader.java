package com.aplana.scanner.util;

import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * Classpath resource loader.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public final class ResourceLoader {
	/**
	 * Returns the default <code>ClassLoader</code> to use: typically the thread context
	 * <code>ClassLoader</code>, if available; the <code>ClassLoader</code> that loaded the
	 * <code>ResourceLoader</code> class will be used as fallback.
	 * 
	 * @return the default <code>ClassLoader</code> (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ResourceLoader.class.getClassLoader();
		}
		return cl;
	}
	
	/**
	 * Returns an URL for reading the specified resource from the classpath.
	 *
	 * @param resource  the resource name
	 * @return an URL for reading the resource or <code>null</code> if the resource could not be found
	 */
	public static URL getResource(String resource) {
		return getDefaultClassLoader().getResource(resource);
	}
	
	/**
	 * Returns an input stream for reading the specified resource from the classpath.
	 *
	 * @param resource  the resource name
	 * @return an input stream for reading the resource or <code>null</code> if the resource could not
	 *         be found
	 */
	public static InputStream getResourceAsStream(String resource) {
		return getDefaultClassLoader().getResourceAsStream(resource);
	}
	
	/**
	 * Returns a <code>Source</code> for reading the specified resource from the classpath.
	 *
	 * @param resource  the resource name
	 * @return a source for reading the resource or <code>null</code> if the resource could not
	 *         be found
	 */
	public static Source getResourceAsSource(String resource) {
		URL url = getDefaultClassLoader().getResource(resource);
		return url == null ? null : new StreamSource(url.toExternalForm());
	}
}
