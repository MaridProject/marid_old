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
package org.marid.ui.webide.base.views.artifacts;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.marid.applib.repository.Artifact;
import org.marid.applib.repository.Repository;
import org.marid.applib.spring.init.Init;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.repositories.RepositoryManager;

import java.util.stream.Collectors;

import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
@PrototypeScoped
public class ArtifactFinderDialog extends Window {

  private final RepositoryManager manager;
  private final FormLayout form = new FormLayout();
  private final Accordion accordion = new Accordion();
  private final Accordion.Tab formTab;
  private final Accordion.Tab gridTab;
  private final Grid<Artifact> artifactGrid = new Grid<>();
  private final HorizontalLayout buttons = new HorizontalLayout();
  private final Binder<ArtifactFinderDialog> binder = new Binder<>();

  private String group;
  private String artifact;
  private String klass;

  public ArtifactFinderDialog(RepositoryManager manager) {
    super(s("searchArtifacts"), new VerticalLayout());
    this.manager = manager;
    setModal(true);

    form.setMargin(true);
    buttons.setMargin(false);

    artifactGrid.setSizeFull();
    artifactGrid.setSelectionMode(Grid.SelectionMode.MULTI);

    formTab = accordion.addTab(form, s("query"), VaadinIcons.FORM);
    gridTab = accordion.addTab(artifactGrid, s("artifacts"), VaadinIcons.LIST);
    accordion.setSizeFull();

    setWidth(400, Unit.PIXELS);
    setHeight(400, Unit.PIXELS);

    getContent().setMargin(true);
    getContent().addComponents(accordion, buttons);
    getContent().setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
    getContent().setExpandRatio(accordion, 1);
    getContent().setSizeFull();
  }

  @Override
  public VerticalLayout getContent() {
    return (VerticalLayout) super.getContent();
  }

  @Init
  public void initGroup() {
    final var field = new TextField(s("group"));
    field.setWidth(100, Unit.PERCENTAGE);
    binder.forField(field).bind(d -> d.group, (d, v) -> d.group = v);
    form.addComponent(field);
  }

  @Init
  public void initArtifact() {
    final var field = new TextField(s("artifact"));
    field.setWidth(100, Unit.PERCENTAGE);
    binder.forField(field).bind(d -> d.artifact, (d, v) -> d.artifact = v);
    form.addComponent(field);
  }

  @Init
  public void initClass() {
    final var field = new TextField(s("class"));
    field.setWidth(100, Unit.PERCENTAGE);
    binder.forField(field).bind(d -> d.klass, (d, v) -> d.klass = v);
    form.addComponent(field);
  }

  @Init
  public void initGroupColumn() {
    artifactGrid.addColumn(Artifact::getGroupId)
        .setId("groupId")
        .setCaption("groupId")
        .setExpandRatio(1);
  }

  @Init
  public void initArtifactColumn() {
    artifactGrid.addColumn(Artifact::getArtifactId)
        .setId("artifactId")
        .setCaption("artifactId")
        .setExpandRatio(2);
  }

  @Init
  public void initVersionColumn() {
    artifactGrid.addColumn(Artifact::getVersion)
        .setId("version")
        .setCaption("version")
        .setExpandRatio(1);
  }

  @Init
  public void addFindButton() {
    final var button = new Button(s("find"), VaadinIcons.SEARCH);
    button.addClickListener(e -> {
      if (binder.writeBeanIfValid(this)) {
        final var found = manager.repositories().stream()
            .map(Repository::getArtifactFinder)
            .flatMap(f -> f.find(group, artifact, klass).stream())
            .filter(a -> "jar".equals(a.getPackaging()))
            .filter(a -> "".equals(a.getClassifier()))
            .collect(Collectors.toUnmodifiableList());
        artifactGrid.setItems(found);
        if (found.isEmpty()) {
          Notification.show(s("emptyArtifactList"), WARNING_MESSAGE);
        } else {
          accordion.setSelectedTab(gridTab);
        }
      }
    });
    accordion.addSelectedTabChangeListener(e -> button.setEnabled(accordion.getSelectedTab() == formTab.getComponent()));
    buttons.addComponent(button);
  }

  @Init
  public void addCloseButton() {
    final var button = new Button(s("close"), VaadinIcons.CLOSE);
    button.addClickListener(e -> close());
    buttons.addComponent(button);
  }

  @Init
  public void addAddButton(ArtifactManager manager) {
    final var button = new Button(s("add"), VaadinIcons.FILE_ADD);
    button.setEnabled(false);
    button.addClickListener(e -> {
      manager.addArtifacts(artifactGrid.getSelectedItems());
      close();
    });
    artifactGrid.addSelectionListener(e -> button.setEnabled(!artifactGrid.getSelectedItems().isEmpty()));
    buttons.addComponent(button);
  }

  public void show() {
    UI.getCurrent().addWindow(this);
  }
}
