package biz.netcentric.script;

import javax.script.*;

/**
 * Created by RafaelLopez on 2/19/17.
 */
public class ScriptExecutor {

    private static final String NASHORN_ENGINE = "nashorn";
    private ScriptEngine engine;
    private Bindings bindings;

    public ScriptExecutor() {
        engine = new ScriptEngineManager().getEngineByName(NASHORN_ENGINE);
        bindings = new SimpleBindings();
    }

    /**
     * Send Attributes to Modify the Javascript variables
     *
     * @param attributeName  the name of the attribute in JavaScript
     * @param attributeValue the new value
     */
    public void sendAttribute(final String attributeName, final Object attributeValue) {
        bindings.put(attributeName, attributeValue);
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    /**
     * Execute a JavaScriptCode
     *
     * @param javaScriptCode
     * @return Object with the response of the execution
     * @throws ScriptException
     */
    public Object executeJavaScript(final String javaScriptCode) throws ScriptException {
        return engine.eval(javaScriptCode);
    }

}
