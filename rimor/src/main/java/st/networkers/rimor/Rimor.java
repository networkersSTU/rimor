package st.networkers.rimor;

import st.networkers.rimor.aop.Advice;
import st.networkers.rimor.aop.AdviceRegistry;
import st.networkers.rimor.aop.CommandExecutor;
import st.networkers.rimor.aop.ExceptionHandler;
import st.networkers.rimor.aop.exception.ExceptionHandlerRegistry;
import st.networkers.rimor.command.CommandRegistry;
import st.networkers.rimor.command.CommandResolver;
import st.networkers.rimor.command.RimorCommand;
import st.networkers.rimor.extension.ExtensionManager;
import st.networkers.rimor.extension.ExtensionManagerImpl;
import st.networkers.rimor.extension.RimorExtension;
import st.networkers.rimor.inject.RimorInjector;
import st.networkers.rimor.internal.execute.CommandExecutorImpl;
import st.networkers.rimor.internal.inject.RimorInjectorImpl;
import st.networkers.rimor.internal.provide.OptionalProvider;
import st.networkers.rimor.internal.resolve.PathResolverImpl;
import st.networkers.rimor.provide.ProviderRegistry;
import st.networkers.rimor.provide.RimorProvider;
import st.networkers.rimor.resolve.PathResolver;

import java.util.Objects;

public class Rimor {

    private final RimorInjector injector;

    private final CommandRegistry commandRegistry;
    private final ExceptionHandlerRegistry exceptionHandlerRegistry;
    private final AdviceRegistry adviceRegistry;
    private final ExtensionManager extensionManager;
    private final ProviderRegistry providerRegistry;

    private final CommandExecutor executor;
    private final CommandResolver commandResolver;
    private final PathResolver pathResolver;

    private boolean initialized = false;

    public Rimor() {
        this(new ProviderRegistry());
    }

    public Rimor(ProviderRegistry providerRegistry) {
        this(new RimorInjectorImpl(providerRegistry), new ExceptionHandlerRegistry(), new AdviceRegistry(advice, exceptionHandlers), providerRegistry);
    }

    public Rimor(RimorInjector injector,
                 ExceptionHandlerRegistry exceptionHandlerRegistry,
                 AdviceRegistry adviceRegistry,
                 ProviderRegistry providerRegistry) {
        this(injector, new CommandRegistry(), exceptionHandlerRegistry, adviceRegistry, new ExtensionManagerImpl(),
                new ProviderRegistry(), new CommandExecutorImpl(injector, exceptionHandlerRegistry, adviceRegistry),
                new CommandResolver(), new PathResolverImpl());
    }

    public Rimor(RimorInjector injector, CommandRegistry commandRegistry, ExceptionHandlerRegistry exceptionHandlerRegistry,
                 AdviceRegistry adviceRegistry, ExtensionManager extensionManager, ProviderRegistry providerRegistry,
                 CommandExecutor executor, CommandResolver commandResolver, PathResolver pathResolver) {
        this.injector = injector;
        this.commandRegistry = commandRegistry;
        this.exceptionHandlerRegistry = exceptionHandlerRegistry;
        this.adviceRegistry = adviceRegistry;
        this.extensionManager = extensionManager;
        this.providerRegistry = providerRegistry;
        this.executor = executor;
        this.commandResolver = commandResolver;
        this.pathResolver = pathResolver;

        this.registerProvider(new OptionalProvider(injector));
    }

    /**
     * Registers the given {@link RimorCommand}.
     *
     * @param command the command to register
     */
    public Rimor registerCommand(RimorCommand command) {
        Objects.requireNonNull(command);
        commandRegistry.registerCommand(commandResolver.resolve(command));
        return this;
    }

    /**
     * Registers the given {@link RimorCommand}s.
     *
     * @param commands the commands to register
     */
    public Rimor registerCommands(RimorCommand... commands) {
        Objects.requireNonNull(commands);
        for (RimorCommand command : commands)
            this.registerCommand(command);
        return this;
    }

    /**
     * Registers the given {@link ExceptionHandler}.
     *
     * @param handler the exception handler to register
     */
    public Rimor registerExceptionHandler(ExceptionHandler<?> handler) {
        Objects.requireNonNull(handler);
        this.exceptionHandlerRegistry.registerExceptionHandler(handler);
        return this;
    }

