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
        model.addAttribute("summaryRequest", new SummaryRequest());
        return "home";
    }

    @PostMapping("/")
    public String addSummary(Model model, @ModelAttribute SummaryRequest summaryRequest) {
        if (summaryService.checkInstrument(summaryRequest.getInstrumentName())) {
            try {
                SummaryEntity summaryEntity = new SummaryEntity();
                summaryEntity.setInstrumentName(summaryRequest.getInstrumentName());
                summaryEntity.setUserName("Artem");
                summaryService.addToSummery(summaryEntity);
                System.out.println("Added");
            } catch (NumberFormatException e) {
                System.out.println("Error");
            }
        } else System.out.println("Already exist");
        model.addAttribute("summary", summaryService.getAllByUserName("Artem"));
        return "home";
    }
}
