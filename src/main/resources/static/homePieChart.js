function homePieChart(elements) {
    let value = "";
    let prev = 0.0;
    let container = document.getElementById("legenda");
    for (let i = 0; i < elements.length; i++) {
        if (i > 0) value += ", ";
        let col = '#' + Math.floor(Math.random() * 16777215).toString(16);
        value += col + ' ';
        value += prev + '%' + ' ';
        prev += elements[i].percentFromAll;
        value += prev + '%'
        let entry = document.createElement('div');
        entry.className = "entry";
        let text = document.createElement('div')
        text.className = "entry-text";
        text.innerHTML = elements[i].instrumentName;
        let color = document.createElement('div')
        color.className = "entry-color";
        color.style.backgroundColor = col;
        entry.appendChild(color);
        entry.appendChild(text);
        container.appendChild(entry);
    }
    console.log(value)
    document.getElementById("my-pie-chart").style.background = "conic-gradient(" + value + ")";
}

function homePieChart1(elements) {
    let value = "";
    let prev = 0.0;
    let container = document.getElementById("legenda");
    for (let i = 0; i < elements.length; i++) {
        if (i > 0) value += ", ";
        let col = '#' + Math.floor(Math.random() * 16777215).toString(16);
        value += col + ' ';
        value += prev + '%' + ' ';
        prev += elements[i].currentShare;
        value += prev + '%'
        let entry = document.createElement('div');
        entry.className = "entry";
        let text = document.createElement('div')
        text.className = "entry-text";
        text.innerHTML = elements[i].name;
        let color = document.createElement('div')
        color.className = "entry-color";
        color.style.backgroundColor = col;
        entry.appendChild(color);
        entry.appendChild(text);
        container.appendChild(entry);
    }
    console.log(value)
    document.getElementById("my-pie-chart").style.background = "conic-gradient(" + value + ")";
}