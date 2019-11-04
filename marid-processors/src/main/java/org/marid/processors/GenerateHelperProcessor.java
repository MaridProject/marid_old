package org.marid.processors;

/*-
 * #%L
 * marid-processors
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import com.nqzero.permit.Permit;
import com.nqzero.permit.Unsafer;

import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;

public class GenerateHelperProcessor implements Processor {

  private Filer filer;
  private Messager messager;
  private Types types;
  private Elements elements;
  private JavaFileManager fileManager;

  private final ConcurrentSkipListSet<String> annotationTypes = new ConcurrentSkipListSet<>();

  @Override
  public Set<String> getSupportedOptions() {
    return Set.of();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return annotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void initTypes(String typeName, ClassLoader classLoader) throws IOException {
    annotationTypes.add(typeName);

    for (final var e = classLoader.getResources("marid/annotations/" + typeName); e.hasMoreElements(); ) {
      final var url = e.nextElement();
      try (final var scanner = new Scanner(url.openStream(), StandardCharsets.UTF_8)) {
        while (scanner.hasNextLine()) {
          final var line = scanner.nextLine().trim();
          if (line.isEmpty()) {
            continue;
          }
          annotationTypes.add(line);
          initTypes(line, classLoader);
        }
      }
    }
  }

  @Override
  public void init(ProcessingEnvironment processingEnv) {
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    types = processingEnv.getTypeUtils();
    elements = processingEnv.getElementUtils();

    try {
      final var unsafe = Unsafer.uu;
      final var privateLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      final var privateLookupAddr = unsafe.staticFieldOffset(privateLookup);
      final var lookup = (MethodHandles.Lookup) unsafe.getObject(MethodHandles.Lookup.class, privateLookupAddr);

      final var getContextMethod = lookup.unreflect(processingEnv.getClass().getDeclaredMethod("getContext"));
      final var context = getContextMethod.invokeWithArguments(processingEnv);
      final var contextGetMethod = lookup.unreflect(context.getClass().getMethod("get", Class.class));
      final var manager = contextGetMethod.invokeWithArguments(context, JavaFileManager.class);
      fileManager = (JavaFileManager) manager;

      final var classLoader = fileManager.getClassLoader(CLASS_PATH);

      initTypes(GenerateHelper.class.getName(), classLoader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
    final var map = new HashMap<TypeElement, Set<TypeElement>>();
    final var targetElements = new HashSet<TypeElement>();

    for (var anns = annotations; !anns.isEmpty(); ) {
      messager.printMessage(NOTE, "Processing " + anns);
      final var els = anns.stream()
          .map(a -> Map.entry(a, env.getElementsAnnotatedWith(a)))
          .peek(e -> e.getValue().stream()
              .filter(a -> a.getKind() == ANNOTATION_TYPE)
              .map(TypeElement.class::cast)
              .forEach(t -> map.computeIfAbsent(e.getKey(), v -> new HashSet<>()).add(t))
          )
          .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

      els.values().forEach(vs -> vs.stream()
          .filter(e -> e.getKind() != ANNOTATION_TYPE)
          .filter(TypeElement.class::isInstance)
          .map(TypeElement.class::cast)
          .forEach(targetElements::add)
      );

      anns = els.values().stream()
          .flatMap(es -> es.stream().filter(a -> a.getKind() == ANNOTATION_TYPE).map(TypeElement.class::cast))
          .collect(Collectors.toUnmodifiableSet());
    }

    map.forEach((ann, nested) -> {
      messager.printMessage(NOTE, "Adding nested annotations", ann);
      try {
        annotationTypes.add(ann.getQualifiedName().toString());
        final var file = filer.createResource(CLASS_OUTPUT, "marid.annotations", ann.getQualifiedName().toString());
        try (final var writer = file.openWriter()) {
          for (final var nestedType : nested) {
            writer.write(nestedType.getQualifiedName().toString());
            writer.write('\n');
          }
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    for (final var targetElement : targetElements) {

    }

    return true;
  }

  @Override
  public List<Completion> getCompletions(Element e, AnnotationMirror a, ExecutableElement m, String t) {
    return List.of();
  }
}
