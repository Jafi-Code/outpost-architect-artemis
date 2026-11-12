package com.outpostarchitect.ui;

import com.outpostarchitect.game.Crisis;
import com.outpostarchitect.game.CrisisService;
import com.outpostarchitect.game.Decision;
import com.outpostarchitect.game.MissionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("play")
@PageTitle("Crisis Protocol")
public class CrisisView extends VerticalLayout {

    private final CrisisService crisisService;
    private  MissionState state;
    private final VerticalLayout mainContent;

    @Autowired
    public CrisisView(CrisisService crisisService) {
        this.crisisService = crisisService;
        this.state = new MissionState();
        this.mainContent = new VerticalLayout();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);
        
        getStyle()
                .set("background-color", "#0A1628")
                .set("background-image", "url('images/bg-dashboard.jpeg')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("font-family", "'Inter', sans-serif");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.getStyle().set("background-color", "rgba(10, 22, 40, 0.9)").set("border-bottom", "1px solid #1F2E45");
        
        Image logoSmall = new Image("images/logo.png", "Logo");
        logoSmall.setHeight("30px");
        
        Span missionText = new Span("ARES-01 · LUNAR SOUTH POLE");
        missionText.getStyle().set("color", "#8B9BB4").set("font-size", "0.9rem").set("letter-spacing", "2px");
        
        header.add(logoSmall, missionText);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(missionText);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Main Content Container
        mainContent.setMaxWidth("900px");
        mainContent.setWidthFull();
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        mainContent.getStyle().set("margin-top", "40px");

        add(header, mainContent);
        
        loadCurrentState();
    }

    private void loadCurrentState() {
        mainContent.removeAll();

        // Check Win/Lose Conditions
        if (state.crew <= 0) {
            showDebrief(false, "Crew health reached zero. Mission failed.");
            return;
        }
        if (state.day > 30) {
            showDebrief(true, "30 days survived. The outpost is stable.");
            return;
        }

        Crisis crisis = crisisService.getCrisisForDay(state.day);

        if (crisis != null) {
            renderCrisis(crisis);
        } else {
            renderQuietDay();
        }
    }

    private void renderCrisis(Crisis crisis) {
        // Day Indicator
        Span dayBadge = new Span("DAY " + state.day + " / 30 · INCIDENT REPORT");
        dayBadge.getStyle()
                .set("color", "#FF5C5C") // Solar Red
                .set("font-weight", "700")
                .set("letter-spacing", "3px")
                .set("font-size", "0.85rem");

        // Crisis Title
        H1 title = new H1(crisis.title);
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("color", "#F4F6FA")
                .set("margin", "0")
                .set("font-size", "2.5rem");

        // Description
        Paragraph desc = new Paragraph(crisis.description);
        desc.getStyle()
                .set("color", "#F4F6FA")
                .set("font-size", "1.2rem")
                .set("line-height", "1.6")
                .set("margin-bottom", "30px");

        // Character Communication
        Image portrait = new Image(crisis.characterImage, crisis.characterName);
        portrait.setWidth("80px");
        portrait.setHeight("80px");
        portrait.getStyle().set("border-radius", "50%").set("border", "2px solid #00D4E5").set("object-fit", "cover");

        Span charName = new Span(crisis.characterName);
        charName.getStyle().set("font-weight", "700").set("color", "#00D4E5").set("display", "block");
        
        VerticalLayout nameGroup = new VerticalLayout(charName);
        nameGroup.setSpacing(false);
        nameGroup.setPadding(false);
        nameGroup.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        HorizontalLayout commsHeader = new HorizontalLayout(portrait, nameGroup);
        commsHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        // Science Panel (Expandable)
        Dialog scienceDialog = new Dialog();
        scienceDialog.setHeaderTitle("SCIENCE BEHIND THE DECISION");
        scienceDialog.add(new Paragraph(crisis.scienceContext));
        scienceDialog.getFooter().add(new Button("Close", e -> scienceDialog.close()));
        
        Button scienceBtn = new Button("🔬 VIEW NASA DATA");
        scienceBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        scienceBtn.getStyle().set("color", "#8B9BB4").set("font-size", "0.8rem");
        scienceBtn.addClickListener(e -> scienceDialog.open());

        VerticalLayout commsBox = new VerticalLayout(commsHeader, scienceBtn);
        commsBox.setPadding(true);
        commsBox.getStyle()
                .set("background-color", "rgba(17, 30, 51, 0.9)")
                .set("border-left", "4px solid #00D4E5")
                .set("border-radius", "8px")
                .set("margin-bottom", "30px");

        // Decisions
        H3 decisionPrompt = new H3("HOW SHOULD WE RESPOND?");
        decisionPrompt.getStyle().set("color", "#FFB347").set("letter-spacing", "2px").set("font-size", "1rem");

        VerticalLayout choicesLayout = new VerticalLayout();
        choicesLayout.setSpacing(true);
        choicesLayout.setPadding(false);
        choicesLayout.setWidthFull();

        for (Decision decision : crisis.choices) {
            Button choiceBtn = new Button();
            choiceBtn.setWidthFull();
            choiceBtn.getStyle()
                    .set("background-color", "rgba(31, 46, 69, 0.95)")
                    .set("border", "1px solid #1F2E45")
                    .set("border-radius", "8px")
                    .set("padding", "16px")
                    .set("text-align", "left")
                    .set("cursor", "pointer")
                    .set("color", "#F4F6FA")
                    .set("height", "auto");

            Span choiceTitle = new Span(decision.label);
            choiceTitle.getStyle()
                    .set("font-weight", "700")
                    .set("font-size", "1.1rem")
                    .set("color", "#00D4E5")
                    .set("display", "block");
            
            Span choiceTradeOff = new Span(decision.tradeOff);
            choiceTradeOff.getStyle()
                    .set("font-size", "0.85rem")
                    .set("color", "#FF5C5C")
                    .set("display", "block")
                    .set("margin-top", "6px");

            VerticalLayout btnContent = new VerticalLayout(choiceTitle, choiceTradeOff);
            btnContent.setPadding(false);
            btnContent.setSpacing(false);
            btnContent.setAlignItems(FlexComponent.Alignment.START);
            btnContent.setWidthFull();
            choiceBtn.setIcon(btnContent);
            choiceBtn.getElement().setAttribute("aria-label",
                    decision.label + ". " + decision.tradeOff);

            choiceBtn.addClickListener(e -> resolveDecision(crisis, decision));
            choicesLayout.add(choiceBtn);
        }

        mainContent.add(dayBadge, title, desc, commsBox, decisionPrompt, choicesLayout);
    }

