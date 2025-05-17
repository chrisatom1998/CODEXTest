const taskInput = document.getElementById('task-input');
const addBtn = document.getElementById('add-btn');
const taskList = document.getElementById('task-list');
const filterButtons = document.querySelectorAll('.filters button');
const settingsBtn = document.getElementById('settings-btn');
const settingsMenu = document.getElementById('settings-menu');
const themeSelect = document.getElementById('theme-select');

let tasks = JSON.parse(localStorage.getItem('tasks') || '[]');
let currentFilter = 'all';
const savedTheme = localStorage.getItem('theme') || 'light';
applyTheme(savedTheme);
themeSelect.value = savedTheme;

function saveTasks() {
    localStorage.setItem('tasks', JSON.stringify(tasks));
}

function renderTasks() {
    taskList.innerHTML = '';
    const filtered = tasks.filter(task => {
        if (currentFilter === 'active') return !task.completed;
        if (currentFilter === 'completed') return task.completed;
        return true;
    });

    filtered.forEach(task => {
        const li = document.createElement('li');
        li.className = 'task-item' + (task.completed ? ' completed' : '');

        const span = document.createElement('span');
        span.textContent = task.text;
        span.className = 'task-text';

        const left = document.createElement('div');
        left.style.display = 'flex';
        left.style.alignItems = 'center';
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.checked = task.completed;
        checkbox.addEventListener('change', () => {
            task.completed = checkbox.checked;
            saveTasks();
            renderTasks();
        });
        left.appendChild(checkbox);
        left.appendChild(span);
        left.style.gap = '0.5rem';

        const delBtn = document.createElement('button');
        delBtn.textContent = '✖';
        delBtn.className = 'delete-btn';
        delBtn.addEventListener('click', () => {
            tasks = tasks.filter(t => t.id !== task.id);
            saveTasks();
            renderTasks();
        });

        li.appendChild(left);
        li.appendChild(delBtn);
        taskList.appendChild(li);
    });
}

addBtn.addEventListener('click', () => {
    const text = taskInput.value.trim();
    if (!text) return;
    const newTask = { id: Date.now(), text, completed: false };
    tasks.push(newTask);
    taskInput.value = '';
    saveTasks();
    renderTasks();
});

filterButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        filterButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        currentFilter = btn.getAttribute('data-filter');
        renderTasks();
    });
});

settingsBtn.addEventListener('click', () => {
    settingsMenu.classList.toggle('open');
});

themeSelect.addEventListener('change', () => {
    const theme = themeSelect.value;
    applyTheme(theme);
    localStorage.setItem('theme', theme);
});

function applyTheme(theme) {
    document.body.classList.remove('light-theme', 'dark-theme');
    document.body.classList.add(`${theme}-theme`);
}

renderTasks();
