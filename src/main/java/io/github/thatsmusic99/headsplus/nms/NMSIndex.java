package io.github.thatsmusic99.headsplus.nms;

public enum NMSIndex {

    v1_8_R1(1),
    v1_8_R2(2),
    v1_8_R3(3),
    v1_9_R1(4),
    v1_9_R2(5),
    v1_10_R1(6),
    v1_11_R1(7),
    v1_12_R1(8),
    v1_13_R1(9),
    v1_13_R2(10),
    v1_14_R1(11),
    v1_15_R1(12),
    v1_16_R1(13),
    v1_16_R2(14),
    v1_16_R3(15);

    private final int order;

    NMSIndex(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

}
