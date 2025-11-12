import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CadeiaValidacaoDocumentos {
    private final List<EtapaValidacao> etapas;
    private final Map<String, Set<String>> regrasPuloPosFalha;
    private final int limiteFalhasCircuitBreaker;

    private CadeiaValidacaoDocumentos(List<EtapaValidacao> etapas,
                                      Map<String, Set<String>> regrasPuloPosFalha,
                                      int limiteFalhasCircuitBreaker) {
        this.etapas = etapas;
        this.regrasPuloPosFalha = regrasPuloPosFalha;
        this.limiteFalhasCircuitBreaker = limiteFalhasCircuitBreaker;
    }

    public ResultadoCadeiaValidacao executar(DocumentoFiscal documento, ContextoValidacao contexto) {
        List<RegistroResultadoValidador> registros = new ArrayList<>();
        Set<String> validadoresQueFalharam = new HashSet<>();
        Deque<ValidadorDocumento> pilhaRollback = new ArrayDeque<>();
        int falhasAcumuladas = 0;
        boolean circuitoAberto = false;
        boolean cadeiaAindaValida = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            for (EtapaValidacao etapa : etapas) {
                if (circuitoAberto) {
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.CIRCUITO_INTERROMPIDO,
                            "Execucao interrompida pelo circuit breaker"));
                    continue;
                }

                if (devePular(validadoresQueFalharam, etapa)) {
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.PULADO,
                            "Pulado devido a falha anterior configurada"));
                    continue;
                }

                if (etapa.executarSomenteSeCadeiaInteiraValida && !cadeiaAindaValida) {
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.PULADO,
                            "Pulado porque houve falha em validador anterior"));
                    continue;
                }

                Callable<ResultadoValidacaoIndividual> tarefa = () -> etapa.validador.validar(documento, contexto);
                try {
                    Future<ResultadoValidacaoIndividual> futuro = executor.submit(tarefa);
                    ResultadoValidacaoIndividual resultado = futuro.get(etapa.timeout.toMillis(), TimeUnit.MILLISECONDS);

                    if (resultado.isValido()) {
                        registros.add(new RegistroResultadoValidador(
                                etapa.validador.nome(),
                                StatusValidacaoEtapa.SUCESSO,
                                resultado.getMensagem()));
                        if (etapa.validador instanceof ValidadorComRollback) {
                            pilhaRollback.push(etapa.validador);
                        }
                    } else {
                        registros.add(new RegistroResultadoValidador(
                                etapa.validador.nome(),
                                StatusValidacaoEtapa.FALHA,
                                resultado.getMensagem()));
                        validadoresQueFalharam.add(etapa.validador.nome());
                        falhasAcumuladas++;
                        cadeiaAindaValida = false;
                    }
                } catch (TimeoutException timeoutException) {
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.TIMEOUT,
                            "Tempo limite excedido apos " + etapa.timeout.toMillis() + " ms"));
                    validadoresQueFalharam.add(etapa.validador.nome());
                    falhasAcumuladas++;
                    cadeiaAindaValida = false;
                } catch (ExecutionException ex) {
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.ERRO,
                            "Erro inesperado: " + ex.getCause().getMessage()));
                    validadoresQueFalharam.add(etapa.validador.nome());
                    falhasAcumuladas++;
                    cadeiaAindaValida = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    registros.add(new RegistroResultadoValidador(
                            etapa.validador.nome(),
                            StatusValidacaoEtapa.ERRO,
                            "Validador interrompido: " + ex.getMessage()));
                    validadoresQueFalharam.add(etapa.validador.nome());
                    falhasAcumuladas++;
                    cadeiaAindaValida = false;
                }

                if (falhasAcumuladas >= limiteFalhasCircuitBreaker) {
                    circuitoAberto = true;
                }
            }
        } finally {
            executor.shutdownNow();
        }

        if (!cadeiaAindaValida) {
            desfazerAlteracoes(pilhaRollback, documento, contexto, registros);
        }

        boolean sucesso = registros.stream()
                .noneMatch(r -> r.getStatus() == StatusValidacaoEtapa.FALHA
                        || r.getStatus() == StatusValidacaoEtapa.ERRO
                        || r.getStatus() == StatusValidacaoEtapa.TIMEOUT);

        return new ResultadoCadeiaValidacao(sucesso, circuitoAberto, registros);
    }

    private void desfazerAlteracoes(Deque<ValidadorDocumento> pilhaRollback,
                                    DocumentoFiscal documento,
                                    ContextoValidacao contexto,
                                    List<RegistroResultadoValidador> registros) {
        while (!pilhaRollback.isEmpty()) {
            ValidadorDocumento validador = pilhaRollback.pop();
            if (validador instanceof ValidadorComRollback) {
                try {
                    ((ValidadorComRollback) validador).desfazer(documento, contexto);
                    registros.add(new RegistroResultadoValidador(
                            validador.nome() + " (rollback)",
                            StatusValidacaoEtapa.SUCESSO,
                            "Rollback executado com sucesso"));
                } catch (Exception ex) {
                    registros.add(new RegistroResultadoValidador(
                            validador.nome() + " (rollback)",
                            StatusValidacaoEtapa.ERRO,
                            "Falha ao executar rollback: " + ex.getMessage()));
                }
            }
        }
    }

    private boolean devePular(Set<String> validadoresQueFalharam, EtapaValidacao etapa) {
        for (String validadorFalho : validadoresQueFalharam) {
            Set<String> dependentes = regrasPuloPosFalha.get(validadorFalho);
            if (dependentes != null && dependentes.contains(etapa.validador.nome())) {
                return true;
            }
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class EtapaValidacao {
        private final ValidadorDocumento validador;
        private final Duration timeout;
        private final boolean executarSomenteSeCadeiaInteiraValida;

        private EtapaValidacao(ValidadorDocumento validador,
                               Duration timeout,
                               boolean executarSomenteSeCadeiaInteiraValida) {
            this.validador = validador;
            this.timeout = timeout;
            this.executarSomenteSeCadeiaInteiraValida = executarSomenteSeCadeiaInteiraValida;
        }
    }

    public static class Builder {
        private final List<EtapaValidacao> etapas = new ArrayList<>();
        private final Map<String, Set<String>> regrasPuloPosFalha = new HashMap<>();
        private int limiteFalhasCircuitBreaker = 3;

        public Builder adicionarEtapa(ValidadorDocumento validador, Duration timeout) {
            return adicionarEtapa(validador, timeout, false);
        }

        public Builder adicionarEtapa(ValidadorDocumento validador, Duration timeout, boolean somenteSeAnteriorSucesso) {
            etapas.add(new EtapaValidacao(validador, timeout, somenteSeAnteriorSucesso));
            return this;
        }

        public Builder pularSeFalhar(String validadorDependencia, String validadorASerPulado) {
            regrasPuloPosFalha.computeIfAbsent(validadorDependencia, chave -> new HashSet<>())
                    .add(validadorASerPulado);
            return this;
        }

        public Builder limiteFalhasCircuitBreaker(int limite) {
            this.limiteFalhasCircuitBreaker = limite;
            return this;
        }

        public CadeiaValidacaoDocumentos construir() {
            return new CadeiaValidacaoDocumentos(new ArrayList<>(etapas),
                    new HashMap<>(regrasPuloPosFalha),
                    limiteFalhasCircuitBreaker);
        }
    }
}


