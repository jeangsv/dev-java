package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class AcervoEmMemoria implements AcervoService {

    private final Map<String, Item> itensPorCodigo = new LinkedHashMap<>();
    private final Map<String, Exemplar> exemplaresPorTombo = new HashMap<>();

    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");
        exigirTextoPreenchido(autoria, "autoria");
        exigirTextoPreenchido(categoria, "categoria");

        String codigoFormatado = codigo.trim();
        if (itensPorCodigo.containsKey(codigoFormatado)) {
            throw new RecursoDuplicadoException("Já existe item com o código " + codigoFormatado);
        }

        Item novoItem = new Item(codigoFormatado, titulo, autoria, categoria, ano);
        itensPorCodigo.put(codigoFormatado, novoItem);
    }

    @Override
    public ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException {
        if (codigo == null || codigo.isBlank()) {
            throw new RecursoNaoEncontradoException("Código do item não pode ser nulo ou vazio.");
        }
        Item item = itensPorCodigo.get(codigo.trim());
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigo);
        }
        return paraView(item);
    }

    @Override
    public List<ItemView> listarItens() {
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itensPorCodigo.values()) {
            resultado.add(paraView(item));
        }
        resultado.sort(Comparator.comparing(ItemView::titulo, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(resultado);
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {
        if (trecho == null) {
            throw new DadosInvalidosException("O trecho de busca não pode ser nulo.");
        }
        String termo = trecho.trim().toLowerCase();
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itensPorCodigo.values()) {
            if (item.getTitulo().toLowerCase().contains(termo)) {
                resultado.add(paraView(item));
            }
        }
        resultado.sort(Comparator.comparing(ItemView::titulo, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(resultado);
    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {
        throw new UnsupportedOperationException("Entrega 2: implementar buscarPorCategoria");
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException {

        exigirTextoPreenchido(codigoDoItem, "codigoDoItem");
        exigirTextoPreenchido(tombo, "tombo");

        String codigoFormatado = codigoDoItem.trim();
        String tomboFormatado = tombo.trim();

        Item item = itensPorCodigo.get(codigoFormatado);
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigoDoItem);
        }

        if (exemplaresPorTombo.containsKey(tomboFormatado)) {
            throw new RecursoDuplicadoException("Já existe exemplar cadastrado com o tombo " + tomboFormatado);
        }

        Exemplar novoExemplar = new Exemplar(tomboFormatado, item);
        exemplaresPorTombo.put(tomboFormatado, novoExemplar);
        item.adicionarExemplar(novoExemplar);
    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem)
            throws RecursoNaoEncontradoException {

        if (codigoDoItem == null || codigoDoItem.isBlank()) {
            throw new RecursoNaoEncontradoException("Código do item não pode ser nulo ou vazio.");
        }

        Item item = itensPorCodigo.get(codigoDoItem.trim());
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigoDoItem);
        }

        List<ExemplarView> resultado = new ArrayList<>();
        for (Exemplar exemplar : item.getExemplares()) {
            resultado.add(new ExemplarView(exemplar.getTombo(), item.getCodigo(), exemplar.getStatus()));
        }
        return Collections.unmodifiableList(resultado);
    }

    @Override
    public void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar baixarExemplar");
    }

    private ItemView paraView(Item item) {
        return new ItemView(
                item.getCodigo(),
                item.getTitulo(),
                item.getAutoria(),
                item.getCategoria(),
                item.getAno(),
                item.getTotalDeExemplares(),
                item.getExemplaresDisponiveis());
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    List<Item> itens() {
        return Collections.unmodifiableList(new ArrayList<>(itensPorCodigo.values()));
    }
}
