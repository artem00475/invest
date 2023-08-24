const change_btn = document.getElementById('change_btn');
const save_btn = document.getElementById('save_btn');
const cancel_btn = document.getElementById('cancel_btn');
const table = document.getElementById('table').children[0].children;
const previous_val = [];


change_btn.onclick = () => {
    change_btn.style.display = 'none';
    save_btn.style.display = 'block';
    cancel_btn.style.display = 'block';
    for (let i = 1; i < table.length;i++) {
        const element = table[i].children[5].innerHTML;
        previous_val.push(element);
        table[i].children[5].innerHTML="<input type='text' width='100%' value='"+element+"' max='100' min='0' id='input_"+i+"'>"
    }
}

save_btn.onclick = () => {
    let ifSave = true;
    const current_val = [];
    let sum = 0;
    for (let i = 1; i < table.length;i++) {
        const input = document.getElementById("input_"+i);
        if (!isNaN(input.value) && 0<=input.value && input.value<=100) {
            current_val.push(input.value);
            sum += parseFloat(input.value);
            input.style.borderColor='black';
        }
        else {
            ifSave = false;
            input.style.borderColor='red';

        }
    }
    if (ifSave && sum === 100) {
        for (let i = 1; i < table.length;i++) {
            table[i].children[5].innerHTML = current_val[i-1];
        }
        change_btn.style.display = 'block';
        save_btn.style.display = 'none';
        cancel_btn.style.display = 'none';
        alert("Saved.")
        for (let i = 0; i<accounts.length; i++) current_val[i] += "###"+accounts[i].ticker;
        document.getElementById("change_percent_array").value=current_val;
        document.getElementById("change_percent_form_btn").click();
    }
    else alert("Correct values " + sum)
}

cancel_btn.onclick = () => {
    change_btn.style.display = 'block';
    save_btn.style.display = 'none';
    cancel_btn.style.display = 'none';
    for (let i = 1; i < table.length;i++) {
        table[i].children[5].innerHTML= previous_val[i-1];
    }
    alert("Canceled.")
}