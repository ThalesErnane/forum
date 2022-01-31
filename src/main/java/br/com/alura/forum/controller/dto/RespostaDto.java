package br.com.alura.forum.controller.dto;

import java.time.LocalDateTime;

import br.com.alura.forum.model.Resposta;

public class RespostaDto {

	private Long id;
	private String mensagemn;
	private LocalDateTime dataCriacao;
	private String nomeAutor;

	public RespostaDto(Resposta resposta) {
		this.id = resposta.getId();
		this.mensagemn = resposta.getMensagem();
		this.dataCriacao = resposta.getDataCriacao();
		this.nomeAutor = resposta.getAutor().getNome();
	}

	public Long getId() {
		return id;
	}

	public String getMensagemn() {
		return mensagemn;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public String getNomeAutor() {
		return nomeAutor;
	}

}
