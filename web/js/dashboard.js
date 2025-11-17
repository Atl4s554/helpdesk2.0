/*
 * Arquivo: web/js/dashboard.js
 * Descrição: Script comum para todas as páginas de dashboard.
 * Verifica a sessão do usuário.
 */
document.addEventListener("DOMContentLoaded", () => {

    // Pega o nome do usuário que o LoginServlet colocou na página
    const nomeUsuarioEl = document.getElementById('nome-usuario');

    // Função para buscar os dados da sessão do usuário
    async function carregarDadosSessao() {
        try {
            const response = await fetch('login?acao=verificar');

            if (response.status === 401 || !response.ok) {
                 // 401: Não autorizado. Chuta para o login.
                window.location.href = 'login.html';
                return;
            }

            const usuario = await response.json();

            // Atualiza o "Olá, [Nome]" na página
            if (nomeUsuarioEl && usuario.nome) {
                nomeUsuarioEl.textContent = usuario.nome;
            }

        } catch (error) {
            console.error("Erro ao carregar dados da sessão:", error);
            window.location.href = 'login.html';
        }
    }

    // Só executa se não estivermos na página de login
    if (!window.location.pathname.endsWith('login.html')) {
        carregarDadosSessao();
    }
});