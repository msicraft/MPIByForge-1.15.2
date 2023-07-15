package me.msicraft.mpibyforge.DataFile;

import me.msicraft.mpibyforge.MPIByForge;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDataFile {

    private static final String folderPath = MPIByForge.directoryPath + File.separator + "mpibyforge" + File.separator + "config";
    private static String getFileName() { return "config.txt"; }

    private static final List<String> variables = Arrays.asList("NoDamageTick", "MinAttackPower", "PumpkinjuiceDropRate", "PumpkinJuiceDropLevelRange");
    private static final Map<String, Object> variablesMap = new HashMap<>();

    public static void setUp() {
        File file = getFile();
        loadFileToMap();
        MPIByForge.getLogger().info("Load Config file: " + file.getPath());
    }

    public static Object getVariableValue(String s) {
        Object o = null;
        if (variablesMap.containsKey(s)) {
            o = variablesMap.get(s);
        }
        return o;
    }

    public static void setVariableValue(String s, Object o) { variablesMap.put(s, o); }

    private static File getFile() {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder + File.separator + getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();
                setDefaultVariables(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            /*
            if (isEmptyInFile()) {
                setDefaultVariables(false);
            }

             */
        }
        return file;
    }

    private static boolean isEmptyInFile() {
        File file = getFile();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int lineCount = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineCount++;
            }
            if (lineCount == 0) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void setDefaultVariables(boolean append) {
        File file = getFile();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append));
            for (String s : variables) {
                String a;
                switch (s) {
                    case "NoDamageTick":
                        a = s + ":" + 20;
                        bufferedWriter.write(a);
                        bufferedWriter.newLine();
                        continue;
                    case "MinAttackPower":
                        a = s + ":" + 0.0;
                        bufferedWriter.write(a);
                        bufferedWriter.newLine();
                        continue;
                    case "PumpkinjuiceDropRate":
                        a = s + ":" + 0.0001;
                        bufferedWriter.write(a);
                        bufferedWriter.newLine();
                        continue;
                    case "PumpkinJuiceDropLevelRange":
                        a = s + ":" + 5;
                        bufferedWriter.write(a);
                        bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasVariableToFile(String v) {
        File file = getFile();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(v)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getInt(String variable) {
        int i = 0;
        File file = getFile();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(variable)) {
                    String[] a = line.split(":");
                    String a1 = a[1];
                    return Integer.parseInt(a1);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static double getDouble(String variable) {
        double i = 0.0;
        File file = getFile();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(variable)) {
                    String[] a = line.split(":");
                    String a1 = a[1];
                    return Double.parseDouble(a1);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void saveMapToFile() {
        File file = getFile();
        Object o;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String lineS;
            for (String s : variables) {
                switch (s) {
                    case "NoDamageTick":
                        o = variablesMap.getOrDefault("NoDamageTick", 20);
                        lineS = s + ":" + o;
                        bufferedWriter.write(lineS);
                        bufferedWriter.newLine();
                        continue;
                    case "MinAttackPower":
                        o = variablesMap.getOrDefault("MinAttackPower", 0.0);
                        lineS = s + ":" + o;
                        bufferedWriter.write(lineS);
                        bufferedWriter.newLine();
                        continue;
                    case "PumpkinjuiceDropRate":
                        o = variablesMap.getOrDefault("PumpkinjuiceDropRate", 0.0001);
                        lineS = s + ":" + o;
                        bufferedWriter.write(lineS);
                        bufferedWriter.newLine();
                        continue;
                    case "PumpkinJuiceDropLevelRange":
                        o = variablesMap.getOrDefault("PumpkinJuiceDropLevelRange", 5);
                        lineS = s + ":" + o;
                        bufferedWriter.write(lineS);
                        bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFileToMap() {
        int i;
        double d;
        for (String s : variables) {
            switch (s) {
                case "NoDamageTick":
                    if (hasVariableToFile(s)) {
                        i = getInt(s);
                    } else {
                        i = 20;
                    }
                    variablesMap.put(s,i);
                    continue;
                case "MinAttackPower":
                    if (hasVariableToFile(s)) {
                        d = getDouble(s);
                    } else {
                        d = 0.0;
                    }
                    variablesMap.put(s,d);
                    continue;
                case "PumpkinjuiceDropRate":
                    if (hasVariableToFile(s)) {
                        d = getDouble(s);
                    } else {
                        d = 0.0001;
                    }
                    variablesMap.put(s,d);
                    continue;
                case "PumpkinJuiceDropLevelRange":
                    if (hasVariableToFile(s)) {
                        i = getInt(s);
                    } else {
                        i = 5;
                    }
                    variablesMap.put(s,i);
            }
        }
    }

}
