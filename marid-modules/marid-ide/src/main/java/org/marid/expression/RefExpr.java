/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.expression;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.marid.runtime.expression.RefExpression;

import javax.annotation.Nonnull;

public class RefExpr extends AbstractExpression implements RefExpression {

    public final StringProperty ref = new SimpleStringProperty();

    public RefExpr() {
        this("");
    }

    public RefExpr(@Nonnull String ref) {
        this.ref.set(ref);
    }

    @Nonnull
    @Override
    public String getReference() {
        return ref.get();
    }

    @Override
    public void setReference(@Nonnull String reference) {
        this.ref.set(reference);
    }
}