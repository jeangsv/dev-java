package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.Objects;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

/**
 * A cópia física de um item. O que se empresta é o exemplar, não o item.
 * Protege suas invariantes no construtor.
 */
public class Exemplar {

    private final String tombo;
    private final Item item;
    private StatusExemplar status;

    public Exemplar(String tombo, Item item) {
        if (tombo == null || tombo.isBlank()) {
            throw new DadosInvalidosException("O tombo do exemplar é obrigatório.");
        }
        if (item == null) {
            throw new DadosInvalidosException("O item associado ao exemplar é obrigatório.");
        }
        this.tombo = tombo.trim();
        this.item = item;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public String getTombo() {
        return tombo;
    }

    public Item getItem() {
        return item;
    }

    public StatusExemplar getStatus() {
        return status;
    }

    public void setStatus(StatusExemplar status) {
        if (status == null) {
            throw new DadosInvalidosException("O status do exemplar não pode ser nulo.");
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemplar exemplar = (Exemplar) o;
        return Objects.equals(tombo, exemplar.tombo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tombo);
    }

    @Override
    public String toString() {
        return "Exemplar{" +
                "tombo='" + tombo + '\'' +
                ", itemCodigo=" + item.getCodigo() +
                ", status=" + status +
                '}';
    }
}
