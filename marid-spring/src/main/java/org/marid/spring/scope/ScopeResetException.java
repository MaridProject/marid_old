package org.marid.spring.scope;

public class ScopeResetException extends Exception {

  private final ResettableScope scope;

  public ScopeResetException(ResettableScope scope) {
    super("Unable to reset scope " + scope.getConversationId());
    this.scope = scope;
  }

  public ResettableScope getScope() {
    return scope;
  }
}
