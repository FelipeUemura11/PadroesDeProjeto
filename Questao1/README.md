# Questão 1

Visão geral: o programa calcula métricas de risco diferentes a partir dos mesmos parâmetros de carteira.

Decisões de design:
- Mantive `RiskCalculator` responsável apenas por orquestrar a estratégia atual e expor o contexto. Isso evita acoplamento com fórmulas específicas.
- `RiskContext` encapsula os parâmetros de entrada como um mapa imutável aos consumidores, garantindo coesão e facilitando testes.

Padrões aplicados:
- Strategy (`RiskStrategy` e implementações) permite trocar o algoritmo de risco em tempo de execução sem alterar o cliente. O problema pede flexibilidade para rodar Value at Risk, Expected Shortfall e Stress Testing sobre o mesmo conjunto de dados.

Princípios SOLID usados:
- SRP: cada classe trata de uma responsabilidade clara (contexto, cálculo, estratégias).
- OCP: novas estratégias podem ser adicionadas sem modificar `RiskCalculator`.
- LSP: todas as estratégias respeitam o contrato de `RiskStrategy`.
- ISP: a interface tem apenas o método necessário para cálculo.
- DIP: `RiskCalculator` trabalha contra a abstração `RiskStrategy`, não contra implementações concretas.
