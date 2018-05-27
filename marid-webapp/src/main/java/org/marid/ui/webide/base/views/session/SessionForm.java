/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.marid.ui.webide.base.views.session;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;
import org.marid.applib.annotation.SpringComponent;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;

import static com.vaadin.icons.VaadinIcons.FEMALE;
import static com.vaadin.icons.VaadinIcons.MALE;

@SpringComponent
public class SessionForm extends FormLayout implements Inits {

  public SessionForm() {
    setSpacing(true);
    setMargin(true);
    setSizeFull();
  }

  @Init
  public void initProfile(CommonProfile profile, Strs strs) {
    addField(strs.s("user"), profile.getFirstName() + " " + profile.getFamilyName(), profile.getGender() == Gender.MALE ? MALE : FEMALE);
    addField(strs.s("email"), profile.getEmail(), VaadinIcons.MAILBOX);
    addComponent(new Image(strs.s("photo"), new ExternalResource(profile.getPictureUrl().toASCIIString())));
    addField(strs.s("locale"), profile.getLocale().toString(), VaadinIcons.LOCATION_ARROW_CIRCLE);
  }

  private void addField(String label, String value, Resource icon) {
    final var field = new TextField(label, value);
    field.setWidth(100, Unit.PERCENTAGE);
    field.setReadOnly(true);
    field.setIcon(icon);
    addComponent(field);
  }
}
