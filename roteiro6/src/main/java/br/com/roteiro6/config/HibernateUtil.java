package br.com.roteiro6.config;

import io.github.cdimascio.dotenv.Dotenv;
import br.com.roteiro6.model.Autor;
import br.com.roteiro6.model.Categoria;
import br.com.roteiro6.model.Livro;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY =
            criarSessionFactory();

    private static SessionFactory criarSessionFactory() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Configuration configuracao = new Configuration();
            configuracao.configure();
            configuracao.addAnnotatedClass(Autor.class);
            configuracao.addAnnotatedClass(Categoria.class);
            configuracao.addAnnotatedClass(Livro.class);

            configuracao.setProperty("hibernate.connection.url", valorConfiguracao(dotenv, "DB_URL"));
            configuracao.setProperty("hibernate.connection.username", valorConfiguracao(dotenv, "DB_USER"));
            configuracao.setProperty("hibernate.connection.password", valorConfiguracao(dotenv, "DB_PASSWORD"));
            return configuracao.buildSessionFactory();
        } catch (Exception erro) {
            throw new IllegalStateException(
                    "Falha ao iniciar a persistência. Confira .env (ou variáveis DB_URL, DB_USER e DB_PASSWORD).",
                    erro
            );
        }
    }

    private static String valorConfiguracao(Dotenv dotenv, String chave) {
        String valor = System.getenv(chave);
        if (valor == null || valor.isBlank()) {
            valor = dotenv.get(chave);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configuração obrigatória ausente: " + chave);
        }
        return valor;
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void fechar() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
