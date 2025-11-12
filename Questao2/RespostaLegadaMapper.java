package Questao2;

import java.util.Map;

/**
 * Tradução de respostas legadas para o modelo atual transparente ao domínio moderno.
 * Mantido focado para facilitar evolução e centralizar regras de interpretação.
 */
final class RespostaLegadaMapper {

    Autorizacao mapear(Map<String, Object> respostaLegada) {
        int statusCodigo = (Integer) respostaLegada.getOrDefault("statusCodigo", 500);
        boolean autorizada = statusCodigo == 200;

        String mensagem = (String) respostaLegada.getOrDefault("mensagem", "Resposta desconhecida");
        double valor = ((Number) respostaLegada.getOrDefault("valorAutorizado", 0.0)).doubleValue();
        int codigoMoeda = (Integer) respostaLegada.getOrDefault("codigoMoeda", 3);

        return new Autorizacao(
                autorizada,
                mensagem,
                valor,
                ConversorMoeda.paraMoedaModerna(codigoMoeda)
        );
    }
}

