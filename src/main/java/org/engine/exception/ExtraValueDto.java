package org.engine.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonSerialize(using = ExtraValueSerializer.class)
//@JsonDeserialize(using = ExtraValueDeserializer.class)
public class ExtraValueDto {

    public enum TypeEnum {
        DATE("DATE"),
        TRANSLATE("TRANSLATE"),
        STRING("STRING");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String value) {
            for (TypeEnum b : TypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @JsonProperty("type")
    @NotNull
    private TypeEnum type;

    @JsonProperty("value")
    @NotNull
    private String value;
}
