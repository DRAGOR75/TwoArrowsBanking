package dragor.com.webapp.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import dragor.com.webapp.entity.Status;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Status status) {
        if (status == null) return null;
        return switch (status) {
            case PENDING -> 1;
            case COMPLETED -> 2;
            case FAILED -> 3;
        };
    }

    @Override
    public Status convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case 1 -> Status.PENDING;
            case 2 -> Status.COMPLETED;
            case 3 -> Status.FAILED;
            default -> throw new IllegalArgumentException("Unexpected status value: " + dbData);
        };
    }
}