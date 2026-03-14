# 🚀 Super Star Trek

A text-based space strategy game written in Java, inspired by the classic 1971 mainframe game *Star Trek*. You are the captain of the USS Enterprise. Your mission: hunt down Klingon warships scattered across the galaxy before they overwhelm the Federation.

---

## 🎮 How the Game Works

When the game starts, the galaxy is procedurally generated and populated with stars, Klingon warships, and Federation starbases. The Enterprise is placed at a random location, and you are told which galactic quadrant you begin in:

```
Your mission begins with your starship located in the
Galactic Quadrant, 'Antares II'.
```

From here, the game enters a simple command loop. You issue orders, the ship and universe respond, and the game reports back in plain text. There are no timers, no animations — just you, the stars, and the Klingons.

The game tracks a **performance rating** (starting at 100%). Poor decisions — such as flying outside the galaxy — reduce it. The goal is to defeat the Klingons with your performance rating as high as possible.

If you want to quit, you can resign your commission at any time. When a session ends, the game asks if you want to play again, and whether you want the same galaxy layout or a freshly generated one.

---

## ⌨️ Commands

Navigation and exploration are handled through a handful of short, memorable commands. The entire game is operated from the keyboard with no menus or mouse required.

| Command | What it does |
|---------|--------------|
| `nav`   | **Navigate** — engage warp engines. Prompts for a heading and warp factor. |
| `srs`   | **Short Range Scan** — shows objects in the 8×8 sectors of your current quadrant. |
| `lrs`   | **Long Range Scan** — shows Klingon, starbase, and star counts in the 3×3 quadrants surrounding you. |
| `goto`  | **Go to position** — teleport directly to a quadrant and sector (debug/testing command). |
| `help`  | **Help** — get information about a specific command. |
| `xxx`   | **Resign commission** — end the game. |

That's the entire control scheme. Six commands, typed in full, no abbreviations required. If you enter something unrecognised, the game lists the valid options and waits.

---

## 🧭 Navigation — Heading and Warp

The `nav` command is the heart of the game. It asks for two inputs:

**1. Heading (1–9)**

Heading uses a classic compass-rose number pad scheme, where the digits 1–9 map directly to directions:

```
7   8   9
 ↖  ↑  ↗
4 ← · → 6
 ↙  ↓  ↘
1   2   3

(5 = stationary, 9 = same as 1)
```

Each integer maps to an (x, y) direction vector internally:

```
1 → ( 1,  0)   East
2 → ( 1, -1)   North-East
3 → ( 0, -1)   North
4 → (-1, -1)   North-West
5 → (-1,  0)   West
6 → (-1,  1)   South-West
7 → ( 0,  1)   South
8 → ( 1,  1)   South-East
9 → wraps back to 1
```

Fractional headings (e.g. `1.5`) interpolate between two adjacent directions, giving smooth diagonal movement between the eight compass points.

**2. Warp Factor (0–8)**

Warp factor controls how far the Enterprise travels. A warp factor of `1` moves the ship by one quadrant-width in the chosen direction. A warp factor of `8` sends her screaming across eight quadrant-widths in one jump. Higher warp factors consume more energy.

Once both values are entered, the helm officer Sulu confirms the order and the ship moves:

```
Sulu: "Heading 3, Warp Factor 2. Aye, Sir!"
Distance travelled: 12,288 parsecs.
```

The game then prints your new position and a local sector map automatically.

---

## 🌌 How the Galaxy is Built

### The Coordinate System

The entire galaxy is represented as a flat 2D space measured in **parsecs**. Rather than storing objects on a simple grid, every object has a precise parsec coordinate `(parsecX, parsecY)`. The galaxy's total dimensions are:

```
Width:  12 × 64 × 8 = 6,144 parsecs
Height:  4 × 64 × 8 = 2,048 parsecs
```

This continuous coordinate space is then divided into a hierarchy of two named regions:

**Quadrants** — the galaxy is divided into an 8×8 grid of 64 quadrants. Each quadrant is named after a real star (Antares, Sirius, Rigel, etc.) combined with a Roman numeral (I–IV), giving names like *"Deneb III"* or *"Vega I"*. These names are for flavour; navigation works purely on numbers.

**Sectors** — each quadrant is further divided into an 8×8 grid of 64 sectors, giving 4,096 sectors in total across the galaxy. Sectors are the smallest named unit of space and are used by the short-range scan.

Conversion between the two representations (`parsecX/Y` ↔ `quadrantColumn/Row + sectorColumn/Row`) is handled automatically inside `GalacticPosition` whenever a position is set.

### The Galaxy HashMap

All game objects — stars, Klingons, starbases, and the Enterprise — live in a single `HashMap<String, GameObject>` called `galaxy`. The key for each entry is a simple string formed from the object's parsec coordinates:

```
"parsecX:parsecY"   →   GameObject
```

This means collision detection and object lookup are O(1) hash lookups. When the ship moves, its old key is removed from the map and a new key is inserted at the destination. Two objects cannot share the same parsec coordinate, which is enforced when placing objects during world generation.

### World Generation

At the start of each game, four placement passes populate the galaxy:

| Pass | What gets placed | Count |
|------|-----------------|-------|
| Stars | Scattered randomly across the full galactic width and height | ~20,000 (64×64 iterations, 5 attempts each) |
| Klingons | Placed at random unoccupied parsec positions | 8 |
| Starbases | Placed at random unoccupied parsec positions | 3 |
| Enterprise | Placed at a random unoccupied parsec position | 1 |

The same random seed (`7276431`) is used by default each game, so the galaxy layout is reproducible. Choosing "new setup" at the play-again prompt re-seeds the generator with a random value for a fresh galaxy.

### Scanning

The two scan commands work by querying the galaxy HashMap:

**Short Range Scan (`srs`)** iterates over all 64 sectors in the current quadrant and counts objects (Klingons `+K+`, starbases `>!<`, stars `*`, Enterprise `<*>`) in each sector cell, producing an 8×8 tactical grid.

**Long Range Scan (`lrs`)** looks at the 3×3 block of quadrants centred on your current position and counts objects in each, producing a strategic overview showing how many Klingons (`K`), starbases (`B`), and stars (`S`) are in neighbouring territories. Quadrants outside the galaxy boundary are shown as `#`.

---

## 🏗️ Code Structure

| File | Purpose |
|------|---------|
| `Run.java` | Game loop, command parsing, world generation, I/O |
| `GameObject.java` | Abstract base class for all entities; owns position and event listeners |
| `GalacticPosition.java` | Coordinate system; parsec ↔ quadrant/sector conversion |
| `Enterprise.java` | The player's ship; fires `ENTERPRISE_MOVED` events on navigation |
| `Klingon.java` | Enemy ship entity |
| `StarBase.java` | Federation starbase entity |
| `Star.java` | Star entity |
| `LongRangeScan.java` | Logic for the 3×3 quadrant strategic scan |
| `ShortRangeScan.java` | Logic for the 8×8 sector tactical scan |
| `GameEvent.java` | Event object passed to listeners |
| `GameListener.java` | Interface for receiving game events |
| `Vector.java` | 2D/3D vector maths (distance, normalisation, heading) |
| `TextBox.java` | Text layout primitive with alignment and insets |
| `TextBoxGrid.java` | Grid-based text formatter for scan output |

---

## ▶️ Running the Game

Compile all `.java` files in the `superstartrek` package and run the `Run` class:

```bash
javac superstartrek/*.java
java superstartrek.Run
```

No external dependencies. Requires Java 8 or later.
