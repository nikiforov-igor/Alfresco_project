package ru.it.lecm.dictionary.bootstrap;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:26
 */
public class DictionaryDescriptor {
	private String name;
	private String description;
	private String type;
	private boolean showControlInSeparateWindow = true;

	public boolean isShowControlInSeparateWindow() {
		return showControlInSeparateWindow;
	}

	public void setShowControlInSeparateWindow(boolean showControlInSeparateWindow) {
		this.showControlInSeparateWindow = showControlInSeparateWindow;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
