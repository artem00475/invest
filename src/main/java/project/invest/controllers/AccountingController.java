package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.invest.controllers.requests.BuyRequest;
import project.invest.controllers.requests.DividendsRequest;
import project.invest.controllers.requests.SellRequest;
import project.invest.jpa.entities.*;
import project.invest.services.AccountBuyService;
import project.invest.services.AccountService;
import project.invest.services.DividendsService;
import project.invest.services.SellsService;

import java.util.Objects;

@Controller
public class AccountingController {
    private String instrumentName;

    @Autowired
    private final AccountBuyService accountBuyService;
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final DividendsService dividendsService;
    @Autowired
    private final SellsService sellsService;

    public AccountingController(AccountBuyService accountBuyService, AccountService accountService, DividendsService dividendsService, SellsService sellsService) {
        this.accountBuyService = accountBuyService;
        this.accountService = accountService;
        this.dividendsService = dividendsService;
        this.sellsService = sellsService;
    }

    @GetMapping("/Accounting")
    public String getAccounting(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
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
            accountBuy.setSum(accountBuy.getCost() * accountBuy.getCount());
            accountBuy.setInstrumentName(instrumentName);
            accountBuy.setTicker(buyRequest.getTicker());
            accountBuy.setDate(buyRequest.getDate());
            accountBuyService.addBuy(accountBuy);
            System.out.println("Added");
            PaperTypeEnum type;
            if (Objects.equals(buyRequest.getType(), "акция")) type=PaperTypeEnum.STOCK;
            else type=PaperTypeEnum.BOND;
            accountService.addAccount(accountBuy, type);
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountBuyService.getBuys(instrumentName));
        return "buy";
    }

    @GetMapping("/Accounting/Dividends")
    public String getDividends(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("dividends", dividendsService.getDividends(instrumentName));
        model.addAttribute("dividendsRequest", new DividendsRequest());
        return "dividends";
    }

    @PostMapping("/Accounting/Dividends")
    public String addDividends(@ModelAttribute DividendsRequest dividendsRequest, Model model) {
        Account account = accountService.getAccount(instrumentName, dividendsRequest.getTicker());
        if (account != null) {
            try {
                if (account.getCount() == Integer.parseInt(dividendsRequest.getCount())) {
                    Dividends dividends = new Dividends();
                    dividends.setCost(Float.parseFloat(dividendsRequest.getCost()));
                    dividends.setCount(Integer.parseInt(dividendsRequest.getCount()));
                    dividends.setSum(dividends.getCost() * 0.87f * dividends.getCount());
                    dividends.setInstrumentName(instrumentName);
                    dividends.setTicker(dividendsRequest.getTicker());
                    dividends.setDate(dividendsRequest.getDate());
                    dividends.setTax(dividends.getSum() * 0.13f);
                    dividendsService.addDividends(dividends);
                    System.out.println("Added");
                    accountService.addAccount(dividends);
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("dividends", dividendsService.getDividends(instrumentName));
        return "dividends";
    }

    @GetMapping("/Accounting/Sell")
    public String getSells(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("sells", sellsService.getSells(instrumentName));
        model.addAttribute("sellRequest", new SellRequest());
        return "sell";
    }

    @PostMapping("/Accounting/Sell")
    public String addSells(@ModelAttribute SellRequest sellRequest, Model model) {
        Account account = accountService.getAccount(instrumentName, sellRequest.getTicker());
        if (account != null) {
            try {
                if (account.getCount() >= Integer.parseInt(sellRequest.getCount())) {
                    AccountSell accountSell = new AccountSell();
                    accountSell.setCost(Float.parseFloat(sellRequest.getCost()));
                    accountSell.setCount(Integer.parseInt(sellRequest.getCount()));
                    accountSell.setSum(accountSell.getCost() * accountSell.getCount());
                    accountSell.setAverageCost(account != null ? account.getAverageCost() : 0f);
                    accountSell.setAverageSum(accountSell.getAverageCost() * accountSell.getCount());
                    accountSell.setInstrumentName(instrumentName);
                    accountSell.setTicker(sellRequest.getTicker());
                    accountSell.setDate(sellRequest.getDate());
                    accountSell.setChange(accountSell.getSum() - accountSell.getAverageSum());
                    sellsService.addSell(accountSell);
                    System.out.println("Added");
                    accountService.addAccount(accountSell);
                } else {
                    model.addAttribute("error", "Некорректное число бумаг");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Некорректные значения");
                System.out.println("Error");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("sells", sellsService.getSells(instrumentName));
        return "sell";
    }
}
