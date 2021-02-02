"use strict"

const taskList = document.querySelector('ul')
const taskListItems = document.getElementsByTagName("li")
const closeButtons = document.getElementsByClassName("close")

for (let i = 0; i < taskListItems.length; i++) {
    const span = document.createElement("span")
    const text = document.createTextNode("\u00D7")
    span.className = "close"
    span.appendChild(text)
    taskListItems[i].appendChild(span)
}

refreshCloseButtons()

taskList.addEventListener('click', function (event) {
    if (event.target.tagName === 'li') {
        event.target.classList.toggle('checked')
    }
}, false)

function createTask() {
    const taskInput = document.getElementById("task-input")
    const listElement = document.createElement("li")
    const inputValue = taskInput.value
    const text = document.createTextNode(inputValue)

    listElement.appendChild(text)

    if (inputValue !== '') {
        document.getElementById("list").appendChild(listElement)
    }

    taskInput.value = ""

    const closeBtn = document.createElement("span")
    const closeBtnText = document.createTextNode("\u00D7")
    closeBtn.className = "close"
    closeBtn.appendChild(closeBtnText)
    listElement.appendChild(closeBtn)

    refreshCloseButtons()
}

function refreshCloseButtons() {
    for (let i = 0; i < closeButtons.length; i++) {
        closeButtons[i].onclick = function () {
            this.parentElement.remove()
        }
    }
}
