document.querySelector('form').addEventListener('submit', handleSubmit)
const ul = document.querySelector('ul')

function handleSubmit(e) {
    e.preventDefault();
    let input = document.querySelector('input');
    if (input.value !== '') {
        addNewTodo(input.value)
    }
    
    input.value = ''
}

function addNewTodo(todoValue) {
    let li = document.createElement('li')

    li.innerHTML = `
    <span class="todo-item">${todoValue}</span>
    <button name="checkButton"><i class="fas fa-check-square"></i></button>
    <button name="deleteButton"><i class="fas fa-trash"></i></button>
    `;

    li.classList.add('todo-list-item');
    ul.appendChild(li);
}
