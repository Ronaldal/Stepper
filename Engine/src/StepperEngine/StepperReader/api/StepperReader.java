package StepperEngine.StepperReader.api;

import StepperEngine.StepperReader.XMLReadClasses.TheStepper;
import StepperEngine.StepperReader.Exception.ReaderException;

import java.io.InputStream;

import java.io.File;
import java.io.InputStream;

public interface StepperReader {
    TheStepper read(String filePath)throws ReaderException;

    TheStepper read2(InputStream inputStream, String filePath) throws ReaderException;
}
