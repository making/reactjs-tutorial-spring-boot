package demo;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class React {

    private NashornScriptEngine nashorn;

    public React() {
        try {
            nashorn = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
            // polyfill
            nashorn.eval("var global = this;\n" +
                    "var console = {};\n" +
                    "console.debug = print;\n" +
                    "console.warn = print;\n" +
                    "console.log = print;");
        } catch (ScriptException e) {
            throw new IllegalStateException("Script error!", e);
        }
    }

    public React load(String file) {
        try {
            this.nashorn.eval(readFromClassPath(file));
        } catch (ScriptException e) {
            throw new IllegalStateException("Failed to load " + file + "!", e);
        }
        return this;
    }

    public String invokeFunction(String functionName, Object... args) {
        Object ret;
        try {
            ret = this.nashorn.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to invoke " + functionName, e);
        }
        return String.valueOf(ret);
    }

    private Reader readFromClassPath(String path) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalArgumentException(path + " is not found!");
        }
        return new InputStreamReader(in);
    }
}
