Darvin simulation diagram
```mermaid
---
config:
  layout: elk
  theme: redux-dark
  look: neo
---
classDiagram
    class IEntity {
        <<interface>>
        +getPosition() Vector2d
        +setPosition(Vector2d)
        +copy() Entity
    }
    class IAlive {
        <<interface>>
        +getEnergy() int
        +setEnergy(int)
        +addEnergy(int)
        +isAlive() boolean
        +kill() void
    }
    class IMove {
        <<interface>>
        +move(LayerMap)
    }
    class IReproduce {
        <<interface>>
        +reproduce(Creature) Creature
    }
    class IMapChangeListener {
        <<interface>>
        +mapChanged(WorldMap, String)
    }
    class ISimulationManager {
        <<interface>>
        +step(WorldMap, SimulationConfig)
    }
    class MapDirection {
        <<enumeration>>
        NORTH
        NORTHEAST
        EAST
        SOUTHEAST
        SOUTH
        SOUTHWEST
        WEST
        NORTHWEST
        +getUnitVector() Vector2d
        +rotate(MoveDirection) MapDirection
    }
    class MoveDirection {
        <<enumeration>>
        FRONT
        FRONT_RIGHT
        RIGHT
        BACK_RIGHT
        BACK
        BACK_LEFT
        LEFT
        FRONT_LEFT
        +getValue() int
    }
    class Entity {
        <<abstract>>
        #Vector2d position
        #UUID id
        +Entity(Vector2d)
        +getPosition() Vector2d
        +setPosition(Vector2d)
        +copy() Entity
    }
    class Creature {
        <<abstract>>
        #Genotype genotype
        #int energy
        #boolean isAlive
        #MapDirection direction
        #SimulationConfig simulationConfig
        +move(LayerMap)
        +reproduce(Creature)
        +isAlive() boolean
        +kill() void
    }
    class Animal {
        -int age
        -int childrenAmount
        -List~Animal~ children
        +makeOlder()
        +addChild(Animal)
        +getAge() int
        +getChildrenAmount() int
    }
    class Parasite {
        -Animal host
        -boolean panicking
        -int daysWithHost
        +setHost(Animal)
        +getHost() Animal
        +relink(Map~UUID, Animal~)
    }
    class Plant {
    }
    class Genotype {
        -List~MoveDirection~ genes
        -int currentGeneIndex
        -int[] genesCount
        -int size
        +nextGene() MoveDirection
        +cross(Genotype, int, int, SimulationConfig) Genotype
    }
    class WorldMap {
        -LayerMap~Animal~ animalMap
        -LayerMap~Plant~ plantMap
        -LayerMap~Parasite~ parasiteMap
        -List~Animal~ deadAnimals
        -Boundary boundary
        +copy() WorldMap
        +restore(WorldMap)
        +getAnimals() LayerMap~Animal~
        +getPlants() LayerMap~Plant~
        +getParasites() LayerMap~Parasite~
    }
    class LayerMap~T~ {
        -Map~Vector2d, List~T~~ entitiesByPosition
        -Boundary boundary
        +addEntity(T)
        +removeEntity(T)
        +getEntities() Collection~T~
        +isOccupied(Vector2d) boolean
        +move(Creature)
    }
    class Boundary {
        -Vector2d lowerLeft
        -Vector2d upperRight
        +lowerLeft() Vector2d
        +upperRight() Vector2d
    }
    class Vector2d {
        -int x
        -int y
        +add(Vector2d) Vector2d
        +subtract(Vector2d) Vector2d
        +follows(Vector2d) boolean
        +precedes(Vector2d) boolean
    }
    class SimulationConfig {
        <<record>>
        +Boundary mapSize
        +Boundary jungleSize
        +int plantPerDay
        +int energyPerPlant
        +int startingPlants
        +int startingAnimals
        +int startingParasites
        +int startingEnergy
        +int genotypeLength
        +int energyLossDueParasite
        +int energyLossInPanic
        +int dailyEnergyLoss
        +int reproductionMinEnergy
        +int reproductionCost
        +int mutationMin
        +int mutationMax
        +int startingParasiteEnergy
    }
    class SimulationStatistics {
        <<record>>
        +int animalCount
        +int plantCount
        +int parasiteCount
        +int freeFieldsCount
        +List~Genotype~ mostPopularGenotypes
        +double averageEnergy
        +double averageLifeSpan
        +double averageChildren
        +int attachedParasiteCount
        +int panickingParasiteCount
    }
    class StatisticsLogger {
        -String fileName
        -int rowNumber
        +log(SimulationStatistics)
    }
    class StatisticsEngine {
        -WorldMap worldMap
        +update()
        +calculate() SimulationStatistics
        +getFieldsWithMostPlantGrowth() Set~Vector2d~
    }
    class Simulation {
        -SimulationConfig simulationConfig
        -WorldMap worldMap
        -List~ISimulationManager~ managers
        -List~IMapChangeListener~ observers
        -Stack~WorldMap~ history
        +step()
        +undo()
        +runSimulation()
        +addObserver(IMapChangeListener)
    }
    class MoveAnimalsManager
    class MoveParasitesManager
    class FeedAnimalsManager
    class KillAnimalsManager
    class KillParasitesManager
    class ReproduceAnimalsManager
    class ReproduceParasitesManager
    class SetHostsManager
    class AddPlantsManager
    class Main {
        +main(String[])
    }
    class WorldGUI {
        +main(String[])
    }
    class SimulationApp {
        +start(Stage)
    }
    class ConfigurationPresenter {
        -Spinner~Integer~ widthField
        -Spinner~Integer~ heightField
        -Spinner~Integer~ animalCount
        -Spinner~Integer~ parasiteCount
        -CheckBox parasiteToggle
        -Label errorLabel
        -TextField newPresetName
        -ComboBox~String~ presetComboBox
        -PresetManager presetManager
        +initialize()
        +onSimulationStartClicked()
        +onSavePresetClicked()
        +onPresetSelected()
        -spawnSimulationWindow(SimulationConfig)
        -refreshPresets()
    }

    class SimulationWindowPresenter {
        -WorldMap worldMap
        -Simulation simulation
        -StatisticsEngine statisticsEngine
        -StatisticsLogger statisticsLogger
        -MapRenderer mapRenderer
        -StatisticsChartPresenter chartPresenter
        -Canvas mapCanvas
        -Label dayLabel
        -Label infoLabel
        -Spinner~Integer~ speedSpinner
        +initialize()
        +setSimulationConfig(SimulationConfig)
        +mapChanged(WorldMap, String)
        +onStopClicked()
        +onResumeClicked()
        +onNextDayClicked()
        +onUndoClicked()
    }
    
    class StatisticsChartPresenter {
        -ComboBox~String~ statsComboBox
        -LineChart~Number, Number~ statsChart
        -XYChart.Series~Number, Number~ currentSeries
        -int currentDay
        +initialize()
        +update(SimulationStatistics)
        +decrementDay()
    }

    class MapRenderer {
        -Canvas mapCanvas
        +drawMap(WorldMap, Set~Vector2d~, String)
        -drawJungle(GraphicsContext, ...)
        -drawEntities(GraphicsContext, ...)
        -drawCreatures(GraphicsContext, ...)
    }

    class PresetManager {
        -String PRESET_FILE
        +savePreset(String, String)
        +loadPresetNames() List~String~
        +loadPresetData(String) String[]
    }
    Entity ..|> IEntity
    Creature ..|> IAlive
    Creature ..|> IMove
    Creature ..|> IReproduce
    SimulationWindowPresenter ..|> IMapChangeListener
    Entity <|-- Creature
    Entity <|-- Plant
    Creature <|-- Animal
    Creature <|-- Parasite
    WorldMap *-- LayerMap
    WorldMap *-- Boundary
    LayerMap o-- Entity
    Creature *-- Genotype
    Creature --> MapDirection
    Creature --> SimulationConfig
    Parasite o-- Animal

    Simulation *-- WorldMap
    Simulation *-- SimulationConfig
    Simulation o-- ISimulationManager
    Simulation o-- IMapChangeListener
    Main --> Simulation : creates & runs
    WorldGUI --> SimulationApp : launches
    SimulationApp ..> ConfigurationPresenter : loads via FXMLLoader
    ConfigurationPresenter --> PresetManager : uses
    ConfigurationPresenter ..> SimulationWindowPresenter : creates
    ConfigurationPresenter ..> SimulationConfig : creates

    SimulationWindowPresenter --> Simulation
    SimulationWindowPresenter --> WorldMap
    SimulationWindowPresenter --> StatisticsEngine
    SimulationWindowPresenter --> StatisticsLogger
    SimulationWindowPresenter --> MapRenderer
    SimulationWindowPresenter --> StatisticsChartPresenter
    SimulationWindowPresenter ..> SimulationStatistics : displays
    
    MapRenderer ..> WorldMap : renders
    StatisticsChartPresenter ..> SimulationStatistics : charts

    StatisticsEngine --> WorldMap
    StatisticsLogger ..> SimulationStatistics
    ISimulationManager <|.. MoveAnimalsManager
    ISimulationManager <|.. MoveParasitesManager
    ISimulationManager <|.. FeedAnimalsManager
    ISimulationManager <|.. KillAnimalsManager
    ISimulationManager <|.. KillParasitesManager
    ISimulationManager <|.. ReproduceAnimalsManager
    ISimulationManager <|.. ReproduceParasitesManager
    ISimulationManager <|.. SetHostsManager
    ISimulationManager <|.. AddPlantsManager
    ```
