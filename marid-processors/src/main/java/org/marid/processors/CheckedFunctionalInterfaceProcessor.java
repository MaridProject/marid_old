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
import javax.annotation.processing.Generated;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.WARNING;

public class CheckedFunctionalInterfaceProcessor implements Processor {

  private static final Method targetPackageNameMethod;
  private static final Method interfacePrefixMethod;
  private static final Method checkedThrowableClassesMethod;
  private static final Method wrapperExceptionClassMethod;
  private static final Method functionalInterfacesMethod;

  static {
    try {
      targetPackageNameMethod = CheckedFunctionalInterface.class.getMethod("targetPackageName");
      interfacePrefixMethod = CheckedFunctionalInterface.class.getMethod("interfacePrefix");
      checkedThrowableClassesMethod = CheckedFunctionalInterface.class.getMethod("checkedThrowableClasses");
      wrapperExceptionClassMethod = CheckedFunctionalInterface.class.getMethod("wrapperExceptionClass");
      functionalInterfacesMethod = CheckedFunctionalInterface.class.getMethod("functionalInterfaces");
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private Filer filer;
  private Messager messager;
  private Types types;
  private Elements elements;

  @Override
  public Set<String> getSupportedOptions() {
    return Set.of();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(CheckedFunctionalInterface.class.getName(), CheckedFunctionalInterfaces.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public void init(ProcessingEnvironment processingEnv) {
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    types = processingEnv.getTypeUtils();
    elements = processingEnv.getElementUtils();
  }

  private static AnnotationValue value(Map<? extends ExecutableElement, ? extends AnnotationValue> map, Method method) {
    return map.entrySet().parallelStream()
        .filter(e -> e.getKey().getSimpleName().contentEquals(method.getName()))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElseThrow();
  }

  private static void addTypeVariables(List<? extends TypeParameterElement> params, Writer writer) throws IOException {
    if (!params.isEmpty()) {
      writer.append(params.stream().map(Element::getSimpleName).collect(joining(", ", "<", ">")));
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final var elements = annotations.stream()
        .flatMap(a -> roundEnv.getElementsAnnotatedWith(a).stream())
        .collect(Collectors.toSet());

    for (final var element : elements) {
      messager.printMessage(NOTE, "Processing", element);
      final var pkg = (PackageElement) element;

      final var anns = pkg.getAnnotationMirrors().parallelStream()
          .flatMap(a -> {
            final var name = a.getAnnotationType().asElement().getSimpleName();
            if (name.contentEquals(CheckedFunctionalInterface.class.getSimpleName())) {
              return Stream.of(a);
            } else if (name.contentEquals(CheckedFunctionalInterfaces.class.getSimpleName())) {
              return a.getElementValues().entrySet().stream()
                  .filter(e -> e.getKey().getSimpleName().contentEquals("value"))
                  .flatMap(e -> ((List<?>) e.getValue().getValue()).stream())
                  .map(AnnotationMirror.class::cast);
            } else {
              return Stream.empty();
            }
          })
          .collect(toList());

      for (final var ann : anns) {
        final var methods = ann.getElementValues();
        final var targetPackageName = value(methods, targetPackageNameMethod).getValue().toString();
        final var interfacePrefix = value(methods, interfacePrefixMethod).getValue().toString();
        final var checkedThrowableClasses = ((List<?>) value(methods, checkedThrowableClassesMethod).getValue()).stream()
            .map(v -> (TypeMirror) ((AnnotationValue) v).getValue())
            .collect(Collectors.toUnmodifiableList());
        final var wrapperExceptionClass = (TypeMirror) value(methods, wrapperExceptionClassMethod).getValue();

        ((List<?>) value(methods, functionalInterfacesMethod).getValue()).parallelStream()
            .map(v -> (TypeMirror) ((AnnotationValue) v).getValue()).forEach(itf -> {
          final var itfElement = (TypeElement) types.asElement(itf);
          final var sam = sam(itfElement);
          if (sam == null) {
            synchronized (this) {
              messager.printMessage(WARNING, itf + " is not a FunctionalInterface", element);
            }
            return;
          }
          synchronized (this) {
            messager.printMessage(NOTE, "Processing " + itf + "." + sam, element);
          }
          final var name = interfacePrefix + itfElement.getSimpleName().toString();

          try {
            final var resolvedVars = resolveVars((DeclaredType) itf);

            final JavaFileObject resource;
            synchronized (this) {
              resource = filer.createSourceFile(targetPackageName + "." + name);
            }

            try (final var w = resource.openWriter()) {
              w.append("package ").append(targetPackageName).append(";\n\n");

              // interface header
              w.append("@").append(Generated.class.getName())
                  .append('(')
                  .append("value = ").append('"').append("marid").append('"').append(", ")
                  .append("date = ").append('"').append(Instant.now().toString()).append('"')
                  .append(")\n");
              w.append('@').append(FunctionalInterface.class.getSimpleName()).append('\n');
              w.append("public interface ").append(name);
              final var typeVars = itfElement.getTypeParameters();
              addTypeVariables(typeVars, w);
              w.append(" extends ").append(itfElement.getQualifiedName());
              addTypeVariables(typeVars, w);
              w.append(" {\n\n");

              // checked method
              w.append(' ')
                  .append(substitute(sam.getReturnType(), resolvedVars).toString()).append(' ')
                  .append(sam.getSimpleName()).append("Checked")
                  .append(sam.getParameters().stream()
                      .map(p -> substitute(p.asType(), resolvedVars) + " " + p.getSimpleName())
                      .collect(joining(", ", "(", ")")))
                  .append(" throws ")
                  .append(checkedThrowableClasses.stream()
                      .map(TypeMirror::toString)
                      .collect(joining(", ", "", ";\n\n")));

              // default method
              w.append(" default ")
                  .append(substitute(sam.getReturnType(), resolvedVars).toString()).append(' ')
                  .append(sam.getSimpleName())
                  .append(sam.getParameters().stream()
                      .map(p -> substitute(p.asType(), resolvedVars) + " " + p.getSimpleName())
                      .collect(joining(", ", "(", ")")));
              final var throwsOriginal = sam.getThrownTypes();
              if (!throwsOriginal.isEmpty()) {
                w.append(" throws ").append(throwsOriginal.stream()
                    .map(TypeMirror::toString)
                    .collect(joining(", ")));
              }
              w.append(" {\n");
              w.append("  try {\n");

              w.append("    ");
              if (!types.getNoType(TypeKind.VOID).equals(sam.getReturnType())) {
                w.append("return ");
              }
              w.append(sam.getSimpleName()).append("Checked")
                  .append(sam.getParameters().stream()
                      .map(VariableElement::getSimpleName)
                      .collect(joining(", ", "(", ");\n")));
              w.append("  } catch ")
                  .append(checkedThrowableClasses.stream()
                      .map(TypeMirror::toString)
                      .collect(joining(" | ", "(", " e) {\n")));
              w.append("    throw new ")
                  .append(wrapperExceptionClass.toString())
                  .append("(e);\n")
                  .append("  }\n");
              w.append(" }\n\n");

              // static helper
              w.append(" static ");
              addTypeVariables(typeVars, w);
              w.append(' ').append(name);
              addTypeVariables(typeVars, w);
              w.append(" of(");
              w.append(name);
              addTypeVariables(typeVars, w);
              w.append(" value) { return value; }\n\n");

              // end of class
              w.append("}\n");
            }
          } catch (Throwable e) {
            synchronized (this) {
              messager.printMessage(ERROR, "Unexpected error: " + e.getMessage(), element);
              e.printStackTrace();
            }
          }
        });
      }
    }
    return true;
  }

  @Override
  public List<Completion> getCompletions(Element e, AnnotationMirror a, ExecutableElement m, String text) {
    return List.of();
  }

  private ExecutableElement sam(TypeElement type) {
    return elements.getAllMembers(type).stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(e -> !e.isDefault())
        .filter(e -> e.getModifiers().contains(ABSTRACT))
        .findFirst()
        .orElse(null);
  }

  private void resolveVars(DeclaredType type, LinkedHashMap<TypeVariable, TypeMirror> map) {
    final var t = (TypeElement) type.asElement();
    final var args = type.getTypeArguments();
    final var vars = t.getTypeParameters().stream().map(TypeParameterElement::asType).collect(toList());
    final int l = Math.min(vars.size(), args.size());
    for (int i = 0; i < l; i++) {
      map.put((TypeVariable) vars.get(i), args.get(i));
    }
    for (final var itf : t.getInterfaces()) {
      resolveVars((DeclaredType) itf, map);
    }
  }

  private LinkedHashMap<TypeVariable, TypeMirror> resolveVars(DeclaredType type) {
    final var map = new LinkedHashMap<TypeVariable, TypeMirror>();
    resolveVars(type, map);
    map.replaceAll((k, v) -> substitute(v, map));
    return map;
  }

  private TypeMirror substitute(TypeMirror type, LinkedHashMap<TypeVariable, TypeMirror> map) {
    if (type instanceof ArrayType) {
      final var t = (ArrayType) type;
      return types.getArrayType(substitute(t.getComponentType(), map));
    } else if (type instanceof DeclaredType) {
      final var t = (DeclaredType) type;
      return types.getDeclaredType((TypeElement) t.asElement(), t.getTypeArguments().stream()
          .map(e -> substitute(e, map))
          .toArray(TypeMirror[]::new)
      );
    } else if (type instanceof WildcardType) {
      final var t = (WildcardType) type;
      return types.getWildcardType(substitute(t.getExtendsBound(), map), substitute(t.getSuperBound(), map));
    } else if (type instanceof TypeVariable) {
      final var t = (TypeVariable) type;
      final var m = map.get(t);
      return m == null ? t : substitute(m, map);
    } else {
      return type;
    }
  }
}
