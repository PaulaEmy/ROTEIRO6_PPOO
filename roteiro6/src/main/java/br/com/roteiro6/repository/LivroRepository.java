package br.com.roteiro6.repository;

import br.com.roteiro6.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    Livro salvar(Livro livro);
    Livro atualizar(Livro livro);
    void remover(Long id);
    Optional<Livro> buscarPorId(Long id);
    List<Livro> listarComAssociacoes();
    List<Livro> listarPaginado(int pagina, int tamanhoPagina);
}
