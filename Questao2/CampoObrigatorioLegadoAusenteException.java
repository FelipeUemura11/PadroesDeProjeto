package Questao2;

/**
 * Exceção para sinalizar ausência de campos exigidos pelo legado.
 * Garante transparência na integração e facilita troubleshooting.
 */
class CampoObrigatorioLegadoAusenteException extends RuntimeException {

    CampoObrigatorioLegadoAusenteException(String campo) {
        super("Campo obrigatório do legado ausente: " + campo);
    }
}

