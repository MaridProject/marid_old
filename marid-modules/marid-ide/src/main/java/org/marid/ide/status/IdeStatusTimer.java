/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.ide.status;

import javafx.scene.control.Label;
import org.marid.ee.IdeSingleton;
import org.marid.ide.timers.IdeTimers;
import org.marid.pref.PrefSupport;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.Instant.now;
import static java.time.temporal.ChronoField.*;
import static javafx.application.Platform.runLater;
import static org.marid.util.Utils.ZONE_ID;

/**
 * @author Dmitry Ovchinnikov
 */
@IdeSingleton
public class IdeStatusTimer extends Label implements PrefSupport {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public IdeStatusTimer() {
        setMaxHeight(Double.MAX_VALUE);
        setMinHeight(0.0);
    }

    @Inject
    public void init(IdeTimers ideTimers) {
        ideTimers.schedule(1000L, t -> runLater(() -> setText(FORMATTER.format(now().atZone(ZONE_ID)))));
    }
}