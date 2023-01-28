package de.bausdorf.simcacing.tt.web.model;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 - 2023 bausdorf engineering
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

import org.springframework.format.Formatter;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.Locale;

public class OffsetDateTimeFormatter implements Formatter<OffsetDateTime> {
    @Override
    @Nonnull public OffsetDateTime parse(@Nonnull String s, @Nonnull Locale locale) throws ParseException {
        return OffsetDateTime.parse(s);
    }

    @Override
    @Nonnull public String print(@Nonnull OffsetDateTime offsetDateTime, @Nonnull Locale locale) {
        return offsetDateTime.toString();
    }
}
