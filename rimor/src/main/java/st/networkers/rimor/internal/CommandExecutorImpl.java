package st.networkers.rimor.internal;

import st.networkers.rimor.context.ExecutionContext;
import st.networkers.rimor.internal.inject.Injector;
import st.networkers.rimor.internal.instruction.CommandInstruction;

public class CommandExecutorImpl implements CommandExecutor {

    private final Injector injector;

    public CommandExecutorImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object execute(CommandInstruction instruction, ExecutionContext context) {
        return injector.invokeMethod(instruction.getMethod(), instruction.getCommandInstance(), context);
    }
}