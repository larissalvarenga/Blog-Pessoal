package br.org.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.org.generation.blogpessoal.model.Postagem;
import br.org.generation.blogpessoal.repository.PostagemRepository;

@RestController
@RequestMapping("/postagens") 
@CrossOrigin(origins = "*", allowedHeaders = "*") //libera o acesso do front no back
public class PostagemController {
	
		
	@Autowired //Cria uma injeção de dependencia - trasnfere responsabilidade para o Spring
	private PostagemRepository postagemRepository;
	
	@GetMapping //Cria uma injeção de dependencia - trasnfere responsabilidade para o Spring
	public ResponseEntity<List<Postagem>> getAll () //ResponseEntity - classe responsável por responder nossa requisição
	{
		return ResponseEntity.ok(postagemRepository.findAll()); 
	}
	
	//criação de uma variavel de caminho: "/[variavel]"
	//@PathVariable: pega do caminho e retorna para
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable long id) 
	{
		return postagemRepository.findById(id)
			.map(resposta -> ResponseEntity.ok(resposta))
			.orElse(ResponseEntity.notFound().build());
	}
	
	//Consulta por titulo
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo)
	{
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	//Cria uma nova postagem
	@PostMapping
	public ResponseEntity<Postagem> postPostagem (@RequestBody Postagem postagem)
	{
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
	}
	
	//Atualiza, caso o recurso não exista retorna um notFound
	@PutMapping
	public ResponseEntity<Postagem> putPostagem (@RequestBody Postagem postagem)
	{
		return postagemRepository.findById(postagem.getId())
                .map(resposta -> ResponseEntity.ok(postagemRepository.save(postagem)))
                .orElse(ResponseEntity.notFound().build());
	}
			
	//Deleta um id que existe, caso o recurso não exista retorna um notFound
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletaPostagem(@PathVariable long id) 
	{
		 return  postagemRepository.findById(id)
				 .map(resposta -> {
			        	postagemRepository.deleteById(id);
			            return ResponseEntity.noContent().build();
			        })
				 .orElse(ResponseEntity.notFound().build());
	}	

}