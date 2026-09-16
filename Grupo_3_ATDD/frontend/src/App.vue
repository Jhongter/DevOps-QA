<script setup>
import { ref, computed, reactive, watch, onMounted } from 'vue'
import BonusSeal from './components/BonusSeal.vue'
import CallLog from './components/CallLog.vue'
import { createApiClient, ApiError } from './api.js'

const baseUrl = ref('http://localhost:8081/api')
const editingBaseUrl = ref(false)
const api = computed(() => createApiClient(baseUrl.value))

const apiOnline = ref(null) // null = ainda não checado, true/false depois
const alunos = ref([])
const search = ref('')
const selectedAluno = ref(null)
const loadingList = ref(false)
const loadingDetail = ref(false)

const novoAlunoNome = ref('')
const criandoAluno = ref(false)

const novoCursoNome = ref('')
const criandoCurso = ref(false)

const medias = reactive({})
const finalizandoCurso = reactive({})

const verificacaoDedicada = ref(null) // { valor, quando } — resultado do endpoint /direito-bonus isolado

const logs = ref([])

function registrar(method, path, status, message, level) {
  logs.value.unshift({
    time: new Date().toLocaleTimeString('pt-BR', { hour12: false }),
    method,
    path,
    status: status ?? '—',
    message,
    level: level ?? (status && status < 400 ? 'ok' : 'error')
  })
}

async function chamar(descricaoErro, fn) {
  try {
    return await fn()
  } catch (err) {
    if (err instanceof ApiError) {
      registrar(err.method, err.path, err.status || '—', err.message, 'error')
    }
    throw err
  }
}

const alunosFiltrados = computed(() => {
  const termo = search.value.trim().toLowerCase()
  if (!termo) return alunos.value
  return alunos.value.filter((a) => a.nome.toLowerCase().includes(termo))
})

async function carregarAlunos() {
  loadingList.value = true
  try {
    const resultado = await chamar('listar', () => api.value.listarAlunos())
    alunos.value = resultado
    apiOnline.value = true
    registrar('GET', '/alunos', 200, `${resultado.length} aluno(s) carregado(s)`, 'ok')
  } catch {
    apiOnline.value = false
  } finally {
    loadingList.value = false
  }
}

async function selecionarAluno(id) {
  loadingDetail.value = true
  verificacaoDedicada.value = null
  try {
    const resultado = await chamar('buscar', () => api.value.buscarAluno(id))
    selectedAluno.value = resultado
    registrar('GET', `/alunos/${id}`, 200, `Detalhes de "${resultado.nome}" carregados`, 'ok')
  } catch {
    // erro já registrado no log
  } finally {
    loadingDetail.value = false
  }
}

async function criarAluno() {
  const nome = novoAlunoNome.value.trim()
  if (!nome) return
  criandoAluno.value = true
  try {
    const criado = await chamar('criar aluno', () => api.value.criarAluno(nome))
    registrar('POST', '/alunos', 200, `Aluno "${nome}" criado (id ${criado.id})`, 'ok')
    novoAlunoNome.value = ''
    await carregarAlunos()
    await selecionarAluno(criado.id)
  } catch {
    // erro já registrado
  } finally {
    criandoAluno.value = false
  }
}

async function criarCurso() {
  if (!selectedAluno.value) return
  const nome = novoCursoNome.value.trim()
  if (!nome) return
  const alunoId = selectedAluno.value.id
  criandoCurso.value = true
  try {
    await chamar('criar curso', () => api.value.criarCurso(alunoId, nome))
    registrar('POST', `/alunos/${alunoId}/cursos`, 200, `Curso "${nome}" matriculado`, 'ok')
    novoCursoNome.value = ''
    await selecionarAluno(alunoId)
    await carregarAlunos()
  } catch {
    // erro já registrado
  } finally {
    criandoCurso.value = false
  }
}

async function finalizarCurso(curso) {
  if (!selectedAluno.value) return
  const alunoId = selectedAluno.value.id
  const bruto = medias[curso.id]
  const media = parseFloat(String(bruto).replace(',', '.'))
  if (Number.isNaN(media) || media < 0 || media > 10) {
    registrar(
      'POST',
      `/alunos/${alunoId}/cursos/${curso.id}/finalizar`,
      '—',
      `Média inválida para "${curso.nome}": informe um número entre 0 e 10`,
      'error'
    )
    return
  }
  finalizandoCurso[curso.id] = true
  try {
    await chamar('finalizar curso', () => api.value.finalizarCurso(alunoId, curso.id, media))
    registrar(
      'POST',
      `/alunos/${alunoId}/cursos/${curso.id}/finalizar`,
      200,
      `"${curso.nome}" finalizado com média ${media}`,
      'ok'
    )
    delete medias[curso.id]
    await selecionarAluno(alunoId)
    await carregarAlunos()
  } catch {
    // erro já registrado
  } finally {
    finalizandoCurso[curso.id] = false
  }
}

