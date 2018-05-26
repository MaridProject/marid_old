package org.marid.ui.webide.base.views.session;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.FormLayout;
import org.marid.applib.l10n.Strs;
import org.marid.ui.webide.base.MainTabs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionForm extends FormLayout {

  public SessionForm() {
    setSizeFull();
  }

  @Autowired
  private void initTab(Strs strs, MainTabs tabs) {
    tabs.addTab(this, strs.s("session"), VaadinIcons.USER);
  }
}
