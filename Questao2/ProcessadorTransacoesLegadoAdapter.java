package Questao2;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter que converte o contrato moderno nas expectativas do legado.
 * Aplica o padrão Adapter para proteger o domínio atual de mudanças legadas.
 * Depende de abstrações (ProcessadorTransacoes) e usa composição (DIP).
 */
public class ProcessadorTransacoesLegadoAdapter implements ProcessadorTransacoes {

    private final SistemaBancarioLegado legado;
    private final SolicitacaoLegadaMapper solicitacaoMapper;
    private final RespostaLegadaMapper respostaMapper;

    public ProcessadorTransacoesLegadoAdapter(SistemaBancarioLegado legado) {
        this.legado = legado;
        this.solicitacaoMapper = new SolicitacaoLegadaMapper();
        this.respostaMapper = new RespostaLegadaMapper();
    }

    @Override
    public Autorizacao autorizar(String cartao, double valor, String moeda) {
        HashMap<String, Object> parametros = solicitacaoMapper.mapear(cartao, valor, moeda);
        Map<String, Object> respostaLegada = legado.processarTransacao(parametros);
        return respostaMapper.mapear(respostaLegada);
    }
}

