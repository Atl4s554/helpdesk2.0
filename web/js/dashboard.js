/**
 * js/dashboard.js
 * Lógica principal do dashboard (SPA - Single Page Application)
 */

// Elementos Globais
const sidebarNav = document.getElementById('sidebar-nav');
const mainContent = document.getElementById('main-content');
const welcomeMessage = document.getElementById('welcome-message');
const logoutBtn = document.getElementById('logout-btn');

let currentUser = null; // Armazena os dados do usuário logado

// ========================================
// INICIALIZAÇÃO E SESSÃO
// ========================================

// Evento que dispara quando o DOM está pronto
document.addEventListener('DOMContentLoaded', async () => {
    await checkSession();

    if (currentUser) {
        renderSidebar(currentUser.perfil);
        setupNavigationListeners();

        // Carrega a view inicial (Dashboard)
        loadView('dashboard');
    }
});

/**
 * Verifica se o usuário tem uma sessão ativa no backend.
 */
async function checkSession() {
    try {
        const data = await api.get('SessionCheckServlet');
        if (data && data.usuario) {
            currentUser = data.usuario;
            welcomeMessage.textContent = `Bem-vindo(a), ${currentUser.nome.split(' ')[0]}!`;
        } else {
            throw new Error('Sessão inválida.');
        }
    } catch (error) {
        console.error('Falha ao verificar sessão:', error);
        window.location.href = 'login.html'; // Redireciona se não houver sessão
    }
}

// Listener do botão de Logout
logoutBtn.addEventListener('click', async () => {
    try {
        await api.get('LogoutServlet');
    } catch (error) {
        // Ignora erros no logout, apenas redireciona
    } finally {
        window.location.href = 'login.html';
    }
});


// ========================================
// RENDERIZAÇÃO DA BARRA LATERAL (SIDEBAR)
// ========================================

/**
 * Constrói os links da barra lateral com base no perfil do usuário.
 * @param {'ADMIN' | 'TECNICO' | 'CLIENTE'} perfil
 */
function renderSidebar(perfil) {
    let navHtml = '';

    // Link comum a todos
    navHtml += `<a href="#" data-view="dashboard" class="active"><i class="fas fa-fw fa-tachometer-alt"></i> Dashboard</a>`;

    if (perfil === 'ADMIN') {
        navHtml += `
            <div class="nav-submenu-title">Gerenciamento</div>
            <a href="#" data-view="usuarios"><i class="fas fa-fw fa-users-cog"></i> Usuários</a>
            <a href="#" data-view="empresas"><i class="fas fa-fw fa-building"></i> Empresas</a>
            <a href="#" data-view="chamados-admin"><i class="fas fa-fw fa-ticket-alt"></i> Todos Chamados</a>
            <a href="#" data-view="logs"><i class="fas fa-fw fa-clipboard-list"></i> Logs do Sistema</a>
        `;
    }

    if (perfil === 'TECNICO') {
        navHtml += `
            <div class="nav-submenu-title">Atendimento</div>
            <a href="#" data-view="chamados-meus-tec"><i class="fas fa-fw fa-user-clock"></i> Meus Chamados</a>
            <a href="#" data-view="chamados-abertos"><i class="fas fa-fw fa-inbox"></i> Chamados Abertos</a>
        `;
    }

    if (perfil === 'CLIENTE') {
        navHtml += `
            <div class="nav-submenu-title">Chamados</div>
            <a href="#" data-view="chamado-novo"><i class="fas fa-fw fa-plus-circle"></i> Abrir Chamado</a>
            <a href="#" data-view="chamados-meus-cli"><i class="fas fa-fw fa-history"></i> Meus Chamados</a>
        `;
    }

    sidebarNav.innerHTML = navHtml;
}


// ========================================
// NAVEGAÇÃO E CARREGAMENTO DE VIEWS
// ========================================

/**
 * Adiciona listeners de clique aos links da barra lateral.
 */
