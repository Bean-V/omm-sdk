package com.joelc.camera2recorddemo.OpenGL;

public interface Renderer {

    void create();

    void sizeChanged(int width, int height);

    void draw(int texture);

    void destroy();

}
