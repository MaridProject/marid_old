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

package org.marid.proto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Ovchinnikov
 */
public class StdProtoHealth implements ProtoHealth {

    final AtomicLong successfulTransactionCount = new AtomicLong();
    final AtomicLong failedTransactionCount = new AtomicLong();
    final AtomicLong lastSuccessfulTransactionTimestamp = new AtomicLong();
    final AtomicLong lastFailedTransactionTimestamp = new AtomicLong();

    @Override
    public long getSuccessfulTransactionCount() {
        return successfulTransactionCount.get();
    }

    @Override
    public long getFailedTransactionCount() {
        return failedTransactionCount.get();
    }

    @Override
    public Date getLastSuccessfulTransactionTimestamp() {
        return new Timestamp(lastSuccessfulTransactionTimestamp.get());
    }

    @Override
    public Date getLastFailedTransactionTimestamp() {
        return new Timestamp(lastFailedTransactionTimestamp.get());
    }

    @Override
    public void reset() {
        successfulTransactionCount.set(0L);
        failedTransactionCount.set(0L);
        lastSuccessfulTransactionTimestamp.set(System.currentTimeMillis());
        lastFailedTransactionTimestamp.set(0L);
    }
}