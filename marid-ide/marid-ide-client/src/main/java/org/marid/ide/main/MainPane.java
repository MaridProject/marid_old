package org.marid.ide.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@DependsOn("ideToolbar")
public class MainPane extends SashForm {

  public MainPane(Shell mainShell) {
    super(mainShell, SWT.HORIZONTAL);
    setLayoutData(new GridData(GridData.FILL_BOTH));
  }

  @EventListener
  public void onStart(ContextStartedEvent event) {
    setWeights(new int[] {1, 4});
  }
}
