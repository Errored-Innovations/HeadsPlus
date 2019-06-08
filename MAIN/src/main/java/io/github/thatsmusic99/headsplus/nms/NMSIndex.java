package io.github.thatsmusic99.headsplus.nms;

import io.github.thatsmusic99.headsplus.nms.v1_10_NMS.v1_10_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_11_NMS.v1_11_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_12_NMS.v1_12_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_13_NMS.v1_13_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_13_R2_NMS.v1_13_R2_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_14_R1_NMS.v1_14_R1_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_8_R1_NMS.v1_8_R1_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_8_R2_NMS.v1_8_R2_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_8_R3_NMS.v1_8_R3_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_9_NMS.v1_9_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_9_R2_NMS.v1_9_R2_NMS;

public enum NMSIndex {

    v1_8_R1(new v1_8_R1_NMS(), 1),
    v1_8_R2(new v1_8_R2_NMS(), 2),
    v1_8_R3(new v1_8_R3_NMS(), 3),
    v1_9_R1(new v1_9_NMS(), 4),
    v1_9_R2(new v1_9_R2_NMS(), 5),
    v1_10_R1(new v1_10_NMS(), 6),
    v1_11_R1(new v1_11_NMS(), 7),
    v1_12_R1(new v1_12_NMS(), 8),
    v1_13_R1(new v1_13_NMS(), 9),
    v1_13_R2(new v1_13_R2_NMS(), 10),
    v1_14_R1(new v1_14_R1_NMS(), 11);

    private int order;
    private NMSManager nms;

    NMSIndex(NMSManager nmsManager, int order) {
        this.nms = nmsManager;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public NMSManager getNms() {
        return nms;
    }
}
