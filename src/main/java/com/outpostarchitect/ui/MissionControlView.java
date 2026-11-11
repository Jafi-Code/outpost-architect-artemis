package com.outpostarchitect.ui;

import com.outpostarchitect.game.GameService;
import com.outpostarchitect.game.GameState;
import com.outpostarchitect.game.GameStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("")
@PageTitle("Outpost Architect: Artemis")
@StyleSheet("styles.css")
public class MissionControlView extends VerticalLayout {

    private static final String SITE_NAME = "Ridge Site A";
    private static final double SITE_ILLUMINATION = 0.70;

    private final GameService gameService;
    private GameState state;

    // Header
    private final H1 dayLabel = new H1();
    private final Span statusLabel = new Span();
    private final Span illuminationLabel = new Span();
    private final Button restartButton = new Button("↺ RESTART");

    // Resource bars
    private final Div powerBar = new Div();
    private final Div heatBar = new Div();
    private final Div cropsBar = new Div();
    private final Div crewBar = new Div();
    private final Span powerValue = new Span();
    private final Span heatValue = new Span();
    private final Span cropsValue = new Span();
    private final Span crewValue = new Span();

    // Log & action
    private final Image elenaPortrait = new Image("images/char-elena.jpeg", "Elena Park");
    private final Span elenaName = new Span("ELENA PARK");
    private final Span elenaTitle = new Span("Systems Engineer");
    private final Div commsText = new Div();
    private final Button advanceButton = new Button("ADVANCE DAY ▶");
    private final Button conserveBtn = new Button("⚡ CONSERVE POWER");
    private final Button heatingBtn = new Button("🔥 BOOST HEATING");

    @Autowired
    public MissionControlView(GameService gameService) {
        this.gameService = gameService;
        this.state = new GameState(SITE_NAME, SITE_ILLUMINATION);

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setMaxWidth("900px");
        getStyle().set("margin", "0 auto");
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle()
                .set("background-color", "#0A1628")
                .set("color", "#F4F6FA")
                .set("font-family", "'Inter', 'Segoe UI', sans-serif")
                .set("min-height", "100vh")
                .set("gap", "16px");

        buildUI();
        refresh();
    }

    private void buildUI() {
        // ---- Header ----
        Image logo = new Image("images/logo.png", "Outpost Architect: Artemis");
        logo.setHeight("40px");

        HorizontalLayout brandRow = new HorizontalLayout(logo);
        brandRow.setAlignItems(FlexComponent.Alignment.CENTER);

        statusLabel.getStyle()
                .set("font-size", "0.75rem")
                .set("letter-spacing", "3px")
                .set("font-weight", "700")
                .set("color", "#00D4E5");

        restartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        restartButton.getStyle()
                .set("color", "#8B9BB4")
                .set("font-size", "0.75rem")
                .set("letter-spacing", "2px");
        restartButton.addClickListener(e -> {
            state = new GameState(SITE_NAME, SITE_ILLUMINATION);
            refresh();
        });

        HorizontalLayout rightHeader = new HorizontalLayout(statusLabel, restartButton);
        rightHeader.setSpacing(true);
        rightHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(brandRow, rightHeader);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // ---- Day & illumination ----
        dayLabel.getStyle()
                .set("margin", "0")
                .set("font-family", "'Space Grotesk', 'Segoe UI', sans-serif")
                .set("font-size", "2.5rem")
                .set("letter-spacing", "4px")
                .set("color", "#F4F6FA");

        illuminationLabel.getStyle()
                .set("color", "#FFB347")
                .set("font-size", "0.85rem")
                .set("letter-spacing", "1px");

        Span siteSpan = new Span("  ·  " + SITE_NAME + "  ·  LROC-based simulation");
        siteSpan.getStyle()
                .set("color", "#8B9BB4")
                .set("font-size", "0.85rem");

        HorizontalLayout dayRow = new HorizontalLayout(dayLabel, illuminationLabel, siteSpan);
        dayRow.setSpacing(true);
        dayRow.setAlignItems(FlexComponent.Alignment.BASELINE);

        // ---- Resources ----
        VerticalLayout resources = new VerticalLayout(
                resourceRow("POWER", "images/icon-power.png", powerBar, powerValue, "#FFB347"),
                resourceRow("LIFE SUPPORT", "images/icon-life-support.png", heatBar, heatValue, "#00D4E5"),
                resourceRow("FOOD", "images/icon-food.png", cropsBar, cropsValue, "#4ADE80"),
                resourceRow("CREW SAFETY", "images/icon-crew-safety.png", crewBar, crewValue, "#FF5C5C")
        );
        resources.setSpacing(true);
        resources.setPadding(false);
        resources.getStyle()
                .set("background-color", "#111E33")
                .set("padding", "20px 24px")
                .set("border-radius", "12px")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        // ---- Communications Panel ----
        elenaPortrait.setWidth("80px");
        elenaPortrait.setHeight("80px");
        elenaPortrait.getStyle()
                .set("border-radius", "50%")
                .set("border", "2px solid #00D4E5")
                .set("object-fit", "cover")
                .set("box-shadow", "0 0 15px rgba(0, 212, 229, 0.3)");

        elenaName.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "1rem")
                .set("color", "#F4F6FA")
                .set("letter-spacing", "1px");

