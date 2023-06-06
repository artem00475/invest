package project.invest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.invest.controllers.requests.SummaryRequest;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.services.SummaryService;

@Controller
public class MainController {

    @Autowired
    private final SummaryService summaryService;

    public MainController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/")
    public String goHome(Model model) {
        model.addAttribute("summary", summaryService.getAllByUserName("Artem"));
        return "home";
    }

    @GetMapping("/AddSummary")
    public String addSummary(Model model) {
        model.addAttribute("summaryRequest", new SummaryRequest());
        return "addSummary";
    }

    @PostMapping("/AddSummary")
    public String addSummary(Model model, @ModelAttribute SummaryRequest summaryRequest) {
        try {
            SummaryEntity summaryEntity = new SummaryEntity();
            summaryEntity.setInstrumentName(summaryRequest.getInstrumentName());
            summaryEntity.setSum(Float.parseFloat(summaryRequest.getSum()));
            summaryEntity.setChange(Float.parseFloat(summaryRequest.getChange()));
            summaryEntity.setChangeInPercents(Float.parseFloat(summaryRequest.getChangeInPercents()));
            summaryEntity.setPercentFromAll(Float.parseFloat(summaryRequest.getPercentFromAll()));
            summaryEntity.setInvested(Float.parseFloat(summaryRequest.getInvested()));
            summaryEntity.setChangeFromInvested(Float.parseFloat(summaryRequest.getChangeFromInvested()));
            summaryEntity.setUserName("Artem");
            summaryService.addToSummery(summaryEntity);
            System.out.println("Added");
        } catch (NumberFormatException e) {
            System.out.println("Error");
        }
        model.addAttribute("summary", summaryService.getAllByUserName("Artem"));
        return "home";
    }
}
