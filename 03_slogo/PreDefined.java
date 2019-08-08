package interpreter.core.include.prototypes;

import interpreter.core.elements.Element;
import interpreter.core.elements.Function;
import interpreter.core.elements.Value;
import interpreter.exceptions.InvalidSyntaxError;
import interpreter.util.Context;
import interpreter.util.Scope;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class PreDefined<Input> extends Function {
    private Value.Type type;
    private ArrayList<Element> arguments;

    protected PreDefined(Value.Type type, String name, Context context) throws InvalidSyntaxError {
        this(type, name, context, 2);
    }

    protected PreDefined(Value.Type type, String name, Context context, int numVar) throws InvalidSyntaxError {
        this(type, name, context, 2, true);
    }

    protected PreDefined(Value.Type type, String name, Context context, int numVar, boolean multiple) throws InvalidSyntaxError {
        super(name, context);
        this.type = type;
        arguments = Function.readArguments(numVar, context, multiple);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Value resolve(Scope scope) {
        ArrayList<Value> inputs = new ArrayList<>();
        for (Element element : arguments) {
            Value value = element.resolve(scope);
            if (Value.typeOf(value) != type && Value.typeOf(value) != Value.Type.LIST)
                return null;
            inputs.add(value);
        }

        return evaluate(inputs);
    }

    protected Value evaluate(ArrayList<Value> inputs) {
        if (inputs.size() == 0)
            return null;
        if (inputs.size() == 1)
            return apply(inputs.get(0));

        Iterator<Value> iterator = inputs.iterator();
        Value result = apply(iterator.next(), iterator.next());
        while (iterator.hasNext())
            result = apply(result, iterator.next());

        return result;
    }

    protected Input apply(Input in) {
        return in;
    }

    protected Value apply(Value in) {
        if (in == null)
            return null;

        if (Value.typeOf(in) == Value.Type.LIST) {
            ArrayList<Value> list = new ArrayList<>();
            for (Object object : (ArrayList) in.getValue()) {
                Value value = (Value) object;
                if (Value.typeOf(value) != type)
                    return null;
                list.add(new Value<>(apply((Input) value.getValue())));
            }
            return new Value<>(list);
        }
        return new Value<>(apply((Input) in.getValue()));
    }

    protected Input apply(Input in1, Input in2) {
        return in1;
    }

    protected Value apply(Value in1, ArrayList<Value> in2) {
        ArrayList<Value> list = new ArrayList<>();
        for (Value value : in2)
            list.add(new Value<>(apply((Input) in1.getValue(), (Input) value.getValue())));
        return new Value<>(list);
    }

    protected Value apply(ArrayList<Value> in1, Value in2) {
        ArrayList<Value> list = new ArrayList<>();
        for (Value value : in1)
            list.add(new Value<>(apply((Input) value.getValue(), (Input) in2.getValue())));
        return new Value<>(list);
    }

    protected Value apply(ArrayList<Value> in1, ArrayList<Value> in2) {
        if (in1.size() != in2.size())
            return null;
        ArrayList<Value> list = new ArrayList<>();

        for (int i = 0; i < in1.size(); ++i)
            list.add(new Value<>(apply((Input) in1.get(i).getValue(), (Input) in2.get(i).getValue())));
        return new Value<>(list);
    }

    protected Value apply(Value in1, Value in2) {
        boolean list1 = Value.typeOf(in1) == Value.Type.LIST;
        boolean list2 = Value.typeOf(in2) == Value.Type.LIST;


        if (!list1 & !list2)
            return new Value<>(apply((Input) in1.getValue(), (Input) in2.getValue()));

        if (list1 & !list2)
            return apply((ArrayList<Value>) in1.getValue(), in2);

        if (!list1 & list2)
            return apply(in1, (ArrayList<Value>) in2.getValue());

        if (list1 & list2)
            return apply((ArrayList<Value>) in1.getValue(), (ArrayList<Value>) in2.getValue());

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("( ");
        sb.append(getName()).append(" ");
        for (Element element : arguments)
            sb.append(element).append(" ");
        sb.subSequence(0, sb.length() - 2);
        sb.append(")");
        return sb.toString();
    }
}
