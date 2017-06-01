package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 17:46
 */
public class ArmColumn {
	private String title;
	private String field;
	private String formatString;
	private boolean sortable;
	private boolean byDefault;
	private boolean isMarker;
	private String markerIcon;
	private String markerHTML;
	private boolean counter;
    private NodeRef id;

	public ArmColumn(NodeRef nodeRef) {
        this.id = nodeRef;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isCounter() {
		return counter;
	}

	public void setCounter(boolean counter) {
		this.counter = counter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArmColumn armColumn = (ArmColumn) o;
        if (id != null ? !id.equals(armColumn.id) : armColumn.id != null) return false;
		if (sortable != armColumn.sortable) return false;
		if (counter != armColumn.counter) return false;
		if (field != null ? !field.equals(armColumn.field) : armColumn.field != null) return false;
		if (formatString != null ? !formatString.equals(armColumn.formatString) : armColumn.formatString != null) return false;
		if (title != null ? !title.equals(armColumn.title) : armColumn.title != null) return false;
		if (isMarker != armColumn.isMarker) return false;
		if (markerIcon != null ? !markerIcon.equals(armColumn.markerIcon) : armColumn.markerIcon != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (field != null ? field.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (formatString != null ? formatString.hashCode() : 0);
		result = 31 * result + (sortable ? 1 : 0);
		result = 31 * result + (isMarker ? 1 : 0);
		result = 31 * result + (markerIcon != null ? markerIcon.hashCode() : 0);
		result = 31 * result + (counter ? 1 : 0);
		return result;
	}

    public NodeRef getId() {
        return id;
    }

    public boolean isByDefault() {
        return byDefault;
    }

    public void setByDefault(boolean byDefault) {
        this.byDefault = byDefault;
    }

	public boolean isMarker() {
		return isMarker;
	}

	public void setMarker(boolean marker) {
		isMarker = marker;
	}

	public String getMarkerIcon() {
		return markerIcon;
	}

	public void setMarkerIcon(String markerIcon) {
		this.markerIcon = markerIcon;
	}

	public String getMarkerHTML() {
		return markerHTML;
	}

	public void setMarkerHTML(String markerHTML) {
		this.markerHTML = markerHTML;
	}
}
