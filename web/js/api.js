/**
 * js/api.js
 * Funções centralizadas para comunicação com o backend (Servlets)
 */

const api = {
    /**
     * Realiza uma requisição GET
     * @param {string} url - O endpoint do servlet (ex: 'UsuarioServlet?action=listar')
     */
    get: async (url) => {
        try {
            const response = await fetch(url);
            if (response.status === 401) {
                // Não autorizado (sessão expirou), redireciona para o login
                window.location.href = 'login.html';
                return;
            }
            if (!response.ok) {
                throw new Error(`Erro de rede: ${response.statusText}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Erro no API GET (${url}):`, error);
            showToast(`Erro ao buscar dados: ${error.message}`, 'error');
            throw error; // Propaga o erro para quem chamou
        }
    },

    /**
     * Realiza uma requisição POST com corpo JSON
     * @param {string} url - O endpoint do servlet (ex: 'UsuarioServlet?action=criar')
     * @param {object} data - O objeto a ser enviado como JSON
     */
    post: async (url, data) => {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.status === 401) {
                window.location.href = 'login.html';
                return;
            }

            // Tenta ler a resposta como JSON, mesmo se não for OK
            const result = await response.json();

            if (!response.ok) {
                // Se o backend enviou uma mensagem de erro específica
                throw new Error(result.message || `Erro no servidor: ${response.statusText}`);
            }

            return result; // Retorna o JSON de sucesso
        } catch (error) {
            console.error(`Erro no API POST (${url}):`, error);
            // Exibe o erro vindo do 'throw new Error'
            showToast(error.message, 'error');
            throw error;
        }
    }
};