package org.marid.ide.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.marid.ide.images.ImageCache;
import org.marid.spring.init.Init;
import org.springframework.stereotype.Component;

@Component
public class IdeToolbar extends ToolBar {

  public IdeToolbar(Shell mainShell) {
    super(mainShell, SWT.HORIZONTAL);

    setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  @Init
  public void addOpenButton(ImageCache imageCache) {
    final var button = new Button(this, SWT.PUSH);
    button.setImage(imageCache.image("open.png"));
  }

  @Override
  protected void checkSubclass() {
  }
}
