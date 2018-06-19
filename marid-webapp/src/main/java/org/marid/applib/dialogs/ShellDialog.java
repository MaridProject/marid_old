/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.dialogs;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.intellij.lang.annotations.MagicConstant;
import org.marid.applib.image.AppImage;
import org.marid.applib.image.WithImages;
import org.marid.misc.ListenableValue;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.swt.SWT.MouseDown;
import static org.eclipse.swt.SWT.NONE;
import static org.marid.applib.controls.Controls.control;

public abstract class ShellDialog extends Shell implements WithImages {

  public ShellDialog(Shell parent, @MagicConstant(flagsFromClass = SWT.class) int style) {
    super(parent, style);

    final var layout = new GridLayout(1, false);
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    layout.verticalSpacing = 10;

    setLayout(layout);
  }

  public ShellDialog(Shell parent) {
    this(parent, SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  protected int formStyle() {
    return NONE;
  }

  @SafeVarargs
  public final <C extends Control> C addField(String text,
                                              AppImage image,
                                              Function<Composite, C> supplier,
                                              Consumer<C>... controlConsumers) {
    return addField(this, text, image, supplier, controlConsumers);
  }

  @SafeVarargs
  public final <C extends Control> C addField(Composite parent,
                                              String text,
                                              AppImage image,
                                              Function<Composite, C> supplier,
                                              Consumer<C>... controlConsumers) {
    final var form = Stream.of(parent.getChildren())
        .filter(Composite.class::isInstance)
        .map(Composite.class::cast)
        .filter(c -> "form".equals(c.getData("dialogControlType")))
        .findFirst()
        .orElseGet(() -> {
          final var c = new Composite(this, formStyle());
          final var l = new GridLayout(3, false);

          l.marginWidth = l.marginHeight = 10;

          c.setLayout(l);
          c.setLayoutData(new GridData(GridData.FILL_BOTH));
          c.setData("dialogControlType", "form");

          return c;
        });

    final var imgButton = new Label(form, NONE);
    imgButton.setImage(image(image));

    final var label = new Label(form, NONE);
    label.setText(text + ": ");

    final var control = supplier.apply(form);

    imgButton.setData("imageFor", control);
    label.setData("labelFor", control);

    for (final var controlConsumer : controlConsumers) {
      controlConsumer.accept(control);
    }

    if (control.getLayoutData() == null) {
      control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    return control;
  }

  @SafeVarargs
  public final Button addButton(String text, AppImage image, Listener listener, Consumer<Button>... buttonConsumers) {
    final var buttons = Stream.of(getChildren())
        .filter(Composite.class::isInstance)
        .map(Composite.class::cast)
        .filter(c -> "buttons".equals(c.getData("dialogControlType")))
        .findFirst()
        .orElseGet(() -> {
          final var c = new Composite(this, SWT.BORDER);
          final var l = new GridLayout();

          l.horizontalSpacing = 10;

          c.setLayout(l);
          c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
          c.setData("dialogControlType", "buttons");
          return c;
        });

    final var button = new Button(buttons, SWT.PUSH);
    button.setText(text);
    button.setImage(image(image));
    button.addListener(SWT.Selection, listener);
    for (final var buttonConsumer : buttonConsumers) {
      buttonConsumer.accept(button);
    }

    final var layout = (GridLayout) buttons.getLayout();
    layout.numColumns++;

    return button;
  }

  protected void justify(Composite parent, float sizeHint) {
    final var preferredSize = computeSize(SWT.DEFAULT, SWT.DEFAULT);
    final var displaySize = parent.getBounds();
    final var hintSize = new Point((int) (displaySize.width * sizeHint), (int) (displaySize.height * sizeHint));

    if (preferredSize.x < hintSize.x) {
      preferredSize.x = hintSize.x;
    }
    if (preferredSize.y < hintSize.y) {
      preferredSize.y = hintSize.y;
    }
    if (preferredSize.x > displaySize.width) {
      preferredSize.x = displaySize.width;
    }
    if (preferredSize.y > displaySize.height) {
      preferredSize.y = displaySize.height;
    }

    setBounds(
        (displaySize.width - preferredSize.x) / 2,
        (displaySize.height - preferredSize.y) / 2,
        preferredSize.x,
        preferredSize.y
    );
  }

  public void bindValidation(ListenableValue<? extends String> message, Control control) {
    final Consumer<String> consumer = n -> {
      control(this, Label.class, l -> control == l.getData("labelFor")).ifPresent(label -> {
        if (n != null) {
          label.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
          label.setToolTipText(n);
        } else {
          label.setForeground(null);
          label.setToolTipText(null);
        }
      });
    };
    message.addListener((o, n) -> consumer.accept(n));
    control(this, Label.class, l -> control == l.getData("imageFor")).ifPresent(img -> img.addListener(MouseDown, e -> {
      final String text = message.get();
      if (text != null) {
        final var dialog = new ErrorDialog(this, null, null, new Status(ERROR, "dialog", text), ERROR);
        dialog.setBlockOnOpen(false);
        dialog.open();
      }
    }));
    consumer.accept(message.get());
  }

  public void show(float sizeHint) {
    if (!getMaximized()) {
      final Composite parent = getParent();
      justify(parent, sizeHint);
      final Listener listener = e -> justify(parent, sizeHint);
      parent.addListener(SWT.Resize, listener);
      addListener(SWT.Dispose, e -> parent.removeListener(SWT.Resize, listener));
    }
    open();
  }
}
