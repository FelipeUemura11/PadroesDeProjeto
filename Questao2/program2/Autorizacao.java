package Questao2.program2;

/**
 * DTO moderno que representa o resultado da autorização no contexto atual.
 * Mantemos somente dados relevantes para o domínio contemporâneo.
 */
public record Autorizacao(boolean autorizada, String mensagem, double valor, String moeda) {
}

