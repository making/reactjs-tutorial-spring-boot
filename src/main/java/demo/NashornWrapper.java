package demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class NashornWrapper {

    private final NashornScriptEngine nashorn = (NashornScriptEngine) new ScriptEngineManager()
            .getEngineByName("nashorn");

    public NashornWrapper polyfill() {
        try {
            this.nashorn.eval("var global = this;\n" + "var console = {};\n"
                    + "console.debug = print;\n" + "console.warn = print;\n"
                    + "console.log = print;");
        } catch (ScriptException e) {
            throw new IllegalStateException("Failed to polyfill!", e);
        }
        return this;
    }

    public NashornWrapper loadFromClassPath(String file) {
        try {
            this.nashorn.eval(readFromClassPath(file));
        } catch (ScriptException e) {
            throw new IllegalStateException("Failed to loadFromClassPath "
                    + file + "!", e);
        }
        return this;
    }

    public Object invokeFunction(String functionName, Object... args) {
        try {
            return this.nashorn.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to invoke "
                    + functionName, e);
        }
    }

    public <T> T invokeFunction(String functionName,
            Function<Object, T> converter, Object... args) {
        return converter.apply(invokeFunction(functionName, args));
    }

    private String readFromClassPath(String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                path)) {
            if (in == null) {
                throw new IllegalArgumentException(path + " is not found!");
            }
            return copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path, e);
        }
    }

    private static String copyToString(InputStream in, Charset charset) throws IOException {
        StringBuilder out = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(in, charset);) {
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }
            return out.toString();
        }
    }
}
