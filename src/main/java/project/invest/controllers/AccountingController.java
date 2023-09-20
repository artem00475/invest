package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final AccountService accountService;

    public AccountingController(AccountService accountService) {this.accountService=accountService;}

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
    public String getBuy(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountService.getAccountBuyService().getBuys(instrumentName, pageable));
        model.addAttribute("buyRequest", new BuyRequest());
        return "brokerageAccount/buy";
    }

    @PostMapping("/Accounting/Buy")
    public String addBuy(@ModelAttribute BuyRequest buyRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            AccountBuy accountBuy = new AccountBuy();
            accountBuy.setCost(Float.parseFloat(buyRequest.getCost()));
            accountBuy.setCount(Integer.parseInt(buyRequest.getCount()));
            accountBuy.setSum(accountBuy.getCost() * accountBuy.getCount());
            accountBuy.setInstrumentName(instrumentName);
            accountBuy.setTicker(buyRequest.getTicker());
            accountBuy.setDate(buyRequest.getDate());
            PaperTypeEnum type;
            if (Objects.equals(buyRequest.getType(), "Акция")) type=PaperTypeEnum.STOCK;
            else if (Objects.equals(buyRequest.getType(), "Валюта")) type=PaperTypeEnum.CURRENCY;
            else if (Objects.equals(buyRequest.getType(), "Фонд")) type=PaperTypeEnum.FUND;
            else type=PaperTypeEnum.BOND;
            accountService.addAccount(accountBuy, type);
            System.out.println("Added buy on instrument: " + accountBuy.getInstrumentName() +" ticker: " + accountBuy.getTicker());
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountService.getAccountBuyService().getBuys(instrumentName, pageable));
        return "brokerageAccount/buy";
    }

    @GetMapping("/Accounting/Dividends")
    public String getDividends(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("dividends", accountService.getDividendsService().getDividends(instrumentName, pageable));
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("dividendsRequest", new DividendsRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/dividends";
    }

    @PostMapping("/Accounting/Dividends")
    public String addDividends(@ModelAttribute DividendsRequest dividendsRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
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
                    accountService.addAccount(dividends);
                    System.out.println("Added dividends on instrument: " + dividends.getInstrumentName() +" ticker: " + dividends.getTicker());
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("dividends", accountService.getDividendsService().getDividends(instrumentName, pageable));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/dividends";
    }

    @GetMapping("/Accounting/Sell")
    public String getSells(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("sells", accountService.getSellsService().getSells(instrumentName, pageable));
        model.addAttribute("sellRequest", new SellRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/sell";
    }

    @PostMapping("/Accounting/Sell")
    public String addSells(@ModelAttribute SellRequest sellRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
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
                    accountService.addAccount(accountSell);
                    System.out.println("Added sell on instrument: " + accountSell.getInstrumentName() +" ticker: " + accountSell.getTicker());
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
        model.addAttribute("sells", accountService.getSellsService().getSells(instrumentName, pageable));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/sell";
    }

    @GetMapping("/Accounting/Deposit")
    public String getDeposits(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", accountService.getDepositService().getDeposits(instrumentName, pageable));
        model.addAttribute("depositRequest", new DepositRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/deposits";
    }

    @PostMapping("/Accounting/Deposit")
    public String addDeposit(@ModelAttribute DepositRequest depositRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Deposit deposit = new Deposit();
            deposit.setDate(depositRequest.getDate());
            deposit.setSum(Float.parseFloat(depositRequest.getSum()));
            deposit.setInstrumentName(instrumentName);
            accountService.addAccount(deposit);
            System.out.println("Added deposit on instrument: " + deposit.getInstrumentName());
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", accountService.getDepositService().getDeposits(instrumentName, pageable));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/deposits";
    }

    @GetMapping("/Accounting/Commission")
    public String getCommissions(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("commissions", accountService.getCommissionService().getCommissions(instrumentName, pageable));
        model.addAttribute("commissionRequest", new CommissionRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/commissions";
    }

    @PostMapping("/Accounting/Commission")
    public String addCommission(@ModelAttribute CommissionRequest commissionRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Commission commission = new Commission();
            commission.setDate(commissionRequest.getDate());
            commission.setSum(Float.parseFloat(commissionRequest.getSum()));
            commission.setInstrumentName(instrumentName);
            accountService.addAccount(commission);
            System.out.println("Added commission on instrument: " + commission.getInstrumentName());
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("commissions", accountService.getCommissionService().getCommissions(instrumentName, pageable));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/commissions";
    }

    @GetMapping("/Accounting/Amortization")
    public String getAmortizations(@RequestParam String instrumentName, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName));
        model.addAttribute("amortization", accountService.getAmortizationService().getAmortizations(instrumentName, pageable));
        model.addAttribute("amortizationRequest", new AmortizationRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/amortization";
    }

    @PostMapping("/Accounting/Amortization")
    public String addAmortization(@ModelAttribute AmortizationRequest amortizationRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable) {
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
                    accountService.addAccount(amortization);
                    System.out.println("Added amortization on instrument: " + amortization.getInstrumentName() +" ticker: " + amortization.getTicker());
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("amortization", accountService.getAmortizationService().getAmortizations(instrumentName, pageable));
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/amortization";
    }
}
