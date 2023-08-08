package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.List.FilesListDataDef;
import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CollectFilesInFolder extends StepDefinitionAbstract {


    public CollectFilesInFolder() {
        super("Collect Files In Folder", true);
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", "Folder name to scan", DataNecessity.MANDATORY, DataDefinitionRegistry.FOLDER_PATH));
        addInput(new DataDefinitionDeclarationImpl("FILTER", "Filter only this files", DataNecessity.OPTIONAL, DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", "Files list", DataNecessity.NA, DataDefinitionRegistry.FILES_LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", "Total files found", DataNecessity.NA, DataDefinitionRegistry.NUMBER));
    }

    /**
     * Given a path to the directory, it will go through and scan all the files that are in it and return a list of values of type File.
     * @param context-interface that saves all system data
     * @param nameToAlias-Map of the name of the information definition to the name of the information in the current flow
     * @param stepName- The step name in the flow
     */
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        Instant start = Instant.now();
        String folderPath = context.getDataValue(nameToAlias.get("FOLDER_NAME"), String.class);
        Optional<String> filterStr = Optional.ofNullable(context.getDataValue(nameToAlias.get("FILTER"), String.class));
        StepStatus stepStatus = StepStatus.SUCCESS;
        context.addLog(stepName,"Reading folder " + folderPath + "content with filter " + filterStr.orElse(""));
        Path path = Paths.get(folderPath);

        File folder = path.toFile();
        File[] files = filterStr.map(filter -> folder.listFiles((dir, name) -> name.endsWith(filter)))
                .orElse(folder.listFiles());
        List<File> fileList = new ArrayList<>(); // create an empty list to store the files

        if(files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file); // add the file to the list if it is a file
                }
            }
        }

        Integer size=fileList.size();

        context.storeValue(nameToAlias.get("FILES_LIST"),new FilesListDataDef(fileList));
        context.storeValue(nameToAlias.get("TOTAL_FOUND"),size);
        if(!Files.isDirectory(path)){
            context.addLog(stepName,"There are no folder in path "+folderPath);
            context.setInvokeSummery(stepName,"There are no folder in path "+folderPath);
            context.setStepStatus(stepName,StepStatus.FAIL);
            stepStatus = StepStatus.FAIL;
        }
        else if (fileList.size() == 0) {
            context.addLog(stepName,"No files in folder matching the filter.");
            context.setInvokeSummery(stepName,"The folder is empty.");
            context.setStepStatus(stepName,StepStatus.WARNING);
            stepStatus = StepStatus.WARNING;
        }
        else {
            context.addLog(stepName, "Found " + size + " files in folder matching the filter ");
            context.setInvokeSummery(stepName, "The files in " + folderPath + " collected successfully");
            context.setStepStatus(stepName, StepStatus.SUCCESS);
        }
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return stepStatus;
    }
}


