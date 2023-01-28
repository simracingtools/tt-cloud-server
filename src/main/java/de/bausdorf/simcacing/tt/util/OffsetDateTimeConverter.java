package de.bausdorf.simcacing.tt.util;

/*-
 * #%L
 * racecontrol-server
 * %%
 * Copyright (C) 2020 - 2022 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Converter
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, String> {
    @Override
    public String convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
        if(offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toString();
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String s) {
        if(s == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(s);
        } catch (DateTimeParseException e) {
            int dotPos = s.indexOf('.');
            if (dotPos >= 0) {
                s = s.substring(0, dotPos);
            }
            return OffsetDateTime.of(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneOffset.UTC);
        }
    }
}
