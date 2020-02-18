package utilities.misc;

public class CodeHelper {
    public static Object nullReplacer(Object input, Object...replacement){
        // if replacement is supplied use it
        if(varargsChecker(replacement))
            return  input == null ? replacement[0] : input;
        // else use defaults
        if(input instanceof String)
            return "";
        return null;
    }

    public static boolean varargsChecker(Object[] varargs){
        return varargs != null && varargs.length > 0 && varargs[0] != null;
    }
}
