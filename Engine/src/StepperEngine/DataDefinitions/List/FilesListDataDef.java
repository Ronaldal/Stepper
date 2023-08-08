package StepperEngine.DataDefinitions.List;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilesListDataDef extends DataDefList<File> {
    public FilesListDataDef(List<File> list) {
        this.list = list;
    }

    public List<File> getFilesList() {
        return list;
    }

    public List<String> getPaths() {
       List<String> strings=new ArrayList<>();
       for (File file:list)
       {
           strings.add(file.getAbsolutePath());
       }
       return strings;
    }

    @Override
    public String toString() {
        return IntStream.iterate(0, i -> i + 1)
                .limit(list.size())
                .mapToObj(i -> (i + 1) + "." + list.get(i).getAbsolutePath())
                .collect(Collectors.joining("\n"));
    }
}