    /**
     * Registers the given {@link ExceptionHandler}s.
     *
     * @param handlers the exception handlers to register
     */
    public Rimor registerExceptionHandlers(ExceptionHandler<?>... handlers) {
        Objects.requireNonNull(handlers);
        for (ExceptionHandler<?> handler : handlers)
            this.registerExceptionHandler(handler);
        return this;
    }

    /**
     * Registers the given {@link RimorProvider}.
     *
     * @param provider the provider to register into the injector
     */
    public Rimor registerProvider(RimorProvider<?> provider) {
        this.providerRegistry.register(provider);
        return this;
    }

    /**
     * Registers the given {@link RimorProvider}s.
     *
     * @param providers the providers to register into the injector
     */
    public Rimor registerProviders(RimorProvider<?>... providers) {
        for (RimorProvider<?> provider : providers)
            this.registerProvider(provider);
        return this;
    }

    /**
     * Registers the given {@link Advice}.
     *
     * @param advice the execution task to register
     */
    public Rimor registerExecutionTask(Advice advice) {
        if (advice instanceof PreAdvice)
            return this.registerPreExecutionTask((PreAdvice) advice);
        else if (advice instanceof PostAdvice)
            return this.registerPostExecutionTask((PostAdvice) advice);
        throw new IllegalArgumentException(advice + " is neither a PreExecutionTask nor a PostExecutionTask");
    }

    /**
     * Registers the given {@link Advice}s.
     *
     * @param advice the execution tasks to register
     */
    public Rimor registerExecutionTasks(Advice... advice) {
        for (Advice advice : advice)
            this.registerExecutionTask(advice);
        return this;
    }

    /**
     * Registers the given {@link PreAdvice}.
     *
     * @param executionTask the pre-execution task to register
     */
    public Rimor registerPreExecutionTask(PreAdvice executionTask) {
        this.adviceRegistry.registerPreExecutionTask(executionTask);
        return this;
    }

    /**
     * Registers the given {@link PreAdvice}s.
     *
     * @param executionTasks the pre-execution tasks to register
     */
    public Rimor registerPreExecutionTasks(PreAdvice... executionTasks) {
        for (PreAdvice executionTask : executionTasks)
            this.registerPreExecutionTask(executionTask);
        return this;
    }

    /**
     * Registers the given {@link PostAdvice}.
     *
     * @param executionTask the post-execution task to register
     */
    public Rimor registerPostExecutionTask(PostAdvice executionTask) {
        this.adviceRegistry.registerPostExecutionTask(executionTask);
        return this;
    }

    /**
     * Registers the given {@link PostAdvice}s.
     *
     * @param executionTasks the post-execution tasks to register
     */
    public Rimor registerPostExecutionTasks(PostAdvice... executionTasks) {
        for (PostAdvice executionTask : executionTasks)
            this.registerPostExecutionTask(executionTask);
        return this;
    }

    /**
     * Registers the given {@link RimorExtension}.
     *
     * @param extension the extension to register
     */
    public Rimor registerExtension(RimorExtension extension) {
        this.extensionManager.registerExtension(this, extension);
        return this;
    }

    /**
     * Registers the given {@link RimorExtension}.
     *
     * @param extensions the extensions to register
     */
    public Rimor registerExtensions(RimorExtension... extensions) {
        for (RimorExtension extension : extensions)
            this.registerExtension(extension);
        return this;
    }

    /**
     * Initializes all the registered extensions.
     */
    public Rimor initialize() {
        if (this.isInitialized())
            throw new IllegalStateException("this Rimor instance has already been initialized!");

        this.extensionManager.initialize();
        this.initialized = true;
        return this;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public RimorInjector getInjector() {
        return injector;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public ExceptionHandlerRegistry getExceptionHandlerRegistry() {
        return exceptionHandlerRegistry;
    }

    public AdviceRegistry getExecutionTaskRegistry() {
        return adviceRegistry;
    }

    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    public ProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public CommandResolver getMappedCommandResolver() {
        return commandResolver;
    }

    public PathResolver getPathResolver() {
        return pathResolver;
    }
}
