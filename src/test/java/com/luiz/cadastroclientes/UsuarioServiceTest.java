package com.luiz.cadastroclientes;

import com.luiz.cadastroclientes.entities.Usuario;
import com.luiz.cadastroclientes.enums.UsuarioRole;
import com.luiz.cadastroclientes.repository.UsuarioRepository;
import com.luiz.cadastroclientes.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1l, "Brenno", 0, "teste@gmail.com", "senha123", UsuarioRole.ADMIN);
    }

    @Test
    @DisplayName("insert deve salvar usuário com senha criptografada")
    void insertDeveSalvarUsuarioComSenhaCriptografada() {
        when(usuarioRepository.save(usuario)).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");

        Usuario salvo = usuarioService.insert(usuario);

        assertEquals("senhaCriptografada", salvo.getSenha());
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(usuario);

    }

    @Test
    @DisplayName("findById deve retornar o usuario quando existir")
    void findByIdDeveRetornarUsuarioQuandoExistir() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.findById(1L);

        assertEquals(usuario, encontrado);
    }

    @Test
    @DisplayName("findByEmail deve retornar o usuario quando existir")
    void findByEmailDeveRetornarUsuarioQuandoExistir() {
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.findByEmail("joao@email.com");

        assertEquals(usuario, encontrado);
    }

    @Test
    @DisplayName("findAll deve retornar a lista de usuarios")
    void findAllDeveRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.findAll();

        assertThat(resultado).containsExactly(usuario);
    }

    @Test
    @DisplayName("delete deve remover o usuario quando o id existir")
    void deleteDeveRemoverUsuario() {
        usuarioService.delete(1L);

        verify(usuarioRepository).deleteById(1L);
    }
}