async function verificarBonusDedicado() {
  if (!selectedAluno.value) return
  const alunoId = selectedAluno.value.id
  try {
    const valor = await chamar('verificar bonus', () => api.value.verificarBonus(alunoId))
    verificacaoDedicada.value = { valor, quando: new Date().toLocaleTimeString('pt-BR', { hour12: false }) }
    registrar(
      'GET',
      `/alunos/${alunoId}/direito-bonus`,
      200,
      `Endpoint dedicado respondeu: ${valor ? 'SIM' : 'NÃO'}`,
      'ok'
    )
  } catch {
    // erro já registrado
  }
}

watch(baseUrl, () => {
  apiOnline.value = null
  alunos.value = []
  selectedAluno.value = null
  carregarAlunos()
})

onMounted(carregarAlunos)
</script>

<template>
  <div class="app">
    <header class="header">
      <div class="header__title">
        <h1>Gamificação</h1>
        <p>Painel de testes · regras de bônus por média de curso</p>
      </div>

      <div class="header__status">
        <button class="status" :class="apiOnline ? 'status--ok' : apiOnline === false ? 'status--off' : 'status--unknown'" @click="editingBaseUrl = !editingBaseUrl">
          <span class="status__dot" />
          {{ apiOnline === null ? 'verificando API…' : apiOnline ? 'API conectada' : 'API offline' }}
        </button>
        <div v-if="editingBaseUrl" class="baseurl-edit">
          <input v-model="baseUrl" @keyup.enter="editingBaseUrl = false" />
          <button @click="editingBaseUrl = false; carregarAlunos()">Aplicar</button>
        </div>
        <span v-else class="baseurl-label">{{ baseUrl }}</span>
      </div>
    </header>

    <div v-if="apiOnline === false" class="banner banner--error">
      Não foi possível conectar em <strong>{{ baseUrl }}</strong>. Confirme que o backend está
      rodando (<code>docker compose up</code> ou <code>mvn spring-boot:run</code>) e que a porta
      está correta — clique no status acima para ajustar.
    </div>

    <div class="layout">
      <aside class="sidebar">
        <input class="search" v-model="search" placeholder="Buscar aluno…" />

        <div class="new-aluno">
          <input
            v-model="novoAlunoNome"
            placeholder="Nome do novo aluno"
            @keyup.enter="criarAluno"
          />
          <button class="btn btn--gold" :disabled="criandoAluno || !novoAlunoNome.trim()" @click="criarAluno">
            {{ criandoAluno ? 'Criando…' : '+ Aluno' }}
          </button>
        </div>

        <p v-if="!loadingList && alunos.length === 0" class="empty">
          Nenhum aluno cadastrado ainda. Crie o primeiro aluno acima para começar a testar.
        </p>

        <ul class="student-list">
          <li
            v-for="a in alunosFiltrados"
            :key="a.id"
            :class="{ 'student-list__item--active': selectedAluno && selectedAluno.id === a.id }"
            @click="selecionarAluno(a.id)"
          >
            <BonusSeal :earned="a.temDireitoAMaisCursos" size="sm" />
            <span class="student-list__name">{{ a.nome }}</span>
            <span class="student-list__count">{{ a.cursos.length }}</span>
          </li>
        </ul>
      </aside>

      <main class="detail">
        <div v-if="loadingDetail" class="empty">Carregando…</div>

        <div v-else-if="!selectedAluno" class="empty empty--center">
          Selecione um aluno na lista ao lado, ou crie um novo, para ver os detalhes e testar os
          cursos.
        </div>

        <template v-else>
          <div class="student-header">
            <BonusSeal :earned="selectedAluno.temDireitoAMaisCursos" size="lg" />
            <div>
              <h2>{{ selectedAluno.nome }}</h2>
              <p class="student-header__meta">
                aluno #{{ selectedAluno.id }} ·
                {{ selectedAluno.temDireitoAMaisCursos ? 'direito a mais 3 cursos' : 'sem bônus por enquanto' }}
              </p>
            </div>
            <button class="btn btn--ghost verify-btn" @click="verificarBonusDedicado">
              Verificar via /direito-bonus
            </button>
          </div>

          <p v-if="verificacaoDedicada" class="verify-result">
            Endpoint dedicado confirmou às {{ verificacaoDedicada.quando }}:
            <strong>{{ verificacaoDedicada.valor ? 'SIM' : 'NÃO' }}</strong>
          </p>

          <section class="panel">
            <h3>Matricular em curso</h3>
            <div class="inline-form">
              <input
                v-model="novoCursoNome"
                placeholder="Nome do curso"
                @keyup.enter="criarCurso"
              />
              <button class="btn btn--gold" :disabled="criandoCurso || !novoCursoNome.trim()" @click="criarCurso">
                {{ criandoCurso ? 'Adicionando…' : 'Adicionar curso' }}
              </button>
            </div>
          </section>

          <section class="panel panel--ledger">
            <h3>Cursos</h3>
            <p v-if="selectedAluno.cursos.length === 0" class="empty">
              Nenhum curso matriculado ainda.
            </p>
            <div v-else class="ledger">
              <div class="ledger__row ledger__row--head">
                <span>Curso</span>
                <span>Status</span>
                <span>Média</span>
                <span></span>
              </div>
              <div v-for="c in selectedAluno.cursos" :key="c.id" class="ledger__row">
                <span class="ledger__name">{{ c.nome }}</span>
                <span class="ledger__badge" :class="c.finalizado ? 'ledger__badge--done' : 'ledger__badge--pending'">
                  {{ c.finalizado ? 'finalizado' : 'em andamento' }}
                </span>
                <span class="ledger__media">{{ c.media ?? '—' }}</span>
                <span class="ledger__action">
                  <template v-if="!c.finalizado">
                    <input
                      class="media-input"
                      type="text"
                      inputmode="decimal"
                      placeholder="0–10"
                      v-model="medias[c.id]"
                    />
                    <button
                      class="btn btn--gold btn--sm"
                      :disabled="finalizandoCurso[c.id]"
                      @click="finalizarCurso(c)"
                    >
                      {{ finalizandoCurso[c.id] ? '…' : 'Finalizar' }}
                    </button>
                  </template>
                </span>
              </div>
            </div>
          </section>
        </template>
      </main>
    </div>

    <CallLog :entries="logs" />
  </div>
