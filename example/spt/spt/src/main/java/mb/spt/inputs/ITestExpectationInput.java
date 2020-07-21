package mb.spt.inputs;

import mb.common.result.Result;
import mb.pie.api.ExecException;
import mb.spt.ITestFragment;

public interface ITestExpectationInput<T> {

    Result<T, ? extends Exception> get(ITestFragment fragment) throws ExecException, InterruptedException;

}
