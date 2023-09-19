package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import project.invest.controllers.requests.*;
import project.invest.jpa.entities.*;
import project.invest.services.*;

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
    @Autowired
    private final DepositService depositService;
    @Autowired
    private final CommissionService commissionService;
    @Autowired
    private final AmortizationService amortizationService;

    public AccountingController(AccountBuyService accountBuyService, AccountService accountService, DividendsService dividendsService, SellsService sellsService, DepositService depositService, CommissionService commissionService, AmortizationService amortizationService) {
        this.accountBuyService = accountBuyService;
        this.accountService = accountService;
        this.dividendsService = dividendsService;
        this.sellsService = sellsService;
        this.depositService = depositService;
        this.commissionService = commissionService;
        this.amortizationService = amortizationService;
    }

    @GetMapping("/Accounting")
    public String getAccounting(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("balance", accountService.getBalance(instrumentName));
        model.addAttribute("changePercentRequest", new ChangePercentRequest());
        return "brokerageAccount/instrumentAccounting";
    }

    @PostMapping("/Accounting")
    public String changePercent(@ModelAttribute ChangePercentRequest changePercentRequest, Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("balance", accountService.getBalance(instrumentName));
        model.addAttribute("changePercentRequest", new ChangePercentRequest());
        accountService.changePercent(changePercentRequest.getArray(), instrumentName);
        return "brokerageAccount/instrumentAccounting";
    }

    @GetMapping("/Accounting/Buy")
    public String getBuy(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountBuyService.getBuys(instrumentName));
        model.addAttribute("buyRequest", new BuyRequest());
        return "brokerageAccount/buy";
    }

    @PostMapping("/Accounting/Buy")
    public String addBuy(@ModelAttribute BuyRequest buyRequest, Model model, HttpServletRequest request) {
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
            if (Objects.equals(buyRequest.getType(), "Акция")) type=PaperTypeEnum.STOCK;
            else if (Objects.equals(buyRequest.getType(), "Валюта")) type=PaperTypeEnum.CURRENCY;
            else if (Objects.equals(buyRequest.getType(), "Фонд")) type=PaperTypeEnum.FUND;
            else type=PaperTypeEnum.BOND;
            accountService.addAccount(accountBuy, type);
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountBuyService.getBuys(instrumentName));
        return "brokerageAccount/buy";
    }

    @GetMapping("/Accounting/Dividends")
    public String getDividends(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("dividends", dividendsService.getDividends(instrumentName));
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("dividendsRequest", new DividendsRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/dividends";
    }

    @PostMapping("/Accounting/Dividends")
    public String addDividends(@ModelAttribute DividendsRequest dividendsRequest, Model model, HttpServletRequest request) {
        Account account = accountService.getAccount(instrumentName, dividendsRequest.getTicker());
        if (account != null) {
            try {
                if (account.getCount() == Integer.parseInt(dividendsRequest.getCount())) {
                    Dividends dividends = new Dividends();
                    dividends.setCost(Float.parseFloat(dividendsRequest.getCost()));
                    dividends.setCount(Integer.parseInt(dividendsRequest.getCount()));
                    if (instrumentName.equals("ИИС") & accountService.getType(dividendsRequest.getTicker()) == PaperTypeEnum.BOND) dividends.setSum(dividends.getCost() * dividends.getCount());
                    else dividends.setSum(dividends.getCost() * 0.87f * dividends.getCount());
                    dividends.setInstrumentName(instrumentName);
                    dividends.setTicker(dividendsRequest.getTicker());
                    dividends.setDate(dividendsRequest.getDate());
                    if (instrumentName.equals("ИИС") & accountService.getType(dividendsRequest.getTicker()) == PaperTypeEnum.BOND) dividends.setTax(0);
                    else dividends.setTax(dividends.getSum() * 0.13f);
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
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("dividends", dividendsService.getDividends(instrumentName));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/dividends";
    }

    @GetMapping("/Accounting/Sell")
    public String getSells(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("sells", sellsService.getSells(instrumentName));
        model.addAttribute("sellRequest", new SellRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/sell";
    }

    @PostMapping("/Accounting/Sell")
    public String addSells(@ModelAttribute SellRequest sellRequest, Model model, HttpServletRequest request) {
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
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("sells", sellsService.getSells(instrumentName));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/sell";
    }

    @GetMapping("/Accounting/Deposit")
    public String getDeposits(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", depositService.getDeposits(instrumentName));
        model.addAttribute("depositRequest", new DepositRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/deposits";
    }

    @PostMapping("/Accounting/Deposit")
    public String addDeposit(@ModelAttribute DepositRequest depositRequest, Model model, HttpServletRequest request) {
        try {
            Deposit deposit = new Deposit();
            deposit.setDate(depositRequest.getDate());
            deposit.setSum(Float.parseFloat(depositRequest.getSum()));
            deposit.setInstrumentName(instrumentName);
            depositService.deposit(deposit);
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", depositService.getDeposits(instrumentName));
        return "brokerageAccount/deposits";
    }

    @GetMapping("/Accounting/Commission")
    public String getCommissions(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("commissions", commissionService.getCommissions(instrumentName));
        model.addAttribute("commissionRequest", new CommissionRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/commissions";
    }

    @PostMapping("/Accounting/Commission")
    public String addCommission(@ModelAttribute CommissionRequest commissionRequest, Model model, HttpServletRequest request) {
        try {
            Commission commission = new Commission();
            commission.setDate(commissionRequest.getDate());
            commission.setSum(Float.parseFloat(commissionRequest.getSum()));
            commission.setInstrumentName(instrumentName);
            commissionService.addCommission(commission);
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("commissions", commissionService.getCommissions(instrumentName));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/commissions";
    }

    @GetMapping("/Accounting/Amortization")
    public String getAmortizations(@RequestParam String instrumentName, Model model, HttpServletRequest request) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("amortization", amortizationService.getAmortizations(instrumentName));
        model.addAttribute("amortizationRequest", new AmortizationRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/amortization";
    }

    @PostMapping("/Accounting/Amortization")
    public String addAmortization(@ModelAttribute AmortizationRequest amortizationRequest, Model model, HttpServletRequest request) {
        Account account = accountService.getAccount(instrumentName, amortizationRequest.getTicker());
        if (account != null) {
            try {
                if (account.getCount() == Integer.parseInt(amortizationRequest.getCount())) {
                    Amortization amortization = new Amortization();
                    amortization.setCost(Float.parseFloat(amortizationRequest.getCost()));
                    amortization.setCount(Integer.parseInt(amortizationRequest.getCount()));
                    amortization.setSum(amortization.getCost() * amortization.getCount());
                    amortization.setInstrumentName(instrumentName);
                    amortization.setTicker(amortizationRequest.getTicker());
                    amortization.setDate(amortizationRequest.getDate());
                    amortizationService.addAmortization(amortization);
                    System.out.println("Added");
                    accountService.addAccount(amortization);
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("dividends", dividendsService.getDividends(instrumentName));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/amortization";
    }
}
