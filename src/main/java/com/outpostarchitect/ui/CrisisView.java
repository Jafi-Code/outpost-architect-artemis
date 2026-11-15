package com.outpostarchitect.ui;

import com.outpostarchitect.game.Crisis;
import com.outpostarchitect.game.CrisisService;
import com.outpostarchitect.game.Decision;
import com.outpostarchitect.game.MissionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("play")
@PageTitle("Crisis Protocol")
@StyleSheet("styles.css")
public class CrisisView extends VerticalLayout {

    private final CrisisService crisisService;
    private  MissionState state;
    private final VerticalLayout mainContent;
    private final Span statsHud = new Span();

    @Autowired
    public CrisisView(CrisisService crisisService) {
        this.crisisService = crisisService;
        this.state = new MissionState();
        this.mainContent = new VerticalLayout();

        addClassName("game-view");
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

        // --- HUD Header ---
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("game-header");
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle().set("background-color", "rgba(10, 22, 40, 0.95)").set("border-bottom", "2px solid #1F2E45");
        
        Image logoSmall = new Image("images/logo.png", "Logo");
        logoSmall.addClassName("game-logo");
        logoSmall.setHeight("30px");
        
        statsHud.addClassName("game-hud");
        statsHud.getStyle()
                .set("font-family", "'JetBrains Mono', monospace")
                .set("font-size", "1rem")
                .set("font-weight", "700");

        Button resetButton = new Button("RESET MISSION", e -> UI.getCurrent().navigate(""));
        resetButton.addClassName("reset-button");
        resetButton.getElement().setAttribute("aria-label", "Reset mission and return to the title screen");

        header.add(logoSmall, statsHud, resetButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(statsHud);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Main Content Container
        mainContent.addClassName("game-content");
        mainContent.setMaxWidth("900px");
        mainContent.setWidthFull();
        mainContent.setMinHeight("0");
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        setFlexGrow(1, mainContent);

        add(header, mainContent);
        
        loadCurrentState();
    }

    private void loadCurrentState() {
        mainContent.removeAll();
        updateStatsHud();

        // Check Win/Lose Conditions
        if (state.crew <= 0) {
            showDebrief(false, "Crew health reached zero. Mission failed.");
            return;
        }
        if (state.morale <= 0) {
            showDebrief(false, "The crew has mutinied. They refuse to follow your orders. Mission failed.");
            return;
        }
        if (state.food <= 0) {
            showDebrief(false, "The crew starved. Mission failed.");
            return;
        }
        if (state.power <= 0) {
            showDebrief(false, "Power failure. The habitat froze. Mission failed.");
            return;
        }
        if (state.day > 30) {
            showDebrief(true, "30 days survived. The outpost is stable.");
            return;
        }

        Crisis crisis = crisisService.getCrisisForDay(state);

        if (crisis != null) {
            renderCrisis(crisis);
        } else {
            renderQuietDay();
        }
    }

    private void updateStatsHud() {
        String powerColor = state.power < 30 ? "#FF5C5C" : (state.power < 60 ? "#FFB347" : "#00D4E5");
        String foodColor = state.food < 30 ? "#FF5C5C" : (state.food < 60 ? "#FFB347" : "#4ADE80");
        String crewColor = state.crew < 30 ? "#FF5C5C" : (state.crew < 60 ? "#FFB347" : "#00D4E5");
        String moraleColor = state.morale < 30 ? "#FF5C5C" : (state.morale < 60 ? "#FFB347" : "#D946EF");

        statsHud.getElement().setProperty("innerHTML",
                String.format(
                        "<div class='hud-stats'>"
                                + "<div class='hud-stat' style='color:%1$s'>PWR: %2$d%%<div class='hud-stat-bar'><div style='width:%2$d%%;background-color:%1$s'></div></div></div>"
                                + "<div class='hud-stat' style='color:%3$s'>FOOD: %4$d%%<div class='hud-stat-bar'><div style='width:%4$d%%;background-color:%3$s'></div></div></div>"
                                + "<div class='hud-stat' style='color:%5$s'>CREW: %6$d%%<div class='hud-stat-bar'><div style='width:%6$d%%;background-color:%5$s'></div></div></div>"
                                + "<div class='hud-stat' style='color:%7$s'>MORALE: %8$d%%<div class='hud-stat-bar'><div style='width:%8$d%%;background-color:%7$s'></div></div></div>"
                                + "<div class='hud-stat' style='color:var(--text-muted)'>PARTS: %9$d</div>"
                                + "</div>",
                        powerColor, state.power,
                        foodColor, state.food,
                        crewColor, state.crew,
                        moraleColor, state.morale,
                        state.spareParts));
    }

    private void renderCrisis(Crisis crisis) {
        mainContent.addClassName("fade-in");

        if ("crisis_1".equals(crisis.id)) {
            renderPowerGrid(crisis);
            return;
        }

        addClassName("crisis-alert");

        // Day Indicator
        Span dayBadge = new Span("DAY " + state.day + " / 30 · INCIDENT REPORT");
        dayBadge.addClassName("fade-in");
        dayBadge.getStyle()
                .set("color", "#FF5C5C") // Solar Red
                .set("font-weight", "700")
                .set("letter-spacing", "3px")
                .set("font-size", "0.85rem");

        // Crisis Title
        H1 title = new H1(crisis.title);
        title.addClassName("fade-in");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("color", "#F4F6FA")
                .set("margin", "0")
                .set("font-size", "clamp(1.75rem, 5vw, 2.5rem)");

        // Description
        Paragraph desc = new Paragraph(crisis.description);
        desc.addClassName("fade-in");
        desc.addClassName("typing-effect");
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
        decisionPrompt.addClassName("fade-in");
        decisionPrompt.getStyle().set("color", "#FFB347").set("letter-spacing", "2px").set("font-size", "1rem");

        VerticalLayout choicesLayout = new VerticalLayout();
        choicesLayout.addClassName("choice-list");
        choicesLayout.setSpacing(true);
        choicesLayout.setPadding(false);
        choicesLayout.setWidthFull();

        for (Decision decision : crisis.choices) {
            Button choiceBtn = new Button();
            choiceBtn.addClassName("action-card");
            choiceBtn.setWidthFull();
            choiceBtn.getStyle()
                    .set("background-color", "rgba(31, 46, 69, 0.95)")
                    .set("border", "1px solid #00D4E5")
                    .set("border-radius", "8px")
                    .set("padding", "16px")
                    .set("text-align", "left")
                    .set("cursor", "pointer")
                    .set("color", "#F4F6FA")
                    .set("height", "auto");

            Image actionImage = new Image(actionCardImageFor(decision), decision.label + " action card");
            actionImage.addClassName("action-card-image");
            actionImage.getStyle().set("object-fit", "contain").set("flex-shrink", "0");

            Span choiceTitle = new Span(decision.label);
            choiceTitle.getStyle()
                    .set("font-weight", "700")
                    .set("font-size", "1.2rem")
                    .set("color", "#00D4E5")
                    .set("display", "block");
            
            Span choiceTradeOff = new Span(decision.tradeOff);
            choiceTradeOff.getStyle()
                    .set("font-size", "0.85rem")
                    .set("color", "#FF5C5C")
                    .set("display", "block")
                    .set("margin-top", "6px");

            VerticalLayout cardText = new VerticalLayout(choiceTitle, choiceTradeOff);
            cardText.addClassName("action-card-copy");
            cardText.setPadding(false);
            cardText.setSpacing(false);
            cardText.setAlignItems(FlexComponent.Alignment.START);
            cardText.setWidthFull();

            HorizontalLayout cardContent = new HorizontalLayout(actionImage, cardText);
            cardContent.addClassName("action-card-content");
            cardContent.setWidthFull();
            cardContent.setAlignItems(FlexComponent.Alignment.CENTER);
            cardContent.setSpacing(true);
            choiceBtn.setIcon(cardContent);
            choiceBtn.getElement().setAttribute("aria-label",
                    decision.label + ". " + decision.tradeOff);

            choiceBtn.addClickListener(e -> resolveDecision(crisis, decision));
            choicesLayout.add(choiceBtn);
        }

        mainContent.add(dayBadge, title, desc, commsBox, decisionPrompt, choicesLayout);
    }

    private String actionCardImageFor(Decision decision) {
        return switch (decision.label) {
            case "PUSH FORWARD IMMEDIATELY", "PROTECT LIFE SUPPORT" -> "images/action-heat.png";
            case "MAINTAIN AGRICULTURE" -> "images/action-greenhouse.png";
            case "CONSERVE WATER", "ABORT THE EVA" -> "images/action-conserve.png";
            default -> "images/action-inspect.png";
        };
    }

    private void renderPowerGrid(Crisis crisis) {
        mainContent.addClassName("fade-in");
        addClassName("crisis-alert");

        Span dayBadge = new Span("DAY 7 / 30 · EMERGENCY ALLOCATION");
        dayBadge.addClassName("fade-in");
        dayBadge.getStyle()
                .set("color", "#FF5C5C")
                .set("font-weight", "700")
                .set("letter-spacing", "3px");

        H1 title = new H1(crisis.title);
        title.addClassName("fade-in");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("color", "#F4F6FA");

        Paragraph desc = new Paragraph(
                "The solar array is failing. You have 100 Megawatts to distribute. "
                        + "If any system drops too low, the mission is compromised.");
        desc.addClassName("fade-in");
        desc.addClassName("typing-effect");
        desc.getStyle().set("color", "#F4F6FA").set("font-size", "1.2rem");

        Span totalLabel = new Span();
        totalLabel.getStyle()
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("color", "#FFB347")
                .set("margin-bottom", "20px");

        SliderControl heatControl = createSlider(
                "HABITAT HEATING", 40, "Minimum 40 MW to keep crew alive", 40);
        SliderControl greenhouseControl = createSlider(
                "GREENHOUSE", 30, "Minimum 30 MW to keep crops alive", 35);
        SliderControl commsControl = createSlider(
                "COMMS & LIFE SUPPORT", 20, "Minimum 20 MW to maintain oxygen", 25);

        VerticalLayout sliderContainer = new VerticalLayout(
                heatControl.layout(), greenhouseControl.layout(), commsControl.layout());
        sliderContainer.addClassName("power-grid-panel");
        sliderContainer.setWidthFull();
        sliderContainer.setPadding(true);
        sliderContainer.getStyle()
                .set("background", "rgba(17, 30, 51, 0.4)")
                .set("border-radius", "12px")
                .set("border", "1px solid #1F2E45");

        Button confirmBtn = new Button("ENGAGE POWER GRID");
        confirmBtn.setWidthFull();
        confirmBtn.getStyle()
                .set("background-color", "#00D4E5")
                .set("color", "#0A1628")
                .set("font-weight", "700")
                .set("font-size", "1.2rem")
                .set("padding", "16px")
                .set("margin-top", "30px");

        Runnable updateTotal = () -> {
            int total = heatControl.slider().getValue()
                    + greenhouseControl.slider().getValue()
                    + commsControl.slider().getValue();
            int remaining = 100 - total;
            totalLabel.setText(remaining < 0
                    ? "OVER ALLOCATION: " + -remaining + " MW"
                    : "REMAINING POWER: " + remaining + " MW");
            totalLabel.getStyle().set("color", remaining < 0 ? "#FF5C5C" : "#FFB347");
            confirmBtn.setEnabled(total == 100);
        };

        heatControl.slider().addValueChangeListener(e -> updateTotal.run());
        greenhouseControl.slider().addValueChangeListener(e -> updateTotal.run());
        commsControl.slider().addValueChangeListener(e -> updateTotal.run());
        updateTotal.run();

        confirmBtn.addClickListener(e -> {
            int heat = heatControl.slider().getValue();
            int greenhouse = greenhouseControl.slider().getValue();
            int comms = commsControl.slider().getValue();
            int total = heat + greenhouse + comms;

            if (total != 100) {
                Notification.show("Allocate exactly 100 MW before engaging the power grid.");
                return;
            }

            state = crisisService.resolvePowerGrid(state, heat, greenhouse, comms);

            Dialog outcomeDialog = new Dialog();
            outcomeDialog.setHeaderTitle("POWER GRID RESOLVED");
            StringBuilder outcomeText = new StringBuilder("You distributed the power. ");
            if (state.flags.contains("greenhouseCrippled")) {
                outcomeText.append("The greenhouse froze. We lost our crops. ");
            }
            if (state.flags.contains("crewFroze")) {
                outcomeText.append("The habitat got too cold. The crew is suffering. ");
            }
            if (state.flags.contains("commsLost")) {
                outcomeText.append("Communications failed. The crew feels abandoned. ");
            }
            if (!state.flags.contains("greenhouseCrippled")
                    && !state.flags.contains("crewFroze")
                    && !state.flags.contains("commsLost")) {
                outcomeText.append("The crew survived the night.");
            }

            outcomeDialog.add(new Paragraph(outcomeText.toString()));
            Button continueBtn = new Button("ACKNOWLEDGE", event -> {
                outcomeDialog.close();
                loadCurrentState();
            });
            outcomeDialog.getFooter().add(continueBtn);
            outcomeDialog.open();
        });

        mainContent.add(
                dayBadge, title, desc, totalLabel,
                sliderContainer, confirmBtn);
    }

    private SliderControl createSlider(String title, int minimum, String helpText, int initialValue) {
        Span titleSpan = new Span(title);
        titleSpan.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-weight", "700")
                .set("color", "var(--cyan)")
                .set("font-size", "1.1rem")
                .set("letter-spacing", "1px")
                .set("display", "block");

        Span helpSpan = new Span(helpText);
        helpSpan.getStyle()
                .set("font-family", "'JetBrains Mono', monospace")
                .set("font-size", "0.75rem")
                .set("color", "var(--text-muted)")
                .set("display", "block")
                .set("margin-bottom", "8px");

        IntegerSlider slider = new IntegerSlider(0, 100);
        slider.setValue(initialValue);
        slider.setWidthFull();
        slider.setStep(1);
        slider.getElement().setAttribute(
                "aria-description", "Minimum allocation: " + minimum + " MW. " + helpText);
        slider.getStyle().set("padding-bottom", "20px");

        VerticalLayout sliderLayout = new VerticalLayout(titleSpan, helpSpan, slider);
        sliderLayout.addClassName("power-slider-card");
        sliderLayout.setPadding(false);
        sliderLayout.setSpacing(false);
        sliderLayout.setWidthFull();
        sliderLayout.getStyle()
                .set("background", "rgba(17, 30, 51, 0.5)")
                .set("padding", "16px")
                .set("border-radius", "8px")
                .set("border-left", "2px solid var(--cyan)");

        return new SliderControl(sliderLayout, slider);
    }

    private record SliderControl(VerticalLayout layout, IntegerSlider slider) {
    }

    private void renderQuietDay() {
        Span dayBadge = new Span("DAY " + state.day + " / 30 · ROUTINE OPERATIONS");
        dayBadge.addClassName("fade-in");
        dayBadge.getStyle().set("color", "#4ADE80").set("font-weight", "700").set("letter-spacing", "3px");
        
        H1 title = new H1("ALL SYSTEMS NOMINAL");
        title.addClassName("fade-in");
        title.getStyle().set("font-family", "'Space Grotesk', sans-serif").set("color", "#F4F6FA");
        
        Paragraph desc = new Paragraph("The outpost is operating smoothly. Use this time to prepare for the next phase. How should the crew spend their time?");
        desc.addClassName("fade-in");
        desc.addClassName("typing-effect");
        desc.getStyle().set("color", "#8B9BB4").set("font-size", "1.2rem");

        Button restBtn = new Button("REST & RECOVER (+10 CREW)");
        restBtn.setWidthFull();
        restBtn.getStyle()
                .set("background-color", "#1F2E45")
                .set("color", "#00D4E5")
                .set("padding", "16px");
        restBtn.addClickListener(e -> {
            state.crew = Math.min(100, state.crew + 10);
            state.day += 3;
            loadCurrentState();
        });

        Button workBtn = new Button("CONDUCT EFFICIENCY DRILLS (+10 POWER)");
        workBtn.setWidthFull();
        workBtn.getStyle()
                .set("background-color", "#1F2E45")
                .set("color", "#FFB347")
                .set("padding", "16px");
        workBtn.addClickListener(e -> {
            state.power = Math.min(100, state.power + 10);
            state.day += 3;
            loadCurrentState();
        });

        VerticalLayout choices = new VerticalLayout(restBtn, workBtn);
        choices.setPadding(false);
        choices.setWidthFull();

        mainContent.add(dayBadge, title, desc, choices);
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
        title.addClassName("fade-in");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("color", success ? "#4ADE80" : "#FF5C5C")
                .set("font-size", "clamp(2rem, 7vw, 3rem)")
                .set("margin-bottom", "0");

        Paragraph reason = new Paragraph(message);
        reason.addClassName("fade-in");
        reason.getStyle()
                .set("color", "#F4F6FA")
                .set("font-size", "1.2rem")
                .set("margin-bottom", "30px");

        H3 logTitle = new H3("COMMANDER'S LOG");
        logTitle.addClassName("fade-in");
        logTitle.getStyle().set("color", "#FFB347").set("letter-spacing", "2px");

        VerticalLayout logEntries = new VerticalLayout();
        logEntries.setSpacing(true);
        logEntries.setPadding(true);
        logEntries.getStyle()
                .set("background-color", "rgba(17, 30, 51, 0.9)")
                .set("border-left", "4px solid #00D4E5")
                .set("border-radius", "8px")
                .set("width", "100%");

        for (String entry : state.decisionHistory) {
            Paragraph paragraph = new Paragraph(entry);
            paragraph.getStyle().set("color", "#F4F6FA").set("margin", "0");
            logEntries.add(paragraph);
        }

        String reflection = "I don't know if we'll make it. ";
        if (state.flags.contains("greenhouseCrippled")) {
            reflection += "I sacrificed the greenhouse to save power. Now the crew is starving. ";
        }
        if (state.flags.contains("waterCrippled")) {
            reflection += "The water recycler is dead. We're living on fumes. ";
        }
        if (state.flags.contains("solarCrippled")) {
            reflection += "The solar array is still broken. Every day we lose more power. ";
        }
        if (success && state.flags.isEmpty()) {
            reflection = "We made it through without crippling a single major system. "
                    + "The crew trusted me, and I didn't let them down.";
        }

        Paragraph reflectionParagraph = new Paragraph(reflection);
        reflectionParagraph.addClassName("fade-in");
        reflectionParagraph.getStyle()
                .set("color", "#8B9BB4")
                .set("font-style", "italic")
                .set("margin-top", "20px");

        Button restartBtn = new Button("TRY AGAIN");
        restartBtn.getStyle()
                .set("background-color", "#00D4E5")
                .set("color", "#0A1628")
                .set("font-weight", "700")
                .set("margin-top", "20px");
        restartBtn.addClickListener(e -> UI.getCurrent().navigate(""));

        VerticalLayout debriefBox = new VerticalLayout(
                title, reason, logTitle, logEntries, reflectionParagraph, restartBtn);
        debriefBox.setAlignItems(FlexComponent.Alignment.CENTER);
        debriefBox.setPadding(true);
        debriefBox.setMaxWidth("800px");

        mainContent.removeAll();
        mainContent.add(debriefBox);
    }
}