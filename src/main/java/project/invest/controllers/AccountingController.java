package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.invest.controllers.requests.BuyRequest;
import project.invest.jpa.entities.AccountBuy;
import project.invest.services.AccountBuyService;

import java.util.Date;

@Controller
public class AccountingController {
    private String instrumentName;

    @Autowired
    private final AccountBuyService accountBuyService;

    public AccountingController(AccountBuyService accountBuyService) {
        this.accountBuyService = accountBuyService;
    }

    @GetMapping("/Accounting")
    public String getAccounting(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        return "instrumentAccounting";
    }

    @GetMapping("/Accounting/Buy")
    public String getBuy(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountBuyService.getBuys(instrumentName));
        model.addAttribute("buyRequest", new BuyRequest());
        return "buy";
    }

    @PostMapping("/Accounting/Buy")
    public String addBuy(@ModelAttribute BuyRequest buyRequest, Model model) {
        try {
            AccountBuy accountBuy = new AccountBuy();
            accountBuy.setCost(Float.parseFloat(buyRequest.getCost()));
            accountBuy.setCount(Integer.parseInt(buyRequest.getCount()));
            accountBuy.setSum(accountBuy.getCost()*accountBuy.getCount());
            accountBuy.setInstrumentName(instrumentName);
            accountBuy.setTicker(buyRequest.getTicker());
            accountBuy.setDate(new Date());
            accountBuyService.addBuy(accountBuy);
            System.out.println("Added");
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountBuyService.getBuys(instrumentName));
        return "buy";
    }
}
