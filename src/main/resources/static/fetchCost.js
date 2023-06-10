function fetchCost() {
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
            console.log(data[1]["securities"][0]["PREVPRICE"])
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}