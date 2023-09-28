package project.invest.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import project.invest.controllers.requests.*;
import project.invest.jpa.entities.*;
import project.invest.services.*;

import java.util.Objects;

@Controller
public class AccountingController {

    private final AccountService accountService;

    private final UserService userService;

    @Autowired
    public AccountingController(AccountService accountService, UserService userService) {this.accountService=accountService;
        this.userService = userService;
    }

    private String checkAccess(String instrumentName, String instrumentName1, HttpServletResponse response) {
        if (instrumentName!= null &&!instrumentName.isEmpty()) {
            Cookie cookie = new Cookie("instrument", instrumentName);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return instrumentName;
        } else if (instrumentName1!=null &&!instrumentName1.isEmpty())  return instrumentName1;
        return null;
    }

    @GetMapping("/Accounting")
    public String getAccounting(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName, userService.getUserName()));
        model.addAttribute("balance", accountService.getBalance(instrumentName));
        model.addAttribute("changePercentRequest", new ChangePercentRequest());
        return "brokerageAccount/instrumentAccounting";
    }


    @PostMapping("/Accounting")
    public String changePercent(@ModelAttribute ChangePercentRequest changePercentRequest, @CookieValue(name = "instrument") String instrumentName) {
        accountService.changePercent(changePercentRequest.getArray(), instrumentName, userService.getUserName());
        return "redirect:/Accounting";
    }

    @GetMapping("/Accounting/Buy")
    public String getBuy(Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("buys", accountService.getAccountBuyService().getBuys(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("buyRequest", new BuyRequest());
        return "brokerageAccount/buy";
    }

    @PostMapping("/Accounting/Buy")
    public String addBuy(@ModelAttribute BuyRequest buyRequest, @CookieValue(name = "instrument") String instrumentName) {
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
            accountBuy.setUser(userService.findByUserName(userService.getUserName()));
            accountService.addAccount(accountBuy, type, userService.getUserName());
            System.out.println("Added buy on instrument: " + accountBuy.getInstrumentName() +" ticker: " + accountBuy.getTicker());
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        return "redirect:/Accounting/Buy";
    }

    @GetMapping("/Accounting/Dividends")
    public String getDividends(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("dividends", accountService.getDividendsService().getDividends(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("accounts", accountService.getAccounts(instrumentName, userService.getUserName()));
        model.addAttribute("dividendsRequest", new DividendsRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/dividends";
    }

    @PostMapping("/Accounting/Dividends")
    public String addDividends(@ModelAttribute DividendsRequest dividendsRequest, Model model, @CookieValue(name = "instrument") String instrumentName) {
        Account account = accountService.getAccount(instrumentName, dividendsRequest.getTicker(), userService.getUserName());
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
                    dividends.setUser(userService.findByUserName(userService.getUserName()));
                    accountService.addAccount(dividends, userService.getUserName());
                    System.out.println("Added dividends on instrument: " + dividends.getInstrumentName() +" ticker: " + dividends.getTicker());
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        return "redirect:/Accounting/Dividends";
    }

    @GetMapping("/Accounting/Sell")
    public String getSells(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName, userService.getUserName()));
        model.addAttribute("sells", accountService.getSellsService().getSells(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("sellRequest", new SellRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/sell";
    }

    @PostMapping("/Accounting/Sell")
    public String addSells(@ModelAttribute SellRequest sellRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, @CookieValue(name = "instrument") String instrumentName) {
        Account account = accountService.getAccount(instrumentName, sellRequest.getTicker(), userService.getUserName());
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
                    accountSell.setUser(userService.findByUserName(userService.getUserName()));
                    accountService.addAccount(accountSell, userService.getUserName());
                    System.out.println("Added sell on instrument: " + accountSell.getInstrumentName() +" ticker: " + accountSell.getTicker());
                } else {
                    model.addAttribute("error", "Некорректное число бумаг");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Некорректные значения");
                System.out.println("Error");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        return "redirect:/Accounting/Sell";
    }

    @GetMapping("/Accounting/Deposit")
    public String getDeposits(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", accountService.getDepositService().getDeposits(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("depositRequest", new DepositRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/deposits";
    }

    @PostMapping("/Accounting/Deposit")
    public String addDeposit(@ModelAttribute DepositRequest depositRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, @CookieValue(name = "instrument") String instrumentName) {
        try {
            Deposit deposit = new Deposit();
            deposit.setDate(depositRequest.getDate());
            deposit.setSum(Float.parseFloat(depositRequest.getSum()));
            deposit.setInstrumentName(instrumentName);
            deposit.setUser(userService.findByUserName(userService.getUserName()));
            accountService.addAccount(deposit);
            System.out.println("Added deposit on instrument: " + deposit.getInstrumentName());
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        return "redirect:/Accounting/Deposit";
    }

    @GetMapping("/Accounting/Commission")
    public String getCommissions(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("commissions", accountService.getCommissionService().getCommissions(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("commissionRequest", new CommissionRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/commissions";
    }

    @PostMapping("/Accounting/Commission")
    public String addCommission(@ModelAttribute CommissionRequest commissionRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, @CookieValue(name = "instrument") String instrumentName) {
        try {
            Commission commission = new Commission();
            commission.setDate(commissionRequest.getDate());
            commission.setSum(Float.parseFloat(commissionRequest.getSum()));
            commission.setInstrumentName(instrumentName);
            commission.setUser(userService.findByUserName(userService.getUserName()));
            accountService.addAccount(commission);
            System.out.println("Added commission on instrument: " + commission.getInstrumentName());
        } catch (NumberFormatException e) {
            System.out.println("Error");
            model.addAttribute("error", "Некорректные значения");
        }
        return "redirect:/Accounting/Commission";
    }

    @GetMapping("/Accounting/Amortization")
    public String getAmortizations(@RequestParam(required = false) String instrumentName, @CookieValue(name = "instrument", required = false) String instrumentName1, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, HttpServletResponse response) {
        instrumentName = checkAccess(instrumentName, instrumentName1, response);
        if (instrumentName == null) return "redirect:/";
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("accounts", accountService.getAccounts(instrumentName, userService.getUserName()));
        model.addAttribute("amortization", accountService.getAmortizationService().getAmortizations(instrumentName, userService.getUserName(), pageable));
        model.addAttribute("amortizationRequest", new AmortizationRequest());
        model.addAttribute("currentUri", request.getRequestURI());
        return "brokerageAccount/amortization";
    }

    @PostMapping("/Accounting/Amortization")
    public String addAmortization(@ModelAttribute AmortizationRequest amortizationRequest, Model model, HttpServletRequest request, @PageableDefault(sort = {"date"}, direction = Sort.Direction.ASC) Pageable pageable, @CookieValue(name = "instrument") String instrumentName) {
        Account account = accountService.getAccount(instrumentName, amortizationRequest.getTicker(), userService.getUserName());
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
                    amortization.setUser(userService.findByUserName(userService.getUserName()));
                    accountService.addAccount(amortization, userService.getUserName());
                    System.out.println("Added amortization on instrument: " + amortization.getInstrumentName() +" ticker: " + amortization.getTicker());
                }else model.addAttribute("error", "Некорректное число бумаг");
            } catch (NumberFormatException e) {
                System.out.println("Error");
                model.addAttribute("error", "Некорректные значения");
            }
        } else model.addAttribute("error", "Бумага отсутствует в данном инстременте");
        return "redirect:/Accounting/Amortization";
    }
}
