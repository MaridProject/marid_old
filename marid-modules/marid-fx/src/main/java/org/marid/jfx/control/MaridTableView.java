/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.jfx.control;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import org.marid.jfx.action.FxAction;
import org.marid.jfx.action.SpecialActions;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmitry Ovchinnikov
 */
public class MaridTableView<T> extends TableView<T> implements MaridActionsListControl<T>, AutoCloseable {

    private final ObservableList<Function<T, FxAction>> actions = FXCollections.observableArrayList();
    private final List<Observable> observables = new ArrayList<>();
    private final List<Runnable> onDestroy = new ArrayList<>();
    private final List<Runnable> onConstruct = new ArrayList<>();

    protected Supplier<TableRow<T>> rowSupplier = TableRow::new;

    public MaridTableView(ObservableList<T> list) {
        super(list);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    }

    public MaridTableView() {
        this(FXCollections.observableArrayList());
    }

    @Resource
    public void setSpecialActions(SpecialActions specialActions) {
        final InvalidationListener invalidationListener = o -> {
            if (isFocused()) {
                specialActions.assign(actions(getSelectionModel().getSelectedItem()));
            }
        };
        onConstruct.add(() -> {
            observables.forEach(o -> o.addListener(invalidationListener));
            setRowFactory(param -> {
                final TableRow<T> row = rowSupplier.get();
                row.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                    final Collection<FxAction> fxActions = actions(row.getItem());
                    row.setContextMenu(fxActions.isEmpty() ? null : FxAction.grouped(fxActions));
                });
                return row;
            });
            addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                final Collection<FxAction> fxActions = actions(null);
                setContextMenu(fxActions.isEmpty() ? null : FxAction.grouped(fxActions));
            });
            getSelectionModel().selectedItemProperty().addListener(invalidationListener);
            focusedProperty().addListener((o, oV, nV) -> {
                if (nV) {
                    invalidationListener.invalidated(o);
                } else {
                    specialActions.reset();
                }
            });
        });
        onDestroy.add(() -> observables.forEach(o -> o.removeListener(invalidationListener)));
    }

    private Collection<FxAction> actions(T element) {
        return actions.stream()
                .map(a -> a.apply(element))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void onConstruct() {
        onConstruct.forEach(Runnable::run);
    }

    @Override
    public void close() {
        onDestroy.forEach(Runnable::run);
    }

    @Override
    public ObservableList<Function<T, FxAction>> actions() {
        return actions;
    }

    @Override
    public List<Observable> observables() {
        return observables;
    }

    @Override
    public List<Runnable> onConstructListeners() {
        return onConstruct;
    }

    @Override
    public List<Runnable> onDestroyListeners() {
        return onDestroy;
    }
}