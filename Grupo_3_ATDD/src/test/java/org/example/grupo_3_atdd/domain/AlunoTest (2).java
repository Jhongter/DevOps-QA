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
    // LETICIA
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média acima de 7,0
    // Então o aluno tem direito a mais 3 cursos
    // =========================================================================================

    // --- RED: média usada (6.0) não é "acima de 7,0" como o cenário pede -> assert falha ---
    // @Test
    // void deveAvaliarMediaValidaParaCursoBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 6.0);
    //     assertTrue(joao.temDireitoAMaisCursos()); // FALHA: 6.0 < 7.0
    // }

    // --- GREEN: média corrigida para 8.5 (acima de 7,0) -> passa, mas sem refino/@DisplayName ---
    // @Test
    // void deveAvaliarMediaValidaParaCursoBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 8.5);
    //     assertTrue(joao.temDireitoAMaisCursos()); // passa
    // }

    // --- BLUE: versão final, refatorada com helpers e @DisplayName, mantendo o teste verde ---
    @Test
    @DisplayName("LETICIA - média acima de 7,0 em curso finalizado dá direito a mais 3 cursos")
    void deveAvaliarMediaValidaParaCursoBonus() {
        var curso = novoCurso();
        var joao = novoAluno();

        curso.finalizar(joao, 8.5);

        assertTrue(joao.temDireitoAMaisCursos());
    }

    // =========================================================================================
    // LUCAS
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média abaixo de 7,0
    // Então o aluno não tem direito a mais 3 cursos
    // =========================================================================================

    // --- RED: média usada (8.0) não é "abaixo de 7,0" como o cenário pede -> assert falha ---
    // @Test
    // void deveAvaliarMediaInvalidaParaCursoBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 8.0);
    //     assertFalse(joao.temDireitoAMaisCursos()); // FALHA: 8.0 >= 7.0
    // }

    // --- GREEN: média corrigida para 5.0 (abaixo de 7,0) -> passa, mas sem refino/@DisplayName ---
    // @Test
    // void deveAvaliarMediaInvalidaParaCursoBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 5.0);
    //     assertFalse(joao.temDireitoAMaisCursos()); // passa
    // }

    // --- BLUE: versão final, refatorada com helpers e @DisplayName, mantendo o teste verde ---
    @Test
    @DisplayName("LUCAS - média abaixo de 7,0 em curso finalizado NÃO dá direito a mais 3 cursos")
    void deveAvaliarMediaInvalidaParaCursoBonus() {
        var curso = novoCurso();
        var joao = novoAluno();

        curso.finalizar(joao, 5.0);

        assertFalse(joao.temDireitoAMaisCursos());
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

    // =========================================================================================
    // IZABELLY
    // Dado um curso finalizado, E aluno
    // Quando curso está encerrado, E a média é exatamente 7,0
    // Então o aluno tem direito a mais 3 cursos
    // =========================================================================================

    // --- RED: média usada (5.0) não é "exatamente 7,0" como o cenário pede -> assert falha ---
    // @Test
    // void deveAvaliarMediaLimiteBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 5.0);
    //     assertTrue(joao.temDireitoAMaisCursos()); // FALHA: 5.0 < 7.0
    // }

    // --- GREEN: média corrigida para 7.0 (caso limite exato) -> passa, mas sem refino/@DisplayName ---
    // @Test
    // void deveAvaliarMediaLimiteBonus() {
    //     var curso = novoCurso();
    //     var joao = novoAluno();
    //     curso.finalizar(joao, 7.0);
    //     assertTrue(joao.temDireitoAMaisCursos()); // passa
    // }

    // --- BLUE: versão final, refatorada com helpers e @DisplayName, mantendo o teste verde ---
    @Test
    @DisplayName("IZABELLY - média exatamente 7,0 (caso limite) dá direito a mais 3 cursos")
    void deveAvaliarMediaLimiteBonus() {
        var curso = novoCurso();
        var joao = novoAluno();

        curso.finalizar(joao, 7.0);

        assertTrue(joao.temDireitoAMaisCursos());
    }
}
