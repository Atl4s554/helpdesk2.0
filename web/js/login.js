// Script para página de login

// Verifica parâmetros da URL ao carregar
window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const erro = urlParams.get('erro');
    const msg = urlParams.get('msg');
    const mensagemDiv = document.getElementById('mensagem');

    if (erro) {
        switch(erro) {
            case 'campos_vazios':
                mostrarMensagem('Por favor, preencha todos os campos.', 'erro');
                break;
            case 'credenciais_invalidas':
                mostrarMensagem('Email ou senha incorretos.', 'erro');
                break;
            default:
                mostrarMensagem('Erro ao fazer login.', 'erro');
        }
    }

    if (msg) {
        switch(msg) {
            case 'logout_sucesso':
                mostrarMensagem('Logout realizado com sucesso.', 'sucesso');
                break;
        }
    }
});

// Função para mostrar mensagens
function mostrarMensagem(texto, tipo) {
    const mensagemDiv = document.getElementById('mensagem');
    mensagemDiv.textContent = texto;
    mensagemDiv.className = 'mensagem ' + tipo;

    // Remove mensagem após 5 segundos
    setTimeout(() => {
        mensagemDiv.style.display = 'none';
    }, 5000);
}

// Validação do formulário antes de enviar
document.getElementById('loginForm').addEventListener('submit', function(e) {
    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value.trim();

    if (!email || !senha) {
        e.preventDefault();
        mostrarMensagem('Por favor, preencha todos os campos.', 'erro');
        return false;
    }

    // Validação básica de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        e.preventDefault();
        mostrarMensagem('Por favor, insira um email válido.', 'erro');
        return false;
    }

    // Se passou nas validações, o formulário será enviado normalmente
});