function setupNavigationListeners() {
    sidebarNav.addEventListener('click', (event) => {
        const link = event.target.closest('a[data-view]');
        if (link) {
            event.preventDefault();
            const viewName = link.dataset.view;

            // Remove 'active' de todos e adiciona no clicado
            sidebarNav.querySelectorAll('a').forEach(a => a.classList.remove('active'));
            link.classList.add('active');

            // Carrega a view
            loadView(viewName);
        }
    });
}

/**
 * Função "roteadora" principal. Carrega o conteúdo na <main>.
 * @param {string} viewName - O nome da view (definido no 'data-view' do link).
 */
function loadView(viewName) {
    // Limpa o conteúdo principal e mostra um "loading"
    mainContent.innerHTML = '<div class="page-header"><h2>Carregando...</h2></div>';

    switch (viewName) {
        case 'dashboard':
            loadViewDashboard();
            break;
        // Admin
        case 'usuarios':
            loadViewGestaoUsuarios();
            break;
        case 'empresas':
            loadViewGestaoEmpresas();
            break;
        case 'chamados-admin':
            loadViewChamadosAdmin();
            break;
        case 'logs':
            loadViewLogs();
            break;
        // Técnico
        case 'chamados-meus-tec':
            loadViewChamadosTecnico(); // Chamados atribuídos a ele
            break;
        case 'chamados-abertos':
            loadViewChamadosAbertos(); // Fila de chamados
            break;
        // Cliente
        case 'chamado-novo':
            loadViewAbrirChamado();
            break;
        case 'chamados-meus-cli':
            loadViewChamadosCliente();
            break;
        default:
            mainContent.innerHTML = '<div class="page-header"><h2>Página não encontrada</h2></div>';
    }
}

// ========================================
// FUNÇÕES DE "VIEW" (DASHBOARD)
// ========================================

/**
 * Carrega o conteúdo da view principal do Dashboard (Estatísticas).
 */
async function loadViewDashboard() {
    try {
        const data = await api.get('DashboardServlet');

        let statsHtml = '<div class="stats-grid">';

        // Stats do Admin
        if (data.totalChamadosAbertos !== undefined) {
            statsHtml += `
                <div class="stat-card">
                    <div class="stat-card-icon red"><i class="fas fa-exclamation-circle"></i></div>
                    <div class="stat-card-info"><h4>${data.totalChamadosAbertos}</h4><p>Chamados Abertos</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon yellow"><i class="fas fa-tasks"></i></div>
                    <div class="stat-card-info"><h4>${data.totalChamadosAtendimento}</h4><p>Em Atendimento</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon green"><i class="fas fa-check-circle"></i></div>
                    <div class="stat-card-info"><h4>${data.totalChamadosFechados}</h4><p>Fechados (Total)</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon grey"><i class="fas fa-users"></i></div>
                    <div class="stat-card-info"><h4>${data.totalClientes}</h4><p>Clientes</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon grey"><i class="fas fa-user-tie"></i></div>
                    <div class="stat-card-info"><h4>${data.totalTecnicos}</h4><p>Técnicos</p></div>
                </div>
            `;
        }

        // Stats do Técnico
        if (data.atendimentosHoje !== undefined) {
            statsHtml += `
                <div class="stat-card">
                    <div class="stat-card-icon green"><i class="fas fa-check"></i></div>
                    <div class="stat-card-info"><h4>${data.atendimentosHoje}</h4><p>Resolvidos Hoje</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon yellow"><i class="fas fa-tasks"></i></div>
                    <div class="stat-card-info"><h4>${data.atendimentosMes}</h4><p>Resolvidos no Mês</p></div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-icon blue"><i class="fas fa-user-clock"></i></div>
                    <div class="stat-card-info"><h4>${data.mediaTempo} min</h4><p>Tempo Médio Resposta</p></div>
                </div>
            `;
        }

        statsHtml += '</div>';

        // Logs (se for admin)
        let logsHtml = '';
        if (data.ultimosLogs) {
            logsHtml = `
                <div class="card" style="margin-top: 2rem;">
                    <div class="card-header"><h3>Últimos 10 Logs do Sistema</h3></div>
                    <div class="card-content">
                        <ul class="log-list">
            `;
            if (data.ultimosLogs.length > 0) {
                data.ultimosLogs.forEach(log => {
                    logsHtml += `
                        <li>
                            <span class="log-message">[${log.tipo}] ${log.mensagem} (User: ${log.idUsuario})</span>
                            <span class="log-timestamp">${new Date(log.timestamp).toLocaleString('pt-BR')}</span>
                        </li>
                    `;
                });
            } else {
                logsHtml += '<li>Nenhum log encontrado.</li>';
            }
            logsHtml += '</ul></div></div>';
        }

        mainContent.innerHTML = `
            <div class="page-header"><h2>Dashboard</h2></div>
            ${statsHtml}
            ${logsHtml}
        `;

    } catch (error) {
        mainContent.innerHTML = '<div class="page-header"><h2>Erro ao carregar dashboard.</h2></div>';
    }
}

