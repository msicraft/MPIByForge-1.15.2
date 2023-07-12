package me.msicraft.mpibyforge.DataFile;

import me.msicraft.mpibyforge.MPIByForge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PumpkinJuiceLogDataFile {

    private static final String logFolderPath = MPIByForge.directoryPath + File.separator + "mpibyforge" + File.separator + "Log";

    private static String getFileName() {
        return "PumpkinJuice-log.txt";
    }

    private static File getFile() {
        File folder = new File(logFolderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(logFolderPath + File.separator + getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean addLog(String log) {
        boolean success = false;
        File file = getFile();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.newLine();
            bufferedWriter.write(log);
            bufferedWriter.flush();
            bufferedWriter.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

}
