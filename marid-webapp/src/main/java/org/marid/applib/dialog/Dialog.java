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
package org.marid.applib.dialog;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Dialog<T> extends Window {

  private final T bean;
  private final Binder<T> binder = new Binder<>();
  private final FormLayout form = new FormLayout();
  private final HorizontalLayout buttons = new HorizontalLayout();

  public Dialog(String caption, T bean, boolean modal, int width, int height) {
    super(caption, new VerticalLayout());
    this.bean = bean;
    setModal(modal);
    setWidth(width, Unit.PIXELS);
    setHeight(height, Unit.PIXELS);

    form.setMargin(true);
    form.setSizeFull();

    buttons.setMargin(true);

    getContent().setMargin(false);
    getContent().addComponent(form);
    getContent().setExpandRatio(form, 1);
    getContent().addComponent(buttons);
    getContent().setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
    getContent().setSizeFull();
  }

  public Dialog(String caption, T bean, int width, int height) {
    this(caption, bean, true, width, height);
  }

  @SafeVarargs
  public final Dialog<T> addTextField(String label,
                                      String value,
                                      BiConsumer<TextField, BindingBuilder<T, String>>... consumers) {
    final var field = new TextField(label, value);
    field.setWidth(100, Unit.PERCENTAGE);
    form.addComponent(field);
    final var builder = binder.forField(field);
    for (final var consumer : consumers) {
      consumer.accept(field, builder);
    }
    return this;
  }

  @SafeVarargs
  public final <V, C extends Component & HasValue<V>> Dialog<T> add(Supplier<C> supplier,
                                                                    BiConsumer<C, BindingBuilder<T, V>>... consumers) {
    final var field = supplier.get();
    field.setWidth(100, Unit.PERCENTAGE);
    form.addComponent(field);
    final var builder = binder.forField(field);
    for (final var consumer : consumers) {
      consumer.accept(field, builder);
    }
    return this;
  }

  @SafeVarargs
  public final Dialog<T> addCancelButton(String text, Consumer<Button>... consumers) {
    final var button = new Button(text, VaadinIcons.CLOSE);
    button.addClickListener(e -> close());
    buttons.addComponent(button);
    for (final var consumer : consumers) {
      consumer.accept(button);
    }
    return this;
  }

  @SafeVarargs
  public final Dialog<T> addSubmitButton(String text, Consumer<T> onSuccess, Consumer<Button>... consumers) {
    final var button = new Button(text, VaadinIcons.ENTER);
    button.addClickListener(event -> {
      if (binder.writeBeanIfValid(bean)) {
        onSuccess.accept(bean);
        close();
      }
    });
    buttons.addComponent(button);
    for (final var consumer : consumers) {
      consumer.accept(button);
    }
    return this;
  }

  @SafeVarargs
  public final <C extends Component> Dialog<T> addComponent(C component, Consumer<C>... consumers) {
    final int count = getContent().getComponentCount();
    getContent().addComponent(component, count - 1);
    component.setWidth(100, Unit.PERCENTAGE);
    getContent().setExpandRatio(component, 1);
    for (final var consumer : consumers) {
      consumer.accept(component);
    }
    return this;
  }

  public Dialog<T> resizable(boolean resizable) {
    setResizable(resizable);
    return this;
  }

  public Dialog<T> closeable(boolean closeable) {
    setClosable(closeable);
    return this;
  }

  public Dialog<T> draggable(boolean draggable) {
    setDraggable(draggable);
    return this;
  }

  public void show() {
    UI.getCurrent().addWindow(this);
  }

  @Override
  public VerticalLayout getContent() {
    return (VerticalLayout) super.getContent();
  }
}
