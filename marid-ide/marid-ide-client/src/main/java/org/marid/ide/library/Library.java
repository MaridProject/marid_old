package org.marid.ide.library;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.marid.ide.main.MainPane;
import org.springframework.stereotype.Component;

@Component
public class Library extends ScrolledComposite {

  public Library(MainPane mainPane) {
    super(mainPane, SWT.V_SCROLL | SWT.BORDER);
    setExpandHorizontal(true);
    setExpandVertical(true);
    addListener(SWT.Resize, e -> {
      final int w = getClientArea().width;
      setMinSize(mainPane.computeSize(w, SWT.DEFAULT));
    });
  }
}
