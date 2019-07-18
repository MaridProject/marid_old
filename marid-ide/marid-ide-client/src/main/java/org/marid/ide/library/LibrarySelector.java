package org.marid.ide.library;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.springframework.stereotype.Component;

@Component
public class LibrarySelector extends Combo {

  public LibrarySelector(LibraryPane libraryPane) {
    super(libraryPane, SWT.DROP_DOWN | SWT.BORDER);
    setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  @Override
  protected void checkSubclass() {
  }
}
