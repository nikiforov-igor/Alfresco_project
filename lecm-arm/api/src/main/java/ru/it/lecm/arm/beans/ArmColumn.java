package ru.it.lecm.arm.beans;

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

	public ArmColumn() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArmColumn armColumn = (ArmColumn) o;

		if (sortable != armColumn.sortable) return false;
		if (field != null ? !field.equals(armColumn.field) : armColumn.field != null) return false;
		if (formatString != null ? !formatString.equals(armColumn.formatString) : armColumn.formatString != null) return false;
		if (title != null ? !title.equals(armColumn.title) : armColumn.title != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (field != null ? field.hashCode() : 0);
		result = 31 * result + (formatString != null ? formatString.hashCode() : 0);
		result = 31 * result + (sortable ? 1 : 0);
		return result;
	}
}
