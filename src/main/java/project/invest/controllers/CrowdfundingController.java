package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.invest.controllers.requests.DepositRequest;
import project.invest.jpa.entities.Deposit;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.services.CrowdfundingService;
import project.invest.services.DepositService;

@Controller
public class CrowdfundingController {
    private String instrumentName;

    @Autowired
    private final CrowdfundingService crowdfundingService;

    @Autowired
    private final DepositService depositService;

    public CrowdfundingController(CrowdfundingService crowdfundingService, DepositService depositService) {
        this.crowdfundingService = crowdfundingService;
        this.depositService = depositService;
    }

    @GetMapping("/Crowdfunding")
    public String getAccounting(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        SummaryEntity summaryEntity = crowdfundingService.getSummary(instrumentName);
        model.addAttribute("sum", summaryEntity.getSum() - summaryEntity.getBalance());
        model.addAttribute("balance", summaryEntity.getBalance());
        return "crowdfunding/crowdfunding";
    }

    @GetMapping("/Crowdfunding/Deposit")
    public String getDeposits(@RequestParam String instrumentName, Model model) {
        this.instrumentName = instrumentName;
        model.addAttribute("instrumentName", instrumentName);
        model.addAttribute("deposits", depositService.getDeposits(instrumentName));
        model.addAttribute("depositRequest", new DepositRequest());
        return "crowdfunding/deposit";
    }

    @PostMapping("/Crowdfunding/Deposit")
    public String addDeposit(@ModelAttribute DepositRequest depositRequest, Model model) {
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
        return "crowdfunding/deposit";
    }
}
