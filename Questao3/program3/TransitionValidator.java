package program3;
@FunctionalInterface
public interface TransitionValidator {
    boolean isAllowed(TransitionContext context);
}

