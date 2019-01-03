package com.github.fridujo.glacio.running.api.extension;

public interface BeforeExampleCallback extends Extension {

    /**
     * Callback that is invoked <em>before</em> each example is invoked.
     *
     * @param context the current extension context; never {@code null}
     * @throws Exception if an error occur during the callback execution
     */
    void beforeExample(ExtensionContext context) throws Exception;
}
