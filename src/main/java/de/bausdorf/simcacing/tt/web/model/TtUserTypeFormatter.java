package de.bausdorf.simcacing.tt.web.model;

/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
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

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.web.security.TtUserType;

public class TtUserTypeFormatter implements Formatter<TtUserType> {

	@Override
	public TtUserType parse(String s, Locale locale) throws ParseException {
		if (s != null) {
			return TtUserType.valueOf(s);
		}
		return TtUserType.TT_NEW;
	}

	@Override
	public String print(TtUserType ttUserType, Locale locale) {
		if (ttUserType != null) {
			return ttUserType.name();
		}
		return "TT_NEW";
	}
}
