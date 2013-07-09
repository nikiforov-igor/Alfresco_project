package ru.it.lecm.reports.api.model;

public interface QueryDescriptor extends Mnemonicable {

	String getText();
	void setText(String text);

	int getOffset();
	void setOffset(int value);

	int getLimit();
	void setLimit(int value);

	int getPgSize();
	void setPgSize(int value);

	boolean isAllVersions();
	void setAllVersions(boolean value);

	/**
	 * (необ) Тип документов в выборке
	 * Имеет вид сслыки на тип, например: "lecm-contract:document"
	 * @return
	 */
	String getPreferedNodeType();
	void setPreferedNodeType(String value);
}
