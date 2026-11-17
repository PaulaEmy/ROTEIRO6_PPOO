package br.com.roteiro6.repository;

import br.com.roteiro6.model.Livro;
import br.com.roteiro6.model.Autor;
import br.com.roteiro6.model.Categoria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateLivroRepository implements LivroRepository {

    private final SessionFactory sessionFactory;

    public HibernateLivroRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Livro salvar(Livro livro) {
        return executarTransacao(session -> {
            if (livro.getAutor() == null || livro.getCategoria() == null) {
                throw new IllegalArgumentException("Autor e categoria são obrigatórios.");
            }
            Autor autor = session.find(Autor.class, livro.getAutor().getId());
            Categoria categoria = session.find(Categoria.class, livro.getCategoria().getId());
            if (autor == null || categoria == null) {
                throw new IllegalArgumentException("Autor ou categoria inexistente.");
            }
            livro.setAutor(autor);
            livro.setCategoria(categoria);
            session.persist(livro);
            session.flush();
            return livro;
        });
    }

    @Override
    public Livro atualizar(Livro livro) {
        return executarTransacao(session -> {
            Livro atualizado = session.merge(livro);
            session.flush();
            return atualizado;
        });
    }

    @Override
    public void remover(Long id) {
        executarTransacao(session -> {
            Livro livro = session.find(Livro.class, id);
            if (livro != null) {
                session.remove(livro);
                session.flush();
            }
            return null;
        });
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Livro livro = session.createQuery(
                    "select l from Livro l join fetch l.autor join fetch l.categoria where l.id = :id",
                    Livro.class
            ).setParameter("id", id).uniqueResult();
            return Optional.ofNullable(livro);
        }
    }

    @Override
    public List<Livro> listarComAssociacoes() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select distinct l from Livro l join fetch l.autor join fetch l.categoria order by l.titulo",
                    Livro.class
            ).getResultList();
        }
    }

    @Override
    public List<Livro> listarPaginado(int pagina, int tamanhoPagina) {
        if (pagina < 0 || tamanhoPagina < 1) {
            throw new IllegalArgumentException("Página e tamanho devem ser positivos.");
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select distinct l from Livro l join fetch l.autor join fetch l.categoria order by l.titulo",
                    Livro.class
            ).setFirstResult(pagina * tamanhoPagina).setMaxResults(tamanhoPagina).getResultList();
        }
    }

    private <T> T executarTransacao(Operacao<T> operacao) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T resultado = operacao.executar(session);
            transaction.commit();
            return resultado;
        } catch (RuntimeException erro) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw erro;
        }
    }

    @FunctionalInterface
    private interface Operacao<T> {
        T executar(Session session);
    }
}
