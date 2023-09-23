function fetchCost(tickers) {
    let costs_dict = {};
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
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                if (tickers[0].includes(el["SECID"])) {
                    costs_dict[el["SECID"]] = [el["PREVPRICE"],el["SECNAME"]]
                }
            }
            getReceiptsCost(tickers, costs_dict)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getReceiptsCost(tickers, costs_dict) {
    fetch("\n" +
        "https://iss.moex.com/iss/engines/stock/markets/shares/boardgroups/57/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=130&sort_column=VALTODAY&sort_order=desc", {
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
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                if (tickers[0].includes(el["SECID"])) {
                    costs_dict[el["SECID"]] = [el["PREVPRICE"],el["SECNAME"]];
                }
            }
            fetchCostStocks(tickers, costs_dict)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function fetchCostStocks(tickers, costs_dict) {
    let costs = [];
    fetch("https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities.xml?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST", {
        method: "GET",
        headers: {
            "Content-Type": "application/xml",
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
            const parser = new DOMParser();
            const xml = parser.parseFromString(data, "application/xml");
            const elements = Array.from(xml.getElementsByTagName('row'));

            for (let i = 0;i<elements.length;i++) {
                if (costs_dict.length === 0) break;
                const id = elements[i].attributes['SECID'].value
                const cost = elements[i].attributes['LAST'].value
                if (id && cost) {
                    for (let key in costs_dict) {
                        if (key === id) {
                            costs.push(id + '###' + cost + '###' + costs_dict[id][1]);
                            delete costs_dict[key];
                        }
                    }
                }
            }
            for (let key in costs_dict) {
                costs.push(key + '###' + costs_dict[key][0] + '###' + costs_dict[key][1]);
            }
            console.log(costs);
            getCurrencyCost(tickers, costs)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getCurrencyCost(tickers, costs) {
    let costs_dict={}
    fetch("https://iss.moex.com/iss/engines/currency/markets/selt/boardgroups/13/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=177&sort_column=VALTODAY&sort_order=desc", {
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
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                if (tickers[2].includes(el["SECID"])) {
                    costs_dict[el["SECID"]] = [el["PREVPRICE"],el["SECNAME"]]
                    // costs.push(el["SECID"]+'###'+el["PREVPRICE"]+'###'+el["SECNAME"])
                }
            }
            console.log(costs)
            getCurrentCurrencyCost(tickers, costs_dict, costs);
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}
function getCurrentCurrencyCost(tickers, costs_dict, costs) {
    fetch("https://iss.moex.com/iss/statistics/engines/currency/markets/selt/rates/securities.xml?iss.meta=off", {
        method: "GET",
        headers: {
            "Content-Type": "application/XML",
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
        const parser = new DOMParser();
        const xml = parser.parseFromString(data, "application/xml");
        const elements = Array.from(xml.getElementsByTagName('row'));
        console.log(elements)
        for (let i = 0;i<elements.length;i++) {
            if (costs_dict.length === 0) break;
            if (!elements[i].attributes['secid'] || !elements[i].attributes['price']) continue;
            const id = elements[i].attributes['secid'].value
            let cost = elements[i].attributes['price'].value
            if (id && cost) {
                for (let key in costs_dict) {
                    if (key === id) {
                        costs.push(id + '###' + cost + '###' + costs_dict[id][1]);
                        delete costs_dict[key];
                    }
                }
            }
        }
        for (let key in costs_dict) {
            costs.push(key + '###' + costs_dict[key][0] + '###' + costs_dict[key][1]);
        }
        console.log(costs);
        getFundCOst(tickers, costs)
    })
    .catch(function (error) {
        console.log("Error: ",error)
    })
}


function getFundCOst(tickers, costs) {
    fetch("\n" +
        "https://iss.moex.com/iss/engines/stock/markets/shares/boardgroups/57/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=59&sort_column=VALTODAY&sort_order=desc", {
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
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                if (tickers[3].includes(el["SECID"])) {
                    costs.push(el["SECID"] + '###' + el["PREVPRICE"] + '###' + el["SECNAME"])
                }
            }
            console.log(costs)
            getFund1COst(tickers, costs)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getFund1COst(tickers, costs) {
    fetch("\n" +
        "https://iss.moex.com/iss/engines/stock/markets/shares/boardgroups/57/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=151&sort_column=VALTODAY&sort_order=desc", {
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
            for (let i = 0; i< data[1]["securities"].length;i++) {
                const el = data[1]["securities"][i]
                if (tickers[3].includes(el["SECID"])) {
                    costs.push(el["SECID"] + '###' + el["PREVPRICE"] + '###' + el["SECNAME"])
                }
            }
            console.log(costs)
            getBondsCost(tickers[1], costs)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getBondsCost(tickers, costs) {
    let costs_dict={};
    fetch("https://iss.moex.com/iss/engines/stock/markets/bonds/boardgroups/58/securities.jsonp?iss.meta=off&iss.json=extended&callback=JSON_CALLBACK&lang=ru&security_collection=7&sort_column=VALTODAY&sort_order=desc", {
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
                if (tickers.includes(el["SECID"])) {
                    costs_dict[el["SECID"]] = [el["PREVPRICE"],el["SECNAME"], el["LOTVALUE"], el["ACCRUEDINT"]]
                }
            }
            console.log(costs)
            getGivBondsCost(tickers, costs_dict, costs)
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getGivBondsCost(tickers, costs_dict, costs) {
    fetch("https://iss.moex.com/iss/engines/stock/markets/bonds/boards/TQOB/securities.xml?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST", {
        method: "GET",
        headers: {
            "Content-Type": "application/XML",
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
            const parser = new DOMParser();
            const xml = parser.parseFromString(data, "application/xml");
            const elements = Array.from(xml.getElementsByTagName('row'));
            getCurrentBondsCost(tickers, costs_dict, costs, elements);
        })
        .catch(function (error) {
            console.log("Error: ",error)
        })
}

function getCurrentBondsCost(tickers, costs_dict, costs, elements) {
    fetch("https://iss.moex.com/iss/engines/stock/markets/bonds/boards/TQCB/securities.xml?iss.meta=off&iss.only=marketdata&marketdata.columns=SECID,LAST", {
        method: "GET",
        headers: {
            "Content-Type": "application/XML",
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
            const parser = new DOMParser();
            const xml = parser.parseFromString(data, "application/xml");
            elements = elements.concat(Array.from(xml.getElementsByTagName('row')));

            for (let i = 0;i<elements.length;i++) {
                if (costs_dict.length === 0) break;
                const id = elements[i].attributes['SECID'].value
                let cost = elements[i].attributes['LAST'].value
                if (id && cost) {
                    for (let key in costs_dict) {
                        if (key === id) {
                            cost = cost * 0.01 * costs_dict[id][2] + costs_dict[id][3]
                            costs.push(id + '###' + cost + '###' + costs_dict[id][1]);
                            delete costs_dict[key];
                        }
                    }
                }
            }
            for (let key in costs_dict) {
                let cost = costs_dict[key][0] * 0.01 * costs_dict[key][2] + costs_dict[key][3]
                costs.push(key + '###' + cost + '###' + costs_dict[key][1]);
            }
            console.log(costs);
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