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

package org.marid.ide.timers;

import org.marid.logging.LogSupport;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
@ApplicationScoped
public class IdeTimers implements LogSupport {

    private final Timer timer = new Timer();

    private TimerTask timerTask(Consumer<TimerTask> task) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    task.accept(this);
                } catch (Exception x) {
                    log(WARNING, "Unable to execute {0}", x, task);
                }
            }
        };
    }

    public TimerTask schedule(long delayMillis, long periodMillis, Consumer<TimerTask> task) {
        final TimerTask timerTask = timerTask(task);
        timer.schedule(timerTask, delayMillis, periodMillis);
        return timerTask;
    }

    public TimerTask schedule(long periodMillis, Consumer<TimerTask> task) {
        return schedule(0L, periodMillis, task);
    }

    public TimerTask delayed(long delayMillis, Consumer<TimerTask> task) {
        final TimerTask timerTask = timerTask(task);
        timer.schedule(timerTask, delayMillis);
        return timerTask;
    }

    public TimerTask schedule(Date date, Consumer<TimerTask> task) {
        final TimerTask timerTask = timerTask(task);
        timer.schedule(timerTask, date);
        return timerTask;
    }

    public TimerTask schedule(Date date, long periodMillis, Consumer<TimerTask> task) {
        final TimerTask timerTask = timerTask(task);
        timer.schedule(timerTask, date, periodMillis);
        return timerTask;
    }

    @PreDestroy
    private void destroy() {
        timer.cancel();
    }
}