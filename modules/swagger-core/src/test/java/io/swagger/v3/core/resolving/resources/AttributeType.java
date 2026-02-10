package io.swagger.v3.core.resolving.resources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DateAttributeTypeImpl.class, name = "date")
})
public interface AttributeType {
    String getName();
}
