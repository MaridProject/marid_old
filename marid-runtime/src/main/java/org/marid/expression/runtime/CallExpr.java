/*-
 * #%L
 * marid-runtime
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

package org.marid.expression.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.marid.cellar.ExecutionContext;
import org.marid.expression.generic.CallExpression;
import org.marid.expression.xml.XmlExpression;
import org.w3c.dom.Element;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.marid.expression.generic.CallExpression.invokable;
import static org.marid.types.Classes.value;

public class CallExpr extends Expr implements CallExpression {

  @NotNull
  private final Expr target;

  @NotNull
  private final String method;

  @NotNull
  private final List<Expr> args;

  public CallExpr(@NotNull Expr target, @NotNull String method, @NotNull Expr... args) {
    this.target = target;
    this.method = method;
    this.args = List.of(args);
  }

  CallExpr(@NotNull Element element) {
    super(element);
    target = XmlExpression.target(element, Expr::of, ClassExpr::new, RefExpr::new);
    method = XmlExpression.method(element);
    args = XmlExpression.args(element, Expr::of, StringExpr::new, Collectors.toUnmodifiableList());
  }

  @Override
  protected Object execute(@Nullable Object self, @Nullable Type owner, @NotNull ExecutionContext context) {
    final Type[] argTypes = getArgs().stream().map(a -> a.getType(owner, context)).toArray(Type[]::new);
    return invokable(getTarget().getTargetClass(owner, context), getMethod(), argTypes)
        .map(invokable -> {
          final Class<?>[] argClasses = invokable.getParameterClasses();
          final Object[] args = new Object[argClasses.length];
          for (int i = 0; i < args.length; i++) {
            args[i] = value(argClasses[i], this.args.get(i).evaluate(self, owner, context));
          }
          try {
            if (invokable.isStatic()) {
              return invokable.execute(null, args);
            } else {
              return invokable.execute(getTarget().evaluate(self, owner, context), args);
            }
          } catch (ReflectiveOperationException x) {
            context.throwError(new IllegalStateException(x));
            return null;
          }
        })
        .orElseGet(() -> {
          context.throwError(new NoSuchElementException(getMethod()));
          return null;
        });
  }

  @Override
  @NotNull
  public Expr getTarget() {
    return target;
  }

  @Override
  @NotNull
  public String getMethod() {
    return method;
  }

  @Override
  @NotNull
  public List<Expr> getArgs() {
    return args;
  }

  @Override
  public String toString() {
    return args.stream().map(Object::toString).collect(joining(",", target + "." + method + "(", ")"));
  }
}
