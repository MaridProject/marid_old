/*
 *
 */

package org.marid.ide.status;

/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableSet;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.marid.ide.panes.main.IdeStatusBar;
import org.marid.jfx.icons.FontIcons;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static org.marid.ide.IdeNotifications.n;
import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.logging.Log.log;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class IdeService<V extends Node> extends Service<Duration> {

    protected static final Set<EventType<?>> DONE_EVENT_TYPES = ImmutableSet.of(
            WorkerStateEvent.WORKER_STATE_SUCCEEDED,
            WorkerStateEvent.WORKER_STATE_FAILED,
            WorkerStateEvent.WORKER_STATE_CANCELLED
    );

    private final SimpleObjectProperty<V> graphic = new SimpleObjectProperty<>();
    private final CountDownLatch latch = new CountDownLatch(1);
    protected Button button;

    @Autowired
    private void init(IdeStatusBar statusBar) {
        addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, event -> {
            button = new Button();
            button.setOnMousePressed(e -> button.getProperties().put("_lastEvent", e));
            button.textProperty().bind(titleProperty());
            button.graphicProperty().bind(graphic);
            button.tooltipProperty().bind(createObjectBinding(
                    () -> StringUtils.isBlank(getMessage()) ? null : new Tooltip(getMessage()), messageProperty()));
            statusBar.add(button);
        });
        addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            statusBar.remove(button);
            button = null;
            final Duration duration = (Duration) event.getSource().getValue();
            final String durationText = DurationFormatUtils.formatDurationHMS(duration.toMillis());
            n(INFO, "{0} succeeded in {1}", event.getSource().getTitle(), durationText);
        });
        addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
            statusBar.remove(button);
            button = null;
            n(WARNING, "{0} failed", event.getSource().getException(), event.getSource().getTitle());
        });
        addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, event -> {
            button.setDisable(true);
            button.setEffect(new MotionBlur(0.1, 0.1));
            final Thread thread = new Thread(null, () -> {
                try {
                    latch.await();
                    LockSupport.parkNanos(1_000_000_000L);
                    Platform.runLater(() -> {
                        statusBar.remove(button);
                        button = null;
                    });
                } catch (InterruptedException x) {
                    log(WARNING, "Interrupted", x);
                }
            }, "Watcher: " + getTitle(), 96L * 1024L);
            thread.setDaemon(true);
            thread.start();
        });
    }

    @Override
    protected abstract IdeTask createTask();

    protected abstract class IdeTask extends Task<Duration> {

        protected abstract void execute() throws Exception;

        @Nonnull
        protected abstract V createGraphic();

        protected abstract ContextMenu contextMenu();

        protected void updateGraphic(Consumer<V> consumer) {
            if (Platform.isFxApplicationThread()) {
                consumer.accept(graphic.get());
            } else {
                Platform.runLater(() -> consumer.accept(graphic.get()));
            }
        }

        @Override
        protected Duration call() throws Exception {
            try {
                final long startTime = System.nanoTime();
                {
                    final V node = createGraphic();
                    final ContextMenu contextMenu = contextMenu();
                    final MenuItem cancelItem = new MenuItem();
                    cancelItem.setGraphic(FontIcons.glyphIcon("D_CLOSE_CIRCLE"));
                    cancelItem.textProperty().bind(ls("Cancel"));
                    cancelItem.setOnAction(event -> cancel());
                    if (!contextMenu.getItems().isEmpty()) {
                        contextMenu.getItems().add(new SeparatorMenuItem());
                    }
                    contextMenu.getItems().add(cancelItem);
                    Platform.runLater(() -> {
                        graphic.set(node);
                        button.setContextMenu(contextMenu);
                        button.setOnAction(event -> {
                            final MouseEvent e = (MouseEvent) button.getProperties().remove("_lastEvent");
                            final double x = e != null ? e.getX() : 0;
                            final double y = e != null ? e.getY() : 0;
                            contextMenu.show(button, Side.TOP, x, y);
                        });
                    });
                }
                try {
                    execute();
                } finally {
                    Platform.runLater(() -> graphic.set(null));
                }
                return Duration.ofNanos(System.nanoTime() - startTime);
            } finally {
                latch.countDown();
            }
        }
    }
}