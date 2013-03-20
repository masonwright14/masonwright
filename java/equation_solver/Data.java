package edu.vanderbilt.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Data {

    private final List<Condition> allConditions;
    
    public Data() {
        this.allConditions = new ArrayList<Condition>();
        initializeConditions();
    }
    
    public void print() {
        for (int i = 0; i < this.allConditions.size(); i++) {
            System.out.println((i + 1) + " " + this.allConditions.get(i));
        }
    }
    
    public void printByValue() {
        printWithValue("null", getConditionsByValue(null));
        for (Value value: Value.values()) {
            printWithValue(value.toString(), getConditionsByValue(value));
        }
    }
    
    public void setValues(final Map<ConditionTest, Value> map) {
        for (Entry<ConditionTest, Value> entry: map.entrySet()) {
            setValueForPassingConditions(entry.getKey(), entry.getValue());
        }
    }
    
    public void setValueForPassingConditions(
        final ConditionTest test, 
        final Value value
    ) {
        for (Condition condition: getPassingConditions(test)) {
            condition.setValue(value);
        }
    }
    
    private void printWithValue(
        final String value, 
        final List<Condition> conditions
    ) {
        System.out.println();
        System.out.println(value + ": " + conditions.size());
        for (Condition condition: conditions) {
            System.out.println(condition);
        }
    }
    
    public List<Condition> getConditionsByValue(final Value value) {
        List<Condition> result = new ArrayList<Condition>();
        for (Condition condition: this.allConditions) {
            if (condition.getValue() == value) {
                result.add(condition);
            }
        }
        
        return result;
    }
    
    public List<Condition> getPassingConditions(final ConditionTest aTest) {
        List<Condition> result = new ArrayList<Condition>();
        for (Condition condition: this.allConditions) {
            if (aTest.test(condition)) {
                result.add(condition);
            }
        }
        
        return result;
    }
    
    private void initializeConditions() {
        for (Term value1: Term.values()) {
            for (Term value2: Term.values()) {
                for (Term value3: Term.values()) {
                    for (Term value4: Term.values()) {
                        List<Term> list = new ArrayList<Term>();
                        list.add(value1);
                        list.add(value2);
                        list.add(value3);
                        list.add(value4);
                        allConditions.add(new Condition(list));
                    }
                }
            }
        }
    }
}
