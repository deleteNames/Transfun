package net.translives.app.cache;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.translives.app.util.StreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public final class ReadStateHelper {
    private final static Map<String, ReadStateHelper> helperCache = new HashMap<>();
    private final File file;
    private final Map<String, Long> cache = new HashMap<>();
    private final int maxPoolSize;

    private ReadStateHelper(File file, int maxPoolSize) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead() || !file.canWrite()) {
            throw new NullPointerException("file not null.");
        }
        this.maxPoolSize = maxPoolSize;
        this.file = file;
        read();
    }

    public static ReadStateHelper create(Context context, String fileName, int maxPoolSize) {
        fileName = fileName + ".json";
        if (helperCache.containsKey(fileName)) {
            return helperCache.get(fileName);
        }
        File file = new File(context.getDir("read_state", Context.MODE_PRIVATE), fileName);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException("can't mkdirs by:" + parent.toString());
            }
            try {
                if (!file.createNewFile())
                    throw new IOException("can't createNewFile by:" + file.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ReadStateHelper helper = new ReadStateHelper(file, maxPoolSize);
        helperCache.put(fileName, helper);
        return helper;
    }

    /**
     * 添加已读状态
     *
     * @param key 一般为资讯等Id
     */
    public void put(long key) {
        put(String.valueOf(key));
    }

    /**
     * 添加已读状态
     *
     * @param key 一般为资讯等Id
     */
    public void put(String key) {
        if (TextUtils.isEmpty(key) || cache.containsKey(key))
            return;
        if (cache.size() >= maxPoolSize) {
            adjustCache();
        }
        cache.put(key, System.currentTimeMillis());
        save();
    }

    /**
     * 获取是否为已读
     *
     * @param key 一般为资讯等Id
     * @return True 已读
     */
    public boolean already(long key) {
        return already(String.valueOf(key));
    }

    /**
     * 获取是否为已读
     *
     * @param key 一般为资讯等Id
     * @return True 已读
     */
    public boolean already(String key) {
        return !TextUtils.isEmpty(key) && cache.containsKey(key);
    }

    /**
     * 清理一次当前缓存
     */
    public void adjustCache() {
        if (cache.size() == 0)
            return;
        List<Map.Entry<String, Long>> info = new ArrayList<>(cache.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                long et = (o1.getValue() - o2.getValue());
                return et > 0 ? 1 : (et == 0 ? 0 : -1);
            }
        });

        // Default adjustCache 70% old data
        int deleteSize = (int) (info.size() * 0.7f);
        if (deleteSize <= 0)
            return;
        for (Map.Entry<String, Long> stringLongEntry : info) {
            // Remove
            cache.remove(stringLongEntry.getKey());
            if (--deleteSize <= 0)
                break;
        }
    }

    private void read() {
        Reader reader = null;
        try {
            Map<String, Long> data = new Gson().fromJson(reader = new FileReader(file),
                    new TypeToken<Map<String, Long>>() {
                    }.getType());
            if (data != null && data.size() > 0)
                cache.putAll(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(reader);
        }
    }

    private void save() {
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            new Gson().toJson(cache, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(writer);
        }
    }

}
