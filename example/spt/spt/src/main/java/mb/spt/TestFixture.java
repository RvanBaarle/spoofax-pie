package mb.spt;

/**
 * Implementation of {@link ITestFixture}.
 */
public class TestFixture implements ITestFixture {

    private final ITestFragment fragment;

    protected TestFixture(ITestFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public ITestFragment getFragment() {
        return this.fragment;
    }

}
