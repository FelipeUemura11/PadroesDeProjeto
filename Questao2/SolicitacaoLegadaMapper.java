package Questao2;

import java.util.HashMap;

/**
 * Responsável por preparar o payload esperado pelo legado.
 * Mantido separado para respeitar SRP e evitar que o adapter acumule responsabilidades.
 */
final class SolicitacaoLegadaMapper {

    private static final String CANAL_OPERACAO = "canalOperacao";

    HashMap<String, Object> mapear(String cartao, double valor, String moeda) {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("numeroCartao", cartao);
        parametros.put("valor", valor);
        parametros.put("codigoMoeda", ConversorMoeda.paraCodigoLegado(moeda));

        parametros.put(CANAL_OPERACAO, obterCanalObrigatorio());

        return parametros;
    }

    private String obterCanalObrigatorio() {
        String canal = "E_COMMERCE";
        if (canal == null || canal.isBlank()) {
            throw new CampoObrigatorioLegadoAusenteException(CANAL_OPERACAO);
        }
        return canal;
    }
}

