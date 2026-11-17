package br.com.roteiro6.repository;

import br.com.roteiro6.model.Autor;
import br.com.roteiro6.model.Categoria;
import br.com.roteiro6.model.Livro;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HibernateLivroRepositoryTest {

    private static SessionFactory sessionFactory;
    private static LivroRepository repositorio;

    @BeforeAll
    static void iniciar() {
        Configuration configuracao = new Configuration();
        configuracao.setProperty("jakarta.persistence.jdbc.url", "jdbc:h2:mem:biblioteca6;DB_CLOSE_DELAY=-1");
        configuracao.setProperty("jakarta.persistence.jdbc.user", "sa");
        configuracao.setProperty("jakarta.persistence.jdbc.password", "");
        configuracao.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        configuracao.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuracao.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuracao.setProperty("hibernate.show_sql", "true");
        configuracao.addAnnotatedClass(Autor.class);
        configuracao.addAnnotatedClass(Categoria.class);
        configuracao.addAnnotatedClass(Livro.class);
        sessionFactory = configuracao.buildSessionFactory();
        repositorio = new HibernateLivroRepository(sessionFactory);
    }

    @AfterAll
    static void fechar() {
        sessionFactory.close();
    }

    @AfterEach
    void limpar() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("delete from Livro").executeUpdate();
            session.createMutationQuery("delete from Autor").executeUpdate();
            session.createMutationQuery("delete from Categoria").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void persisteLivroECarregaRelacionamentos() {
        Autor autor = new Autor("Machado de Assis");
        Categoria categoria = new Categoria("Romance");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(autor);
            session.persist(categoria);
            transaction.commit();
        }

        Livro livro = repositorio.salvar(new Livro("Dom Casmurro", "9780001", autor, categoria));

        assertNotNull(livro.getId());
        Livro carregado = repositorio.buscarPorId(livro.getId()).orElseThrow();
        assertEquals("Machado de Assis", carregado.getAutor().getNome());
        assertEquals("Romance", carregado.getCategoria().getNome());
    }

    @Test
    void fazRollbackQuandoRelacionamentoObrigatorioEstaAusente() {
        Livro semCategoria = new Livro("Livro inválido", "000", new Autor("Autor"), null);

        assertThrows(RuntimeException.class, () -> repositorio.salvar(semCategoria));
    }
}
