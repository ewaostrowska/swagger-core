package io.swagger.v3.core.resolving.resources;

public class DateAttributeTypeImpl implements AttributeType {
    private String name;
    private String format;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
