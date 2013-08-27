package ru.it.lecm.signed.docflow;

import java.io.File;

/**
 *
 * @author vlevin
 */
public class Utils {
	public static File createTmpDir() {
		int x = (int) (Math.random() * 1000000);
		String s = System.getProperty("java.io.tmpdir");
		File checkExists = new File(s);
		if (!checkExists.exists() || !checkExists.isDirectory()) {
			throw new RuntimeException("The directory "
					+ checkExists.getAbsolutePath()
					+ " does not exist, please set java.io.tempdir"
					+ " to an existing directory");
		}
		if (!checkExists.canWrite()) {
			throw new RuntimeException("The directory "
					+ checkExists.getAbsolutePath()
					+ " is now writable, please set java.io.tempdir"
					+ " to an writable directory");
		}
		File newTmpDir = new File(s, "alfresco-signed-docflow-tmp-" + x);
		while (!newTmpDir.mkdir()) {
			x = (int) (Math.random() * 1000000);
			newTmpDir = new File(s, "alfresco-signed-docflow-tmp-" + x);
		}

		return newTmpDir;
	}
}
