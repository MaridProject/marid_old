/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.applib.components;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.marid.applib.utils.ToolbarSupport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static com.vaadin.icons.VaadinIcons.ADD_DOCK;
import static org.marid.applib.utils.Locales.s;

public class PropertyEditor extends VerticalLayout implements HasValue<Properties> {

  private final ArrayList<Entry<String, String>> data = new ArrayList<>();
  private final ListDataProvider<Entry<String, String>> dataProvider = new ListDataProvider<>(data);
  private final Grid<Entry<String, String>> grid = new Grid<>(dataProvider);
  private final LinkedList<ValueChangeListener<Properties>> listeners = new LinkedList<>();

  public PropertyEditor() {
    addComponent(toolbar());
    addComponentsAndExpand(grid);
  }

  @Override
  public void setValue(Properties value) {
    final var old = getValue();
    data.clear();
    value.stringPropertyNames().forEach(k -> data.add(Map.entry(k, value.getProperty(k))));
    fireValueChangeListeners(old, false);
  }

  @Override
  public Properties getValue() {
    final var result = new Properties(data.size());
    data.forEach(e -> result.setProperty(e.getKey(), e.getValue()));
    return result;
  }

  @Override
  public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
  }

  @Override
  public boolean isRequiredIndicatorVisible() {
    return false;
  }

  @Override
  public void setReadOnly(boolean readOnly) {
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public Registration addValueChangeListener(ValueChangeListener<Properties> listener) {
    listeners.add(listener);
    return () -> listeners.remove(listener);
  }

  protected void fireValueChangeListeners(Properties old, boolean userOriginated) {
    listeners.forEach(l -> l.valueChange(new ValueChangeEvent<Properties>(this, old, userOriginated)));
  }

  private HorizontalLayout toolbar() {
    final var layout = new HorizontalLayout();
    layout.addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);

    {
      final var addButton = ToolbarSupport.button(ADD_DOCK, e -> {
        final var old = getValue();
        data.add(Map.entry(s("newKey"), s("newValue")));
        dataProvider.refreshAll();
        fireValueChangeListeners(old, true);
      }, "addProperty");
      layout.addComponent(addButton);
    }

    return layout;
  }
}
