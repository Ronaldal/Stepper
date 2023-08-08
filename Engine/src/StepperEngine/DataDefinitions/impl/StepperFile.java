package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

import java.io.File;

public class StepperFile extends DataDefinitionAbstract {

    StepperFile(){
        super("File", false, File.class);
    }

}
