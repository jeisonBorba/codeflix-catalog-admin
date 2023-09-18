package br.com.codeflix.catalog.admin.infrastructure.video.persistence;

import br.com.codeflix.catalog.admin.domain.video.Rating;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(final Rating attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public Rating convertToEntityAttribute(final String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return Rating.of(dbData).orElse(null);
    }
}
