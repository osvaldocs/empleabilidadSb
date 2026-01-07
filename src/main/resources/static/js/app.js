const API_URL = '';
let token = localStorage.getItem('token');
let selectedProjectId = null;
let selectedProjectName = '';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    if (token) {
        showApp();
        loadProjects();
    }
});

// Tab switching
function showTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(form => form.classList.add('hidden'));
    
    if (tab === 'login') {
        document.getElementById('login-form').classList.remove('hidden');
        document.querySelector('.tab-btn:first-child').classList.add('active');
    } else {
        document.getElementById('register-form').classList.remove('hidden');
        document.querySelector('.tab-btn:last-child').classList.add('active');
    }
    clearMessage('auth-message');
}

// Auth functions
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    if (!username || !password) {
        showMessage('auth-message', 'Por favor complete todos los campos', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            token = data.token;
            localStorage.setItem('token', token);
            showApp();
            loadProjects();
        } else {
            showMessage('auth-message', 'Credenciales inválidas', 'error');
        }
    } catch (error) {
        showMessage('auth-message', 'Error de conexión', 'error');
    }
}

async function register() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;

    if (!username || !password) {
        showMessage('auth-message', 'Por favor complete todos los campos', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            token = data.token;
            localStorage.setItem('token', token);
            showApp();
            loadProjects();
        } else {
            showMessage('auth-message', 'Error al registrar usuario', 'error');
        }
    } catch (error) {
        showMessage('auth-message', 'Error de conexión', 'error');
    }
}

function logout() {
    token = null;
    localStorage.removeItem('token');
    selectedProjectId = null;
    document.getElementById('auth-section').classList.remove('hidden');
    document.getElementById('app-section').classList.add('hidden');
    document.getElementById('login-username').value = '';
    document.getElementById('login-password').value = '';
}

function showApp() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('app-section').classList.remove('hidden');
}

// Projects functions
async function loadProjects() {
    try {
        const response = await fetch(`${API_URL}/api/projects`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const projects = await response.json();
            renderProjects(projects);
        } else if (response.status === 401 || response.status === 403) {
            logout();
        }
    } catch (error) {
        showMessage('app-message', 'Error al cargar proyectos', 'error');
    }
}

function renderProjects(projects) {
    const container = document.getElementById('projects-list');
    
    if (projects.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay proyectos. Crea uno nuevo.</div>';
        return;
    }

    container.innerHTML = projects.map(project => `
        <div class="project-item">
            <div class="project-info">
                <div class="project-name">${escapeHtml(project.name)}</div>
                <span class="project-status status-${project.status.toLowerCase()}">${project.status}</span>
            </div>
            <div class="project-actions">
                <button class="btn-tasks" onclick="showTasks('${project.id}', '${escapeHtml(project.name)}')">Ver Tareas</button>
                ${project.status === 'DRAFT' ? 
                    `<button class="btn-activate" onclick="activateProject('${project.id}')">Activar</button>` : 
                    ''}
            </div>
        </div>
    `).join('');
}

async function createProject() {
    const name = document.getElementById('project-name').value;

    if (!name) {
        showMessage('app-message', 'Ingrese un nombre para el proyecto', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/projects`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            document.getElementById('project-name').value = '';
            showMessage('app-message', 'Proyecto creado exitosamente', 'success');
            loadProjects();
        } else {
            showMessage('app-message', 'Error al crear proyecto', 'error');
        }
    } catch (error) {
        showMessage('app-message', 'Error de conexión', 'error');
    }
}

async function activateProject(projectId) {
    try {
        const response = await fetch(`${API_URL}/api/projects/${projectId}/activate`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            showMessage('app-message', 'Proyecto activado exitosamente', 'success');
            loadProjects();
        } else {
            const text = await response.text();
            showMessage('app-message', 'Error: El proyecto debe tener al menos una tarea activa', 'error');
        }
    } catch (error) {
        showMessage('app-message', 'Error de conexión', 'error');
    }
}

// Tasks functions
async function showTasks(projectId, projectName) {
    selectedProjectId = projectId;
    selectedProjectName = projectName;
    
    document.getElementById('projects-container').classList.add('hidden');
    document.getElementById('tasks-section').classList.remove('hidden');
    document.getElementById('selected-project-name').textContent = projectName;
    
    await loadTasks();
}

function hideTasks() {
    selectedProjectId = null;
    document.getElementById('tasks-section').classList.add('hidden');
    document.getElementById('projects-container').classList.remove('hidden');
    loadProjects();
}

async function loadTasks() {
    try {
        const response = await fetch(`${API_URL}/api/projects/${selectedProjectId}/tasks`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const tasks = await response.json();
            renderTasks(tasks);
        } else {
            // Si no hay endpoint de listar tareas, mostrar mensaje
            document.getElementById('tasks-list').innerHTML = 
                '<div class="empty-state">Crea una tarea para este proyecto.</div>';
        }
    } catch (error) {
        document.getElementById('tasks-list').innerHTML = 
            '<div class="empty-state">Crea una tarea para este proyecto.</div>';
    }
}

function renderTasks(tasks) {
    const container = document.getElementById('tasks-list');
    
    if (!tasks || tasks.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay tareas. Crea una nueva.</div>';
        return;
    }

    container.innerHTML = tasks.map(task => `
        <div class="task-item ${task.completed ? 'completed' : ''}">
            <span class="task-title">${escapeHtml(task.title)}</span>
            <span class="task-status">${task.completed ? 'Completada' : 'Pendiente'}</span>
            ${!task.completed ? 
                `<button class="btn-complete" onclick="completeTask('${task.id}')">Completar</button>` : 
                '<button class="btn-complete" disabled>Completada</button>'}
        </div>
    `).join('');
}

async function createTask() {
    const title = document.getElementById('task-title').value;

    if (!title) {
        showMessage('app-message', 'Ingrese un título para la tarea', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/projects/${selectedProjectId}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ title })
        });

        if (response.ok) {
            document.getElementById('task-title').value = '';
            showMessage('app-message', 'Tarea creada exitosamente', 'success');
            loadTasks();
        } else {
            showMessage('app-message', 'Error al crear tarea', 'error');
        }
    } catch (error) {
        showMessage('app-message', 'Error de conexión', 'error');
    }
}

async function completeTask(taskId) {
    try {
        const response = await fetch(`${API_URL}/api/projects/${selectedProjectId}/tasks/${taskId}/complete`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            showMessage('app-message', 'Tarea completada exitosamente', 'success');
            loadTasks();
        } else {
            showMessage('app-message', 'Error al completar tarea', 'error');
        }
    } catch (error) {
        showMessage('app-message', 'Error de conexión', 'error');
    }
}

// Utility functions
function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.className = `message ${type}`;
    
    setTimeout(() => {
        element.textContent = '';
        element.className = 'message';
    }, 3000);
}

function clearMessage(elementId) {
    const element = document.getElementById(elementId);
    element.textContent = '';
    element.className = 'message';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

