/**
 * Cliente HTTP simples para a API do backend.
 * Centraliza as chamadas e o tratamento de erro para que a UI só precise
 * lidar com sucesso/erro, sem repetir fetch/try-catch em cada componente.
 */
export function createApiClient(baseUrl) {
  async function request(method, path, body) {
    const url = `${baseUrl}${path}`
    let response
    try {
      response = await fetch(url, {
        method,
        headers: body ? { 'Content-Type': 'application/json' } : undefined,
        body: body ? JSON.stringify(body) : undefined
      })
    } catch (networkError) {
      throw new ApiError(method, path, 0, 'Sem resposta do servidor (backend está rodando?)')
    }

    let data = null
    const text = await response.text()
    if (text) {
      try {
        data = JSON.parse(text)
      } catch {
        data = text
      }
    }

    if (!response.ok) {
      const message = (data && data.erro) || `HTTP ${response.status}`
      throw new ApiError(method, path, response.status, message)
    }

    return data
  }

  return {
    listarAlunos: () => request('GET', '/alunos'),
    criarAluno: (nome) => request('POST', '/alunos', { nome }),
    buscarAluno: (id) => request('GET', `/alunos/${id}`),
    criarCurso: (alunoId, nome) => request('POST', `/alunos/${alunoId}/cursos`, { nome }),
    finalizarCurso: (alunoId, cursoId, media) =>
      request('POST', `/alunos/${alunoId}/cursos/${cursoId}/finalizar`, { media }),
    verificarBonus: (alunoId) => request('GET', `/alunos/${alunoId}/direito-bonus`)
  }
}

export class ApiError extends Error {
  constructor(method, path, status, message) {
    super(message)
    this.method = method
    this.path = path
    this.status = status
  }
}
