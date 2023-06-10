function fetchCost(tickers) {
    console.log(tickers)
    let costs = [];
    fetch("https://iss.moex.com/iss/engines/stock/markets/shares/boardgroups/57/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=3&sort_column=VALTODAY&sort_order=desc", {
        method: "GET",
        headers: {
            "Content-Type": "application/JSON",
        },
    })
        .then(function (response) {
            if (response.status !== 200) {
                return Promise.reject(new Error(response.statusText))
            }
            return Promise.resolve(response)
        })
        .then((response) => {
            return response.text()
        })
        .then((data) => {
            data = data.replace("JSON_CALLBACK(", '')
            data = data.substr(0, data.length-1)
            data = JSON.parse(data)
            // console.log(data[1]["securities"])
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                console.log(el)
                if (tickers.includes(el["SECID"])) {
                    costs.push(el["SECID"]+' '+el["PREVPRICE"])
                }
            }
            update(costs)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function update(costs) {
    console.log(costs)
    fetch("http://localhost:8080/cost", {
        method: "POST",
        body: JSON.stringify(costs),
        headers: {
            "Content-Type": "application/JSON",
        },
    })
        .then(function (response) {
            if (response.status !== 200) {
                return Promise.reject(new Error(response.statusText))
            }
            return Promise.resolve(response)
        })
        .then((response) => {
            return response.text()
        })
        .then((data) => {
            console.log(data)
            document.getElementById("update_cost").click();
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}