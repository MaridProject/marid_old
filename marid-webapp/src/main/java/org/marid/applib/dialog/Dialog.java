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
import com.vaadin.shared.Registration;
import com.vaadin.ui.*;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Dialog<T> extends Window {

  private final T bean;
  private final Binder<T> binder = new Binder<>();
  private final FormLayout form = new FormLayout();
  private final HorizontalLayout buttons = new HorizontalLayout();
  private final Registration attachRegistration;
  private final Registration initRegistration;

  public Dialog(String caption, T bean, boolean modal) {
    super(caption, new VerticalLayout());
    this.bean = bean;
    setModal(modal);
    setId(UUID.randomUUID().toString());

    form.setSpacing(true);
    form.setMargin(true);
    form.setSizeFull();

    buttons.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
    buttons.setWidth(100, Unit.PERCENTAGE);

    getContent().setSpacing(true);
    getContent().setMargin(true);
    getContent().addComponent(form);
    getContent().setExpandRatio(form, 1);
    getContent().addComponent(buttons);
    getContent().setExpandRatio(buttons, 0);

    attachRegistration = addAttachListener(e -> {
      final var dialog = this;
      dialog.attachRegistration.remove();
      final var javaScript = JavaScript.getCurrent();
      javaScript.addFunction("dialogClientHeight", args -> {
        javaScript.removeFunction("dialogClientHeight");
        final var height = args.getNumber(0);
        final var width = args.getNumber(1);
        setHeight((float) height, Unit.PIXELS);
        setWidth((float) width, Unit.PIXELS);
        getContent().setSizeFull();
      });
    });
    initRegistration = addFocusListener(e -> {
      final var dialog = this;
      dialog.initRegistration.remove();
      JavaScript.eval("dialogClientHeight(document.getElementById('" + getId() + "').clientHeight,document.getElementById('" + getId() + "').clientWidth)");
    });
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
