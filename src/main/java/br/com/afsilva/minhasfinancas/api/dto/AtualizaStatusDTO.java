package br.com.afsilva.minhasfinancas.api.dto;

public class AtualizaStatusDTO {

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AtualizaStatusDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AtualizaStatusDTO(String status) {
		super();
		this.status = status;
	}
}