    private void renderQuietDay() {
        // If it's not a crisis day, let the player advance time.
        Span dayBadge = new Span("DAY " + state.day + " / 30");
        dayBadge.getStyle().set("color", "#00D4E5").set("font-weight", "700").set("letter-spacing", "3px");
        
        H1 title = new H1("ALL SYSTEMS NOMINAL");
        title.getStyle().set("font-family", "'Space Grotesk', sans-serif").set("color", "#F4F6FA");
        
        Paragraph desc = new Paragraph("The outpost is operating smoothly. The crew is conducting routine maintenance and scientific experiments. Prepare for the next phase of the mission.");
        desc.getStyle().set("color", "#8B9BB4").set("font-size", "1.2rem");

        Button advanceBtn = new Button("ADVANCE TO NEXT PHASE");
        advanceBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        advanceBtn.getStyle()
                .set("background-color", "#FFB347")
                .set("color", "#0A1628")
                .set("font-weight", "700")
                .set("padding", "12px 24px")
                .set("margin-top", "20px");

        advanceBtn.addClickListener(e -> {
            state.day += 3; // Skip a few days
            loadCurrentState();
        });

        mainContent.add(dayBadge, title, desc, advanceBtn);
    }

    private void resolveDecision(Crisis crisis, Decision decision) {
        // Resolve logic in backend
        state = crisisService.resolveDecision(state, crisis, decision);

        // Show outcome modal
        Dialog outcomeDialog = new Dialog();
        outcomeDialog.setHeaderTitle("MISSION REPORT");
        outcomeDialog.add(new Paragraph(decision.outcomeNarrative));
        
        Button continueBtn = new Button("ACKNOWLEDGE", e -> {
            outcomeDialog.close();
            loadCurrentState();
        });
        continueBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        outcomeDialog.getFooter().add(continueBtn);
        outcomeDialog.open();
    }

    private void showDebrief(boolean success, String message) {
        H1 title = new H1(success ? "MISSION COMPLETE" : "MISSION FAILED");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("color", success ? "#4ADE80" : "#FF5C5C")
                .set("font-size", "3rem");

        Paragraph desc = new Paragraph(message);
        desc.getStyle().set("color", "#F4F6FA").set("font-size", "1.2rem");

        Button restartBtn = new Button("RETURN TO TITLE");
        restartBtn.addClickListener(e -> UI.getCurrent().navigate(""));

        VerticalLayout debriefBox = new VerticalLayout(title, desc, restartBtn);
        debriefBox.setAlignItems(FlexComponent.Alignment.CENTER);
        debriefBox.setPadding(true);
        debriefBox.getStyle().set("background-color", "rgba(17, 30, 51, 0.9)").set("border-radius", "12px");

        mainContent.add(debriefBox);
    }
}