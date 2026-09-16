package org.example.grupo_3_atdd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * US ESCOLHIDA PELO GRUPO:
 * COMO Aluno de um sistema de gamificação
 * QUERO ter média acima de 7,0
 * PARA ter o direito de realizar mais 3 cursos
 *
 * Cada integrante escreveu um cenário BDD (Given/When/Then) diferente para esta mesma US
 * (LETICIA, LUCAS, JOÃO, IZABELLY). Cada teste abaixo documenta, individualmente, as 3 etapas
 * do próprio ciclo TDD (RED -> GREEN -> BLUE): as versões RED e GREEN ficam comentadas logo
 * acima do teste ativo (BLUE), como evidência/histórico de cada integrante.
 */
public class AlunoTest {

    private Aluno novoAluno() {
        return new Aluno("Joao");
    }

    private Curso novoCurso() {
        return new Curso("Curso Terminado");
    }

    // =========================================================================================
    // JOÃO
    // Dado um curso não finalizado, E aluno
    // Quando o curso não é finalizado, E a média não foi lançada
    // Então o aluno não tem direito a mais 3 cursos
    // =========================================================================================

    // --- RED: assert invertido (checava isFinalizado() == true, mas curso nunca foi finalizado) ---
    // @Test
    // void deveAvaliarMediaNaoAlcancada() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     assertFalse(joao.temDireitoAMaisCursos());
    //     assertTrue(curso.isFinalizado()); // FALHA: curso nunca foi finalizado
    // }

    // --- GREEN: assert corrigido para assertFalse(curso.isFinalizado()) -> passa, sem refino ---
    // @Test
    // void deveAvaliarFinalizacaoCurso() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     assertFalse(curso.isFinalizado());
    //     assertFalse(joao.temDireitoAMaisCursos());
    // }

    // --- BLUE: versão final, refatorada com helpers e @DisplayName, mantendo o teste verde ---
    @Test
    @DisplayName("JOÃO - curso não finalizado (sem média lançada) NÃO dá direito a mais 3 cursos")
    void deveAvaliarFinalizacaoCurso() {
        var curso = novoCurso();
        var joao = novoAluno();

        assertFalse(curso.isFinalizado());
        assertFalse(joao.temDireitoAMaisCursos());
    }


}
