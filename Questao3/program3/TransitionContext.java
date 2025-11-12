package program3;
public final class TransitionContext {
    private final NuclearPlantController controller;
    private final SensorSnapshot snapshot;
    private final TransitionTriggerType triggerType;

    TransitionContext(NuclearPlantController controller, SensorSnapshot snapshot, TransitionTriggerType triggerType) {
        this.controller = controller;
        this.snapshot = snapshot;
        this.triggerType = triggerType;
    }

    public NuclearPlantController controller() {
        return controller;
    }

    public SensorSnapshot snapshot() {
        return snapshot;
    }

    public TransitionTriggerType triggerType() {
        return triggerType;
    }
}

