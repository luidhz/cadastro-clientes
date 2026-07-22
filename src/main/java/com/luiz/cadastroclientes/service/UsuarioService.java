package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entities.Usuario;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario insert(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new DatabaseException("Já existe um usuario com esse email");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario findById(Long id) {
        Optional<Usuario> cliente = usuarioRepository.findById(id);
        return cliente.orElseThrow(() -> new EntityNotFoundException(
                "entidade não encontrada com id: " + id));
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "entidade não encontrada com email: " + email));
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public void updateData(Usuario entidade, Usuario usuario) {
        entidade.setNome(usuario.getNome());
        entidade.setIdade(usuario.getIdade());
        entidade.setEmail(usuario.getEmail());

        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            entidade.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
    }

    public Usuario update(Long id, Usuario usuario){
        try {
            Usuario entidade = usuarioRepository.getReferenceById(id);
            updateData(entidade, usuario);
            return usuarioRepository.save(entidade);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("entidade não encontrada com id: " + id);
        }
    }

    public void delete(Long id) {
        try {
            usuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}