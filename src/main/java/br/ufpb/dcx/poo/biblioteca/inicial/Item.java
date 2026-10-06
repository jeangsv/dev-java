package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

/**
 * Entidade de domínio representando um item do acervo (livro, mídia, etc.).
 * Protege suas invariantes no construtor e encapsula sua lista de exemplares.
 */
public class Item {

    public static final int ANO_MINIMO_PUBLICACAO = 1450; // Advento da imprensa moderna

    private final String codigo;
    private String titulo;
    private String autoria;
    private String categoria;
    private int ano;
    private final List<Exemplar> exemplares = new ArrayList<>();

    public Item(String codigo, String titulo, String autoria, String categoria, int ano) {
        if (codigo == null || codigo.isBlank()) {
            throw new DadosInvalidosException("O código do item é obrigatório.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new DadosInvalidosException("O título do item é obrigatório.");
        }
        if (autoria == null || autoria.isBlank()) {
            throw new DadosInvalidosException("A autoria do item é obrigatória.");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new DadosInvalidosException("A categoria do item é obrigatória.");
        }
        int anoMaximo = Year.now().getValue() + 1;
        if (ano < ANO_MINIMO_PUBLICACAO || ano > anoMaximo) {
            throw new DadosInvalidosException("O ano de publicação deve estar entre " + ANO_MINIMO_PUBLICACAO + " e " + anoMaximo + ".");
        }

        this.codigo = codigo.trim();
        this.titulo = titulo.trim();
        this.autoria = autoria.trim();
        this.categoria = categoria.trim();
        this.ano = ano;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new DadosInvalidosException("O título não pode ser vazio.");
        }
        this.titulo = titulo.trim();
    }

    public String getAutoria() {
        return autoria;
    }

    public void setAutoria(String autoria) {
        if (autoria == null || autoria.isBlank()) {
            throw new DadosInvalidosException("A autoria não pode ser vazia.");
        }
        this.autoria = autoria.trim();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new DadosInvalidosException("A categoria não pode ser vazia.");
        }
        this.categoria = categoria.trim();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        int anoMaximo = Year.now().getValue() + 1;
        if (ano < ANO_MINIMO_PUBLICACAO || ano > anoMaximo) {
            throw new DadosInvalidosException("O ano de publicação deve estar entre " + ANO_MINIMO_PUBLICACAO + " e " + anoMaximo + ".");
        }
        this.ano = ano;
    }

    public void adicionarExemplar(Exemplar exemplar) {
        if (exemplar == null) {
            throw new DadosInvalidosException("Exemplar não pode ser nulo.");
        }
        this.exemplares.add(exemplar);
    }

    public List<Exemplar> getExemplares() {
        return Collections.unmodifiableList(new ArrayList<>(exemplares));
    }

    public int getTotalDeExemplares() {
        return exemplares.size();
    }

    public int getExemplaresDisponiveis() {
        int disponiveis = 0;
        for (Exemplar e : exemplares) {
            if (e.getStatus() == StatusExemplar.DISPONIVEL) {
                disponiveis++;
            }
        }
        return disponiveis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(codigo, item.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Item{" +
                "codigo='" + codigo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autoria='" + autoria + '\'' +
                ", categoria='" + categoria + '\'' +
                ", ano=" + ano +
                ", totalExemplares=" + exemplares.size() +
                '}';
    }
}
