package org.marid.ide.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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
  public void addCreateButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("create24.png"));
    button.setToolTipText("Create a new cellar");

    new ToolItem(this, SWT.SEPARATOR);
  }

  @Init
  public void addOpenButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("open24.png"));
    button.setToolTipText("Open an existing cellar");
  }

  @Init
  public void addSaveButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("save24.png"));
    button.setToolTipText("Save the current cellar");
  }

  @Override
  protected void checkSubclass() {
  }
}
