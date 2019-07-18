package org.marid.ide.library;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.layout.GridData;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("librarySelector")
public class LibraryViewer extends ListViewer {

  public LibraryViewer(LibraryPane libraryPane) {
    super(libraryPane);
    getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
  }
}
