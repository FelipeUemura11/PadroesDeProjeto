# Questão 4

Arquitetura:
- `CadeiaValidacaoDocumentos` executa os validadores na ordem configurada, acumula logs e aciona rollback quando necessário.
- O builder interno monta a cadeia declarativamente (`adicionarEtapa`, `pularSeFalhar`, `limiteFalhasCircuitBreaker`), permitindo cenários diferentes sem criar subclasses.
- `ContextoValidacao` entrega dependências externas (repositório, horas, cliente SEFAZ) aos validadores, mantendo-os testáveis.

Padrões aplicados:
- Chain of Responsibility: cada etapa tenta validar e sinaliza sucesso/falha. Combina com o problema porque novas verificações entram apenas encadeando outra etapa.
- Builder fluente: simplifica a configuração de cadeias complexas e evita múltiplos construtores com combinações de parâmetros.
- Circuit Breaker embutido: limita chamadas sequenciais com falha para proteger integrações externas.

SOLID:
- SRP: validadores cuidam de uma só regra (schema, certificado, negócio, banco, SEFAZ).
- OCP: para acrescentar validações, basta implementar `ValidadorDocumento` e adicioná-la no builder.
- LSP/ISP: as interfaces são minimalistas (`validar`, `desfazer` quando necessário), sem impor métodos extras.
- DIP: dependências reais chegam por `ContextoValidacao`; a cadeia usa abstrações em vez de instanciar serviços diretamente.