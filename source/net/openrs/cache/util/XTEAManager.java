/**
* Copyright (c) Kyle Fricilone
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.openrs.cache.util;

import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import net.openrs.cache.Constants;
import net.openrs.cache.util.runelite.RuneLiteXTEAEntry;

/**
 * @author Kyle Friz
 * @author Sino
 *
 * @since Jun 30, 2015, March 2, 2018
 */
public class XTEAManager {
      private static final String RUNELITE_URL = "http://api.runelite.net/runelite-1.2.18/xtea";

      private static final Map<Integer, int[]> maps = new HashMap<>();
      private static final Map<Integer, int[]> tables = new HashMap<>();

      public static final int[] NULL_KEYS = new int[4];

      public static final int[] lookupTable(int id) {
            int[] keys = tables.get(id);
            if (keys == null)
                  return NULL_KEYS;

            return keys;
      }

      public static final int[] lookupMap(int id) {
            int[] keys = maps.get(id);
            if (keys == null)
                  return NULL_KEYS;

            return keys;
      }

      public static void loadFromRuneLite() {
            try {
                  URL url = new URL(RUNELITE_URL);
                  URLConnection connection = url.openConnection();
                  connection.addRequestProperty("User-Agent", "Mozilla/4.76");

                  StringBuilder bldr = new StringBuilder();
                  try (Scanner scanner = new Scanner(connection.getInputStream())) {
                        while (scanner.hasNext()) {
                              bldr.append(scanner.nextLine());
                        }
                  }

                  loadFromJSON(bldr.toString());
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

      public static void loadFromJSON(String jsonString) {
            try {
                  Gson gson = new Gson();
                  RuneLiteXTEAEntry[] entries = gson.fromJson(jsonString, RuneLiteXTEAEntry[].class);
                  for (RuneLiteXTEAEntry entry : entries) {
                        maps.put(entry.getRegion(), entry.getKeys());
                        tables.put(entry.getRegion(), entry.getKeys());
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

      public static void loadFromText() {
            try {

                  File xMapDir = new File(Constants.XMAP_PATH);

                  if (!xMapDir.exists()) {
                        xMapDir.mkdirs();
                  }

                  for (File file : xMapDir.listFiles()) {
                        if (file.getName().endsWith(".txt")) {
                              Integer regionID = Integer.valueOf(file.getName().substring(0,
                                  file.getName().indexOf(".txt")));

                              int[] keys = Files.lines(Paths.get(".")
                                  .resolve(Constants.XMAP_PATH + file.getName()))
                                  .map(Integer::valueOf).mapToInt(Integer::intValue).toArray();

                              maps.put(regionID, keys);
                        }
                  }

                  File xTableDir = new File(Constants.XTABLE_PATH);

                  if (!xTableDir.exists()) {
                        xTableDir.mkdirs();
                  }

                  for (File file : xTableDir.listFiles()) {
                        if (file.getName().endsWith(".txt")) {
                              Integer typeID = Integer.valueOf(file.getName().substring(0,
                                  file.getName().indexOf(".txt")));

                              int[] keys = Files.lines(Paths.get(".")
                                  .resolve(Constants.XTABLE_PATH + file.getName()))
                                  .map(Integer::valueOf).mapToInt(Integer::intValue).toArray();

                              tables.put(typeID, keys);
                        }
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
}
