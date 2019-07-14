package org.marid.ide;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

@Component("ideContextFinisher")
public class IdeContextFinisher implements Runnable {

  private final Display mainDisplay;
  private final Shell mainShell;

  public IdeContextFinisher(Display mainDisplay, Shell mainShell) {
    this.mainDisplay = mainDisplay;
    this.mainShell = mainShell;
  }

  @Override
  public void run() {
    while (!mainShell.isDisposed()) {
      if (!mainDisplay.readAndDispatch()) {
        mainDisplay.sleep();
      }
    }
    mainDisplay.close();
  }
}
