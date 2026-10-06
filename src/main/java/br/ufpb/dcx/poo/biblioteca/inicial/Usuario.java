package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.Objects;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

/**
 * Entidade de domínio representando um Usuário da biblioteca.
 * Protege suas invariantes no construtor.
 */
public class Usuario {

    private final String matricula;
    private String nome;
    private boolean ativo;
    private int emprestimosAtivos;

    public Usuario(String matricula, String nome) {
        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }
        this.matricula = matricula;
        this.nome = nome;
        this.ativo = true;
        this.emprestimosAtivos = 0;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void setEmprestimosAtivos(int emprestimosAtivos) {
        if (emprestimosAtivos < 0) {
            throw new DadosInvalidosException("Número de empréstimos ativos não pode ser negativo.");
        }
        this.emprestimosAtivos = emprestimosAtivos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(matricula, usuario.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "matricula='" + matricula + '\'' +
                ", nome='" + nome + '\'' +
                ", ativo=" + ativo +
                ", emprestimosAtivos=" + emprestimosAtivos +
                '}';
    }
}
