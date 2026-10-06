package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class UsuariosEmMemoria implements UsuarioService {

    private final Map<String, Usuario> usuariosPorMatricula = new LinkedHashMap<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome)
            throws RecursoDuplicadoException {

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }

        String matriculaFormatada = matricula.trim();
        if (usuariosPorMatricula.containsKey(matriculaFormatada)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matriculaFormatada);
        }

        Usuario usuario = new Usuario(matriculaFormatada, nome.trim());
        usuariosPorMatricula.put(matriculaFormatada, usuario);
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {
        if (matricula == null || matricula.isBlank()) {
            throw new RecursoNaoEncontradoException("Matrícula não pode ser nula ou vazia.");
        }

        Usuario usuario = usuariosPorMatricula.get(matricula.trim());
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }

        return paraView(usuario);
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        for (Usuario usuario : usuariosPorMatricula.values()) {
            resultado.add(paraView(usuario));
        }
        resultado.sort(Comparator.comparing(UsuarioView::nome, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(resultado);
    }

    @Override
    public void desativarUsuario(String matricula)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar desativarUsuario");
    }

    @Override
    public void reativarUsuario(String matricula) throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar reativarUsuario");
    }

    private UsuarioView paraView(Usuario usuario) {
        return new UsuarioView(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.isAtivo(),
                usuario.getEmprestimosAtivos());
    }
}
