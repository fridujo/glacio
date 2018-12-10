package com.github.fridujo.glacio.running.api.extension;

public interface BeforeAllCallback extends Extension {

    /**
     * Callback that is invoked once <em>before</em> all examples in the current
     * configuration.
     *
     * @param context the current extension context; never {@code null}
     */
    void beforeAll(ExtensionContext context) throws Exception;
}
