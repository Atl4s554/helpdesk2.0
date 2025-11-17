/*
 * Arquivo: web/js/admin-empresas.js
 * Descrição: Lógica da página de Gerenciamento de Empresas (CRUD).
 */
document.addEventListener("DOMContentLoaded", () => {

    const tabelaEmpresas = document.getElementById("tabela-empresas");
    const modalEmpresa = new bootstrap.Modal(document.getElementById('empresaModal'));
    const formEmpresa = document.getElementById("form-empresa");
    const modalLabel = document.getElementById("modalLabel");
    const btnNovaEmpresa = document.getElementById("btn-nova-empresa");

    const inputId = document.getElementById("empresa-id");
    const inputNome = document.getElementById("empresa-nome");
    const inputCnpj = document.getElementById("empresa-cnpj");
    const inputTelefone = document.getElementById("empresa-telefone");
    const inputEndereco = document.getElementById("empresa-endereco");

    // --- 1. CARREGAR A TABELA DE EMPRESAS ---
    async function carregarEmpresas() {
        try {
            const response = await fetch('empresas?acao=listar');
            if (!response.ok) throw new Error("Não foi possível carregar as empresas.");

            const empresas = await response.json();

            tabelaEmpresas.innerHTML = '';
            if (empresas.length === 0) {
                 tabelaEmpresas.innerHTML = '<tr><td colspan="5" class="text-center">Nenhuma empresa encontrada.</td></tr>';
                 return;
            }

            empresas.forEach(empresa => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${empresa.id}</td>
                    <td>${empresa.nome}</td>
                    <td>${empresa.cnpj}</td>
                    <td>${empresa.telefone}</td>
                    <td class="acao-coluna">
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${empresa.id}">
                            <i class="bi bi-pencil"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-danger btn-excluir" data-id="${empresa.id}">
                            <i class="bi bi-trash"></i> Excluir
                        </button>
                    </td>
                `;
                tabelaEmpresas.appendChild(tr);
            });

        } catch (error) {
            console.error("Erro ao carregar empresas:", error);
            tabelaEmpresas.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 2. LIDAR COM O FORMULÁRIO (SALVAR) ---
    formEmpresa.addEventListener("submit", async (e) => {
        e.preventDefault();

        const id = inputId.value;
        const ehEdicao = id > 0;

        const dadosEmpresa = {
            id: id,
            nome: inputNome.value,
            cnpj: inputCnpj.value,
            telefone: inputTelefone.value,
            endereco: inputEndereco.value
        };

        try {
            const url = ehEdicao ? `empresas?id=${id}` : 'empresas';
            const method = ehEdicao ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dadosEmpresa)
            });

            if (!response.ok) throw new Error("Erro ao salvar empresa.");

            modalEmpresa.hide();
            carregarEmpresas();

        } catch (error) {
            console.error("Erro ao salvar:", error);
            alert("Erro ao salvar empresa.");
        }
    });

    // --- 3. LIDAR COM BOTÕES (NOVO, EDITAR, EXCLUIR) ---
    btnNovaEmpresa.addEventListener("click", () => {
        modalLabel.textContent = "Nova Empresa";
        formEmpresa.reset();
        inputId.value = 0;
    });

    tabelaEmpresas.addEventListener("click", async (e) => {
        const target = e.target.closest("button");
        if (!target) return;

        const id = target.dataset.id;

        // Botão "Excluir"
        if (target.classList.contains("btn-excluir")) {
            if (confirm(`Tem certeza que deseja excluir a empresa ID ${id}?`)) {
                try {
                    const response = await fetch(`empresas?id=${id}`, { method: 'DELETE' });
                    if (!response.ok) throw new Error("Falha ao excluir.");
                    carregarEmpresas();
                } catch (error) {
                    alert("Erro ao excluir empresa.");
                }
            }
        }

        // Botão "Editar"
        if (target.classList.contains("btn-editar")) {
            try {
                const response = await fetch(`empresas?acao=buscar&id=${id}`);
                if (!response.ok) throw new Error("Empresa não encontrada.");

                const empresa = await response.json();

                modalLabel.textContent = `Editando Empresa: ${empresa.nome}`;
                inputId.value = empresa.id;
                inputNome.value = empresa.nome;
                inputCnpj.value = empresa.cnpj;
                inputTelefone.value = empresa.telefone;
                inputEndereco.value = empresa.endereco;

                modalEmpresa.show();

            } catch (error) {
                alert("Não foi possível carregar os dados para edição.");
            }
        }
    });

    // --- INICIALIZAÇÃO ---
    carregarEmpresas();
});