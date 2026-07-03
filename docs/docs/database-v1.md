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

Usuario
Empresa
EmpresaUsuario
Freelancer


Usuario
- id
- email
- senha
- status
- ultimoLogin
- createdAt
- updatedAt

Empresa
- id
- nomeFantasia
- razaoSocial
- cnpj
- telefone
- descricao
- verificada
- status
- notaMedia
- createdAt
- updatedAt

EmpresaUsuario
- id
- empresaId
- usuarioId
- role
- status
- createdAt

Freelancer
- id
- usuarioId
- nomeCompleto
- cpf
- telefone
- bio
- disponivel
- notaMedia
- createdAt
- updatedAt

- Usuario 1 ─── 0..1 Freelancer

Usuario 1 ─── 0..N EmpresaUsuario

Empresa 1 ─── 0..N EmpresaUsuario


# OpenFree Database v1.0

## Objetivo

Modelar a base de dados inicial da OpenFree para suportar cadastro, empresas, freelancers, vagas, candidaturas, contratos, avaliações e permissões.

---

## Entidades principais

1. Usuario
2. Empresa
3. EmpresaUsuario
...

---

## Bloco 2 — Marketplace e Candidaturas

### Categoria

- id
- nome
- descricao
- ativa
- createdAt
- updatedAt

### Vaga

- id
- empresaId
- categoriaId
- titulo
- descricao
- requisitos
- cidade
- estado
- valor
- quantidadePessoas
- dataServico
- horarioInicio
- horarioFim
- formaPagamento
- status
- createdAt
- updatedAt

### Candidatura

- id
- vagaId
- freelancerId
- mensagem
- valorProposto
- status
- empresaVisualizou
- createdAt
- updatedAt

---

## Relacionamentos

Empresa 1 ─── 0..N Vaga

Categoria 1 ─── 0..N Vaga

Vaga 1 ─── 0..N Candidatura

Freelancer 1 ─── 0..N Candidatura

---

## Status

### Vaga

- RASCUNHO
- PUBLICADA
- EM_ANDAMENTO
- FINALIZADA
- CANCELADA
- ARQUIVADA

### Candidatura

- PENDENTE
- VISUALIZADA
- ACEITA
- RECUSADA
- CANCELADA

---

## Regras de Negócio

1. Apenas empresas podem criar vagas.
2. Uma vaga pertence a uma empresa.
3. Um freelancer não pode se candidatar duas vezes à mesma vaga.

...


## ERD — OpenFree v1.0

```mermaid
erDiagram
    USUARIO ||--o| FREELANCER : possui
    USUARIO ||--o{ EMPRESA_USUARIO : participa
    EMPRESA ||--o{ EMPRESA_USUARIO : possui
    EMPRESA ||--o{ VAGA : publica
    CATEGORIA ||--o{ VAGA : classifica
    VAGA ||--o{ CANDIDATURA : recebe
    FREELANCER ||--o{ CANDIDATURA : envia

    USUARIO {
        long id
        string email
        string senha
        string status
        datetime ultimoLogin
        datetime createdAt
        datetime updatedAt
    }

    EMPRESA {
        long id
        string nomeFantasia
        string razaoSocial
        string cnpj
        string telefone
        boolean verificada
        string status
        decimal notaMedia
    }

    EMPRESA_USUARIO {
        long id
        long empresaId
        long usuarioId
        string role
        string status
    }

    FREELANCER {
        long id
        long usuarioId
        string nomeCompleto
        string cpf
        string telefone
        boolean disponivel
        decimal notaMedia
    }

    CATEGORIA {
        long id
        string nome
        boolean ativa
    }

    VAGA {
        long id
        long empresaId
        long categoriaId
        string titulo
        decimal valor
        string status
        date dataServico
        time horarioInicio
        time horarioFim
    }

    CANDIDATURA {
        long id
        long vagaId
        long freelancerId
        string status
        decimal valorProposto
        boolean empresaVisualizou
    }




