package ru.it.lecm.reports.model.impl;

import java.io.Serializable;

import ru.it.lecm.reports.api.model.SubReportDescriptor.ItemsFormatDescriptor;
import ru.it.lecm.reports.utils.Utils;

public class ItemsFormatDescriptorImpl
		implements ItemsFormatDescriptor, Serializable
{
	private static final long serialVersionUID = 1L;

	private String formatString, itemsDelimiter = "\n", ifEmptyTag = "";

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemsFormatDescriptorImpl [");
		builder.append("ifEmptyTag '").append( Utils.expandToCharPairs(ifEmptyTag)).append("'");
		builder.append(", delimiter '").append( Utils.expandToCharPairs(itemsDelimiter)).append("'");
		builder.append(", format '").append( Utils.expandToCharPairs(formatString)).append("'");
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String getIfEmptyTag() {
		return ifEmptyTag;
	}

	@Override
	public void setIfEmptyTag(String ifEmptyTag) {
		this.ifEmptyTag = ifEmptyTag;
	}

	@Override
	public String getFormatString() {
		return formatString;
	}

	@Override
	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	@Override
	public String getItemsDelimiter() {
		return itemsDelimiter;
	}

	@Override
	public void setItemsDelimiter(String delimiter) {
		this.itemsDelimiter = delimiter;
	}

}
