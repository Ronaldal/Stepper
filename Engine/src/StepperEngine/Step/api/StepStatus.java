package StepperEngine.Step.api;

import java.io.Serializable;

/***
 * Enum for saving the status of the step after invoking him.
 */
public enum StepStatus implements Serializable {
    SUCCESS,
    FAIL,
    WARNING,
    NOT_INVOKED
}
