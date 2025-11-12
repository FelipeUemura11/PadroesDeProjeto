package program3;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        NuclearPlantController controller = new NuclearPlantController();

        System.out.println("Estado inicial: " + controller.getCurrentState());

        SensorSnapshot parametrosSeguros = SensorSnapshot.builder()
                .temperatureCelsius(250)
                .pressureBar(95)
                .radiationSievert(1.5)
                .build();

        controller.transitionTo(StateType.OPERACAO_NORMAL, TransitionTriggerType.MANUAL, parametrosSeguros);
        System.out.println("Transicao manual para OPERACAO_NORMAL: " + controller.getCurrentState());

        SensorSnapshot temperaturaElevada = SensorSnapshot.builder()
                .temperatureCelsius(325)
                .pressureBar(110)
                .radiationSievert(1.7)
                .build();

        controller.transitionTo(StateType.ALERTA_AMARELO, TransitionTriggerType.AUTOMATICO, temperaturaElevada);
        System.out.println("Transicao automatica para ALERTA_AMARELO: " + controller.getCurrentState());

        SensorSnapshot temperaturaCritica = SensorSnapshot.builder()
                .temperatureCelsius(415)
                .pressureBar(118)
                .radiationSievert(1.9)
                .timeAbove400C(Duration.ofSeconds(45))
                .build();

        controller.transitionTo(StateType.ALERTA_VERMELHO, TransitionTriggerType.AUTOMATICO, temperaturaCritica);
        System.out.println("Transicao automatica para ALERTA_VERMELHO: " + controller.getCurrentState());

        SensorSnapshot falhaResfriamento = SensorSnapshot.builder()
                .temperatureCelsius(420)
                .pressureBar(120)
                .radiationSievert(2.4)
                .timeAbove400C(Duration.ofSeconds(60))
                .coolingSystemFailure(true)
                .build();

        controller.transitionTo(StateType.EMERGENCIA, TransitionTriggerType.AUTOMATICO, falhaResfriamento);
        System.out.println("Transicao automatica para EMERGENCIA: " + controller.getCurrentState());

        SensorSnapshot sistemaReestabelecido = SensorSnapshot.builder()
                .temperatureCelsius(260)
                .pressureBar(90)
                .radiationSievert(1.1)
                .coolingSystemFailure(false)
                .build();

        controller.transitionTo(StateType.DESLIGADA, TransitionTriggerType.MANUAL, sistemaReestabelecido);
        System.out.println("Transicao manual para DESLIGADA: " + controller.getCurrentState());

        controller.transitionTo(StateType.OPERACAO_NORMAL, TransitionTriggerType.MANUAL, parametrosSeguros);
        controller.enterMaintenanceMode();
        System.out.println("Modo manutencao ativado: estado atual " + controller.getCurrentState());

        try {
            controller.transitionTo(StateType.ALERTA_AMARELO, TransitionTriggerType.AUTOMATICO, temperaturaElevada);
        } catch (TransitionNotAllowedException ex) {
            System.out.println("Transicao bloqueada em manutencao: " + ex.getMessage());
        }

        controller.exitMaintenanceMode();
        System.out.println("Modo manutencao encerrado, estado restaurado: " + controller.getCurrentState());

        try {
            controller.transitionTo(StateType.DESLIGADA, TransitionTriggerType.MANUAL, parametrosSeguros);
            controller.transitionTo(StateType.OPERACAO_NORMAL, TransitionTriggerType.MANUAL, parametrosSeguros);
            controller.transitionTo(StateType.DESLIGADA, TransitionTriggerType.MANUAL, parametrosSeguros);
        } catch (CircularTransitionException ex) {
            System.out.println("Prevencao de ciclo detectada: " + ex.getMessage());
        }

        System.out.println("Estado final: " + controller.getCurrentState());
    }
}

