package com.outpostarchitect.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Outpost Architect: Artemis")
public class TitleView extends VerticalLayout {

    public TitleView() {
        addClassName("title-screen");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Background & Overlay
        getStyle()
                .set("background-image", "url('images/bg-title.jpeg')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("position", "relative");

        // Dark overlay so text is readable
        VerticalLayout overlay = new VerticalLayout();
        overlay.addClassName("title-overlay");
        overlay.setSizeFull();
        overlay.setAlignItems(FlexComponent.Alignment.CENTER);
        overlay.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        overlay.getStyle().set("background-color", "rgba(10, 22, 40, 0.75)"); // Navy overlay

        // Logo & Text
        Image logo = new Image("images/logo.png", "Outpost Architect");
        logo.addClassName("title-logo");
        logo.setHeight("100px");

        Span title = new Span("CRISIS PROTOCOL");
        title.addClassName("title-heading");
        title.addClassName("fade-in");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-size", "clamp(2rem, 8vw, 3rem)")
                .set("font-weight", "700")
                .set("color", "#FFB347") // Signal Amber
                .set("letter-spacing", "clamp(2px, 1.3vw, 8px)")
                .set("margin-top", "20px");

        Span subtitle = new Span("THE LONG SHADOW");
        subtitle.addClassName("title-subheading");
        subtitle.addClassName("fade-in");
        subtitle.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-size", "clamp(1rem, 4vw, 1.5rem)")
                .set("color", "#00D4E5") // Mission Cyan
                .set("letter-spacing", "clamp(2px, 1.5vw, 12px)")
                .set("margin-bottom", "40px");

        Button startBtn = new Button("INITIATE MISSION");
        startBtn.addClassName("title-start");
        startBtn.setWidth("min(300px, calc(100vw - 48px))");
        startBtn.setHeight("60px");
        startBtn.getStyle()
                .set("background-color", "transparent")
                .set("color", "var(--cyan)")
                .set("border", "2px solid var(--cyan)")
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-weight", "700")
                .set("font-size", "1.2rem")
                .set("letter-spacing", "4px")
                .set("border-radius", "4px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 0 15px rgba(0, 212, 229, 0.2) inset");

        startBtn.addClickListener(e -> UI.getCurrent().navigate("play"));

        overlay.add(logo, title, subtitle, startBtn);
        add(overlay);
    }
}