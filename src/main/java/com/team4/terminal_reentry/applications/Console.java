package com.team4.terminal_reentry.applications;

import java.io.IOException;

public class Console {
    private static final String os = System.getProperty("os.name").toLowerCase();

    private Console() {
    }


    public static void clear() {
        ProcessBuilder var0 = os.contains("windows") ? new ProcessBuilder(new String[]{"cmd", "/c", "cls"}) : new ProcessBuilder(new String[]{"clear"});

        try {
            var0.inheritIO().start().waitFor();
        } catch (InterruptedException var2) {
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

}