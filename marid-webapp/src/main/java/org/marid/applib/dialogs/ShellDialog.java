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
    setLayout(new GridLayout(1, false));
  }

  @SafeVarargs
  public final void addButton(String text, AppImage image, Listener listener, Consumer<Button>... buttonConsumers) {
    final var buttons = Stream.of(getChildren())
        .filter(Composite.class::isInstance)
        .map(Composite.class::cast)
        .filter(c -> "buttons".equals(c.getData("dialogControlType")))
        .findFirst()
        .orElseGet(() -> {
          final var c = new Composite(this, SWT.NONE);
          final var l = new GridLayout(1, false);

          l.marginWidth = 10;
          l.marginHeight = 10;
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
  }
}
