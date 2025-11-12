package Questao2.program2;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface legado simulada. Mantemos implementação simplificada apenas para ilustrar
 * a integração. Em produção, este código representaria uma dependência externa.
 */
public class SistemaBancarioLegado {

    public Map<String, Object> processarTransacao(HashMap<String, Object> parametros) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("statusCodigo", 200);
        resposta.put("mensagem", "Autorizada");
        resposta.put("valorAutorizado", parametros.get("valor"));
        resposta.put("codigoMoeda", parametros.get("codigoMoeda"));
        return resposta;
    }
}

