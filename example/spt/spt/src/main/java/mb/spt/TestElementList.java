package mb.spt;

import java.util.AbstractList;
import java.util.ArrayList;

public class TestElementList extends AbstractList<ITestSuiteElement> {

    private final ArrayList<ITestSuiteElement> elements = new ArrayList<>();

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public ITestSuiteElement get(int index) {
        return this.elements.get(index);
    }

    @Override
    public ITestSuiteElement set(int index, ITestSuiteElement element) {
        final ITestSuiteElement oldElement = remove(index);
        add(index, element);
        return oldElement;
    }

    @Override
    public void add(int index, ITestSuiteElement element) {
        super.add(index, element);
    }

    @Override
    public ITestSuiteElement remove(int index) {
        return super.remove(index);
    }
}
