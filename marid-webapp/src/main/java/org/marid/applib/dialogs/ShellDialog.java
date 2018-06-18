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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.intellij.lang.annotations.MagicConstant;
import org.marid.applib.image.AppImage;
import org.marid.applib.image.WithImages;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class ShellDialog extends Shell implements WithImages {

  public ShellDialog(Shell parent, @MagicConstant(flagsFromClass = SWT.class) int style) {
    super(parent, style);

    final var layout = new GridLayout(1, false);
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    layout.verticalSpacing = 10;

    setLayout(layout);
  }

  @SafeVarargs
  public final void addButton(String text, AppImage image, Listener listener, Consumer<Button>... buttonConsumers) {
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
