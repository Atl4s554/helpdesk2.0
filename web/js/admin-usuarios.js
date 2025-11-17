/*
 * Arquivo: web/js/admin-usuarios.js
 * Descrição: Lógica da página de Gerenciamento de Usuários (CRUD).
 */
document.addEventListener("DOMContentLoaded", () => {

    const tabelaUsuarios = document.getElementById("tabela-usuarios");
    const modalUsuario = new bootstrap.Modal(document.getElementById('usuarioModal'));
    const formUsuario = document.getElementById("form-usuario");
    const modalLabel = document.getElementById("modalLabel");
    const btnNovoUsuario = document.getElementById("btn-novo-usuario");

    const inputId = document.getElementById("usuario-id");
    const inputNome = document.getElementById("usuario-nome");
    const inputEmail = document.getElementById("usuario-email");
    const inputSenha = document.getElementById("usuario-senha");
    const inputTipo = document.getElementById("usuario-tipo");
    const inputCpf = document.getElementById("usuario-cpf");
    // (Adicionar campos de empresa e especialidade)

    // --- 1. CARREGAR A TABELA DE USUÁRIOS ---
    async function carregarUsuarios() {
        try {
            const response = await fetch('usuarios?acao=listar');
            if (!response.ok) {
                throw new Error("Não foi possível carregar os usuários.");
            }
            const usuarios = await response.json();

            // Limpa a tabela
            tabelaUsuarios.innerHTML = '';

            if (usuarios.length === 0) {
                 tabelaUsuarios.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum usuário encontrado.</td></tr>';
                 return;
            }

            // Popula a tabela
            usuarios.forEach(usuario => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${usuario.id}</td>
                    <td>${usuario.nome}</td>
                    <td>${usuario.email}</td>
                    <td>${usuario.cpf ? 'Cliente' : 'Técnico'}</td> <td class="acao-coluna">
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${usuario.id}">
                            <i class="bi bi-pencil"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-danger btn-excluir" data-id="${usuario.id}">
                            <i class="bi bi-trash"></i> Excluir
                        </button>
                    </td>
                `;
                tabelaUsuarios.appendChild(tr);
            });

        } catch (error) {
            console.error("Erro ao carregar usuários:", error);
            tabelaUsuarios.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar.</td></tr>`;
        }
    }

    // --- 2. LIDAR COM O FORMULÁRIO (SALVAR) ---
    formUsuario.addEventListener("submit", async (e) => {
        e.preventDefault(); // Impede o envio tradicional do formulário

        const id = inputId.value;
        const ehEdicao = id > 0;

        // Monta o objeto JSON para enviar
        // (Simplificado apenas para Cliente, como no Servlet)
        const dadosUsuario = {
            id: id,
            nome: inputNome.value,
            email: inputEmail.value,
            senha: inputSenha.value, // O backend deve tratar senha vazia
            cpf: inputCpf.value,
            // (Adicionar empresaId)
        };

        try {
            const url = ehEdicao ? `usuarios?id=${id}` : 'usuarios';
            const method = ehEdicao ? 'PUT' : 'POST'; // (Nosso servlet só tem POST por enquanto)

            const response = await fetch('usuarios', {
                method: 'POST', // Mudar para 'PUT' quando o Servlet suportar
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dadosUsuario)
            });

            if (!response.ok) {
                throw new Error("Erro ao salvar usuário.");
            }

            modalUsuario.hide(); // Fecha o pop-up
            carregarUsuarios(); // Recarrega a tabela

        } catch (error) {
            console.error("Erro ao salvar:", error);
            alert("Erro ao salvar usuário.");
        }
    });

    // --- 3. LIDAR COM BOTÕES (NOVO, EDITAR, EXCLUIR) ---

    // Botão "Novo Usuário"
    btnNovoUsuario.addEventListener("click", () => {
        modalLabel.textContent = "Novo Usuário";
        formUsuario.reset(); // Limpa o formulário
        inputId.value = 0; // Garante que o ID é 0
    });

    // Botões na tabela (Editar e Excluir)
    tabelaUsuarios.addEventListener("click", async (e) => {
        const target = e.target.closest("button"); // Pega o botão clicado
        if (!target) return;

        const id = target.dataset.id; // Pega o data-id="X" do botão

        // Botão "Excluir"
        if (target.classList.contains("btn-excluir")) {
            // Confirmação
            if (confirm(`Tem certeza que deseja excluir o usuário ID ${id}?`)) {
                try {
                    const response = await fetch(`usuarios?id=${id}`, { method: 'DELETE' });
                    if (!response.ok) {
                        throw new Error("Falha ao excluir.");
                    }
                    carregarUsuarios(); // Recarrega a tabela
                } catch (error) {
                    console.error("Erro ao excluir:", error);
                    alert("Erro ao excluir usuário.");
                }
            }
        }

        // Botão "Editar"
        if (target.classList.contains("btn-editar")) {
            try {
                // 1. Busca os dados do usuário específico no Servlet
                const response = await fetch(`usuarios?acao=buscar&id=${id}`);
                if (!response.ok) {
                    throw new Error("Usuário não encontrado.");
                }
                const usuario = await response.json();

                // 2. Preenche o formulário no modal
                modalLabel.textContent = `Editando Usuário: ${usuario.nome}`;
                inputId.value = usuario.id;
                inputNome.value = usuario.nome;
                inputEmail.value = usuario.email;
                inputCpf.value = usuario.cpf;
                inputSenha.value = ""; // Senha nunca é preenchida
                // (Preencher tipo e empresa)

                // 3. Abre o modal
                modalUsuario.show();

            } catch (error) {
                console.error("Erro ao buscar usuário para edição:", error);
                alert("Não foi possível carregar os dados para edição.");
            }
        }
    });

    // --- INICIALIZAÇÃO ---
    carregarUsuarios();
});