package com.aplana.scanner.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aplana.scanner.LocalizedException;
import com.aplana.scanner.ScannerController;
import com.lowagie.text.DocumentException;

/**
 * Task to save pages as local files.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public class SaveTask extends ImageTask {
	private static final Log logger = LogFactory.getLog(SaveTask.class);
	
	private File file;
	
	/**
	 * Constructs a task instance.
	 *
	 * @param controller  the {@link ScannerController}
	 * @param file        the <code>File</code> to save pages to
	 */
	public SaveTask(ScannerController controller, File file) {
		super(controller);
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * @see org.jdesktop.swingworker.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		try {
			writeFile(file);
			getController().newDocument();
		} catch (Throwable t) {
			logger.error("Failed to save images", t);
			setThrowable(new LocalizedException("warning.save.message",
							"Failed to save the scanned pages.", t));
		}
		return null;
	}
	
	private void writeFile(File file) throws DocumentException, IOException {
		String extension = FilenameUtils.getExtension(file.getName());
		OutputStream out = new FileOutputStream(file);
		try {
			if (ScannerController.PDF.equalsIgnoreCase(extension))
				writePdf(out);
			else {
				for (String tiffExt : ScannerController.TIFF_EXTENSIONS) {
					if (tiffExt.equalsIgnoreCase(extension)) {
						writeTiff(out);
						return;
					}
				}
				throw new IllegalArgumentException("Invalid file extensions: " + extension);
			}
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
