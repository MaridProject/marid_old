package org.marid.ide.library;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("librarySelector")
public class LibraryViewer extends Tree {

  public LibraryViewer(LibraryPane libraryPane) {
    super(libraryPane, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
    setLayoutData(new GridData(GridData.FILL_BOTH));
  }

  @Override
  protected void checkSubclass() {
  }
}
