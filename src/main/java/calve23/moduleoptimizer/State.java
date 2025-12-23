package calve23.moduleoptimizer;

public enum State {
    READY, //ready to load, score and create region
    PAUSED,
    READY_TO_CAPTURE,
    READY_TO_SAVE,
    CREATING_REGION,//can be in processing state
    SCORING, //can be in processing state
    CAPTURING,//can be in processing state
    MODULE_REGION_SETUP
}
