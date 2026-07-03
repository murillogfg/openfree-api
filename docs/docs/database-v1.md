# OpenFree Database v1.0

## Objetivo

Modelar a base de dados inicial da OpenFree para suportar cadastro, empresas, freelancers, vagas, candidaturas, contratos, avaliações e permissões.

---

## Entidades principais

1. Usuario
2. Empresa
3. EmpresaUsuario
4. Role
5. Permission
6. Freelancer
7. Vaga
8. Categoria
9. Candidatura
10. Contrato
11. Pagamento
12. Avaliacao
13. Endereco
14. Notificacao

---

## Fluxo principal

Empresa cria vaga → Freelancer se candidata → Empresa aceita → Contrato é criado → Serviço é finalizado → Avaliação é feita.
