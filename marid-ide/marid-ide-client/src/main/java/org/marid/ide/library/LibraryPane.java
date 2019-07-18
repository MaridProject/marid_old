package org.marid.ide.library;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.marid.ide.main.MainPane;
import org.springframework.stereotype.Component;

@Component
public class LibraryPane extends Composite {

  public LibraryPane(MainPane mainPane) {
    super(mainPane, SWT.BORDER);
    setLayout(new GridLayout(1, false));
  }
}
