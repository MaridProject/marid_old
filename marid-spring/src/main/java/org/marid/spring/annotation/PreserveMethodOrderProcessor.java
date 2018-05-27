/*-
 * #%L
 * marid-util
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
package org.marid.spring.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@SupportedSourceVersion(SourceVersion.RELEASE_10)
@SupportedAnnotationTypes({"org.marid.spring.annotation.SpringComponent"})
public class PreserveMethodOrderProcessor extends AbstractProcessor {

  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final HashSet<Element> passed = new HashSet<>();
    for (final var annotation : annotations) {
      for (final var element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement && passed.add(element)) {
          final var type = (TypeElement) element;
          switch (type.getKind()) {
            case ANNOTATION_TYPE:
            case INTERFACE:
              continue;
            default:
              final var methods = type.getEnclosedElements().stream()
                  .filter(ExecutableElement.class::isInstance)
                  .map(ExecutableElement.class::cast)
                  .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
                  .collect(Collectors.toCollection(LinkedHashSet::new));

              if (methods.isEmpty()) {
                break;
              }

              final var pkg = ((PackageElement) type.getEnclosingElement()).getQualifiedName();
              final var name = type.getSimpleName() + ".methods";

              try {
                final var file = filer.createResource(StandardLocation.CLASS_OUTPUT, pkg, name);
                try (final Writer writer = file.openWriter()) {
                  for (final var method : methods) {
                    writer.append(method.getSimpleName());
                    writer.append('\n');
                  }
                }
                messager.printMessage(NOTE, "Written method orders for " + type);
              } catch (IOException x) {
                messager.printMessage(ERROR, "Unable to write method order file for " + type);
              }
              break;
          }
        }
      }
    }
    return true;
  }
}