</template>

<style scoped>
.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  padding: 28px 32px 20px;
  flex-wrap: wrap;
}

.header__title h1 {
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.2px;
}

.header__title p {
  margin: 4px 0 0;
  color: var(--ink-text-muted);
  font-size: 14px;
}

.header__status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.status {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  background: none;
  border: 1px solid var(--navy-700);
  border-radius: 999px;
  padding: 5px 12px;
  color: var(--ink-text-muted);
  font-size: 13px;
}

.status__dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--ink-text-muted);
}

.status--ok .status__dot {
  background: var(--gold);
}

.status--ok {
  color: var(--gold);
  border-color: var(--gold-deep);
}

.status--off .status__dot {
  background: var(--clay);
}

.status--off {
  color: var(--clay);
  border-color: var(--clay);
}

.baseurl-label {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--ink-text-muted);
}

.baseurl-edit {
  display: flex;
  gap: 6px;
}

.baseurl-edit input {
  font-family: var(--font-mono);
  font-size: 12px;
  background: var(--navy-800);
  border: 1px solid var(--navy-700);
  border-radius: var(--radius-s);
  color: var(--ink-text);
  padding: 5px 8px;
  width: 220px;
}

.baseurl-edit button {
  font-size: 12px;
  background: var(--navy-700);
  border: none;
  border-radius: var(--radius-s);
  color: var(--ink-text);
  padding: 5px 10px;
}

.banner {
  margin: 0 32px 16px;
  padding: 12px 16px;
  border-radius: var(--radius-s);
  font-size: 13.5px;
}

.banner--error {
  background: var(--clay-wash);
  border: 1px solid var(--clay);
  color: #f1d3c9;
}

.banner code {
  font-family: var(--font-mono);
  background: rgba(0, 0, 0, 0.2);
  padding: 1px 5px;
  border-radius: 4px;
}

.layout {
  flex: 1;
  display: grid;
  grid-template-columns: 270px 1fr;
  gap: 0;
  padding: 0 32px;
  min-height: 0;
}

