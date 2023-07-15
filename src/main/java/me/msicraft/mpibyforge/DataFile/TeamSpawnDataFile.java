package me.msicraft.mpibyforge.DataFile;

import me.msicraft.mpibyforge.MPIByForge;
import me.msicraft.mpibyforge.a.Location;
import net.minecraft.util.math.ChunkPos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TeamSpawnDataFile {

    private static String getSpawnFileName(String teamName) {
        return teamName + ".txt";
    }

    private static String getChunkFileName(String teamName) {
        return teamName + "-ClaimChunks.txt";
    }

    private static File getLocationFile(String teamName) {
        String fileName = getSpawnFileName(teamName);
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

    private static File getChunkFile(String teamName) {
        String fileName = getChunkFileName(teamName);
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

    public static void setTeamClaimChunks(String teamName, List<ChunkPos> chunkPosList) {
        File file = getChunkFile(teamName);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String s;
            for (ChunkPos chunkPos : chunkPosList) {
                s = chunkPos.x + ":" + chunkPos.z;
                bufferedWriter.write(s);
                bufferedWriter.newLine();
                //bufferedWriter.write("\r\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ChunkPos> getClaimChunks(String teamName) {
        List<ChunkPos> chunkPos = new ArrayList<>();
        File file = getChunkFile(teamName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s;
            ChunkPos pos;
            while ((s = bufferedReader.readLine()) != null) {
                String[] b = s.split(":");
                int x = Integer.parseInt(b[0]);
                int z = Integer.parseInt(b[1]);
                pos = new ChunkPos(x, z);
                chunkPos.add(pos);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunkPos;
    }

    public static void setTeamSpawnLocation(String teamName, Location location) {
        File file = getLocationFile(teamName);
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
        File file = getLocationFile(teamName);
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
