package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.api.model.QueryDescriptor;
import ru.it.lecm.reports.utils.Utils;

public class QueryDescriptorImpl
		extends MnemonicNamedItem
		implements QueryDescriptor 
{
	private static final long serialVersionUID = 1L;

	private String text;
	private int offset, limit, pgSize;
	private boolean allVersions = true;
	private String preferedNodeType;

	private boolean emptyTypeMatchesAny = false;

	/** @return: true, если принимать свой пустой preferedNodeType за подходящий к ЛЮБОМУ внешнему,
	 * false: по-умолчанию считать свой пустой тип не соот-щим НИЧКАКОМУ внешнему, кроме пустого.
	 */
	public boolean isEmptyTypeMatchesAny() {
		return emptyTypeMatchesAny;
	}

	/** true, если принимать пустой внешний тип за подходящий к preferedNodeType */
	public void setEmptyTypeMatchesAny(boolean value) {
		this.emptyTypeMatchesAny = value;
	}

	@Override
	public String getPreferedNodeType() {
		return preferedNodeType;
	}

	@Override
	public void setPreferedNodeType(String value) {
		this.preferedNodeType = value;
	}

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
	public boolean isTypeSupported(String qname) {

		final boolean isOuterEmpty = Utils.isStringEmpty(qname);
		if (isOuterEmpty) // если проверяется пустой внешний тип - считаем что он подходит к любому внутреннему
			return true;

		final boolean isInnerEmpty = Utils.isStringEmpty(getPreferedNodeType());
		if (isInnerEmpty) 
			// если this-тип не заполнен -> вернуть флажок "считать ли свой 
			// пустой тип равным любому внешнему"
			return isEmptyTypeMatchesAny(); 

		// здесь оба не пустые 

		/* сейчас проверяем на простое вхождение, чтобы потом легко было
		 * сделать getPreferedNodeType списком
		 */
		if (getPreferedNodeType().equalsIgnoreCase(qname))
				// совпадение типа (с точностью до регистра)
			return true;

		/* точного соот-вия нет - проверяем вхождение */
		// (NOTE: можно подумать, чтобы иметь набор SET<QName>)
		return getPreferedNodeType().toLowerCase().contains(qname.toLowerCase());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + limit;
		result = prime * result + offset;
		result = prime * result + pgSize;
		result = prime * result + ((preferedNodeType == null) ? 0 : preferedNodeType.hashCode());
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

		if (preferedNodeType == null) {
			if (other.preferedNodeType != null)
				return false;
		} else if (!preferedNodeType.equals(other.preferedNodeType))
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
		builder.append(", nodeType ").append(preferedNodeType);
		builder.append( String.format( "\n\t\t\t<text>\n'%s'\n\t\t\t<text>", text) );
		builder.append("\n\t\t]");
		return builder.toString();
	}

}
