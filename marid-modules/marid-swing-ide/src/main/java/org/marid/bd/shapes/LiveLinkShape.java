/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
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

package org.marid.bd.shapes;

import org.marid.bd.BlockComponent;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.concurrent.ThreadLocalRandom;

import static org.marid.bd.shapes.LinkShapeType.LiveLinkConfigurationEditor.mutationProbability;

/**
 * @author Dmitry Ovchinnikov
 */
public class LiveLinkShape extends AbstractLiveLinkShape<LiveLinkShape.LiveLinkShapeData> {

    protected static final int COUNT = 4;

    public LiveLinkShape(BlockComponent.Output output, BlockComponent.Input input) {
        super(output, input);
    }

    @Override
    protected LiveLinkShapeData defaultSpecie() {
        return new LiveLinkShapeData(output.getConnectionPoint(), input.getConnectionPoint());
    }

    @Override
    protected LiveLinkShapeData crossoverAndMutate(LiveLinkShapeData male, LiveLinkShapeData female, ThreadLocalRandom random) {
        final int[] xs = new int[COUNT], ys = new int[COUNT];
        final int rand = random.nextInt();
        for (int i = 0; i < COUNT; i++) {
            if ((rand & (1 << i)) == 0) {
                xs[i] = male.xs[i];
                ys[i] = male.ys[i];
            } else {
                xs[i] = female.xs[i];
                ys[i] = female.ys[i];
            }
        }
        if (random.nextInt(100) < mutationProbability) {
            final int r = rand & 0x1FFF;
            for (int i = 0; i < COUNT; i++) {
                xs[i] += random.nextInt(-r, r + 1);
                ys[i] += random.nextInt(-r, r + 1);
            }
        }
        return new LiveLinkShapeData(xs, ys);
    }

    private double length(LiveLinkShapeData data) {
        final LiveData ld = liveData;
        double length = Point.distance(ld.out.x, ld.out.y, data.xs[0], data.ys[0]);
        for (int i = 0; i < COUNT - 1; i++) {
            length += Point.distance(data.xs[i], data.ys[i], data.xs[i + 1], data.ys[i + 1]);
        }
        length += Point.distance(data.xs[COUNT - 1], data.ys[COUNT - 1], ld.in.x, ld.in.y);
        return length;
    }

    static int isect(Rectangle r, int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            if (y1 == y2) {
                return r.contains(x1, y1) ? 100 : 0;
            } else {
                if (y1 > y2) {
                    final int yt = y2;
                    y2 = y1;
                    y1 = yt;
                }
                y1 = Math.max(y1, r.y);
                y2 = Math.min(y2, r.y + r.height);
                return y2 > y1 ? (y2 - y1) * 100 : 0;
            }
        } else if (y1 == y2) {
            if (x1 > x2) {
                final int xt = x2;
                x2 = x1;
                x1 = xt;
            }
            x1 = Math.max(x1, r.x);
            x2 = Math.min(x2, r.x + r.width);
            return x2 > x1 ? (x2 - x1) * 100 : 0;
        } else {
            if (x1 > x2) {
                final int xt = x2;
                x2 = x1;
                x1 = xt;
            }
            if (y1 > y2) {
                final int yt = y2;
                y2 = y1;
                y1 = yt;
            }
            x1 = Math.max(r.x, x1);
            x2 = Math.min(r.x + r.width, x2);
            y1 = Math.max(r.y, y1);
            y2 = Math.min(r.y + r.height, y2);
            return y2 > y1 && x2 > x1 ? (x2 - x1) * (y2 - y1) * 100 : 0;
        }
    }

    @Override
    protected double fitness(LiveLinkShapeData specie) {
        final LiveData ld = liveData;
        try {
            final double distFactor = length(specie);
            int isectFactor = 0;
            for (final Rectangle r : ld.rectangles) {
                isectFactor += isect(r, ld.out.x + 1, ld.out.y, specie.xs[0], specie.ys[0]);
                for (int i = 0; i < COUNT - 1; i++) {
                    isectFactor += isect(r, specie.xs[i], specie.ys[i], specie.xs[i + 1], specie.ys[i + 1]);
                }
                isectFactor += isect(r, specie.xs[COUNT - 1], specie.ys[COUNT - 1], ld.in.x - 1, ld.in.y);
            }
            return distFactor + isectFactor;
        } catch (Exception x) {
            log(WARNING, "GA fitness error on {0}", x, this);
            return 0.0;
        }
    }

    @Override
    public Shape getShape() {
        final Point p1 = output.getConnectionPoint();
        final Point p2 = input.getConnectionPoint();
        final Path2D.Double shape = new Path2D.Double();
        shape.moveTo(p1.x, p1.y);
        final LiveLinkShapeData data = bestSpecie();
        for (int i = 0; i < COUNT; i++) {
            shape.lineTo(data.xs[i], data.ys[i]);
        }
        shape.lineTo(p2.x, p2.y);
        return shape;
    }

    protected static class LiveLinkShapeData {

        protected final int[] xs;
        protected final int[] ys;

        protected LiveLinkShapeData(int[] xs, int[] ys) {
            this.xs = xs;
            this.ys = ys;
        }

        public LiveLinkShapeData(Point p1, Point p2) {
            this(new int[COUNT], new int[COUNT]);
            final int dx = (p2.x - p1.x) / COUNT;
            final int dy = (p2.y - p1.y) / COUNT;
            for (int i = 0; i < COUNT; i++) {
                xs[i] = p1.x + dx * (i + 1);
                ys[i] = p1.y + dy * (i + 1);
            }
        }
    }
}
