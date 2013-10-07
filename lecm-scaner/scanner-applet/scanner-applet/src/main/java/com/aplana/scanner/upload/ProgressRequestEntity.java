package com.aplana.scanner.upload;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * <code>RequestEntity</code> decorator that reports progress of a lengthy task.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public class ProgressRequestEntity implements RequestEntity {
	private final RequestEntity entity;
	private final ProgressListener listener;
	
	private class CountingOutputStream extends FilterOutputStream {
		private long transferred;
		
		public CountingOutputStream(OutputStream out) {
			super(out);
			this.transferred = 0;
		}

		/* (non-Javadoc)
		 * @see java.io.FilterOutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
			transferred += len;
			listener.progressUpdate(transferred);
		}

		/* (non-Javadoc)
		 * @see java.io.FilterOutputStream#write(int)
		 */
		@Override
		public void write(int b) throws IOException {
			super.write(b);
			listener.progressUpdate(++transferred);
		}
	}

	/**
	 * Constructs a decorator instance.
	 *
	 * @param entity    the <code>RequestEntity</code> to be decorated
	 * @param listener  the {@link ProgressListener} to be informed of progress
	 */
	public ProgressRequestEntity(RequestEntity entity, ProgressListener listener) {
		this.entity = entity;
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentLength()
	 */
	public long getContentLength() {
		return entity.getContentLength();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
	 */
	public String getContentType() {
		return entity.getContentType();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#isRepeatable()
	 */
	public boolean isRepeatable() {
		return entity.isRepeatable();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.methods.RequestEntity#writeRequest(java.io.OutputStream)
	 */
	public void writeRequest(OutputStream out) throws IOException {
		entity.writeRequest(new CountingOutputStream(out));
	}
}