        elenaTitle.getStyle()
                .set("font-size", "0.75rem")
                .set("color", "#FFB347")
                .set("letter-spacing", "1px")
                .set("margin-bottom", "8px");

        VerticalLayout nameGroup = new VerticalLayout(elenaName, elenaTitle);
        nameGroup.setSpacing(false);
        nameGroup.setPadding(false);
        nameGroup.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        commsText.getStyle()
                .set("background-color", "#1F2E45")
                .set("padding", "16px")
                .set("border-radius", "8px")
                .set("font-family", "'Inter', sans-serif")
                .set("font-size", "0.95rem")
                .set("line-height", "1.5")
                .set("color", "#F4F6FA")
                .set("margin-top", "12px")
                .set("min-height", "80px");

        HorizontalLayout headerComms = new HorizontalLayout(elenaPortrait, nameGroup);
        headerComms.setAlignItems(FlexComponent.Alignment.CENTER);
        headerComms.setSpacing(true);

        VerticalLayout commsPanel = new VerticalLayout(headerComms, commsText);
        commsPanel.setPadding(true);
        commsPanel.setSpacing(false);
        commsPanel.getStyle()
                .set("background-color", "#111E33")
                .set("border-radius", "12px")
                .set("border-left", "4px solid #00D4E5")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        // ---- Turn actions ----
        conserveBtn.setWidth("200px");
        conserveBtn.setHeight("280px");
        conserveBtn.getStyle()
                .set("background-image", "url('images/action-conserve.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("color", "transparent")
                .set("border", "3px solid transparent")
                .set("border-radius", "12px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.5)");
        conserveBtn.addClickListener(e -> {
            state.setConservePowerThisTurn(!state.isConservePowerThisTurn());
            conserveBtn.getStyle().set("border",
                    state.isConservePowerThisTurn() ? "3px solid #00D4E5" : "3px solid transparent");
        });

        heatingBtn.setWidth("200px");
        heatingBtn.setHeight("280px");
        heatingBtn.getStyle()
                .set("background-image", "url('images/action-heat.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("color", "transparent")
                .set("border", "3px solid transparent")
                .set("border-radius", "12px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.5)");
        heatingBtn.addClickListener(e -> {
            state.setBoostHeatingThisTurn(!state.isBoostHeatingThisTurn());
            heatingBtn.getStyle().set("border",
                    state.isBoostHeatingThisTurn() ? "3px solid #FFB347" : "3px solid transparent");
        });

        HorizontalLayout actionRow = new HorizontalLayout(conserveBtn, heatingBtn);
        actionRow.setWidthFull();
        actionRow.setSpacing(true);
        actionRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // ---- Advance button ----
        advanceButton.setWidthFull();
        advanceButton.getStyle()
                .set("background-color", "#FFB347")
                .set("color", "#0A1628")
                .set("font-weight", "700")
                .set("letter-spacing", "3px")
                .set("height", "60px")
                .set("font-size", "1.1rem")
                .set("border-radius", "10px");
        advanceButton.addClickListener(e -> {
            state = gameService.advanceDay(state);
            refresh();
        });

        add(header, dayRow, resources, commsPanel, actionRow, advanceButton);
    }

    private HorizontalLayout resourceRow(String name, String iconPath, Div bar, Span value, String color) {
        Image icon = new Image(iconPath, name);
        icon.setWidth("24px");
        icon.setHeight("24px");
        icon.getStyle().set("margin-right", "12px");

        Span label = new Span(name);
        label.getStyle()
                .set("font-size", "0.78rem")
                .set("letter-spacing", "2px")
                .set("color", "#8B9BB4")
                .set("font-weight", "700")
                .set("min-width", "90px");

        HorizontalLayout labelGroup = new HorizontalLayout(icon, label);
        labelGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        labelGroup.setSpacing(false);
        labelGroup.setMinWidth("160px");

        value.getStyle()
                .set("color", color)
                .set("font-family", "'JetBrains Mono', 'Consolas', monospace")
                .set("font-weight", "700")
                .set("min-width", "55px")
                .set("text-align", "right")
                .set("font-size", "1.1rem");

        Div track = new Div(bar);
        track.getStyle()
                .set("background-color", "#1F2E45")
                .set("border-radius", "6px")
                .set("height", "12px")
                .set("flex-grow", "1")
                .set("overflow", "hidden")
                .set("display", "flex");
        bar.getStyle()
                .set("background-color", color)
                .set("height", "100%")
                .set("border-radius", "6px")
                .set("transition", "width 0.4s ease")
                .set("width", "100%");

        HorizontalLayout row = new HorizontalLayout(labelGroup, track, value);
        row.setWidthFull();
        row.setSpacing(true);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.expand(track);
        return row;
    }

    private void refresh() {
        dayLabel.setText("DAY " + state.getDay() + " / " + GameState.TOTAL_DAYS);
        illuminationLabel.setText(String.format("☀ %.0f%% nominal illumination",
                state.getIllumination() * 100));

        // Status pill
        if (state.getStatus() == GameStatus.IN_PROGRESS) {
            statusLabel.setText("● IN PROGRESS");
            statusLabel.getStyle().set("color", "#00D4E5");
        } else if (state.getStatus() == GameStatus.VICTORY) {
            statusLabel.setText("★ MISSION COMPLETE");
            statusLabel.getStyle().set("color", "#4ADE80");
        } else {
            statusLabel.setText("✖ MISSION FAILED");
            statusLabel.getStyle().set("color", "#FF5C5C");
        }

        // Resource bars
        powerBar.getStyle().set("width", state.getPower() + "%");
        heatBar.getStyle().set("width", state.getHeat() + "%");
        cropsBar.getStyle().set("width", state.getCrops() + "%");
        crewBar.getStyle().set("width", state.getCrew() + "%");

        powerValue.setText(state.getPower() + "%");
        heatValue.setText(state.getHeat() + "%");
        cropsValue.setText(state.getCrops() + "%");
        crewValue.setText(state.getCrew() + "%");

        // Crew bar turns red when critical
        String crewColor = state.getCrew() > 60 ? "#00D4E5"
                : state.getCrew() > 30 ? "#FFB347" : "#FF5C5C";
        crewBar.getStyle().set("background-color", crewColor);
        crewValue.getStyle().set("color", crewColor);

        // Show the latest mission log entry as Elena's communication.
        List<String> log = state.getMissionLog();
        if (!log.isEmpty()) {
            String latestMessage = log.get(log.size() - 1);
            if (latestMessage.contains(":")) {
                latestMessage = latestMessage.substring(latestMessage.indexOf(":") + 1).trim();
            }
            commsText.setText(latestMessage);
        }

        // Advance button state
        boolean over = state.getStatus() != GameStatus.IN_PROGRESS;
        advanceButton.setEnabled(!over);
        if (over) {
            advanceButton.setText(state.getStatus() == GameStatus.VICTORY
                    ? "★ MISSION COMPLETE" : "✖ MISSION FAILED");
            advanceButton.getStyle().set("background-color",
                    state.getStatus() == GameStatus.VICTORY ? "#4ADE80" : "#FF5C5C");
        }

        conserveBtn.getStyle().set("border",
                state.isConservePowerThisTurn() ? "3px solid #00D4E5" : "3px solid transparent");
        heatingBtn.getStyle().set("border",
                state.isBoostHeatingThisTurn() ? "3px solid #FFB347" : "3px solid transparent");
    }
}