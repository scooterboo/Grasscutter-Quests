package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.Loggers;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.Arrays;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class BaseHandler {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    protected int handleUnimplemented(Object... args){
        logger.warn("[LUA] Call unimplemented {} with {}", StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass(), Arrays.toString(args));
        return 0;
    }
}
