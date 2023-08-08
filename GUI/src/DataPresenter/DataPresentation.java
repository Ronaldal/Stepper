package DataPresenter;


import DTO.FlowExecutionData.IOData;
import javafx.scene.Node;

import java.util.Set;

public interface DataPresentation {
    Node getDataPresent(Set<IOData> data);
}
