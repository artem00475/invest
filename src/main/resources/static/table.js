// выберем все строки
let rows = Array.from(document.querySelectorAll('table tr'));
rows.splice(0,1);
console.log(rows);
let next = document.getElementById("next");
let back = document.getElementById("back");
let begin = 0;
let end = 10;
if (rows.length>10) {
// скроем все строки после 10
    rows.forEach((e, i) => e.style.display = i > 10 ? 'none' : 'table-row');
    back.disabled = true;
} else {
    document.getElementById("btn_container").style.display="none";
}

function next10() {
    back.disabled = false;
    begin+=10;
    end+=10;
    if (end >= rows.length) next.disabled=true;
    // перебираем все строки
    rows.forEach((el, i) => {
        // если строка не видна и счетчик меньше 10
        if (i >= begin && i<end) el.style.display = 'table-row';
        else el.style.display = 'none';
    })
}

function back10() {
    next.disabled=false;
    begin-=10;
    end-=10;
    if (begin === 0) back.disabled=true;
    // перебираем все строки
    rows.forEach((el, i) => {
        // если строка не видна и счетчик меньше 10
        if (i >= begin && i<end) el.style.display = 'table-row';
        else el.style.display = 'none';
    })
}