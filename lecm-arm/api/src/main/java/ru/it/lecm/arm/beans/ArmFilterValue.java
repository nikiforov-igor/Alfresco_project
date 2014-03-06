package ru.it.lecm.arm.beans;

/**
 * User: DBashmakov
 * Date: 17.02.14
 * Time: 15:02
 */
public class ArmFilterValue {
    private String title;
    private String code;

    public ArmFilterValue (String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArmFilterValue armFilter = (ArmFilterValue) o;

        if (code != null ? !code.equals(armFilter.code) : armFilter.code != null) return false;
        if (title != null ? !title.equals(armFilter.title) : armFilter.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
