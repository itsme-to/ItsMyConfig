package ua.realalpha.itsmyconfig.offlinecommand;

import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfflineCommandManager {

    private final Map<String, List<OfflineCommandEntry>> offlineCommandEntriesByPlayer = new HashMap<>();
    private final File file;

    public OfflineCommandManager(ItsMyConfig itsMyConfig) {
        this.file = new File(itsMyConfig.getDataFolder(), "offlineCommand.binary");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void consumeOfflineCommandEntries(Player player){
        List<OfflineCommandEntry> offlineCommandEntries = offlineCommandEntriesByPlayer.remove(player.getName());
        if (offlineCommandEntries == null) return;
        offlineCommandEntries.forEach(offlineCommandEntry -> offlineCommandEntry.execute(player, false));
    }

    public void addOfflineCommandEntry(String player, OfflineCommandEntry offlineCommandEntry){
        this.offlineCommandEntriesByPlayer.computeIfAbsent(player, s -> new ArrayList<>()).add(offlineCommandEntry);
    }

    public void write(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.writeInt(offlineCommandEntriesByPlayer.size());

            for (Map.Entry<String, List<OfflineCommandEntry>> entry : offlineCommandEntriesByPlayer.entrySet()) {
                String name = entry.getKey();
                dataOutputStream.writeUTF(name);
                List<OfflineCommandEntry> offlineCommandEntries = entry.getValue();
                dataOutputStream.writeInt(offlineCommandEntries.size());
                for (OfflineCommandEntry offlineCommandEntry : offlineCommandEntries) {
                    dataOutputStream.writeInt(offlineCommandEntry.getDelay());
                    dataOutputStream.writeUTF(offlineCommandEntry.getCommand());
                    dataOutputStream.writeUTF(offlineCommandEntry.getOfflineCommandExecutor().name());
                }
            }

            dataOutputStream.close();
            fileOutputStream.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void read(){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            if (dataInputStream.available() == 0) return;

            int size = dataInputStream.readInt();

            for (int i = 0; i < size; i++) {
                String name = dataInputStream.readUTF();
                List<OfflineCommandEntry> offlineCommandEntries = new ArrayList<>();
                int sizeCommand = dataInputStream.readInt();
                for (int i1 = 0; i1 < sizeCommand; i1++) {
                    int delay = dataInputStream.readInt();
                    String command = dataInputStream.readUTF();
                    String offlineCommandExecutor = dataInputStream.readUTF();
                    offlineCommandEntries.add(new OfflineCommandEntry(OfflineCommandSender.valueOf(offlineCommandExecutor), delay, command));
                }

                offlineCommandEntriesByPlayer.put(name, offlineCommandEntries);
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
