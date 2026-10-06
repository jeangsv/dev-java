package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

/** Testes unitários do gerenciamento de usuários. */
class UsuarioTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaVazia() {
        biblioteca = Fabrica.novaBiblioteca();
    }

    @Test
    @DisplayName("usuário cadastrado começa ativo e sem empréstimos")
    void cadastrarEBuscar() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        UsuarioView usuario = biblioteca.usuarios().buscarUsuario("2026001");

        assertEquals("Ana Souza", usuario.nome());
        assertTrue(usuario.ativo());
        assertEquals(0, usuario.emprestimosAtivos());
    }

    @Test
    @DisplayName("matrícula repetida é recusada")
    void matriculaDuplicada() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.usuarios().cadastrarUsuario("2026001", "Outro Nome"));
    }

    @Test
    @DisplayName("buscar usuário inexistente lança RecursoNaoEncontradoException")
    void buscarInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.usuarios().buscarUsuario("0000"));
    }

    @Test
    @DisplayName("nome em branco é entrada inválida")
    void nomeEmBranco() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.usuarios().cadastrarUsuario("2026001", "   "));
    }

    @Test
    @DisplayName("matrícula em branco é entrada inválida")
    void matriculaEmBranco() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.usuarios().cadastrarUsuario("   ", "Carlos"));
    }

    @Test
    @DisplayName("listar devolve os usuários ordenados por nome")
    void listarOrdenado() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("3", "Carlos");
        biblioteca.usuarios().cadastrarUsuario("1", "Ana");
        biblioteca.usuarios().cadastrarUsuario("2", "Bruno");

        List<UsuarioView> usuarios = biblioteca.usuarios().listarUsuarios();

        assertEquals(3, usuarios.size());
        assertEquals("Ana", usuarios.get(0).nome());
        assertEquals("Bruno", usuarios.get(1).nome());
        assertEquals("Carlos", usuarios.get(2).nome());
    }

    @Test
    @DisplayName("encapsulamento: lista de usuários retornada é imutável")
    void encapsulamentoListaUsuarios() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("1", "Ana");
        List<UsuarioView> lista = biblioteca.usuarios().listarUsuarios();
        assertThrows(UnsupportedOperationException.class, () -> lista.clear());
        assertEquals(1, biblioteca.usuarios().listarUsuarios().size());
    }
}
