package project.invest.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.invest.controllers.requests.ChangePercentRequest;
import project.invest.controllers.requests.SummaryRequest;
import project.invest.jpa.entities.InstrumentTypeEnum;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.services.AccountService;
import project.invest.services.SummaryService;
import project.invest.services.UserService;


@Controller
public class MainController {

    private final SummaryService summaryService;

    private final AccountService accountService;

    private final UserService userService;

    @Autowired
    public MainController(SummaryService summaryService, AccountService accountService, UserService userService) {
        this.summaryService = summaryService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String goHome(Model model, HttpServletResponse response) {
        model.addAttribute("summary", summaryService.getAllByUserName(userService.getUserName()));
        model.addAttribute("total", summaryService.getTotal(userService.getUserName()));
        model.addAttribute("summaryRequest", new SummaryRequest());
        model.addAttribute("ifUpdated", 1);
        model.addAttribute("user", userService.getUserName());
        Cookie cookie = new Cookie("instrument", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "home";
    }

    @PostMapping("/")
    public String addSummary(@ModelAttribute SummaryRequest summaryRequest) {
        if (summaryService.checkInstrument(summaryRequest.getInstrumentName())) {
            try {
                SummaryEntity summaryEntity = new SummaryEntity();
                summaryEntity.setInstrumentName(summaryRequest.getInstrumentName());
                if (summaryRequest.getType().equals("Брокерский счёт")) summaryEntity.setInstrumentTypeEnum(InstrumentTypeEnum.brokerageAccount);
                else summaryEntity.setInstrumentTypeEnum(InstrumentTypeEnum.crowdfunding);
                summaryEntity.setUser(userService.findByUserName(userService.getUserName()));
                summaryService.addToSummery(summaryEntity);
                System.out.println("Added");
            } catch (NumberFormatException e) {
                System.out.println("Error");
            }
        } else System.out.println("Already exist");
        return "redirect:/";
    }

    @PostMapping("/update")
    public String updated(@ModelAttribute ChangePercentRequest costReq) {
        accountService.updateCost(costReq.getArray(), userService.getUserName());
        return "redirect:/";
    }

    @GetMapping("/update")
    public String update(Model model) {
        model.addAttribute("ifUpdated", 0);
        model.addAttribute("tickers", accountService.getTickers());
        model.addAttribute("costList", new ChangePercentRequest());
        return "home";
    }
}
