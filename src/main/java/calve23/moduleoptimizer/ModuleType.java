package calve23.moduleoptimizer;

public enum ModuleType {
    A("Premium Excellent Module"),
    B("Excellent Module"),
    C("Advanced Module"),
    D("Basic Module");

    private final String name;

    ModuleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int requiredRegions() {
        return switch (this) {
            case A, B -> 3;
            case C -> 2;
            case D -> 1;
        };
    }

    public ModuleType nextType() {
        return switch (this) {
            case A -> ModuleType.B;
            case B -> ModuleType.C;
            case C -> ModuleType.D;
            case D -> null;
        };
    }
}


