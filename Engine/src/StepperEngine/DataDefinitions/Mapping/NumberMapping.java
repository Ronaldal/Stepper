package StepperEngine.DataDefinitions.Mapping;

public class NumberMapping extends Mapping<Integer,Integer>{
    public NumberMapping(Integer car, Integer cdr) {
        super(car, cdr);
    }

    @Override
    public String toString() {
        return "car: "+  data.getKey().toString()+"\ncdr: "+data.getValue().toString();

    }
}
