# OpenFree API Design v1.0

## Objetivo

Definir todos os endpoints REST da OpenFree.

Padrão base:

/api/v1

---

# Authentication

POST /auth/register

Descrição:
Cadastrar um novo usuário.

---

POST /auth/login

Descrição:
Autenticar usuário.

---

POST /auth/refresh

Descrição:
Gerar novo Access Token.

---

POST /auth/logout

Descrição:
Encerrar sessão.

---

# Users

GET /users/me

Retorna o usuário autenticado.

---

PUT /users/me

Atualiza os dados do usuário.

---

DELETE /users/me

Desativa a conta.

---

# Companies

POST /companies

Criar empresa.

---

GET /companies/{id}

Consultar empresa.

---

PUT /companies/{id}

Atualizar empresa.

---

DELETE /companies/{id}

Inativar empresa.

---

GET /companies/{id}/employees

Lista funcionários.

---

POST /companies/{id}/employees

Adicionar funcionário.

---

DELETE /companies/{id}/employees/{employeeId}

Remover funcionário.

---

# Freelancers

POST /freelancers

Criar perfil freelancer.

---

GET /freelancers/{id}

Consultar perfil.

---

PUT /freelancers/{id}

Atualizar perfil.

---

GET /freelancers/{id}/reviews

Listar avaliações.

---

# Jobs

POST /jobs

Criar vaga.

---

GET /jobs

Listar vagas.

---

GET /jobs/{id}

Consultar vaga.

---

PUT /jobs/{id}

Editar vaga.

---

DELETE /jobs/{id}

Cancelar vaga.

---

# Applications

POST /applications

Criar candidatura.

---

GET /applications/my

Minhas candidaturas.

---

PUT /applications/{id}/accept

Aceitar candidatura.

---

PUT /applications/{id}/reject

Recusar candidatura.

---

# Contracts

GET /contracts

Listar contratos.

---

GET /contracts/{id}

Consultar contrato.

---

PUT /contracts/{id}/finish

Finalizar contrato.

---

# Reviews

POST /reviews

Criar avaliação.

---

GET /reviews/company/{id}

Avaliações da empresa.

---

GET /reviews/freelancer/{id}

Avaliações do freelancer.

---

# Notifications

GET /notifications

Listar notificações.

---

PUT /notifications/{id}/read

Marcar como lida.
