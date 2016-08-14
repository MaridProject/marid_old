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

package org.marid.proto.health;

/**
 * @author Dmitry Ovchinnikov
 */
public class StdProtoBusHealthMonitorProps {

    private long delaySeconds = 60L;
    private long periodSeconds = 60L;
    private long maxRecencySeconds = 180L;

    public long getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(long delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public void setPeriodSeconds(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    public long getMaxRecencySeconds() {
        return maxRecencySeconds;
    }

    public void setMaxRecencySeconds(long maxRecencySeconds) {
        this.maxRecencySeconds = maxRecencySeconds;
    }
}
