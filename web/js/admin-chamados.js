/*
 * Arquivo: web/js/admin-chamados.js
 * Descrição: Lógica da página de Gerenciamento de Chamados (Admin).
 */
document.addEventListener("DOMContentLoaded", () => {

    const tabelaChamados = document.getElementById("tabela-chamados-admin");
    const modalChamado = new bootstrap.Modal(document.getElementById('chamadoModal'));
    const formAtribuir = document.getElementById("form-atribuir");
    const modalLabel = document.getElementById("modalLabel");

    const inputChamadoId = document.getElementById("chamado-id");
    const selectTecnico = document.getElementById("chamado-tecnico");
    const modalTitulo = document.getElementById("modal-chamado-titulo");
    const modalDescricao = document.getElementById("modal-chamado-descricao");
    const modalCliente = document.getElementById("modal-chamado-cliente");
    const modalStatus = document.getElementById("modal-chamado-status");

    // --- 1. CARREGAR A TABELA DE CHAMADOS ---
    async function carregarChamadosAdmin() {
        try {
            const response = await fetch('chamados?acao=listarTodos');
            if (!response.ok) throw new Error("Não foi possível carregar os chamados.");

            const chamados = await response.json();

            tabelaChamados.innerHTML = '';
            if (chamados.length === 0) {
                 tabelaChamados.innerHTML = '<tr><td colspan="6" class="text-center">Nenhum chamado encontrado.</td></tr>';
                 return;
            }

            chamados.forEach(c => {
                const tr = document.createElement('tr');
                const statusInfo = getStatusInfo(c.status);

                tr.innerHTML = `
                    <td>${c.id}</td>
                    <td>${c.titulo}</td>
                    <td>${c.clienteNome || 'N/A'}</td>
                    <td>${c.tecnicoNome || 'Não atribuído'}</td>
                    <td><span class="badge ${statusInfo.classe}">${statusInfo.texto}</span></td>
                    <td>${new Date(c.dataAbertura).toLocaleDateString()}</td>
                    <td class="acao-coluna">
                        <button class="btn btn-sm btn-primary btn-detalhes" data-id="${c.id}" data-bs-toggle="modal" data-bs-target="#chamadoModal">
                            <i class="bi bi-search"></i> Detalhes
                        </button>
                    </td>
                `;
                tabelaChamados.appendChild(tr);
            });

        } catch (error) {
            console.error("Erro ao carregar chamados:", error);
            tabelaChamados.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 2. CARREGAR TÉCNICOS (para o dropdown) ---
    async function carregarTecnicos() {
        try {
            const response = await fetch('usuarios?acao=listar&tipo=TECNICO');
            const tecnicos = await response.json();

            selectTecnico.innerHTML = '<option value="">Selecione...</option>';
            tecnicos.forEach(t => {
                selectTecnico.innerHTML += `<option value="${t.id}">${t.nome}</option>`;
            });
        } catch (error) {
             console.error("Erro ao carregar técnicos:", error);
             selectTecnico.innerHTML = '<option value="">Erro ao carregar</option>';
        }
    }

    // --- 3. LIDAR COM O FORMULÁRIO (ATRIBUIR) ---
    formAtribuir.addEventListener("submit", async (e) => {
        e.preventDefault();

        const chamadoId = inputChamadoId.value;
        const tecnicoId = selectTecnico.value;

        if (!chamadoId || !tecnicoId) {
            alert("Selecione um técnico.");
            return;
        }

        try {
            const response = await fetch(`chamados?acao=atribuir&id=${chamadoId}&tecnicoId=${tecnicoId}`, {
                method: 'PUT'
            });

            if (!response.ok) throw new Error("Erro ao atribuir chamado.");

            modalChamado.hide();
            carregarChamadosAdmin(); // Recarrega a tabela

        } catch (error) {
            console.error("Erro ao atribuir:", error);
            alert("Erro ao atribuir chamado.");
        }
    });

    // --- 4. LIDAR COM BOTÃO (DETALHES) ---
    tabelaChamados.addEventListener("click", async (e) => {
        const target = e.target.closest("button.btn-detalhes");
        if (!target) return;

        const id = target.dataset.id;

        try {
            // 1. Busca os dados do chamado
            const response = await fetch(`chamados?acao=buscar&id=${id}`);
            if (!response.ok) throw new Error("Chamado não encontrado.");

            const chamado = await response.json();
            const statusInfo = getStatusInfo(chamado.status);

            // 2. Preenche o modal
            modalLabel.textContent = `Detalhes do Chamado #${id}`;
            inputChamadoId.value = chamado.id;
            modalTitulo.textContent = chamado.titulo;
            modalDescricao.textContent = chamado.descricao;
            modalCliente.textContent = `Cliente: ${chamado.clienteNome || 'N/A'}`;
            modalStatus.innerHTML = `Status: <span class="badge ${statusInfo.classe}">${statusInfo.texto}</span>`;

            // 3. Reseta o select
            selectTecnico.value = chamado.tecnicoId || "";

        } catch (error) {
            alert("Não foi possível carregar os dados do chamado.");
        }
    });

    function getStatusInfo(status) {
        switch(status) {
            case 'ABERTO': return { texto: 'Aberto', classe: 'bg-aberto' };
            case 'EM_ANDAMENTO': return { texto: 'Em Andamento', classe: 'bg-andamento' };
            case 'CONCLUIDO': return { texto: 'Concluído', classe: 'bg-concluido' };
            default: return { texto: status, classe: 'text-bg-secondary' };
        }
    }

    // --- INICIALIZAÇÃO ---
    carregarChamadosAdmin();
    carregarTecnicos();
});