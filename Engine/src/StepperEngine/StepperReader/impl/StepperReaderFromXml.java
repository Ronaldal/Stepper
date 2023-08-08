package StepperEngine.StepperReader.impl;

import StepperEngine.StepperReader.XMLReadClasses.TheStepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperReader.api.StepperReader;
import generated.STStepper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class StepperReaderFromXml implements StepperReader {

    TheStepper theStepper;
    @Override
    public TheStepper read(String filePath) throws ReaderException {
        try {
            InputStream inputStream = Files.newInputStream(new File(filePath).toPath());
            return read(inputStream);
        } catch (IOException e) {
            throw new ReaderException("Cannot read file", filePath);
        }
    }

    public TheStepper read(InputStream inputStream) throws ReaderException{
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("generated");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            STStepper stStepper = (STStepper) unmarshaller.unmarshal(inputStream);
            return new TheStepper(stStepper);
        }catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public TheStepper read2(InputStream inputStream,String filePath) throws ReaderException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("generated");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            STStepper stStepper = (STStepper) unmarshaller.unmarshal(inputStream);
            return new TheStepper(stStepper);
        } catch ( JAXBException e) {
            System.out.println(e.getMessage());
            throw new ReaderException("Cannot read file",filePath);
        }
    }
}
