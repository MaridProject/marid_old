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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static org.marid.processors.Unsafer.LOOKUP;

public class GenerateHelperProcessor implements Processor {

  private Filer filer;
  private Messager messager;
  private Elements elements;

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
    elements = processingEnv.getElementUtils();

    try {
      final var getContextMethod = LOOKUP.unreflect(processingEnv.getClass().getDeclaredMethod("getContext"));
      final var context = getContextMethod.invoke(processingEnv);
      final var contextGetMethod = LOOKUP.unreflect(context.getClass().getMethod("get", Class.class));
      final var manager = (JavaFileManager) contextGetMethod.invoke(context, JavaFileManager.class);

      final var classLoader = manager.getClassLoader(CLASS_PATH);

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
    final var targetElements = new HashMap<TypeElement, Set<TypeElement>>();

    for (var anns = annotations; !anns.isEmpty(); ) {
      messager.printMessage(NOTE, "Processing " + anns);
      final var els = anns.stream()
          .map(a -> Map.entry(a, env.getElementsAnnotatedWith(a)))
          .peek(e -> e.getValue().stream()
              .filter(TypeElement.class::isInstance)
              .map(TypeElement.class::cast)
              .forEach(a -> {
                if (a.getKind() == ANNOTATION_TYPE) {
                  map.computeIfAbsent(e.getKey(), v -> new HashSet<>()).add(a);
                } else {
                  targetElements.computeIfAbsent(e.getKey(), v -> new HashSet<>()).add(a);
                }
              })
          )
          .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

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

    final var passed = new HashSet<TypeElement>();
    targetElements.forEach((ann, els) -> {
      for (final var el : els) {
        if (passed.add(el)) {
          final var constructors = elements.getAllMembers(el).stream()
              .filter(m -> m.getKind() == CONSTRUCTOR)
              .map(ExecutableElement.class::cast)
              .filter(c -> c.getModifiers().contains(Modifier.PUBLIC))
              .toArray(ExecutableElement[]::new);

          if (constructors.length == 0) {
            continue;
          }

          try {
            final var helperFile = filer.createSourceFile(el.getQualifiedName() + "Factory");
            try (final var w = helperFile.openWriter()) {
              w.write("package ");
              w.write(elements.getPackageOf(el).getQualifiedName().toString());
              w.write(";\n");
              w.write("public interface ");
              w.write(el.getSimpleName() + "Factory {\n\n");
              for (final var constructor : constructors) {
                final var params = concat(el.getTypeParameters().stream(), constructor.getTypeParameters().stream())
                    .map(t -> {
                      var s = t.getSimpleName().toString();
                      final var bounds = new LinkedHashSet<>(t.getBounds());
                      bounds.removeIf(v -> v.toString().equals(Object.class.getName()));
                      if (!bounds.isEmpty()) {
                        s += bounds.stream()
                            .map(TypeMirror::toString)
                            .collect(joining(",", " extends ", ""));
                      }
                      return s;
                    })
                    .collect(Collectors.toList());
                final var throwns = constructor.getThrownTypes();

                w.write("static");
                if (!params.isEmpty()) {
                  w.write(" <");
                  w.write(String.join(",", params));
                  w.write('>');
                }
                w.write(' ');
                w.write(el.getSimpleName().toString());
                w.write(' ');
                w.write(" $(");
                w.write(constructor.getParameters().stream()
                    .map(p -> p.asType() + " " + p.getSimpleName())
                    .collect(joining(", "))
                );
                w.write(") ");
                if (!throwns.isEmpty()) {
                  w.write(throwns.stream()
                      .map(TypeMirror::toString)
                      .collect(joining(",", "throws ", ""))
                  );
                }
                w.write(" {\n");
                w.write(" return new ");
                w.write(el.getSimpleName().toString());
                if (!params.isEmpty()) {
                  w.write("<>");
                }
                w.write(constructor.getParameters().stream()
                    .map(p -> p.getSimpleName().toString())
                    .collect(joining(",", "(", ");\n"))
                );
                w.write("}\n\n");
              }
              w.write("}");
            }
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }
      }
    });

    return true;
  }

  @Override
  public List<Completion> getCompletions(Element e, AnnotationMirror a, ExecutableElement m, String t) {
    return List.of();
  }
}
