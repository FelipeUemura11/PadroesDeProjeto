package program3;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NuclearPlantController {
    private final Map<StateType, List<TransitionRule>> transitionMap = new EnumMap<>(StateType.class);
    private final Deque<StateType> recentStates = new ArrayDeque<>(5);

    private StateType currentState = StateType.DESLIGADA;
    private boolean maintenanceMode;
    private StateType suspendedState;

    public NuclearPlantController() {
        configureTransitions();
        recordState(currentState);
    }

    public StateType getCurrentState() {
        return currentState;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public StateType getSuspendedState() {
        return suspendedState;
    }

    public void enterMaintenanceMode() {
        if (maintenanceMode) {
            return;
        }
        suspendedState = currentState;
        maintenanceMode = true;
        currentState = StateType.MANUTENCAO;
        recordState(currentState);
    }

    public void exitMaintenanceMode() {
        if (!maintenanceMode) {
            return;
        }
        maintenanceMode = false;
        StateType target = suspendedState != null ? suspendedState : StateType.DESLIGADA;
        suspendedState = null;
        currentState = target;
        recordState(currentState);
    }

    public void transitionTo(StateType target,
                             TransitionTriggerType triggerType,
                             SensorSnapshot snapshot) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(triggerType, "triggerType");

        if (maintenanceMode) {
            if (currentState == StateType.MANUTENCAO) {
                throw new TransitionNotAllowedException("Modo manutencao ativo: finalize a manutencao antes de qualquer transicao");
            }
            if (triggerType == TransitionTriggerType.AUTOMATICO) {
                throw new TransitionNotAllowedException("Transicoes automaticas estao suspensas durante manutencao");
            }
        }

        if (target == currentState) {
            return;
        }

        TransitionRule rule = findRule(currentState, target, triggerType);
        if (rule == null) {
            throw new TransitionNotAllowedException("Nao existe regra para transicao de " + currentState + " para " + target + " via " + triggerType);
        }

        SensorSnapshot effectiveSnapshot = snapshot != null ? snapshot : SensorSnapshot.builder().build();
        ensureNoCircularTransition(rule, target);

        rule.assertAllowed(new TransitionContext(this, effectiveSnapshot, triggerType));

        currentState = target;
        recordState(currentState);
    }

    private TransitionRule findRule(StateType from, StateType target, TransitionTriggerType triggerType) {
        List<TransitionRule> rules = transitionMap.get(from);
        if (rules == null) {
            return null;
        }
        for (TransitionRule rule : rules) {
            if (rule.matches(target, triggerType)) {
                return rule;
            }
        }
        return null;
    }

    private void ensureNoCircularTransition(TransitionRule rule, StateType target) {
        StateType stateBeforePrevious = stateBeforePrevious();
        if (stateBeforePrevious != null && stateBeforePrevious == target && !rule.allowReturnToPrevious()) {
            throw new CircularTransitionException("Transicao circular detectada para o estado " + target);
        }
    }

    private StateType stateBeforePrevious() {
        Iterator<StateType> iterator = recentStates.descendingIterator();
        if (!iterator.hasNext()) {
            return null;
        }
        iterator.next(); // estado atual
        if (!iterator.hasNext()) {
            return null;
        }
        iterator.next(); // estado anterior
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }

    private void recordState(StateType state) {
        recentStates.addLast(state);
        while (recentStates.size() > 5) {
            recentStates.removeFirst();
        }
    }

    private void configureTransitions() {
        register(StateType.DESLIGADA, new TransitionRule(
                StateType.OPERACAO_NORMAL,
                TransitionTriggerType.MANUAL,
                ctx -> {
                    SensorSnapshot s = ctx.snapshot();
                    return s.temperatureCelsius() <= 280.0
                            && s.pressureBar() >= 0
                            && s.pressureBar() <= 150
                            && s.radiationSievert() < 5.0;
                },
                true));

        register(StateType.OPERACAO_NORMAL, new TransitionRule(
                StateType.DESLIGADA,
                TransitionTriggerType.MANUAL,
                ctx -> true,
                true));

        register(StateType.OPERACAO_NORMAL, new TransitionRule(
                StateType.ALERTA_AMARELO,
                TransitionTriggerType.AUTOMATICO,
                ctx -> ctx.snapshot().temperatureCelsius() > 300.0,
                false));

        register(StateType.ALERTA_AMARELO, new TransitionRule(
                StateType.OPERACAO_NORMAL,
                TransitionTriggerType.AUTOMATICO,
                ctx -> ctx.snapshot().temperatureCelsius() <= 300.0,
                true));

        register(StateType.ALERTA_AMARELO, new TransitionRule(
                StateType.ALERTA_VERMELHO,
                TransitionTriggerType.AUTOMATICO,
                ctx -> ctx.snapshot().temperatureCelsius() > 400.0
                        && ctx.snapshot().timeAbove400C().compareTo(Duration.ofSeconds(30)) >= 0,
                false));

        register(StateType.ALERTA_AMARELO, new TransitionRule(
                StateType.DESLIGADA,
                TransitionTriggerType.MANUAL,
                ctx -> true,
                false));

        register(StateType.ALERTA_VERMELHO, new TransitionRule(
                StateType.ALERTA_AMARELO,
                TransitionTriggerType.AUTOMATICO,
                ctx -> ctx.snapshot().temperatureCelsius() <= 400.0
                        && !ctx.snapshot().coolingSystemFailure(),
                true));

        register(StateType.ALERTA_VERMELHO, new TransitionRule(
                StateType.DESLIGADA,
                TransitionTriggerType.MANUAL,
                ctx -> true,
                false));

        register(StateType.ALERTA_VERMELHO, new TransitionRule(
                StateType.EMERGENCIA,
                TransitionTriggerType.AUTOMATICO,
                ctx -> ctx.snapshot().coolingSystemFailure(),
                false));

        register(StateType.EMERGENCIA, new TransitionRule(
                StateType.DESLIGADA,
                TransitionTriggerType.MANUAL,
                ctx -> !ctx.snapshot().coolingSystemFailure(),
                false));
    }

    private void register(StateType from, TransitionRule rule) {
        transitionMap.computeIfAbsent(from, key -> new ArrayList<>()).add(rule);
    }
}

