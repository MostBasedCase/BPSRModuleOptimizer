package calve23.moduleoptimizer;

public enum State {
    READY, //ready to load, score and create region
    READY_TO_CAPTURE,
    READY_TO_SAVE,
    CREATING_REGION,
    SCORING,
    CAPTURING,
}
