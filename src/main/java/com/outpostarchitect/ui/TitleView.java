package com.outpostarchitect.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
        overlay.setSizeFull();
        overlay.setAlignItems(FlexComponent.Alignment.CENTER);
        overlay.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        overlay.getStyle().set("background-color", "rgba(10, 22, 40, 0.75)"); // Navy overlay

        // Logo & Text
        Image logo = new Image("images/logo.png", "Outpost Architect");
        logo.setHeight("100px");

        Span title = new Span("CRISIS PROTOCOL");
        title.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-size", "3rem")
                .set("font-weight", "700")
                .set("color", "#FFB347") // Signal Amber
                .set("letter-spacing", "8px")
                .set("margin-top", "20px");

        Span subtitle = new Span("THE LONG SHADOW");
        subtitle.getStyle()
                .set("font-family", "'Space Grotesk', sans-serif")
                .set("font-size", "1.5rem")
                .set("color", "#00D4E5") // Mission Cyan
                .set("letter-spacing", "12px")
                .set("margin-bottom", "40px");

        Button startBtn = new Button("INITIATE MISSION");
        startBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        startBtn.getStyle()
                .set("background-color", "#00D4E5")
                .set("color", "#0A1628")
                .set("font-weight", "700")
                .set("font-size", "1.2rem")
                .set("letter-spacing", "2px")
                .set("padding", "15px 40px")
                .set("border-radius", "8px")
                .set("cursor", "pointer");

        startBtn.addClickListener(e -> UI.getCurrent().navigate("play"));

        overlay.add(logo, title, subtitle, startBtn);
        add(overlay);
    }
}