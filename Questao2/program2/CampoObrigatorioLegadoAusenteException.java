package Questao2.program2;


class CampoObrigatorioLegadoAusenteException extends RuntimeException {

    CampoObrigatorioLegadoAusenteException(String campo) {
        super("Campo obrigatório do legado ausente: " + campo);
    }
}

