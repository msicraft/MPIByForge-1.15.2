package me.msicraft.mpibyforge.a;

import me.msicraft.mpibyforge.MPIByForge;

import java.io.*;

public class TeamSpawnDataFile {

    private static File getFile(String teamName) {
        String fileName = teamName + ".txt";
        File folder = new File(MPIByForge.directoryPath + File.separator + "mpibyforge");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(MPIByForge.directoryPath + File.separator + "mpibyforge" + File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void setTeamSpawnLocation(String teamName, Location location) {
        File file = getFile(teamName);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String s = location.getX() + ":" + location.getY() + ":" + location.getZ();
            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getTeamSpawnLocation(String teamName) {
        Location location = null;
        File file = getFile(teamName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s = bufferedReader.readLine();
            if (s != null) {
                String[] a = s.split(":");
                int x = (int) Double.parseDouble(a[0]);
                int y = (int) Double.parseDouble(a[1]);
                int z = (int) Double.parseDouble(a[2]);
                location = new Location(x, y, z);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

}
