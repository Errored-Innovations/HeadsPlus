package io.github.thatsmusic99.headsplus.config.customheads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadPackLoader {

    private static List<String> registeredSections = new ArrayList<>();
    private static List<String> downloadedFiles = new ArrayList<>();

    public static void init() {
        File packs = new File(HeadsPlus.getInstance().getDataFolder(), "packs");
        if (!packs.exists()) packs.mkdirs();
        if (packs.listFiles() == null) return;
        for (File packFile : packs.listFiles()) {
            try {
                downloadedFiles.add(packFile.getName());
                String fileName = packFile.getName().substring(0, packFile.getName().lastIndexOf('.'));
                JSONObject object = (JSONObject) new JSONParser().parse(new FileReader(packFile));
                JSONObject details = (JSONObject) object.get("details");
                if ((boolean) details.get("enabled")) {
                    // Heads inside the section
                    List<String> headList = new ArrayList<>();
                    HeadsPlus.getInstance().getLogger().info("Hooked into " + fileName + "!");
                    registeredSections.add(fileName);
                    JSONObject heads = (JSONObject) object.get("heads");
                    for (Object headObj : heads.keySet()) {
                        String head = (String) headObj;
                        JSONObject headInfo = (JSONObject) heads.get(head);
                        headList.add(head);
                        HeadsPlus.getInstance().getHeadsXConfig().headsCache.put(head,
                                HeadsPlus.getInstance().getHeadsXConfig().getSkullFromTexture((String) headInfo.get("texture"), (String) headInfo.get("display-name")));
                        System.out.println("Added " + head);
                    }

                    HeadsPlus.getInstance().getHeadsXConfig().sections.put(fileName, headList);
                    HeadsPlus.getInstance().getHeadsXConfig().sectionsCache.put(fileName,
                            new HeadsPlusConfigCustomHeads.SectionInfo(fileName,
                                    (String) details.get("display-name"),
                                    (String) details.get("texture"),
                                    (String) details.get("permission"),true));
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
