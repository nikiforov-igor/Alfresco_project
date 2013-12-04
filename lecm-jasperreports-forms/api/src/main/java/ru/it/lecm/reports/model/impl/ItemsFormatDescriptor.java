package ru.it.lecm.reports.model.impl;

import ru.it.lecm.reports.utils.Utils;

import java.io.Serializable;

public class ItemsFormatDescriptor
		implements Serializable
{
	private static final long serialVersionUID = 1L;
    public static final String LIST_MARKER = "*";

    private String formatString, itemsDelimiter = ",", ifEmptyTag = "";

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemsFormatDescriptor [");
		builder.append("ifEmptyTag '").append( Utils.expandToCharPairs(ifEmptyTag)).append("'");
		builder.append(", delimiter '").append( Utils.expandToCharPairs(itemsDelimiter)).append("'");
		builder.append(", format '").append( Utils.expandToCharPairs(formatString)).append("'");
		builder.append("]");
		return builder.toString();
	}

	public String getIfEmptyTag() {
		return ifEmptyTag;
	}

	public void setIfEmptyTag(String ifEmptyTag) {
		this.ifEmptyTag = ifEmptyTag;
	}

	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	public String getItemsDelimiter() {
		return itemsDelimiter;
	}

	public void setItemsDelimiter(String delimiter) {
		this.itemsDelimiter = delimiter;
	}
}
