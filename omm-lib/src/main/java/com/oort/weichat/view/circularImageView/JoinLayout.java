//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.oort.weichat.view.circularImageView;

public class JoinLayout {
    public static final String TAG = JoinLayout.class.getSimpleName();
    private static final float[][] rotations = new float[][]{{360.0F}, {45.0F, 360.0F}, {120.0F, 0.0F, -120.0F}, {90.0F, 180.0F, -90.0F, 0.0F}, {144.0F, 72.0F, 0.0F, -72.0F, -144.0F}};
    private static final float[][] sizes = new float[][]{{0.9F, 0.9F}, {0.5F, 0.65F}, {0.45F, 0.8F}, {0.45F, 0.91F}, {0.32F, 0.8F}};

    public JoinLayout() {
    }

    public static int max() {
        return 5;
    }

    public static float[] rotation(int count) {
        return count > 0 && count <= rotations.length ? rotations[count - 1] : null;
    }

    public static float[] size(int count) {
        return count > 0 && count <= sizes.length ? sizes[count - 1] : sizes[4];
    }

    public static float[] offset(int count, int index, float dimension, float[] size) {
        switch (count) {
            case 1:
                return offset1(index, dimension, size);
            case 2:
                return offset2(index, dimension, size);
            case 3:
                return offset3_(index, dimension, size);
            case 4:
                return offset4_(index, dimension, size);
            case 5:
                return offset5_(index, dimension, size);
            case 6:
                return offset6_(index, dimension, size);
            case 7:
                return offset7_(index, dimension, size);
            case 8:
                return offset8_(index, dimension, size);
            case 9:
                return offset9_(index, dimension, size);
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset5(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;
        float x2 = (float) ((double) s1 * Math.cos(0.3316125578789226D));
        float y2 = (float) ((double) s1 * Math.sin(0.3141592653589793D));
        float x3 = (float) ((double) s1 * Math.cos(0.9424777960769379D));
        float y3 = (float) ((double) (-s1) * Math.sin(0.9424777960769379D));
        float x4 = (float) ((double) (-s1) * Math.cos(0.9424777960769379D));
        float y4 = (float) ((double) (-s1) * Math.sin(0.9424777960769379D));
        float x5 = (float) ((double) (-s1) * Math.cos(0.3316125578789226D));
        float y5 = (float) ((double) s1 * Math.sin(0.3141592653589793D));
        float xx1 = (dimension - cd - y3 - s1) / 2.0F;
        float xxc1 = (dimension - cd) / 2.0F;
        switch (index) {
            case 0:
                return new float[]{x1 + xxc1, s1 + xx1};
            case 1:
                return new float[]{x2 + xxc1, y2 + xx1};
            case 2:
                return new float[]{x3 + xxc1, y3 + xx1};
            case 3:
                return new float[]{x4 + xxc1, y4 + xx1};
            case 4:
                return new float[]{x5 + xxc1, y5 + xx1};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }


    private static float[] offset6_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 4)/3;

        switch (index) {
            case 0:
                return new float[]{space, width/2 + space};
            case 1:
                return new float[]{space*2 + width , width/2 + space};
            case 2:
                return new float[]{space*3 + width*2, width/2 + space};
            case 3:
                return new float[]{space, width/2 + space*2 + width};
            case 4:
                return new float[]{space*2 + width , width/2 + space*2 + width};
            case 5:
                return new float[]{space*3 + width*2, width/2 + space*2 + width};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset7_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 4)/3;

        switch (index) {
            case 0:
                return new float[]{width + 2 * space, space};
            case 1:
                return new float[]{space , width + space * 2};
            case 2:
                return new float[]{space*2 + width, width + space * 2};
            case 3:
                return new float[]{space * 3 + width * 2 , space*2 + width};
            case 4:
                return new float[]{space , width*2 + space * 3};
            case 5:
                return new float[]{space*2 + width, width*2 + space * 3};
            case 6:
                return new float[]{space * 3 + width * 2 , space*3 + width * 2};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset8_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 4)/3;

        switch (index) {
            case 0:
                return new float[]{width/2 + space,  space};
            case 1:
                return new float[]{width/2 + space*2 + width,  space};
            case 2:
                return new float[]{space , width + space * 2};
            case 3:
                return new float[]{space*2 + width, width + space * 2};
            case 4:
                return new float[]{space * 3 + width * 2 , space*2 + width};
            case 5:
                return new float[]{space , width*2 + space * 3};
            case 6:
                return new float[]{space*2 + width, width*2 + space * 3};
            case 7:
                return new float[]{space * 3 + width * 2 , space*3 + width * 2};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset9_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 4)/3;

        switch (index) {
            case 0:
                return new float[]{space, space};
            case 1:
                return new float[]{width + 2 * space, space};
            case 2:
                return new float[]{width*2 + 3 * space, space};
            case 3:
                return new float[]{space , width + space * 2};
            case 4:
                return new float[]{space*2 + width, width + space * 2};
            case 5:
                return new float[]{space * 3 + width * 2 , space*2 + width};
            case 6:
                return new float[]{space , width*2 + space * 3};
            case 7:
                return new float[]{space*2 + width, width*2 + space * 3};
            case 8:
                return new float[]{space * 3 + width * 2 , space*3 + width * 2};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset5_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 4)/3;

        switch (index) {
            case 0:
                return new float[]{width/2 + space, width/2 + space};
            case 1:
                return new float[]{width/2 + width  + space, width/2 + space};
            case 2:
                return new float[]{space, width/2 + space*2 + width};
            case 3:
                return new float[]{space*2 + width , width/2 + space*2 + width};
            case 4:
                return new float[]{space*3 + width*2, width/2 + space*2 + width};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset4(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = cd * size[1];
        float x1 = 0.0F;
        float y1 = 0.0F;
        float xx1 = (dimension - cd - s1) / 2.0F;
        switch (index) {
            case 0:
                return new float[]{x1 + xx1, y1 + xx1};
            case 1:
                return new float[]{s1 + xx1, y1 + xx1};
            case 2:
                return new float[]{s1 + xx1, s1 + xx1};
            case 3:
                return new float[]{x1 + xx1, s1 + xx1};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset3(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = cd * size[1];
        float y2 = s1 * 1.0F;
        float x2 = s1 - y2 / 1.73205F;
        float x3 = s1 * 2.0F - x2;
        float xx1 = (dimension - cd - y2) / 2.0F;
        float xxc1 = (dimension - cd) / 2.0F - s1;
        switch (index) {
            case 0:
                return new float[]{s1 + xxc1, xx1};
            case 1:
                return new float[]{x2 + xxc1, y2 + xx1};
            case 2:
                return new float[]{x3 + xxc1, y2 + xx1};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }


    private static float[] offset3_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 3)/2;

        switch (index) {
            case 0:
                return new float[]{width/2 + space, space};
            case 1:
                return new float[]{space, space*2 + width};
            case 2:
                return new float[]{space * 2 + width, space*2 + width};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }
    private static float[] offset4_(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = -cd * size[1];
        float x1 = 0.0F;

        float space = 2;
        float width = (dimension - space * 3)/2;

        switch (index) {
            case 0:
                return new float[]{space, space};
            case 1:
                return new float[]{width + space * 2, space};
            case 2:
                return new float[]{space , space*2 + width};
            case 3:
                return new float[]{space * 2 + width, space*2 + width};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset2(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float s1 = cd * size[1];
        float x1 = 0.0F;
        float y1 = 0.0F;
        float xx1 = (dimension - cd - s1) / 2.0F;
        switch (index) {
            case 0:
                return new float[]{x1 + xx1, y1 + xx1};
            case 1:
                return new float[]{s1 + xx1, s1 + xx1};
            default:
                return new float[]{0.0F, 0.0F};
        }
    }

    private static float[] offset1(int index, float dimension, float[] size) {
        float cd = dimension * size[0];
        float offset = (dimension - cd) / 2.0F;
        return new float[]{offset, offset};
    }
}
