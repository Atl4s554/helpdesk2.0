/**
 * Ponto de entrada principal quando o HTML do dashboard é carregado.
 */
document.addEventListener('DOMContentLoaded', () => {
    // 1. Define a aba 'dashboard' como ativa ao carregar
    mostrarSecao('dashboard');

    // 2. Adiciona os listeners (ouvintes) para os links da barra lateral
    configurarLinksSidebar();

    // 3. Adiciona o listener para o botão de 'Sair'
    document.querySelector('button[onclick="logout()"]').addEventListener('click', logout);
});

/**
 * Adiciona os eventos de clique a todos os links da barra lateral.
 * Isso evita a necessidade de 'onclick' no HTML.
 */
function configurarLinksSidebar() {
    const links = document.querySelectorAll('.sidebar nav a');
    links.forEach(link => {
        // Pega o ID da seção a partir do atributo onclick
        const secaoId = link.getAttribute('onclick').match(/'([^']+)'/)[1];
        if (secaoId) {
            link.addEventListener('click', (event) => {
                event.preventDefault(); // Impede o link de navegar
                mostrarSecao(secaoId);
            });
        }
    });
}

/**
 * Função central para mostrar uma seção e esconder as outras.
 * @param {string} idSecao - O ID da seção para mostrar (ex: 'dashboard', 'usuarios').
 */
function mostrarSecao(idSecao) {
    // 1. Esconde todas as seções
    const secoes = document.querySelectorAll('.main-content section');
    secoes.forEach(secao => {
        secao.classList.remove('secao-ativa');
        secao.classList.add('secao-oculta');
    });

    // 2. Mostra a seção desejada
    const secaoAlvo = document.getElementById('secao-' + idSecao);
    if (secaoAlvo) {
        secaoAlvo.classList.remove('secao-oculta');
        secaoAlvo.classList.add('secao-ativa');
    }

    // 3. Atualiza o link "ativo" na barra lateral
    const links = document.querySelectorAll('.sidebar nav a');
    links.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('onclick').includes(`'${idSecao}'`)) {
            link.classList.add('active');
        }
    });

    // 4. Carrega os dados específicos da seção
    carregarDadosDaSecao(idSecao);
}

/**
 * Direciona o carregamento de dados com base na seção ativa.
 * @param {string} idSecao - O ID da seção que acabou de ser aberta.
 */
function carregarDadosDaSecao(idSecao) {
    switch (idSecao) {
        case 'dashboard':
            carregarEstatisticas();
            carregarLogsRecentes();
            break;
        case 'usuarios':
            // Funções a serem implementadas
            // carregarClientes();
            // carregarTecnicos();
            break;
        case 'logs':
            // Função a ser implementada
            // carregarLogsCompletos();
            break;
        // Adicionar outros 'cases' para 'empresas', 'chamados', etc.
    }
}

/**
 * * 📊 PASSO 2: BUSCAR ESTATÍSTICAS (MySQL)
 * Busca os dados dos cards do dashboard (Chamados Abertos, etc.).
 * Esta função chama o 'DashboardStatsServlet' que criaremos.
 */
async function carregarEstatisticas() {
    // URLs dos Servlets que criaremos
    const url = 'api/stats'; // Este Servlet buscará as contagens no MySQL

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const stats = await response.json();

        // Atualiza os números nos cards do HTML
        document.getElementById('stat-abertos').textContent = stats.abertos || 0;
        document.getElementById('stat-atendimento').textContent = stats.emAtendimento || 0;
        document.getElementById('stat-fechados').textContent = stats.fechadosHoje || 0;
        document.getElementById('stat-usuarios').textContent = stats.totalUsuarios || 0;

    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
        // Exibe 'Erro' nos cards se a busca falhar
        document.getElementById('stat-abertos').textContent = 'Erro';
        document.getElementById('stat-atendimento').textContent = 'Erro';
        document.getElementById('stat-fechados').textContent = 'Erro';
        document.getElementById('stat-usuarios').textContent = 'Erro';
    }
}

/**
 * 📝 PASSO 3: BUSCAR LOGS RECENTES (MongoDB)
 * Busca os últimos 5 logs para o painel "Atividades Recentes".
 * Esta função chama o 'LogServlet' que criaremos.
 */
async function carregarLogsRecentes() {
    const container = document.getElementById('logs-recentes');
    container.innerHTML = '<p>Carregando...</p>';

    // Este Servlet buscará os dados no MongoDB
    // Pedimos os 5 mais recentes (limite=5)
    const url = 'api/logs?limite=5';

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const logs = await response.json();

        // Limpa o container
        container.innerHTML = '';

        if (!logs || logs.length === 0) {
            container.innerHTML = '<p>Nenhuma atividade recente.</p>';
            return;
        }

        // Cria o HTML para cada log
        logs.forEach(log => {
            const logEntry = document.createElement('div');
            logEntry.className = 'log-item'; // Você pode estilizar .log-item no seu CSS

            // Converte o timestamp (que vem como string ou objeto) para um formato legível
            const dataFormatada = new Date(log.timestamp).toLocaleString('pt-BR');

            logEntry.innerHTML = `
                <span class="log-timestamp"><strong>${dataFormatada}</strong></span>
                <span class="log-tipo">[${log.tipo}]</span>
                <span class="log-mensagem">${log.mensagem}</span>
            `;
            container.appendChild(logEntry);
        });

    } catch (error) {
        console.error('Erro ao carregar logs recentes:', error);
        container.innerHTML = '<p>Erro ao carregar atividades.</p>';
    }
}

/**
 * 🚪 PASSO 1: FUNÇÃO DE LOGOUT
 * Redireciona para o Servlet de Logout.
 */
function logout() {
    // Vamos mapear seu LogoutServlet para esta URL no web.xml
    window.location.href = 'api/logout';
}