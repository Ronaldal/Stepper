package StepperEngine.DataDefinitions.Relation;

public interface RelationInterface {
    public String relationToCSV();
    public Integer numOfRows();
    public String createPropertiesExporter(Integer totalProperties);
    public boolean isEmpty();

}
