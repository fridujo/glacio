package com.github.fridujo.glacio.running.api.extension;

public interface AfterAllCallback extends Extension {

    /**
     * Callback that is invoked once <em>after</em> all examples in the current
     * configuration.
     *
     * @param context the current extension context; never {@code null}
     */
    void afterAll(ExtensionContext context) throws Exception;
}
