/*
 * Arquivo: web/js/cliente-chamados.js
 * Descrição: Lógica da página do Cliente (CRUD Chamados).
 */
document.addEventListener("DOMContentLoaded", () => {

    const tabelaChamados = document.getElementById("tabela-meus-chamados-cliente");
    const modalChamado = new bootstrap.Modal(document.getElementById('chamadoModal'));
    const formChamado = document.getElementById("form-chamado");
    const modalLabel = document.getElementById("modalLabel");
    const btnNovoChamado = document.getElementById("btn-novo-chamado");

    const inputId = document.getElementById("chamado-id");
    const inputTitulo = document.getElementById("chamado-titulo");
    const inputDescricao = document.getElementById("chamado-descricao");

    // --- 1. CARREGAR A TABELA DE CHAMADOS ---
    async function carregarMeusChamados() {
        try {
            const response = await fetch('chamados?acao=listarPorCliente');
            if (!response.ok) throw new Error("Não foi possível carregar seus chamados.");

            const chamados = await response.json();

            tabelaChamados.innerHTML = '';
            if (chamados.length === 0) {
                 tabelaChamados.innerHTML = '<tr><td colspan="5" class="text-center">Você ainda não abriu nenhum chamado.</td></tr>';
                 return;
            }

            chamados.forEach(c => {
                const tr = document.createElement('tr');
                const statusInfo = getStatusInfo(c.status);

                tr.innerHTML = `
                    <td>${c.id}</td>
                    <td>${c.titulo}</td>
                    <td>${c.tecnicoNome || 'Aguardando'}</td>
                    <td><span class="badge ${statusInfo.classe}">${statusInfo.texto}</span></td>
                    <td>${new Date(c.dataAbertura).toLocaleDateString()}</td>
                    <td class="acao-coluna">
                        <button class="btn btn-sm btn-primary btn-visualizar" data-id="${c.id}" data-bs-toggle="modal" data-bs-target="#chamadoModal">
                            <i class="bi bi-search"></i> Ver
                        </button>
                    </td>
                `;
                tabelaChamados.appendChild(tr);
            });

        } catch (error) {
            console.error("Erro ao carregar chamados:", error);
            tabelaChamados.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 2. LIDAR COM O FORMULÁRIO (SALVAR) ---
    formChamado.addEventListener("submit", async (e) => {
        e.preventDefault();

        const id = inputId.value;
        const ehEdicao = id > 0; // (Este formulário só cria, mas a lógica está aqui)

        const dadosChamado = {
            id: id,
            titulo: inputTitulo.value,
            descricao: inputDescricao.value
            // O ID do cliente será pego da sessão no backend
        };

        try {
            const response = await fetch('chamados', {
                method: 'POST', // Sempre POST para criar
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dadosChamado)
            });

            if (!response.ok) throw new Error("Erro ao abrir chamado.");

            modalChamado.hide();
            carregarMeusChamados(); // Recarrega a tabela

        } catch (error) {
            console.error("Erro ao salvar:", error);
            alert("Erro ao abrir chamado.");
        }
    });

    // --- 3. LIDAR COM BOTÕES (NOVO, VISUALIZAR) ---
    btnNovoChamado.addEventListener("click", () => {
        modalLabel.textContent = "Abrir Novo Chamado";
        formChamado.reset();
        inputId.value = 0;

        // Habilita campos
        inputTitulo.disabled = false;
        inputDescricao.disabled = false;
        formChamado.querySelector("button[type='submit']").style.display = 'block';
    });

    tabelaChamados.addEventListener("click", async (e) => {
        const target = e.target.closest("button.btn-visualizar");
        if (!target) return;

        const id = target.dataset.id;

        try {
            const response = await fetch(`chamados?acao=buscar&id=${id}`);
            if (!response.ok) throw new Error("Chamado não encontrado.");

            const chamado = await response.json();

            modalLabel.textContent = `Detalhes do Chamado #${id}`;
            inputId.value = chamado.id;
            inputTitulo.value = chamado.titulo;
            inputDescricao.value = chamado.descricao;

            // Desabilita campos (apenas visualização)
            inputTitulo.disabled = true;
            inputDescricao.disabled = true;
            formChamado.querySelector("button[type='submit']").style.display = 'none';

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
    carregarMeusChamados();
});