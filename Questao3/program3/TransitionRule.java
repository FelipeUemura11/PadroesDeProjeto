package program3;
public final class TransitionRule {
    private final StateType targetState;
    private final TransitionTriggerType triggerType;
    private final TransitionValidator validator;
    private final boolean allowReturnToPrevious;

    public TransitionRule(StateType targetState,
                          TransitionTriggerType triggerType,
                          TransitionValidator validator,
                          boolean allowReturnToPrevious) {
        this.targetState = targetState;
        this.triggerType = triggerType;
        this.validator = validator;
        this.allowReturnToPrevious = allowReturnToPrevious;
    }

    public boolean matches(StateType target, TransitionTriggerType trigger) {
        return targetState == target && triggerType == trigger;
    }

    public void assertAllowed(TransitionContext context) {
        if (!validator.isAllowed(context)) {
            throw new TransitionNotAllowedException(
                    "Transicao de " + context.controller().getCurrentState() + " para " + targetState + " nao permitida pelo sensor");
        }
    }

    public boolean allowReturnToPrevious() {
        return allowReturnToPrevious;
    }
}

