package test.zopa.infrastructure;

import com.google.inject.*;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public final class GuiceFactory implements IFactory {
    private final Injector injector = Guice.createInjector(new QuoteFinderModule());

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            return injector.getInstance(aClass);
        } catch (ConfigurationException ex) {
            return CommandLine.defaultFactory().create(aClass);
        }
    }
}