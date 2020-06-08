package com.quvideo.application.gallery.comparator;

import com.quvideo.application.gallery.model.MediaItem;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Chandler on 2017/3/9.
 */

public class MediaItemComparator implements Comparator<MediaItem> {
    public static final int SORT_TYPE_NONE = 0;
    public static final int SORT_TYPE_TITLE = 1;
    public static final int SORT_TYPE_DATE = 2;
    public static final int SORT_TYPE_DATE_DESC = 3;

    int mOrder = 0;

    public MediaItemComparator(int order) {
        mOrder = order;
    }

    @Override
    public int compare(MediaItem lhs, MediaItem rhs) {
        if (null == lhs || null == rhs || lhs == rhs) {
            return 0;
        }

        int flags = 0;
        if (mOrder == SORT_TYPE_DATE) {
            if (lhs.date < rhs.date) {
                flags = -1;
            } else if (lhs.date > rhs.date) {
                flags = 1;
            } else {
                flags = 0;
            }
        } else if (mOrder == SORT_TYPE_DATE_DESC) {
            if (lhs.date > rhs.date) {
                flags = -1;
            } else if (lhs.date < rhs.date) {
                flags = 1;
            } else {
                flags = 0;
            }
        } else {
            Collator ca = null;
            try {
                ca = Collator.getInstance(Locale.CHINA);
            } catch (Exception ex) {

            }
            if (ca != null && lhs.title != null && rhs.title != null) {
                if (ca.compare(lhs.title, rhs.title) < 0) {
                    flags = -1;
                } else if (ca.compare(lhs.title, rhs.title) > 0) {
                    flags = 1;
                } else {
                    flags = 0;
                }
            }
        }

        return flags;

    }
}
