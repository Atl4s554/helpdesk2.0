/**
 * js/ui.js
 * Funções para componentes de Interface (Modals, Toasts, Tabelas)
 */

// ========================================
// TOASTS (Notificações)
// ========================================

/**
 * Exibe uma notificação toast.
 * @param {string} message - A mensagem a ser exibida.
 * @param {'success' | 'error'} type - O tipo de toast.
 */
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    const iconClass = type === 'success' ? 'fa-check-circle' : 'fa-times-circle';

    toast.innerHTML = `
        <div class="toast-icon">
            <i class="fas ${iconClass}"></i>
        </div>
        <div class="toast-message">
            <h4>${type === 'success' ? 'Sucesso' : 'Erro'}</h4>
            <p>${message}</p>
        </div>
    `;

    container.appendChild(toast);

    // Remove o toast após 3 segundos
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}


// ========================================
// MODAIS (Popups)
// ========================================

const modalContainer = document.getElementById('modal-container');
const modalTitle = document.getElementById('modal-title');
const modalContent = document.getElementById('modal-content');
const modalCloseBtn = document.getElementById('modal-close-btn');

/**
 * Abre o modal com um título e conteúdo HTML.
 * @param {string} title - Título do modal.
 * @param {string} contentHtml - O HTML a ser injetado no corpo do modal.
 * @param {function} onOpen - (Opcional) Função a ser chamada após o modal abrir.
 */
function openModal(title, contentHtml, onOpen) {
    if (!modalContainer) return;

    modalTitle.textContent = title;
    modalContent.innerHTML = contentHtml;
    modalContainer.style.display = 'flex';

    if (onOpen && typeof onOpen === 'function') {
        onOpen();
    }
}

/**
 * Fecha o modal.
 */
function closeModal() {
    if (!modalContainer) return;
    modalContainer.style.display = 'none';
    modalTitle.textContent = '';
    modalContent.innerHTML = '';
}

// Adiciona listeners globais para fechar o modal
modalCloseBtn.addEventListener('click', closeModal);
modalContainer.addEventListener('click', (event) => {
    // Fecha somente se clicar no backdrop (fundo)
    if (event.target === modalContainer) {
        closeModal();
    }
});


// ========================================
// RENDERIZAÇÃO DE TABELAS (com filtro e sort)
// ========================================

/**
 * Renderiza uma tabela dinâmica, paginada, com filtro e ordenação.
 * @param {string} containerSelector - Seletor do elemento onde a tabela e controles entrarão.
 * @param {Array<object>} columns - Configuração das colunas. Ex: [{ key: 'nome', label: 'Nome', sortable: true }]
 * @param {Array<object>} data - O array de dados vindo da API.
 * @param {object} options - Opções adicionais.
 * @param {string} options.filterInputId - ID do input de filtro.
 * @param {string} options.tableId - ID para a tabela.
 * @param {function} options.rowActions - Função que retorna HTML para botões de ação. Ex: (row) => '<button>Editar</button>'
 */
function renderTable(containerSelector, columns, data, options = {}) {
    const container = document.querySelector(containerSelector);
    if (!container) {
        console.error(`Container da tabela "${containerSelector}" não encontrado.`);
        return;
    }

    const { filterInputId = 'table-filter', tableId = 'dynamic-table', rowActions } = options;

    // 1. Criar HTML da Tabela
    let headersHtml = '';
    for (const col of columns) {
        headersHtml += `<th ${col.sortable ? `class="sortable" data-key="${col.key}"` : ''}>
            ${col.label}
            ${col.sortable ? '<i class="fas fa-sort sort-icon"></i><i class="fas fa-sort-up sort-icon"></i><i class="fas fa-sort-down sort-icon"></i>' : ''}
        </th>`;
    }
    if (rowActions) {
        headersHtml += '<th>Ações</th>';
    }

    const tableHtml = `
        <div class="table-toolbar">
            <input type="text" id="${filterInputId}" class="table-search-input" placeholder="Buscar na tabela...">
        </div>
        <div class="table-wrapper">
            <table class="data-table" id="${tableId}">
                <thead>
                    <tr>${headersHtml}</tr>
                </thead>
                <tbody>
                    </tbody>
            </table>
        </div>
        `;
    container.innerHTML = tableHtml;

    const tableBody = container.querySelector(`#${tableId} tbody`);
    const filterInput = container.querySelector(`#${filterInputId}`);

    let allRows = [];

    // 2. Função para renderizar as linhas
    function displayRows(rows) {
        tableBody.innerHTML = '';
        if (rows.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="${columns.length + (rowActions ? 1 : 0)}">Nenhum dado encontrado.</td></tr>`;
            return;
        }

        for (const row of rows) {
            const tr = document.createElement('tr');

            for (const col of columns) {
                // Formata o valor se um formatador for fornecido
                const value = col.formatter ? col.formatter(row[col.key], row) : row[col.key];
                tr.innerHTML += `<td>${value}</td>`;
            }

            if (rowActions) {
                tr.innerHTML += `<td><div class="btn-group">${rowActions(row)}</div></td>`;
            }

            tableBody.appendChild(tr);
        }
    }

    // 3. Lógica de Filtragem (client-side)
    filterInput.addEventListener('keyup', () => {
        const searchTerm = filterInput.value.toLowerCase();
        if (searchTerm === '') {
            displayRows(allRows);
            return;
        }

        const filteredRows = allRows.filter(row => {
            // Procura em todas as colunas
            return columns.some(col => {
                const value = String(row[col.key]).toLowerCase();
                return value.includes(searchTerm);
            });
        });
        displayRows(filteredRows);
    });

    // 4. Lógica de Ordenação
    container.querySelectorAll('th.sortable').forEach(th => {
        th.addEventListener('click', () => {
            const key = th.dataset.key;
            const currentSort = th.classList.contains('sorted-asc');

            // Limpa classes de outros cabeçalhos
            container.querySelectorAll('th.sortable').forEach(h => h.classList.remove('sorted-asc', 'sorted-desc'));

            let newSortDir = 'asc';
            if (currentSort) {
                th.classList.add('sorted-desc');
                newSortDir = 'desc';
            } else {
                th.classList.add('sorted-asc');
            }

            // Ordena os dados
            allRows.sort((a, b) => {
                let valA = a[key];
                let valB = b[key];

                if (typeof valA === 'string') {
                    valA = valA.toLowerCase();
                    valB = valB.toLowerCase();
                }

                if (valA < valB) return newSortDir === 'asc' ? -1 : 1;
                if (valA > valB) return newSortDir === 'asc' ? 1 : -1;
                return 0;
            });

            // Re-renderiza as linhas filtradas e ordenadas
            filterInput.dispatchEvent(new Event('keyup'));
        });
    });

    // 5. Renderização inicial
    allRows = [...data]; // Clona os dados
    displayRows(allRows);
}