package br.com.roteiro6;

import br.com.roteiro6.config.HibernateUtil;
import br.com.roteiro6.model.Livro;
import br.com.roteiro6.repository.HibernateLivroRepository;
import br.com.roteiro6.repository.LivroRepository;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        LivroRepository repositorio = new HibernateLivroRepository(HibernateUtil.getSessionFactory());
        try {
            List<Livro> livros = repositorio.listarComAssociacoes();
            for (Livro livro : livros) {
                System.out.println("--------------------------");
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("ISBN: " + livro.getIsbn());
                System.out.println("Autor: " + livro.getAutor().getNome());
                System.out.println("Categoria: " + livro.getCategoria().getNome());
            }
        } catch (Exception e) {
            System.err.println("Não foi possível consultar os livros: " + e.getMessage());
        } finally {
            HibernateUtil.fechar();
        }
    }
}