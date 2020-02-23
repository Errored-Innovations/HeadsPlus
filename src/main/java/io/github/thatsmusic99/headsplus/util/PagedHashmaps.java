package io.github.thatsmusic99.headsplus.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class PagedHashmaps<K, V> {

    private LinkedHashMap<K, V> hs;
    private int pages;
    private int contents;
    private int currentPage;
    private int contentsPerPage;

    public PagedHashmaps(LinkedHashMap<K, V> hs, int contentsPerPage) {
        if (contentsPerPage < 1) {
            throw new IllegalArgumentException("The provided int must be bigger than 0 for contents per page!");
        }
        this.hs = hs;
        int pages = 1;
        int bls = hs.size();
        while (bls > contentsPerPage) {
            pages++;
            bls = bls - contentsPerPage;
        }
        this.pages = pages;
        this.contents = hs.size();
        this.currentPage = 1;
        this.contentsPerPage = contentsPerPage;
    }

    public int getTotalPages() {
        return pages;
    }

    public int getTotalContents() {
        return contents;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public LinkedHashMap<K, V> getHs() {
        return hs;
    }

    public LinkedHashMap<K, V> getContentsInPage(int page) {
        if (page > getTotalPages()) {
            throw new IllegalArgumentException("The provided page is an int larger than the total number of pages!");
        } else if (page < 1) {
            throw new IllegalArgumentException("The provided page must be bigger than 0!");
        }
        int sIndex = (page - 1) * getContentsPerPage(); // 0
        int eIndex = getContentsPerPage() + sIndex; // 1
        if (eIndex > getHs().size()) { // 1 > 2
            eIndex = getHs().size();
        }
        setPage(page);
        return splitHashmap(sIndex, eIndex);
    }

    private LinkedHashMap<K, V> splitHashmap(int sIndex, int eIndex) {
        List<K> mapKeys = new ArrayList<>(hs.keySet());
        List<V> mapValues = new ArrayList<>(hs.values());
        mapKeys = mapKeys.subList(sIndex, eIndex);
        mapValues = mapValues.subList(sIndex, eIndex);

        LinkedHashMap<K, V> sortedMap =
                new LinkedHashMap<>();

        for (V val : mapValues) {
            Iterator<K> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                K key = keyIt.next();
                V comp1 = hs.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    private void setPage(int page) {
        this.currentPage = page;
    }

    public int getContentsPerPage() {
        return contentsPerPage;
    }
}
