package ru.it.lecm.reports.model;

import ru.it.lecm.reports.api.model.QueryDescriptor;

public class QueryDescriptorImpl
		extends MnemonicNamedItem
		implements QueryDescriptor {

	private String text;
	private int offset, limit, pgSize;
	private boolean allVersions = true;

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int getOffset() {
		return this.offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public int getPgSize() {
		return this.pgSize;
	}

	@Override
	public void setPgSize(int pgSize) {
		this.pgSize = pgSize;
	}

	@Override
	public boolean isAllVersions() {
		return this.allVersions;
	}

	@Override
	public void setAllVersions(boolean flag) {
		this.allVersions = flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + limit;
		result = prime * result + offset;
		result = prime * result + pgSize;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QueryDescriptorImpl other = (QueryDescriptorImpl) obj;
		if (limit != other.limit)
			return false;
		if (offset != other.offset)
			return false;
		if (pgSize != other.pgSize)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( String.format( "QueryDescriptorImpl [ mnem '%s'", getMnem()) );
		builder.append(", offset ").append(offset);
		builder.append(", limit ").append(limit);
		builder.append(", pgSize ").append(pgSize);
		builder.append( String.format( "\n\t\t\t<text>\n'%s'\n\t\t\t<text>", text) );
		builder.append("\n\t\t]");
		return builder.toString();
	}

}
