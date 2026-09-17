# Grupo 3 - Gamificação para Engajamento de Educação Continuada (ATDD)

Projeto Spring Boot desenvolvido para a atividade de ATDD (BDD + TDD) da disciplina.

## 1. Descrição do estudo de caso

Uma plataforma vende cursos online/EAD por assinatura. O aluno paga mensalidade e acessa um
conjunto de cursos. A cada curso finalizado com média acima de 7,0, o aluno ganha o direito de
realizar mais 3 cursos. Alunos que participam do fórum ganham cursos extras; ao acumular 12
cursos, o plano vira "Premium" com vouchers, moedas e acesso a projetos reais.

**US escolhida pelo grupo (a única implementada neste sprint):**

> COMO Aluno de um sistema de gamificação
> QUERO ter média acima de 7,0
> PARA ter o direito de realizar mais 3 cursos

## 2. US e BDDs por integrante

Cada integrante escreveu um cenário Given/When/Then para a mesma US (ver
`src/test/java/.../domain/AlunoTest.java`):

| Integrante | Cenário (Given/When/Then) | Teste |
|---|---|---|
| LETICIA | Curso finalizado com média **acima** de 7,0 → aluno tem direito a +3 cursos | `deveAvaliarMediaValidaParaCursoBonus` |
| LUCAS | Curso finalizado com média **abaixo** de 7,0 → aluno NÃO tem direito | `deveAvaliarMediaInvalidaParaCursoBonus` |
| JOÃO | Curso **não finalizado**, média não lançada → aluno NÃO tem direito | `deveAvaliarFinalizacaoCurso` |
| IZABELLY | Curso finalizado com média **exatamente** 7,0 (caso limite) → aluno tem direito | `deveAvaliarMediaLimiteBonus` |

## 3. Ciclo TDD (RED → GREEN → BLUE)

O arquivo `AlunoTest.java` documenta as 3 etapas do ciclo:

- **RED** (comentado no arquivo): versão original, com dados que não batiam com o cenário
  descrito por cada integrante — os 4 testes falhavam.
- **GREEN** (comentado no arquivo): dados corrigidos para bater com cada BDD — os 4 testes
  passam, mas com código duplicado entre os testes.
- **BLUE** (ativo/rodando): refatorado (métodos auxiliares `novoAluno()`/`novoCurso()`,
  `@DisplayName` em português), sem duplicação, mantendo os 4 testes verdes.

Para reproduzir a evidência de RED: descomente o bloco `ETAPA 1 - RED` e comente o bloco BLUE,
rode `mvn test` e capture o print da falha. O mesmo vale para o GREEN.

Cobertura: os 4 cenários cobrem todos os ramos de `Aluno.temDireitoAMaisCursos()` (curso
finalizado/não finalizado, média acima/abaixo/igual ao limite) — rode com o coverage do
IntelliJ ou `mvn test jacoco:report` para conferir 100%.

## 4. Camadas da aplicação

- `domain` — regras de negócio puras, testadas via TDD (`Aluno`, `Curso`).
- `entity` — mapeamento JPA (`AlunoEntity`, `CursoEntity`).
- `repository` — Spring Data JPA (`AlunoRepository`, `CursoRepository`).
- `dto` — objetos de entrada/saída da API.
- `service` — orquestra persistência e reaplica a regra de negócio do `domain` (evita duplicar
  a condição "média >= 7,0").
- `controller` — API REST documentada com Swagger/OpenAPI.

## 5. Como rodar localmente (H2, sem Docker)

Pré-requisitos: JDK 21 e Maven (ou usar o `./mvnw` incluso).

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080` usando H2 em memória (perfil default).

- Swagger UI: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console
  (JDBC URL: `jdbc:h2:mem:gamificacao`, usuário `sa`, senha em branco)

## 6. Como rodar com Docker + Postgres (+ pgAdmin)

Pré-requisitos: Docker e Docker Compose.

```bash
docker compose up --build
```

Isso sobe 3 containers:

- `gamificacao-postgres` — Postgres 16, banco `gamificacao` (porta `5432`)
- `gamificacao-pgadmin` — pgAdmin (porta `5050`, login `admin@admin.com` / `admin`)
- `gamificacao-backend` — a aplicação Spring Boot com perfil `postgres` (porta `8080`)

Para parar tudo: `docker compose down` (adicione `-v` para apagar também o volume do banco).

## 7. Endpoints principais (ver detalhes/testar no Swagger)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/alunos` | Cria um aluno |
| GET | `/api/alunos` | Lista alunos |
| GET | `/api/alunos/{id}` | Busca aluno (com cursos e direito a bônus) |
| POST | `/api/alunos/{id}/cursos` | Matricula aluno em um novo curso |
| POST | `/api/alunos/{id}/cursos/{cursoId}/finalizar` | Finaliza curso lançando a média |
| GET | `/api/alunos/{id}/direito-bonus` | Verifica se o aluno tem direito a +3 cursos |

## 8. Front-end (Vue) — foco em testar a funcionalidade

Em `frontend/`. É propositalmente simples (sem CSS framework): serve para exercitar manualmente
todos os endpoints (criar aluno, criar curso, finalizar curso com média, ver se ganhou direito
a bônus) e tem um log de chamadas na tela para conferência.

```bash
cd frontend
npm install
npm run dev
```

Abre em `http://localhost:5173`. Requer o backend rodando em `http://localhost:8080`
(local ou via Docker).

## 9. Planilha (BDD/TDD)

A planilha/PDF de backlog usada como base para as US e cenários está versionada junto com este
README (ver arquivo na raiz do projeto).
