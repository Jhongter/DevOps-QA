package org.example.grupo_3_atdd.domain;


public class Curso {

    private String nome;
    private boolean finalizado;
    private Double media;

    public Curso(String nome) {
        this.nome = nome;
        this.finalizado = false;
        this.media = null;
    }

    /**
     * Encerra o curso para um aluno, registrando a média obtida.
     * Regra de negócio (US compartilhada pelo grupo): a média define se o
     * aluno terá direito a mais 3 cursos (ver Aluno#temDireitoAMaisCursos).
     */
    public void finalizar(Aluno aluno, double media) {
        this.finalizado = true;
        this.media = media;
        aluno.registrarCursoFinalizado(this);
    }

    public String getNome() {
        return nome;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public Double getMedia() {
        return media;
    }
}
