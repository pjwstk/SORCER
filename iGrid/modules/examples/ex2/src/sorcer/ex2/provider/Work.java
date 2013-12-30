package sorcer.ex2.provider;

import sorcer.service.Context;
import sorcer.service.ContextException;

import java.io.Serializable;

public interface Work extends Serializable {

    public Context exec(Context context) throws InvalidWork, ContextException;
}
