/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

package org.marid.project.ivy.infrastructure;

import org.apache.ivy.util.MessageLogger;

import java.util.Collections;
import java.util.List;

import static org.apache.ivy.util.Message.*;

@FunctionalInterface
public interface BaseMessageLogger extends MessageLogger {

  @Override
  default void rawlog(String msg, int level) {
    log(msg, level);
  }

  @Override
  default void debug(String msg) {
    log(msg, MSG_DEBUG);
  }

  @Override
  default void info(String msg) {
    log(msg, MSG_INFO);
  }

  @Override
  default void rawinfo(String msg) {
    info(msg);
  }

  @Override
  default void error(String msg) {
    log(msg, MSG_ERR);
  }

  @Override
  default void verbose(String msg) {
    log(msg, MSG_VERBOSE);
  }

  @Override
  default void warn(String msg) {
    log(msg, MSG_WARN);
  }

  @Override
  default void deprecated(String msg) {
    log(msg, MSG_WARN);
  }

  @Override
  default List<String> getProblems() {
    return Collections.emptyList();
  }

  @Override
  default List<String> getWarns() {
    return Collections.emptyList();
  }

  @Override
  default List<String> getErrors() {
    return Collections.emptyList();
  }

  @Override
  default void clearProblems() {
  }

  @Override
  default void sumupProblems() {
  }

  @Override
  default void endProgress() {
  }

  @Override
  default void endProgress(String msg) {
  }

  @Override
  default boolean isShowProgress() {
    return false;
  }

  @Override
  default void setShowProgress(boolean progress) {
  }

  @Override
  default void progress() {
  }
}
