package org.example.grupo_3_atdd.domain;
import java.util.ArrayList;
import java.util.List;

/**
 * US: COMO Aluno de um sistema de gamificação
 *     QUERO ter média acima de 7,0
 *     PARA ter o direito de realizar mais 3 cursos
 */
public class Aluno {

    private static final double MEDIA_MINIMA_PARA_BONUS = 7.0;

    private String nome;
    private List<Curso> cursosFinalizados = new ArrayList<>();

    public Aluno(String nome) {
        this.nome = nome;
    }

    public void registrarCursoFinalizado(Curso curso) {
        this.cursosFinalizados.add(curso);
    }

    /**
     * Regra de negócio: o aluno tem direito a mais 3 cursos quando possui
     * ao menos um curso finalizado com média maior ou igual a 7,0.
     */
    public boolean temDireitoAMaisCursos() {
        return cursosFinalizados.stream()
                .filter(Curso::isFinalizado)
                .anyMatch(curso -> curso.getMedia() != null && curso.getMedia() >= MEDIA_MINIMA_PARA_BONUS);
    }

    public String getNome() {
        return nome;
    }

    public List<Curso> getCursosFinalizados() {
        return cursosFinalizados;
    }
}
