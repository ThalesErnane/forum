package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	// Salvar, alterar e excluir se utiliza a anotação @Transactional
	// https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.5-Release-Notes#hibernate-and-datasql

	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
	@GetMapping 
	@Cacheable(value = "listaDeTopicos")
	public List<TopicoDto> lista(@RequestParam(required = false) String nomeCurso) {
		
		if (nomeCurso == null) {
			List<Topico> topicos = topicoRepository.findAll();
			return TopicoDto.converter(topicos);
		} else {
			List<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso);
			return TopicoDto.converter(topicos);
		}

	}
	
	@GetMapping("/listaComPageable") // 
	public List<TopicoDto> listaComPageable(@RequestParam(required = false) 
	String nomeCurso, @PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) {
		
		if (nomeCurso == null) {
			List<Topico> topicos = topicoRepository.findAll();
			return TopicoDto.converter(topicos);
		} else {
			List<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso);
			return TopicoDto.converter(topicos);
		}

	}
	
	@GetMapping("/listaOrdenada") // listaOrdenada?pagina=0&qtd=3&ordenacao=id
	public Page<TopicoDto> listaOrdenada(@RequestParam(required = false) String nomeCurso, 
			@RequestParam int pagina, @RequestParam int qtd, @RequestParam String ordenacao) {
		
			Pageable paginacao = PageRequest.of(pagina, qtd, Direction.ASC, ordenacao);
		
		if (nomeCurso == null) {
			Page<Topico> topicos = (Page<Topico>) topicoRepository.findAll(paginacao);
			return TopicoDto.converterPaginacao(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converterPaginacao(topicos);
		}

	}
	
	@GetMapping("/listaPaginada") // listaPaginada?page=0&size=10&sort=id,asc 
	public Page<TopicoDto> listaPaginada(@RequestParam(required = false) String nomeCurso, 
			@RequestParam int pagina, @RequestParam int qtd) {
		
			Pageable paginacao = PageRequest.of(pagina, qtd);
		
		if (nomeCurso == null) {
			Page<Topico> topicos = (Page<Topico>) topicoRepository.findAll(paginacao);
			return TopicoDto.converterPaginacao(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converterPaginacao(topicos);
		}

	}
	
    @PostMapping
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriComponentsBuilder) {
    	Topico topico = form.converter(cursoRepository);
    	topicoRepository.save(topico);
    	
    	URI uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
    	
    	return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) { // @PathVariable("id") Long codigo
    	 Optional<Topico> topico = topicoRepository.findById(id);
    	 
    	 if(topico.isPresent()) {
    		 return ResponseEntity.ok( new DetalhesDoTopicoDto(topico.get())); // 200
    	 }
    	
    	 return ResponseEntity.notFound().build(); // 404
    }
    
    @PutMapping("/{id}")
    @Transactional // avisa o spring para commitar a transacao 
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public  ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form){
    	 Optional<Topico> optional = topicoRepository.findById(id);
    	 
    	 if(optional.isPresent()) {
    		 Topico topico = form.atualizar(id, topicoRepository);
    		 return ResponseEntity.ok(new TopicoDto(topico));
    	}
    	 return ResponseEntity.notFound().build(); // 404
    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true)
    public ResponseEntity<?> remover(@PathVariable Long id){
    Optional<Topico> optional = topicoRepository.findById(id);
    
   	 if(optional.isPresent()) {
   		topicoRepository.deleteById(id);
    	return ResponseEntity.ok().build();
   	 }
    	
   	 return ResponseEntity.notFound().build(); // 404
    }
    
}
