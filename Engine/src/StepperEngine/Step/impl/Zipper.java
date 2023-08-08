package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;

import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.DataDefinitions.impl.StepperZipperEnumeration;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper extends StepDefinitionAbstract {

    public Zipper(){
        super("Zipper",false);
        addInput(new DataDefinitionDeclarationImpl("SOURCE","Source", DataNecessity.MANDATORY, DataDefinitionRegistry.FILE_PATH));
        addInput(new DataDefinitionDeclarationImpl("OPERATION","Operation type", DataNecessity.MANDATORY, DataDefinitionRegistry.ZIPPER_ENUMERATION));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", "Zip operation result", DataNecessity.NA, DataDefinitionRegistry.STRING));
    }

    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        Instant start = Instant.now();
        String invokeSummery;
        String source = context.getDataValue(nameToAlias.get("SOURCE"), String.class);
        ZipEnumerator operation = context.getDataValue(nameToAlias.get("OPERATION"), ZipEnumerator.class);
        context.addLog(stepName,"About to perform operation "+ operation+ "on source "+source);
        if (operation.equals(ZipEnumerator.ZIP)) {
            invokeSummery=invokeZip(context, source, nameToAlias, stepName);
        } else {
            invokeSummery=invokeUnzip(context, source, nameToAlias, stepName);
        }
        context.setInvokeSummery(stepName, invokeSummery);
        context.storeValue(nameToAlias.get("RESULT"),invokeSummery);
        if(context.getStepStatus(stepName).equals(StepStatus.FAIL)){
            context.addLog(stepName,"There is a problem to perform operation "+ operation+ "on source "+source);
        }
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return context.getStepStatus(stepName);
    }
    private String invokeZip(StepExecutionContext context, String source, Map<String, String> nameToAlias, String stepName)  {
        try {
            File file = new File(source);
            String zipPath;
            if (file.isDirectory()) {
                zipPath = source + ".zip";
            }else {
                String parentPath = file.getParent();
                String fileName = file.getName();
                zipPath = parentPath + File.separator + fileName + ".zip";
            }
            // Create a ZipOutputStream to write the compressed data
            FileOutputStream fos = new FileOutputStream(zipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Add the folder or file to the zip
            if (file.isDirectory()) {
                addFolderToZip(file, file.getName(), zos);
            } else {
                addFileToZip(file, file.getName(), zos);
            }
            // Close the streams
            zos.close();
            fos.close();

            context.setStepStatus(stepName,StepStatus.SUCCESS);
        } catch (FileNotFoundException e) {
            context.setStepStatus(stepName,StepStatus.FAIL);
            return "There are no file/directory in: "+source;
        } catch (IOException e) {
            context.setStepStatus(stepName,StepStatus.FAIL);
            return "Some file in: "+source +" failed to zip";
        }
        return "The file/directory zipped successfully!";
    }


    private static void addFileToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

    private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + File.separator + file.getName(), zos);
            } else {
                addFileToZip(file, parentFolder + File.separator + file.getName(), zos);
            }
        }
    }

    private String  invokeUnzip(StepExecutionContext context, String source, Map<String, String> nameToAlias, String stepName) {
        String destinationFolderPath = source.replace(".zip", "");
        try {
            File destDir = new File(destinationFolderPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            FileInputStream fis = new FileInputStream(source);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File newFile = new File(destinationFolderPath + File.separator + entryName);

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    extractFile(zis, newFile);
                }

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }

            zis.close();
            fis.close();
            context.setStepStatus(stepName,StepStatus.SUCCESS);
        } catch (FileNotFoundException e) {
            context.setStepStatus(stepName,StepStatus.FAIL);
            return "There are no zip in: "+source;
        } catch (IOException e) {
            context.setStepStatus(stepName,StepStatus.FAIL);
            return "Some file in: "+source +" failed to unzip";
        }
        return "The directory unzipped successfully!";
    }

    private static void extractFile(ZipInputStream zis, File outputFile) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        FileOutputStream fos = new FileOutputStream(outputFile);

        while ((length = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }

        fos.close();
    }


}