.sidebar {
  border-right: 1px solid var(--navy-700);
  padding-right: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.search {
  background: var(--navy-800);
  border: 1px solid var(--navy-700);
  border-radius: var(--radius-s);
  padding: 8px 10px;
  color: var(--ink-text);
  font-size: 13.5px;
}

.new-aluno {
  display: flex;
  gap: 6px;
}

.new-aluno input {
  flex: 1;
  min-width: 0;
  background: var(--navy-800);
  border: 1px solid var(--navy-700);
  border-radius: var(--radius-s);
  padding: 8px 10px;
  color: var(--ink-text);
  font-size: 13.5px;
}

.student-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.student-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: var(--radius-s);
  cursor: pointer;
  color: var(--ink-text-muted);
}

.student-list li:hover {
  background: var(--navy-800);
}

.student-list__item--active {
  background: var(--navy-800);
  color: var(--ink-text);
}

.student-list__name {
  flex: 1;
  font-size: 14px;
}

.student-list__count {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--ink-text-muted);
}

.detail {
  padding: 4px 0 24px 28px;
  min-width: 0;
}

.empty {
  color: var(--ink-text-muted);
  font-size: 13.5px;
  padding: 10px 0;
}

.empty--center {
  padding-top: 60px;
  max-width: 40ch;
}

.student-header {
  display: flex;
  align-items: center;
  gap: 18px;
  margin: 12px 0 8px;
}

.student-header h2 {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.student-header__meta {
  margin: 3px 0 0;
  color: var(--ink-text-muted);
  font-size: 13px;
}

.verify-btn {
  margin-left: auto;
}

.verify-result {
  font-size: 13px;
  color: var(--ink-text-muted);
  margin: 0 0 20px;
}

.verify-result strong {
  color: var(--gold);
}

.panel {
  margin-bottom: 26px;
}

.panel h3 {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 600;
  color: var(--ink-text-muted);
  margin: 0 0 10px;
}

.inline-form {
  display: flex;
  gap: 8px;
  max-width: 420px;
}

.inline-form input {
  flex: 1;
  background: var(--navy-800);
  border: 1px solid var(--navy-700);
  border-radius: var(--radius-s);
  padding: 8px 10px;
  color: var(--ink-text);
  font-size: 13.5px;
}

.btn {
  border: none;
  border-radius: var(--radius-s);
  padding: 8px 14px;
  font-size: 13.5px;
  font-weight: 500;
  white-space: nowrap;
}

.btn:disabled {
  opacity: 0.5;
  cursor: default;
}

.btn--gold {
  background: var(--gold);
  color: #1c1404;
}

.btn--gold:not(:disabled):hover {
  background: #d7ac52;
}

.btn--ghost {
  background: none;
  border: 1px solid var(--navy-700);
  color: var(--ink-text-muted);
}

.btn--ghost:hover {
  color: var(--ink-text);
  border-color: var(--navy-600);
}

.btn--sm {
  padding: 5px 10px;
  font-size: 12.5px;
}

.panel--ledger {
  background: var(--paper);
  border-radius: var(--radius-m);
  padding: 18px 20px 8px;
  color: var(--text-on-paper);
}

.panel--ledger h3 {
  color: var(--text-on-paper-muted);
}

.ledger__row {
  display: grid;
  grid-template-columns: 1.6fr 1fr 0.7fr 1.4fr;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--paper-line);
  font-size: 13.5px;
}

.ledger__row:last-child {
  border-bottom: none;
}

.ledger__row--head {
  color: var(--text-on-paper-muted);
  font-size: 11.5px;
  font-weight: 500;
  border-bottom: 1px solid var(--paper-line);
}

.ledger__name {
  font-weight: 500;
}

.ledger__badge {
  font-size: 12px;
  justify-self: start;
}

.ledger__badge--done {
  color: var(--gold-deep);
}

.ledger__badge--pending {
  color: var(--text-on-paper-muted);
}

.ledger__media {
  font-family: var(--font-mono);
}

.ledger__action {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}

.media-input {
  width: 56px;
  background: #fff;
  border: 1px solid var(--paper-line);
  border-radius: var(--radius-s);
  padding: 5px 6px;
  font-size: 13px;
  text-align: center;
}

@media (max-width: 780px) {
  .layout {
    grid-template-columns: 1fr;
    padding: 0 18px;
  }

  .sidebar {
    border-right: none;
    border-bottom: 1px solid var(--navy-700);
    padding-right: 0;
    padding-bottom: 16px;
    margin-bottom: 12px;
  }

  .detail {
    padding-left: 0;
  }

  .header {
    padding: 20px 18px 14px;
  }

  .ledger__row {
    grid-template-columns: 1fr;
    row-gap: 4px;
  }

  .ledger__row--head {
    display: none;
  }
}
</style>