// ========================================
// FUNÇÕES DE "VIEW" (ADMIN)
// ========================================

/**
 * Carrega a view "Gerenciar Usuários"
 */
async function loadViewGestaoUsuarios() {
    mainContent.innerHTML = `
        <div class="page-header">
            <h2>Gerenciar Usuários</h2>
            <button id="btn-novo-usuario" class="btn btn-primary"><i class="fas fa-plus"></i> Novo Usuário</button>
        </div>
        <div class="card">
            <div class="card-content" id="tabela-usuarios-container">
                <p>Carregando tabela...</p>
            </div>
        </div>
    `;

    document.getElementById('btn-novo-usuario').addEventListener('click', showFormNovoUsuario);

    try {
        const data = await api.get('UsuarioServlet?action=listar');

        renderTable('#tabela-usuarios-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'nome', label: 'Nome', sortable: true },
                { key: 'email', label: 'Email', sortable: true },
                { key: 'cpf', label: 'CPF', sortable: true },
                { key: 'perfil', label: 'Perfil', sortable: true, formatter: (val) => val === 'CLIENTE' ? 'Cliente' : 'Técnico' },
                { key: 'especialidade', label: 'Especialidade', formatter: (val) => val || 'N/A' }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-secondary btn-sm" onclick="showFormEditarUsuario(${row.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn btn-danger btn-sm" onclick="deleteUsuario(${row.id})"><i class="fas fa-trash"></i></button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-usuarios-container').innerHTML = '<p>Erro ao carregar usuários.</p>';
    }
}

/**
 * Exibe o modal para criar um novo usuário.
 */
function showFormNovoUsuario() {
    const formHtml = `
        <form id="form-usuario">
            <div class="form-grid">
                <div class="input-group">
                    <label for="nome">Nome Completo*</label>
                    <input type="text" id="nome" name="nome" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group">
                    <label for="email">Email*</label>
                    <input type="email" id="email" name="email" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group">
                    <label for="senha">Senha*</label>
                    <input type="password" id="senha" name="senha" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group">
                    <label for="cpf">CPF*</label>
                    <input type="text" id="cpf" name="cpf" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group">
                    <label for="perfil">Perfil*</label>
                    <select id="perfil" name="perfil" required onchange="toggleEspecialidade(this.value)">
                        <option value="">Selecione...</option>
                        <option value="CLIENTE">Cliente</option>
                        <option value="TECNICO">Técnico</option>
                    </select>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group" id="group-especialidade" style="display: none;">
                    <label for="especialidade">Especialidade</label>
                    <input type="text" id="especialidade" name="especialidade">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar Usuário</button>
            </div>
        </form>
    `;
    openModal('Novo Usuário', formHtml, () => {
        // Adiciona listener de submit ao formulário DENTRO do modal
        document.getElementById('form-usuario').addEventListener('submit', handleUsuarioSubmit);
    });
}

/**
 * Exibe o modal para editar um usuário (função chamada pelo onclick)
 */
window.showFormEditarUsuario = async (id) => {
    // Esta função precisaria buscar os dados do usuário primeiro
    // Por simplicidade do MVP, vamos focar no 'criar' e 'listar'
    showToast('Função "Editar" ainda não implementada.', 'error');

    // Lógica futura:
    // const usuario = await api.get(`UsuarioServlet?action=buscar&id=${id}`);
    // openModal('Editar Usuário', formHtml, () => { ... preencher campos ... });
}

/**
 * Processa o submit do formulário de usuário (novo ou edição)
 */
async function handleUsuarioSubmit(event) {
    event.preventDefault();
    const form = event.target;

    // Validação simples (conforme 6.2)
    let isValid = true;
    form.querySelectorAll('[required]').forEach(input => {
        const errorSpan = input.nextElementSibling;
        if (!input.value) {
            isValid = false;
            if (errorSpan && errorSpan.classList.contains('form-validation-error')) {
                errorSpan.style.display = 'block';
            }
        } else {
            if (errorSpan && errorSpan.classList.contains('form-validation-error')) {
                errorSpan.style.display = 'none';
            }
        }
    });

    if (!isValid) {
        showToast('Preencha todos os campos obrigatórios.', 'error');
        return;
    }

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    try {
        // Usaremos 'action=criar' (a lógica de edição seria diferente)
        await api.post('UsuarioServlet?action=criar', data);
        showToast('Usuário criado com sucesso!', 'success');
        closeModal();
        loadView('usuarios'); // Recarrega a tabela
    } catch (error) {
        // O toast de erro já é exibido pela api.post()
    }
}

/**
 * Função global para o onchange do select de perfil
 */
window.toggleEspecialidade = (perfil) => {
    const group = document.getElementById('group-especialidade');
    if (perfil === 'TECNICO') {
        group.style.display = 'block';
    } else {
        group.style.display = 'none';
    }
}

/**
 * Deleta um usuário (função chamada pelo onclick)
 */
window.deleteUsuario = async (id) => {
    if (confirm(`Tem certeza que deseja excluir o usuário ID ${id}?`)) {
        try {
            await api.post('UsuarioServlet?action=excluir', { id: id });
            showToast('Usuário excluído com sucesso!', 'success');
            loadView('usuarios'); // Recarrega a tabela
        } catch (error) {
            // O toast de erro já é exibido
        }
    }
}

/**
 * Carrega a view "Gerenciar Empresas"
 */
async function loadViewGestaoEmpresas() {
    mainContent.innerHTML = `
        <div class="page-header">
            <h2>Gerenciar Empresas</h2>
            <button id="btn-nova-empresa" class="btn btn-primary"><i class="fas fa-plus"></i> Nova Empresa</button>
        </div>
        <div class="card">
            <div class="card-content" id="tabela-empresas-container">
                <p>Carregando tabela...</p>
            </div>
        </div>
    `;

    document.getElementById('btn-nova-empresa').addEventListener('click', showFormNovaEmpresa);

    try {
        const data = await api.get('EmpresaServlet?action=listar');
        renderTable('#tabela-empresas-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'nome', label: 'Nome Fantasia', sortable: true },
                { key: 'razaoSocial', label: 'Razão Social', sortable: true },
                { key: 'cnpj', label: 'CNPJ', sortable: true }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-secondary btn-sm" onclick="showToast('Editar não implementado.', 'error')"><i class="fas fa-edit"></i></button>
                    <button class="btn btn-danger btn-sm" onclick="showToast('Excluir não implementado.', 'error')"><i class="fas fa-trash"></i></button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-empresas-container').innerHTML = '<p>Erro ao carregar empresas.</p>';
    }
}

function showFormNovaEmpresa() {
     const formHtml = `
        <form id="form-empresa">
            <div class="form-grid">
                <div class="input-group">
                    <label for="nome">Nome Fantasia*</label>
                    <input type="text" id="nome" name="nome" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group">
                    <label for="razaoSocial">Razão Social*</label>
                    <input type="text" id="razaoSocial" name="razaoSocial" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="input-group full-width">
                    <label for="cnpj">CNPJ*</label>
                    <input type="text" id="cnpj" name="cnpj" required>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar Empresa</button>
            </div>
        </form>
    `;
    openModal('Nova Empresa', formHtml, () => {
        document.getElementById('form-empresa').addEventListener('submit', async (event) => {
            event.preventDefault();
            const form = event.target;
            if (!form.nome.value || !form.razaoSocial.value || !form.cnpj.value) {
                showToast('Preencha todos os campos.', 'error');
                return;
            }
            const data = {
                nome: form.nome.value,
                razaoSocial: form.razaoSocial.value,
                cnpj: form.cnpj.value
            };
            try {
                await api.post('EmpresaServlet?action=criar', data);
                showToast('Empresa criada com sucesso!', 'success');
                closeModal();
                loadView('empresas');
            } catch (e) {}
        });
    });
}


/**
 * Carrega a view "Todos os Chamados" (Admin)
 */
async function loadViewChamadosAdmin() {
    mainContent.innerHTML = `
        <div class="page-header"><h2>Todos os Chamados</h2></div>
        <div class="card">
            <div class="card-content" id="tabela-chamados-admin-container">
                <p>Carregando chamados...</p>
            </div>
        </div>
    `;

    try {
        const data = await api.get('ChamadoServlet?action=listarTodos');
        renderTable('#tabela-chamados-admin-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'titulo', label: 'Título', sortable: true },
                { key: 'status', label: 'Status', sortable: true, formatter: formatarStatus },
                { key: 'prioridade', label: 'Prioridade', sortable: true },
                { key: 'nomeCliente', label: 'Cliente', sortable: true },
                { key: 'nomeTecnico', label: 'Técnico', sortable: true, formatter: (val) => val || 'Não atribuído' },
                { key: 'dataAbertura', label: 'Abertura', sortable: true, formatter: (val) => new Date(val).toLocaleDateString('pt-BR') }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-secondary btn-sm" onclick="showModalAtribuir(${row.id})"><i class="fas fa-user-plus"></i> Atribuir</button>
                    <button class="btn btn-secondary btn-sm" onclick="verHistorico(${row.id})"><i class="fas fa-history"></i></button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-chamados-admin-container').innerHTML = '<p>Erro ao carregar chamados.</p>';
    }
}

/**
 * Exibe o modal para atribuir um técnico (Admin)
 */
window.showModalAtribuir = async (chamadoId) => {
    try {
        // Busca a lista de técnicos
        const tecnicos = await api.get('UsuarioServlet?action=listarTecnicos');
        let optionsHtml = '<option value="">Selecione um técnico...</option>';
        tecnicos.forEach(tec => {
            optionsHtml += `<option value="${tec.id}">${tec.nome} (${tec.especialidade})</option>`;
        });

        const formHtml = `
            <form id="form-atribuir">
                <input type="hidden" name="chamadoId" value="${chamadoId}">
                <div class="input-group full-width">
                    <label for="tecnicoId">Técnico*</label>
                    <select id="tecnicoId" name="tecnicoId" required>
                        ${optionsHtml}
                    </select>
                    <span class="form-validation-error">Campo obrigatório.</span>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Atribuir</button>
                </div>
            </form>
        `;
        openModal(`Atribuir Chamado #${chamadoId}`, formHtml, () => {
            document.getElementById('form-atribuir').addEventListener('submit', async (event) => {
                event.preventDefault();
                const data = {
                    chamadoId: chamadoId,
                    tecnicoId: event.target.tecnicoId.value
                };
                if (!data.tecnicoId) {
                    showToast('Selecione um técnico.', 'error');
                    return;
                }
                try {
                    await api.post('ChamadoServlet?action=atribuir', data);
                    showToast('Técnico atribuído com sucesso!', 'success');
                    closeModal();
                    loadView('chamados-admin');
                } catch (e) {}
            });
        });
    } catch (error) {
        showToast('Erro ao carregar lista de técnicos.', 'error');
    }
}

/**
 * Carrega a view "Logs do Sistema"
 */
async function loadViewLogs() {
    mainContent.innerHTML = `
        <div class="page-header"><h2>Logs do Sistema</h2></div>
        <div class="card">
            <div class="card-content" id="logs-container">
                <p>Carregando logs...</p>
            </div>
        </div>
    `;
    try {
        const logs = await api.get('LogServlet?action=listarTodos'); // Você precisará criar este Servlet
        let logsHtml = '<ul class="log-list">';
        if (logs.length > 0) {
            logs.forEach(log => {
                logsHtml += `
                    <li>
                        <span class="log-message">[${log.tipo}] ${log.mensagem} (User: ${log.idUsuario})</span>
                        <span class="log-timestamp">${new Date(log.timestamp).toLocaleString('pt-BR')}</span>
                    </li>
                `;
            });
        } else {
            logsHtml += '<li>Nenhum log encontrado.</li>';
        }
        logsHtml += '</ul>';
        document.getElementById('logs-container').innerHTML = logsHtml;
    } catch (error) {
         document.getElementById('logs-container').innerHTML = '<p>Erro ao carregar logs. Verifique se o LogServlet está configurado.</p>';
    }
}


// ========================================
// FUNÇÕES DE "VIEW" (TÉCNICO)
// ========================================

/**
 * Carrega a view "Meus Chamados" (Técnico)
 */
async function loadViewChamadosTecnico() {
    mainContent.innerHTML = `
        <div class="page-header"><h2>Meus Chamados Atribuídos</h2></div>
        <div class="card">
            <div class="card-content" id="tabela-chamados-tec-container">
                <p>Carregando chamados...</p>
            </div>
        </div>
    `;

    try {
        const data = await api.get('ChamadoServlet?action=listarAtribuidos');
        renderTable('#tabela-chamados-tec-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'titulo', label: 'Título', sortable: true },
                { key: 'status', label: 'Status', sortable: true, formatter: formatarStatus },
                { key: 'prioridade', label: 'Prioridade', sortable: true },
                { key: 'nomeCliente', label: 'Cliente', sortable: true },
                { key: 'dataAbertura', label: 'Abertura', sortable: true, formatter: (val) => new Date(val).toLocaleDateString('pt-BR') }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-primary btn-sm" onclick="showFormAtendimento(${row.id})"><i class="fas fa-play"></i> Atender</button>
                    <button class="btn btn-secondary btn-sm" onclick="verHistorico(${row.id})"><i class="fas fa-history"></i></button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-chamados-tec-container').innerHTML = '<p>Erro ao carregar chamados.</p>';
    }
}

/**
 * Carrega a view "Chamados Abertos" (Técnico)
 */
async function loadViewChamadosAbertos() {
    mainContent.innerHTML = `
        <div class="page-header"><h2>Fila de Chamados Abertos</h2></div>
        <div class="card">
            <div class="card-content" id="tabela-chamados-abertos-container">
                <p>Carregando chamados...</p>
            </div>
        </div>
    `;

    try {
        const data = await api.get('ChamadoServlet?action=listarAbertos');
        renderTable('#tabela-chamados-abertos-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'titulo', label: 'Título', sortable: true },
                { key: 'prioridade', label: 'Prioridade', sortable: true },
                { key: 'nomeCliente', label: 'Cliente', sortable: true },
                { key: 'nomeEmpresa', label: 'Empresa', sortable: true },
                { key: 'dataAbertura', label: 'Abertura', sortable: true, formatter: (val) => new Date(val).toLocaleDateString('pt-BR') }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-primary btn-sm" onclick="pegarChamado(${row.id})"><i class="fas fa-hand-paper"></i> Pegar</button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-chamados-abertos-container').innerHTML = '<p>Erro ao carregar chamados.</p>';
    }
}

/**
 * Ação do técnico para pegar um chamado (atribuir a si mesmo)
 */
window.pegarChamado = async (chamadoId) => {
    if (confirm(`Tem certeza que deseja pegar o chamado #${chamadoId}?`)) {
        try {
            await api.post('ChamadoServlet?action=pegar', { chamadoId: chamadoId });
            showToast('Chamado atribuído a você!', 'success');
            // Recarrega as duas views de técnico
            loadView('chamados-abertos');
            // (Idealmente, só recarregaria a view atual, mas por simplicidade vamos recarregar esta)
            // Para atualizar as duas, o ideal seria o usuário clicar na outra aba.
        } catch (error) {}
    }
}

/**
 * Mostra o formulário para o técnico registrar um atendimento
 */
window.showFormAtendimento = (chamadoId) => {
    const formHtml = `
        <form id="form-atendimento">
            <input type="hidden" name="chamadoId" value="${chamadoId}">
            <div class="input-group full-width">
                <label for="descricao">Descrição do Atendimento*</label>
                <textarea id="descricao" name="descricao" required></textarea>
                <span class="form-validation-error">Campo obrigatório.</span>
            </div>
            <div class="form-grid">
                <div class="input-group">
                    <label for="novoStatus">Atualizar Status*</label>
                    <select id="novoStatus" name="novoStatus" required>
                        <option value="EM_ATENDIMENTO">Em Atendimento</option>
                        <option value="FECHADO">Fechado (Resolvido)</option>
                    </select>
                </div>
                <div class="input-group">
                    <label for="tempoGasto">Tempo Gasto (minutos)</label>
                    <input type="number" id="tempoGasto" name="tempoGasto" value="30">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Registrar Atendimento</button>
            </div>
        </form>
    `;
    openModal(`Atender Chamado #${chamadoId}`, formHtml, () => {
        document.getElementById('form-atendimento').addEventListener('submit', async (event) => {
            event.preventDefault();
            const form = event.target;
            if (!form.descricao.value) {
                showToast('A descrição é obrigatória.', 'error');
                return;
            }
            const data = {
                chamadoId: chamadoId,
                descricao: form.descricao.value,
                novoStatus: form.novoStatus.value,
                tempoGasto: form.tempoGasto.value || 0
            };
            try {
                // Usaremos o AtendimentoServlet para isso
                await api.post('AtendimentoServlet?action=registrar', data);
                showToast('Atendimento registrado com sucesso!', 'success');
                closeModal();
                loadView('chamados-meus-tec'); // Recarrega a lista
            } catch (e) {}
        });
    });
}


// ========================================
// FUNÇÕES DE "VIEW" (CLIENTE)
// ========================================

/**
 * Carrega a view "Abrir Novo Chamado" (Cliente)
 */
async function loadViewAbrirChamado() {
    let empresaOptions = '<option value="">Carregando empresas...</option>';
    try {
        const empresas = await api.get('EmpresaServlet?action=listar');
        empresaOptions = empresas.map(emp => `<option value="${emp.id}">${emp.nome}</option>`).join('');
    } catch (e) {
        empresaOptions = '<option value="">Não foi possível carregar empresas</option>';
    }

    mainContent.innerHTML = `
        <div class="page-header"><h2>Abrir Novo Chamado</h2></div>
        <div class="card">
            <div class="card-content">
                <form id="form-novo-chamado">
                    <div class="form-grid">
                        <div class="input-group full-width">
                            <label for="titulo">Título*</label>
                            <input type="text" id="titulo" name="titulo" required>
                            <span class="form-validation-error">Campo obrigatório.</span>
                        </div>
                        <div class="input-group">
                            <label for="idEmpresa">Minha Empresa*</label>
                            <select id="idEmpresa" name="idEmpresa" required>
                                <option value="">Selecione...</option>
                                ${empresaOptions}
                            </select>
                            <span class="form-validation-error">Campo obrigatório.</span>
                        </div>
                        <div class="input-group">
                            <label for="prioridade">Prioridade*</label>
                            <select id="prioridade" name="prioridade" required>
                                <option value="BAIXA">Baixa</option>
                                <option value="MEDIA">Média</option>
                                <option value="ALTA">Alta</option>
                            </select>
                            <span class="form-validation-error">Campo obrigatório.</span>
                        </div>
                        <div class="input-group full-width">
                            <label for="descricao">Descrição do Problema*</label>
                            <textarea id="descricao" name="descricao" rows="6" required></textarea>
                            <span class="form-validation-error">Campo obrigatório.</span>
                        </div>
                    </div>
                    <div class="btn-group" style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary">Abrir Chamado</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    document.getElementById('form-novo-chamado').addEventListener('submit', async (event) => {
        event.preventDefault();
        const form = event.target;

        // Validação
        let isValid = true;
        form.querySelectorAll('[required]').forEach(input => {
            if (!input.value) isValid = false;
        });

        if (!isValid) {
            showToast('Preencha todos os campos obrigatórios.', 'error');
            return;
        }

        const data = {
            titulo: form.titulo.value,
            idEmpresa: form.idEmpresa.value,
            prioridade: form.prioridade.value,
            descricao: form.descricao.value
        };

        try {
            await api.post('ChamadoServlet?action=criar', data);
            showToast('Chamado aberto com sucesso!', 'success');
            // Limpa o formulário e muda para a view "Meus Chamados"
            loadView('chamados-meus-cli');
            sidebarNav.querySelector('a[data-view="chamados-meus-cli"]').classList.add('active');
            sidebarNav.querySelector('a[data-view="chamado-novo"]').classList.remove('active');
        } catch (e) {}
    });
}

/**
 * Carrega a view "Meus Chamados" (Cliente)
 */
async function loadViewChamadosCliente() {
     mainContent.innerHTML = `
        <div class="page-header"><h2>Meus Chamados</h2></div>
        <div class="card">
            <div class="card-content" id="tabela-chamados-cli-container">
                <p>Carregando chamados...</p>
            </div>
        </div>
    `;

    try {
        const data = await api.get('ChamadoServlet?action=listarMeus');
        renderTable('#tabela-chamados-cli-container',
            [
                { key: 'id', label: 'ID', sortable: true },
                { key: 'titulo', label: 'Título', sortable: true },
                { key: 'status', label: 'Status', sortable: true, formatter: formatarStatus },
                { key: 'prioridade', label: 'Prioridade', sortable: true },
                { key: 'nomeTecnico', label: 'Técnico', sortable: true, formatter: (val) => val || 'Aguardando' },
                { key: 'dataAbertura', label: 'Abertura', sortable: true, formatter: (val) => new Date(val).toLocaleDateString('pt-BR') }
            ],
            data,
            {
                rowActions: (row) => `
                    <button class="btn btn-secondary btn-sm" onclick="verHistorico(${row.id})"><i class="fas fa-history"></i> Ver Histórico</button>
                `
            }
        );
    } catch (error) {
        document.getElementById('tabela-chamados-cli-container').innerHTML = '<p>Erro ao carregar seus chamados.</p>';
    }
}


// ========================================
// FUNÇÕES UTILITÁRIAS (Comuns)
// ========================================

/**
 * Formata o status de um chamado para um badge HTML.
 */
function formatarStatus(status) {
    if (!status) return '';
    const statusLimpo = status.toUpperCase();
    let className = '';
    let texto = status;

    if (statusLimpo === 'ABERTO') {
        className = 'badge-aberto';
        texto = 'Aberto';
    } else if (statusLimpo === 'EM_ATENDIMENTO' || statusLimpo === 'EM ATENDIMENTO') {
        className = 'badge-atendimento';
        texto = 'Em Atendimento';
    } else if (statusLimpo === 'FECHADO') {
        className = 'badge-fechado';
        texto = 'Fechado';
    }

    return `<span class="badge ${className}">${texto}</span>`;
}

/**
 * Exibe o histórico de um chamado (Mongo)
 */
window.verHistorico = async (chamadoId) => {
    let historicoHtml = '<p>Carregando histórico...</p>';

    openModal(`Histórico do Chamado #${chamadoId}`, historicoHtml);

    try {
        const data = await api.get(`AtendimentoServlet?action=listarHistorico&chamadoId=${chamadoId}`);

        if (data && data.entradas && data.entradas.length > 0) {
            historicoHtml = '<ul class="simple-list">';
            data.entradas.forEach(entrada => {
                historicoHtml += `
                    <li>
                        <div>
                            <strong>${entrada.nomeTecnico || 'Sistema'}</strong>
                            <p>${entrada.descricao}</p>
                        </div>
                        <span class="log-timestamp">${new Date(entrada.timestamp).toLocaleString('pt-BR')}</span>
                    </li>
                `;
            });
            historicoHtml += '</ul>';
        } else {
            historicoHtml = '<p>Nenhum histórico de atendimento registrado para este chamado.</p>';
        }

        // Reabre o modal com o conteúdo carregado
        openModal(`Histórico do Chamado #${chamadoId}`, historicoHtml);

    } catch (error) {
        openModal(`Histórico do Chamado #${chamadoId}`, '<p>Erro ao carregar histórico.</p>');
    }
}