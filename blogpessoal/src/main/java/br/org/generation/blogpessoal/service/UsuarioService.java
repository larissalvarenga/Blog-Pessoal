package br.org.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.org.generation.blogpessoal.model.Usuario;
import br.org.generation.blogpessoal.model.UsuarioLogin;
import br.org.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// Método para CADASTRAR o usuario e verificar se o mesmo já existe.
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}
	
	// Método para ATUALIZAR o usuario e verificar se o mesmo já existe.
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		
		if (usuarioRepository.findById(usuario.getId()).isPresent()) {
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
				return Optional.of(usuarioRepository.save(usuario));
		} 
			
		return Optional.empty();
	}
	
	// Método para Logar/Autenticar o Usuario
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if(usuario.isPresent()) {
			if(compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
				
				String token = gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha());

				usuarioLogin.get().setId(usuario.get().getId());				
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				usuarioLogin.get().setToken(token);
				
				return usuarioLogin;
			}
		}
		
		return Optional.empty();
		
	}
	
	// Método para criptografar a senha
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
			return encoder.encode(senha);
	}
	
	// Método para Comparar as senhas
	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.matches(senhaDigitada, senhaBanco);
	}

	// Método para gerar um Token
	private String gerarBasicToken(String usuario, String senha) {
		
		String tokenBase = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(tokenBase.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);
	}


}
