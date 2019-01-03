package com.github.fridujo.glacio.running.api.extension;

public interface AfterConfigurationCallback extends Extension {

    /**
     * Callback that is invoked once <em>after</em> all examples in the current
     * configuration.
     *
     * @param context the current extension context; never {@code null}
     * @throws Exception if an error occur during the callback execution
     */
    void afterConfiguration(ExtensionContext context) throws Exception;
}
