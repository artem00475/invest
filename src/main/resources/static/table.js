// выберем все строки
let rows = Array.from(document.querySelectorAll('table tr'));
rows.splice(0,1);
console.log(rows);
let next = document.getElementById("next");
let back = document.getElementById("back");
let pointer = document.getElementById("page-pointer");
let th = document.querySelectorAll('table th');
document.getElementById('table-div').style.width=th.length*100 + 'px';
let page_number = 0;
let begin = 0;
let end = 10;
let last = 0;
if (rows.length>10) {
// скроем все строки после 10
    document.getElementById('table-div').style.height='410px';
    rows.forEach((e, i) => e.style.display = i > 9 ? 'none' : 'table-row');
    last = 9;
    rows[last].id='last-row';
    back.disabled = true;
    const pages = Math.ceil(rows.length/10);
    pointer.style.width=(pages+1)*20 + 'px';
    for (let i = 0;i<pages; i++) {
        let point = document.createElement('div');
        point.className = 'point';
        if (i===0) point.className = 'current_page';
        pointer.appendChild(point);
    }
} else {
    document.getElementById("btn_container").style.display="none";
}

function next10() {
    back.disabled = false;
    begin+=10;
    end+=10;
    pointer.children.item(page_number).className = 'point';
    page_number += 1;
    pointer.children.item(page_number).className = 'current_page';
    if (end >= rows.length) next.disabled=true;
    // перебираем все строки
    rows[last].id='';
    rows.forEach((el, i) => {
        // если строка не видна и счетчик меньше 10
        if (i >= begin && i<end) {
            el.style.display = 'table-row';
            last = i;
        }
        else el.style.display = 'none';
    })
    rows[last].id='last-row';
}

function back10() {
    next.disabled=false;
    begin-=10;
    end-=10;
    pointer.children.item(page_number).className = 'point';
    page_number -= 1;
    pointer.children.item(page_number).className = 'current_page';
    if (begin === 0) back.disabled=true;
    // перебираем все строки
    rows[last].id='';
    rows.forEach((el, i) => {
        // если строка не видна и счетчик меньше 10
        if (i >= begin && i<end) {
            el.style.display = 'table-row';
            last = i;
        }
        else el.style.display = 'none';
    })
    rows[last].id='last-row';
}