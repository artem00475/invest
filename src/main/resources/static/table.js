const next = document.getElementById("next");
const back = document.getElementById("back");
const url = new URL(document.location);
const params = url.searchParams;

function setPageNavigation(page) {

    function next10() {
        console.log('next');
        params.set('page', page.number+1);
        document.location = url;
    }

    function back10() {
        console.log('back');
        params.set('page', page.number-1);
        document.location = url;
    }

    console.log(page.number);

    next.addEventListener('click', next10);
    back.addEventListener('click', back10);
    if (page.number == page.totalPages-1) next.disabled=true;
    if (page.number === 0) back.disabled=true;
    let th = document.querySelectorAll('table th');
    document.getElementById('table-div').style.width=th.length*100 + 'px';
    document.getElementById('table-div').style.height='410px';
    if ((pages_count = page.totalPages) > 1) {
        let pointer = document.getElementById("page-pointer");
        pointer.style.width=(page.totalPages+1)*20 + 'px';
        for (let i = 0;i<pages_count; i++) {
            let point = document.createElement('div');
            point.className = 'point';
            if (i===page.number) point.className = 'current_page';
            pointer.appendChild(point);
        }
    } else document.getElementById("btn_container").style.display="none";
}
