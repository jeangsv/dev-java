package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

class AcervoTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaVazia() {
        biblioteca = Fabrica.novaBiblioteca();
    }

    @Test
    @DisplayName("um item cadastrado pode ser recuperado pelo código")
    void cadastrarEBuscar() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        ItemView item = biblioteca.acervo().buscarItem("L1");

        assertEquals("L1", item.codigo());
        assertEquals("Java Efetivo", item.titulo());
        assertEquals("Bloch", item.autoria());
        assertEquals(2019, item.ano());
    }

    @Test
    @DisplayName("item novo começa sem exemplares")
    void itemNovoNaoTemExemplares() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        ItemView item = biblioteca.acervo().buscarItem("L1");

        assertEquals(0, item.totalDeExemplares());
        assertEquals(0, item.exemplaresDisponiveis());
    }

    @Test
    @DisplayName("código repetido é recusado")
    void codigoDuplicado() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Outro", "Outra", "livro", 2020));
    }

    @Test
    @DisplayName("buscar item inexistente lança RecursoNaoEncontradoException")
    void buscarInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.acervo().buscarItem("NAO-EXISTE"));
    }

    @Test
    @DisplayName("código em branco é entrada inválida, não regra de negócio")
    void codigoEmBranco() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("  ", "Título", "Autoria", "livro", 2019));
    }

    @Test
    @DisplayName("listar devolve os itens ordenados por título")
    void listarOrdenado() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Refatoração", "Fowler", "livro", 2004);
        biblioteca.acervo().cadastrarItem("L2", "Código limpo", "Martin", "livro", 2009);
        biblioteca.acervo().cadastrarItem("L3", "Java Efetivo", "Bloch", "livro", 2019);

        List<ItemView> itens = biblioteca.acervo().listarItens();

        assertEquals(3, itens.size());
        assertEquals("Código limpo", itens.get(0).titulo());
        assertEquals("Java Efetivo", itens.get(1).titulo());
        assertEquals("Refatoração", itens.get(2).titulo());
    }

    @Test
    @DisplayName("acervo vazio devolve lista vazia, não null")
    void acervoVazio() {
        assertEquals(List.of(), biblioteca.acervo().listarItens());
    }

    @Test
    @DisplayName("busca de item deve funcionar com instâncias distintas de String (defeito oculto com ==)")
    void buscarItemComNovaInstanciaDeString() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        String codigoDiferenteReferencia = new String("L1");

        ItemView item = biblioteca.acervo().buscarItem(codigoDiferenteReferencia);
        assertEquals("L1", item.codigo());
        assertEquals("Java Efetivo", item.titulo());
    }

    @Test
    @DisplayName("exemplar adicionado entra como DISPONIVEL e conta no item")
    void adicionarExemplar() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");
        biblioteca.acervo().adicionarExemplar("L1", "T-002");

        ItemView item = biblioteca.acervo().buscarItem("L1");
        assertEquals(2, item.totalDeExemplares());
        assertEquals(2, item.exemplaresDisponiveis());

        List<ExemplarView> exemplares = biblioteca.acervo().listarExemplares("L1");
        assertEquals(2, exemplares.size());
        assertEquals(StatusExemplar.DISPONIVEL, exemplares.get(0).status());
        assertEquals("T-001", exemplares.get(0).tombo());
        assertEquals("T-002", exemplares.get(1).tombo());
    }

    @Test
    @DisplayName("tombo é único no acervo inteiro, não apenas dentro do item")
    void tomboDuplicadoEntreItensDiferentes() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().adicionarExemplar("L2", "T-001"));
    }

    @Test
    @DisplayName("tombo duplicado no mesmo item lança RecursoDuplicadoException")
    void tomboDuplicadoNoMesmoItem() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().adicionarExemplar("L1", "T-001"));
    }

    @Test
    @DisplayName("não se adiciona exemplar a item que não existe")
    void exemplarDeItemInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.acervo().adicionarExemplar("NAO-EXISTE", "T-001"));
    }

    @Test
    @DisplayName("listar exemplares de item inexistente lança RecursoNaoEncontradoException")
    void listarExemplaresDeItemInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.acervo().listarExemplares("NAO-EXISTE"));
    }

    @Test
    @DisplayName("busca por título ignora maiúsculas e aceita trecho")
    void buscarPorTitulo() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);

        assertEquals(1, biblioteca.acervo().buscarPorTitulo("efetivo").size());
        assertEquals(1, biblioteca.acervo().buscarPorTitulo("JAVA").size());
    }

    @Test
    @DisplayName("busca sem resultado devolve lista vazia, não exceção")
    void buscarPorTituloSemResultado() {
        assertEquals(List.of(), biblioteca.acervo().buscarPorTitulo("inexistente"));
    }

    @Test
    @DisplayName("busca por título com trecho nulo lança DadosInvalidosException")
    void buscarPorTituloNulo() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().buscarPorTitulo(null));
    }

    @Test
    @DisplayName("validação de entradas inválidas no cadastro de itens")
    void cadastroComCamposInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem(null, "Título", "Autor", "livro", 2020));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", null, "Autor", "livro", 2020));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Título", null, "livro", 2020));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Título", "Autor", null, 2020));
    }

    @Test
    @DisplayName("regra autoral: ano de publicação anterior à imprensa (1450) é rejeitado")
    void anoDePublicacaoInvalidoPassado() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Manuscrito Antigo", "Autor", "livro", 1400));
    }

    @Test
    @DisplayName("regra autoral: ano de publicação no futuro distante é rejeitado")
    void anoDePublicacaoInvalidoFuturo() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().cadastrarItem("L1", "Livro do Futuro", "Autor", "livro", 2150));
    }

    @Test
    @DisplayName("adicionar exemplar com tombo nulo ou vazio lança DadosInvalidosException")
    void adicionarExemplarTomboInvalido() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().adicionarExemplar("L1", null));
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.acervo().adicionarExemplar("L1", "   "));
    }

    @Test
    @DisplayName("encapsulamento: coleções retornadas são imutáveis e protegem o estado interno")
    void encapsulamentoDasListasRetornadas() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().adicionarExemplar("L1", "T-001");

        List<ItemView> itens = biblioteca.acervo().listarItens();
        assertThrows(UnsupportedOperationException.class, () -> itens.clear());

        List<ExemplarView> exemplares = biblioteca.acervo().listarExemplares("L1");
        assertThrows(UnsupportedOperationException.class, () -> exemplares.clear());

        assertEquals(1, biblioteca.acervo().listarItens().size());
        assertEquals(1, biblioteca.acervo().listarExemplares("L1").size());
    }

    @Test
    @DisplayName("atomicidade: falha ao adicionar exemplar duplicado não corrompe o estado")
    void atomicidadeAdicionarExemplar() throws BibliotecaException {
        biblioteca.acervo().cadastrarItem("L1", "Java Efetivo", "Bloch", "livro", 2019);
        biblioteca.acervo().cadastrarItem("L2", "Refatoração", "Fowler", "livro", 2004);

        biblioteca.acervo().adicionarExemplar("L1", "T-001");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.acervo().adicionarExemplar("L2", "T-001"));

        assertEquals(1, biblioteca.acervo().buscarItem("L1").totalDeExemplares());
        assertEquals(0, biblioteca.acervo().buscarItem("L2").totalDeExemplares());
    }
}
