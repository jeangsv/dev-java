package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;

/**
 * Verifica o ponto de entrada do contrato.
 * Garante que a fábrica instancia serviços independentes sem compartilhamento de estado estático.
 */
class FabricaTest {

    @Test
    @DisplayName("a fábrica devolve uma biblioteca com os quatro serviços")
    void servicosDisponiveis() {
        Biblioteca biblioteca = Fabrica.novaBiblioteca();

        assertNotNull(biblioteca);
        assertNotNull(biblioteca.acervo());
        assertNotNull(biblioteca.usuarios());
        assertNotNull(biblioteca.emprestimos());
        assertNotNull(biblioteca.relatorios());
    }

    @Test
    @DisplayName("cada chamada devolve uma biblioteca independente")
    void instanciasIndependentes() throws BibliotecaException {
        Biblioteca uma = Fabrica.novaBiblioteca();
        Biblioteca outra = Fabrica.novaBiblioteca();

        assertNotSame(uma, outra);

        uma.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        uma.acervo().adicionarExemplar("L1", "T-001");

        assertEquals(1, uma.acervo().listarItens().size());
        assertEquals(1, uma.acervo().listarExemplares("L1").size());
        assertEquals(0, outra.acervo().listarItens().size());

        // A outra biblioteca pode cadastrar o mesmo tombo pois são instâncias isoladas
        outra.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);
        outra.acervo().adicionarExemplar("L2", "T-001");
        assertEquals(1, outra.acervo().listarExemplares("L2").size());
    }
}
