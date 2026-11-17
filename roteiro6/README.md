# Roteiro 6 - Biblioteca com Hibernate

Projeto Maven/Java que demonstra persistencia de livros, autores e categorias com Hibernate 7.

## Pre-requisitos

- JDK 25
- Maven
- MySQL para a execucao principal

## Configuracao

1. Copie `.env.example` para `.env`.
2. Informe no `.env` a URL, usuario e senha do MySQL.
3. Crie o banco de dados:

```sql
CREATE DATABASE biblioteca6 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

As mesmas configuracoes podem ser fornecidas pelas variaveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. O `.env` e ignorado pelo Git.

## Execucao

```bash
mvn compile
mvn exec:java -Dexec.mainClass=br.com.roteiro6.Main
```

O Maven pode iniciar o programa pelas configuracoes registradas no projeto; `Main` consulta livros pela camada de repositorio e nao acessa diretamente `Session`.

## Testes

```bash
mvn test
```

Os testes usam H2 em memoria e verificam:

- inclusao de livro e carregamento de autor/categoria;
- rejeicao de livro sem categoria.

## Estrutura Hibernate

- `HibernateUtil` cria uma unica `SessionFactory` no carregamento da classe.
- `HibernateLivroRepository` delimita sessao e transacao em cada operacao.
- A operacao commitada ocorre apos `flush`; falhas fazem rollback e fechamento da sessao.
- `listarComAssociacoes` usa `JOIN FETCH` para evitar LazyInitializationException fora da sessao.
- `listarPaginado` demonstra paginação por `setFirstResult` e `setMaxResults`.
- `@Version` habilita concorrencia otimista em `Livro`.

Não registre credenciais em `hibernate.cfg.xml` nem no código-fonte.
