package dragor.com.webapp.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import dragor.com.webapp.entity.Type;

@Converter(autoApply = true)
public class TypeConverter implements AttributeConverter<Type, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Type type) {
        if (type == null) return null;
        return switch (type) {
            case WITHDRAWAL -> 0;
            case DEPOSIT -> 1;
            case DEBIT -> 2;
            case CONVERSION -> 3;
            case CREDIT -> 4;
        };
    }

    @Override
    public Type convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case 0 -> Type.WITHDRAWAL;
            case 1 -> Type.DEPOSIT;
            case 2 -> Type.DEBIT;
            case 3 -> Type.CONVERSION;
            case 4 -> Type.CREDIT;
            default -> throw new IllegalArgumentException("Unexpected type value: " + dbData);
        };
    }
}