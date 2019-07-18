package org.marid.ide.library;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.springframework.stereotype.Component;

@Component
public class LibraryPane extends Composite {

  public LibraryPane(Library library) {
    super(library, SWT.NONE);
    setLayout(new GridLayout(1, false));

    library.setContent(this);
  }
}
