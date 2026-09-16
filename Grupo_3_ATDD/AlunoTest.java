package org.example.grupo_3_atdd.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * US ESCOLHIDA PELO GRUPO:
 * COMO Aluno de um sistema de gamificação
 * QUERO ter média acima de 7,0
 * PARA ter o direito de realizar mais 3 cursos
 *
 * Cada integrante escreveu um cenário BDD (Given/When/Then) diferente para
 * esta mesma US, conforme identificado em cada teste abaixo.
 */

/* TESTES RED
public class AlunoTest {

    // LETICIA
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média acima de 7,0
    // Então o aluno tem direito a mais 3 cursos
    @Test
    void deveAvaliarMediaValidaParaCursoBonus() {
        var curso = new Curso("Curso Terminado");
        var joao = new Aluno("Joao");

        curso.finalizar(joao, 6.0);

        assertTrue(joao.temDireitoAMaisCursos());
    }

    // LUCAS
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média abaixo de 7,0
    // Então o aluno não tem direito a mais 3 cursos
    @Test
    void deveAvaliarMediaInvalidaParaCursoBonus() {
        var curso = new Curso("Curso Terminado");
        var joao = new Aluno("Joao");

        curso.finalizar(joao,8.0);

        assertFalse(joao.temDireitoAMaisCursos());
    }

    // JOÃO
    // Dado um curso não finalizado, E aluno
    // Quando o curso não é finalizado, E a média não foi lançada
    // Então o aluno não tem direito a mais 3 cursos
    @Test
    void deveAvaliarMediaNaoAlcancada() {
        var curso = new Curso("Curso Terminado");
        var joao = new Aluno("Joao");

        assertFalse(joao.temDireitoAMaisCursos());
        assertTrue(curso.isFinalizado());
    }

    //  IZABELLY
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média é exatamente 7,0
    // Então o aluno tem direito a mais 3 cursos
    @Test
    void deveAvaliarMediaLimiteBonus() {
        var curso = new Curso("Curso Terminado");
        var joao = new Aluno("Joao");

        curso.finalizar(joao, 5.0);

        assertTrue(joao.temDireitoAMaisCursos());
    }
}
TESTES RED
*/
// TESTES GREEN

public class AlunoTest {

    // JOÃO
    // Dado um curso não finalizado, E aluno
    // Quando o curso não é finalizado, E a média não foi lançada
    // Então o aluno não tem direito a mais 3 cursos
    @Test
    void deveAvaliarMediaNaoAlcancada() {
        var curso = new Curso("Curso Terminado");
        var joao = new Aluno("Joao");

        assertFalse(joao.temDireitoAMaisCursos());
        assertFalse(curso.isFinalizado());
    }

}



