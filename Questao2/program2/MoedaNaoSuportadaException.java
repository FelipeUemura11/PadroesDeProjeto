package Questao2.program2;

/**
 * Exceção dedicada para falhas de conversão, mantendo semântica clara e
 * permitindo tratamento específico em camadas superiores.
 */
class MoedaNaoSuportadaException extends RuntimeException {

    MoedaNaoSuportadaException(String moeda) {
        super("Moeda não suportada: " + moeda);
    }
}

