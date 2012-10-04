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
	private String modelType;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
