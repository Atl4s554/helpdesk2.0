/**
 * Ponto de entrada principal quando o HTML do dashboard é carregado.
 */
document.addEventListener('DOMContentLoaded', () => {
    mostrarSecao('dashboard');
    configurarLinksSidebar();
});

function configurarLinksSidebar() {
    const links = document.querySelectorAll('.sidebar nav a');
    links.forEach(link => {
        const secaoId = link.getAttribute('onclick').match(/'([^']+)'/)[1];
        if (secaoId) {
            link.addEventListener('click', (event) => {
                event.preventDefault();
                mostrarSecao(secaoId);
            });
        }
    });
}

function mostrarSecao(idSecao) {
    const secoes = document.querySelectorAll('.main-content section');
    secoes.forEach(secao => {
        secao.classList.remove('secao-ativa');
        secao.classList.add('secao-oculta');
    });

    const secaoAlvo = document.getElementById('secao-' + idSecao);
    if (secaoAlvo) {
        secaoAlvo.classList.remove('secao-oculta');
        secaoAlvo.classList.add('secao-ativa');
    }

    const links = document.querySelectorAll('.sidebar nav a');
    links.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('onclick').includes(`'${idSecao}'`)) {
            link.classList.add('active');
        }
    });

    carregarDadosDaSecao(idSecao);
}

function carregarDadosDaSecao(idSecao) {
    // RESTAURANDO A LÓGICA DE FETCH (API)
    switch (idSecao) {
        case 'dashboard':
            carregarEstatisticas();
            carregarLogsRecentes();
            break;
        case 'usuarios':
            // FUNÇÕES QUE JÁ ESTAVAM FUNCIONANDO
            carregarClientes();
            carregarTecnicos();
            break;
        // ... outros cases
    }
}

// FUNÇÕES DE FETCH RESTAURADAS

async function carregarEstatisticas() {
    const url = 'api/stats';
    try {
        const response = await fetch(url);
        const stats = await response.json();
        // Atualiza os números nos cards do HTML (assume que o Servlet foi corrigido)
        document.getElementById('stat-abertos').textContent = stats.abertos || 0;
        document.getElementById('stat-atendimento').textContent = stats.emAtendimento || 0;
        document.getElementById('stat-fechados').textContent = stats.fechadosHoje || 0;
        document.getElementById('stat-usuarios').textContent = stats.totalUsuarios || 0;
    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    }
}

async function carregarLogsRecentes() {
    const container = document.getElementById('logs-recentes');
    const url = 'api/logs?limite=5';
    try {
        const response = await fetch(url);
        const logs = await response.json();
        container.innerHTML = '';
        if (logs && logs.length > 0) {
            logs.forEach(log => {
                const dataTimestamp = log.timestamp && log.timestamp.$date ? log.timestamp.$date : log.timestamp;
                const dataFormatada = new Date(dataTimestamp).toLocaleString('pt-BR');
                const logMensagem = log.mensagem || 'Mensagem não disponível';

                container.innerHTML += `
                    <div class="log-item">
                        <span class="log-timestamp"><strong>${dataFormatada}</strong></span>
                        <span class="log-tipo">[${log.tipo}]</span>
                        <span class="log-mensagem">${logMensagem}</span>
                    </div>
                `;
            });
        } else {
             container.innerHTML = '<p>Nenhuma atividade recente.</p>';
        }
    } catch (error) {
        console.error('Erro ao carregar logs recentes:', error);
        container.innerHTML = '<p>Erro ao carregar atividades.</p>';
    }
}

function carregarTecnicos() {
    // Lógica que funcionava para popular a tabela de Técnicos
    const url = 'api/tecnicos';
    const tbody = document.querySelector('#tabela-tecnicos tbody');
    tbody.innerHTML = '<tr><td colspan="4">Carregando Técnicos...</td></tr>';
    fetch(url).then(response => response.json()).then(data => {
        tbody.innerHTML = '';
        data.forEach(tecnico => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${tecnico.id}</td>
                <td>${tecnico.nome}</td>
                <td>${tecnico.email}</td>
                <td>${tecnico.especialidade || 'N/A'}</td>
            `;
            tbody.appendChild(tr);
        });
    }).catch(error => console.error('Erro ao carregar técnicos:', error));
}

function carregarClientes() {
    // Lógica que funcionava para popular a tabela de Clientes
    const url = 'api/clientes';
    const tbody = document.querySelector('#tabela-clientes tbody');
    tbody.innerHTML = '<tr><td colspan="4">Carregando Clientes...</td></tr>';
    fetch(url).then(response => response.json()).then(data => {
        tbody.innerHTML = '';
        data.forEach(cliente => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${cliente.id}</td>
                <td>${cliente.nome}</td>
                <td>${cliente.email}</td>
                <td>${cliente.cpf || 'N/A'}</td>
            `;
            tbody.appendChild(tr);
        });
    }).catch(error => console.error('Erro ao carregar clientes:', error));
}

// ... Outras funções como mostrarFormNovoCliente, etc.