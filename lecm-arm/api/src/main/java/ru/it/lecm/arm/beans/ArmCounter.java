package ru.it.lecm.arm.beans;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 16:56
 */
public class ArmCounter {
	private String query;
	public String description;

	public ArmCounter() {
	}

	public ArmCounter(String query, String description) {
		this.query = query;
		this.description = description;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArmCounter that = (ArmCounter) o;

		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (query != null ? !query.equals(that.query) : that.query != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = query != null ? query.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}
