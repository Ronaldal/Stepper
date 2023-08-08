package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.List.FilesListDataDef;
import StepperEngine.DataDefinitions.Relation.RelationOfStringRows;
import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilesContentExtractor extends StepDefinitionAbstract {
    public FilesContentExtractor() {
        super("Files Content Extractor", true);
        this.addInput(new DataDefinitionDeclarationImpl("FILES_LIST","Files to extract", DataNecessity.MANDATORY, DataDefinitionRegistry.FILES_LIST));
        this.addInput(new DataDefinitionDeclarationImpl("LINE","Line number to extract", DataNecessity.MANDATORY, DataDefinitionRegistry.NUMBER));
        this.addOutput(new DataDefinitionDeclarationImpl("DATA","Data extraction",DataNecessity.NA,DataDefinitionRegistry.RELATION_STRING));
    }

    /***
     *Given a list of text files and a relevant line number, returns the content of the relevant line.
     * @param context-interface that saves all system data
     * @param nameToAlias-Map of the name of the information definition to the name of the information in the current flow
     * @param stepName- The step name in the flow
     */
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        Instant start = Instant.now();
        FilesListDataDef filesListDataDef=context.getDataValue(nameToAlias.get("FILES_LIST"),FilesListDataDef.class);
        List<File> files=filesListDataDef.getFilesList();
        Integer lineNumber=context.getDataValue(nameToAlias.get("LINE"), Integer.class);
        String line;
        Integer rowNumber=0;
        RelationOfStringRows result = createRelationOfStringRows();

        for (File file : files) {
            context.addLog(stepName,"About to start work on file "+file.getName());
            List<String> row =new ArrayList<>();
            rowNumber++;
            boolean isRowFound = false;
            row.add(rowNumber.toString());
            row.add(file.getName());
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                int currentLine = 1;
                while ((line = br.readLine()) != null) {
                    if (currentLine == lineNumber) {
                        row.add(line);
                        isRowFound=true;
                        break;
                    }
                    currentLine++;
                }
                if (!isRowFound){row.add("Not such line");}
            } catch (FileNotFoundException e) {
                row.add("File not found");
                context.addLog(stepName,"Problem extracting line number "+lineNumber.toString()+" from file "+file.getName());
            } catch (IOException e) {
                context.addLog(stepName,"Problem extracting line number "+lineNumber.toString()+" from file "+file.getName());
            }
            result.addRow(row);
        }
        context.storeValue(nameToAlias.get("DATA"),result);
        if(files.isEmpty()){
            context.setInvokeSummery(stepName,"There are no files to extract");
            context.setStepStatus(stepName,StepStatus.SUCCESS);
        }
        if(result.isEmpty()){
            context.setStepStatus(stepName,StepStatus.WARNING);
        }else {
            context.setInvokeSummery(stepName, "The row extracted from each file successfully");
            context.setStepStatus(stepName,StepStatus.SUCCESS);
        }
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return context.getStepStatus(stepName);
    }

    private static RelationOfStringRows createRelationOfStringRows() {
        List<String> colNames=new ArrayList<>();
        colNames.add("Serial Number");
        colNames.add("Original File Name");
        colNames.add("The textual information retrieved from the file");
        RelationOfStringRows result=new RelationOfStringRows(colNames);
        return result;
    }
}
