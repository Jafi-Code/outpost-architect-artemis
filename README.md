# Outpost Architect: Artemis — Crisis Protocol

**A NASA-data-driven lunar settlement survival simulator.**

> _Every decision has a consequence. Every consequence changes the mission._

## What It Is

**Outpost Architect: Artemis** is a narrative-driven crisis simulator where you take command of **ARES-01**, a fictional lunar south pole research outpost. Over a 30-day mission, you face three interconnected emergencies that test your judgment, resource management, and leadership.

You are the **Junior Outpost Commander**. Your crew of three depends on your decisions. The wrong call could save the mission — or doom it.

## The Challenge

Built for the **NASA Space Apps Challenge 2026 — "Build a Junior Astronaut Mission Trainer."** The challenge asks teams to create an interactive experience where students experience the trade-offs of running a lunar outpost.

## The Three Crises

| Day    | Crisis                    | Core Decision                                                                                     |
| :----- | :------------------------ | :------------------------------------------------------------------------------------------------ |
| **07** | **The Long Shadow**       | Conserve power (protect life support) vs. maintain agriculture (risk power collapse).             |
| **12** | **One Loop Broken**       | Repair the water recycler (use your last spare part) vs. conserve water (cripple the greenhouse). |
| **20** | **The Window to Shelter** | Abort the EVA (protect the astronaut) vs. complete the repair (restore critical power).           |

## NASA Data Integration

Each crisis is grounded in real NASA research:

- **LROC** (Lunar Reconnaissance Orbiter Camera): Illumination data drives the Long Shadow crisis.
- **ECLSS** (Environmental Control and Life Support System): Water recovery benchmarks inform the water recycler crisis.
- **CRaTER** (Cosmic Ray Telescope for the Effects of Radiation): Radiation environment research informs the EVA crisis.

See [`SCIENCE.md`](./SCIENCE.md) for full citations and methodology.

## Key Features

- **Meaningful Trade-offs:** Every choice permanently changes the mission state.
- **The Ripple Effect:** Decisions in early crises dynamically change the narrative of later ones.
- **Morale System:** The crew is more than health points — if morale hits zero, they mutiny.
- **Commander's Log Debrief:** At the end, the game reads back your decisions and generates a personal reflection based on what you sacrificed.
- **Science Behind the Decision:** Every crisis has an expandable NASA data panel.

## Tech Stack

- **Backend:** Java 21, Spring Boot
- **Frontend:** Vaadin 25 (server-side rendering)
- **Build:** Maven
- **Deployment:** Render (Docker)

## Run Locally

```bash
# Requires JDK 21
./mvnw spring-boot:run
```

Open http://localhost:8080/.

## Project Structure

src/main/java/com/outpostarchitect/
├── game/ # Simulation engine
│ ├── MissionState.java
│ ├── Crisis.java
│ ├── Decision.java
│ └── CrisisService.java
└── ui/
├── TitleView.java # Landing screen
└── CrisisView.java # Gameplay, HUD, debrief

src/main/resources/META-INF/resources/images/
└── (all game assets)

## Author

Built by Mojalefa Mokgadi for the NASA Space Apps Challenge 2026.
