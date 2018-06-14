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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.marid.applib.image.AppIcon;
import org.marid.ui.webide.base.common.UserImages;

import java.util.Optional;
import java.util.function.Consumer;

public class InputDialogBuilder {

  private final UserImages userImages;

  private AppIcon icon;
  private String title;
  private String message;
  private Shell shell;
  private String value;
  private IInputValidator validator;
  private Consumer<Optional<String>> callback;

  InputDialogBuilder(UserImages userImages) {
    this.userImages = userImages;
  }

  public InputDialogBuilder setShell(Shell shell) {
    this.shell = shell;
    return this;
  }

  public InputDialogBuilder setValue(String value) {
    this.value = value;
    return this;
  }

  public InputDialogBuilder setValidator(IInputValidator validator) {
    this.validator = validator;
    return this;
  }

  public InputDialogBuilder setTitle(String title) {
    this.title = title;
    return this;
  }

  public InputDialogBuilder setIcon(AppIcon icon) {
    this.icon = icon;
    return this;
  }

  public InputDialogBuilder setMessage(String message) {
    this.message = message;
    return this;
  }

  public InputDialogBuilder setCallback(Consumer<Optional<String>> callback) {
    this.callback = callback;
    return this;
  }

  public InputDialogBuilder addCallback(Consumer<Optional<String>> callback) {
    if (this.callback == null) {
      this.callback = callback;
    } else {
      final var old = this.callback;
      this.callback = v -> {
        old.accept(v);
        callback.accept(v);
      };
    }
    return this;
  }

  public void open() {
    final var dialog = new InputDialog(shell, title, message, value, validator) {
      @Override
      public void create() {
        super.create();
        if (icon != null) {
          getShell().setImage(userImages.image(icon));
        }
      }

      @Override
      public boolean close() {
        final boolean result = super.close();
        if (callback != null) {
          switch (getReturnCode()) {
            case OK:
              callback.accept(Optional.ofNullable(getValue()));
              break;
            default:
              callback.accept(Optional.empty());
              break;
          }
        }
        return result;
      }
    };
    dialog.setBlockOnOpen(false);
    dialog.open();
  }
}
