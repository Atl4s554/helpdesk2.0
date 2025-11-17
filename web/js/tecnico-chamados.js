/*
 * Arquivo: web/js/tecnico-chamados.js
 * Descrição: Lógica das páginas de Técnico.
 */
document.addEventListener("DOMContentLoaded", () => {

    // Elementos da página "Meus Chamados"
    const tabelaMeusChamados = document.getElementById("tabela-meus-chamados");
    const modalAtendimento = new bootstrap.Modal(document.getElementById('atendimentoModal'));
    const formAtendimento = document.getElementById("form-atendimento");
    const inputChamadoId = document.getElementById("atendimento-chamado-id");
    const modalChamadoTitulo = document.getElementById("modal-chamado-titulo");

    // Elementos da página "Chamados Abertos"
    const tabelaChamadosAbertos = document.getElementById("tabela-chamados-abertos");

    // --- 1. CARREGAR MEUS CHAMADOS (Atribuídos) ---
    async function carregarMeusChamados() {
        try {
            const response = await fetch('chamados?acao=listarPorTecnico');
            if (!response.ok) throw new Error("Não foi possível carregar seus chamados.");

            const chamados = await response.json();

            tabelaMeusChamados.innerHTML = '';
            if (chamados.length === 0) {
                 tabelaMeusChamados.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum chamado atribuído a você.</td></tr>';
                 return;
            }

            chamados.forEach(c => {
                const tr = document.createElement('tr');
                const statusInfo = getStatusInfo(c.status);

                tr.innerHTML = `
                    <td>${c.id}</td>
                    <td>${c.titulo}</td>
                    <td>${c.clienteNome || 'N/A'}</td>
                    <td><span class="badge ${statusInfo.classe}">${statusInfo.texto}</span></td>
                    <td class="acao-coluna">
                        <button class="btn btn-sm btn-primary btn-atendimento" data-id="${c.id}" data-titulo="${c.titulo}" data-bs-toggle="modal" data-bs-target="#atendimentoModal">
                            <i class="bi bi-plus-circle"></i> Atendimento
                        </button>
                         <button class="btn btn-sm btn-success btn-concluir" data-id="${c.id}">
                            <i class="bi bi-check-circle"></i> Concluir
                        </button>
                    </td>
                `;
                tabelaMeusChamados.appendChild(tr);
            });

        } catch (error) {
            console.error("Erro ao carregar chamados:", error);
            tabelaMeusChamados.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 2. CARREGAR CHAMADOS ABERTOS (Disponíveis) ---
    async function carregarChamadosAbertos() {
        try {
            const response = await fetch('chamados?acao=listarAbertos');
            if (!response.ok) throw new Error("Não foi possível carregar chamados abertos.");

            const chamados = await response.json();

            tabelaChamadosAbertos.innerHTML = '';
            if (chamados.length === 0) {
                 tabelaChamadosAbertos.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum chamado aberto no momento.</td></tr>';
                 return;
            }

            chamados.forEach(c => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${c.id}</td>
                    <td>${c.titulo}</td>
                    <td>${c.clienteNome || 'N/A'}</td>
                    <td>${new Date(c.dataAbertura).toLocaleDateString()}</td>
                    <td class="acao-coluna">
                        <button class="btn btn-sm btn-success btn-aceitar" data-id="${c.id}">
                            <i class="bi bi-check-lg"></i> Aceitar
                        </button>
                    </td>
                `;
                tabelaChamadosAbertos.appendChild(tr);
            });
        } catch (error) {
            console.error("Erro ao carregar chamados abertos:", error);
            tabelaChamadosAbertos.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 3. LIDAR COM FORMULÁRIO (SALVAR ATENDIMENTO) ---
    if(formAtendimento) {
        formAtendimento.addEventListener("submit", async (e) => {
            e.preventDefault();

            const dadosAtendimento = {
                chamadoId: inputChamadoId.value,
                descricao: document.getElementById("atendimento-descricao").value,
                // O ID do técnico será pego da sessão no backend
            };

            try {
                const response = await fetch('atendimentos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(dadosAtendimento)
                });

                if (!response.ok) throw new Error("Erro ao salvar atendimento.");

                modalAtendimento.hide();
                carregarMeusChamados(); // Recarrega a tabela

            } catch (error) {
                alert("Erro ao salvar atendimento.");
            }
        });
    }

    // --- 4. LIDAR COM BOTÕES (Aceitar, Atendimento, Concluir) ---
    document.body.addEventListener("click", async (e) => {
        const target = e.target.closest("button");
        if (!target) return;

        const id = target.dataset.id;

        // Botão "Adicionar Atendimento" (em Meus Chamados)
        if (target.classList.contains("btn-atendimento")) {
            modalChamadoTitulo.textContent = `Atendimento para: ${target.dataset.titulo}`;
            inputChamadoId.value = id;
            formAtendimento.reset();
        }

        // Botão "Concluir" (em Meus Chamados)
        if (target.classList.contains("btn-concluir")) {
            if (confirm("Tem certeza que deseja marcar este chamado como CONCLUÍDO?")) {
                await atualizarStatusChamado(id, 'CONCLUIDO');
            }
        }

        // Botão "Aceitar" (em Chamados Abertos)
        if (target.classList.contains("btn-aceitar")) {
            if (confirm("Tem certeza que deseja aceitar este chamado?")) {
                await atualizarStatusChamado(id, 'EM_ANDAMENTO'); // O backend atribui ao técnico logado
            }
        }
    });

    async function atualizarStatusChamado(id, status) {
        try {
            const response = await fetch(`chamados?acao=mudarStatus&id=${id}&status=${status}`, { method: 'PUT' });
            if (!response.ok) throw new Error("Não foi possível atualizar o status.");

            // Recarrega a(s) tabela(s) correta(s)
            if (tabelaMeusChamados) carregarMeusChamados();
            if (tabelaChamadosAbertos) carregarChamadosAbertos();

        } catch (error) {
            alert("Erro: " + error.message);
        }
    }

    function getStatusInfo(status) {
        switch(status) {
            case 'ABERTO': return { texto: 'Aberto', classe: 'bg-aberto' };
            case 'EM_ANDAMENTO': return { texto: 'Em Andamento', classe: 'bg-andamento' };
            case 'CONCLUIDO': return { texto: 'Concluído', classe: 'bg-concluido' };
            default: return { texto: status, classe: 'text-bg-secondary' };
        }
    }

    // --- INICIALIZAÇÃO ---
    // Roda a função de carregar apropriada para a página atual
    if (tabelaMeusChamados) {
        carregarMeusChamados();
    }
    if (tabelaChamadosAbertos) {
        carregarChamadosAbertos();
    }
});