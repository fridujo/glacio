package com.github.fridujo.glacio.running.api.extension;

public interface BeforeConfigurationCallback extends Extension {

    /**
     * Callback that is invoked once <em>before</em> all examples in the current
     * configuration.
     *
     * @param context the current extension context; never {@code null}
     * @throws Exception if an error occur during the callback execution
     */
    void beforeConfiguration(ExtensionContext context) throws Exception;
}
