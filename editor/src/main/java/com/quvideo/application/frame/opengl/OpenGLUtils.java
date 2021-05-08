package com.quvideo.application.frame.opengl;

import android.opengl.GLES11;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lb6905 on 2017/6/12.
 */

public class OpenGLUtils {

  //每行前两个值为顶点坐标，后两个为纹理坐标
  private static final float[] mVertexData = {
      1f, 1f, 1f, 1f,
      -1f, 1f, 0f, 1f,
      -1f, -1f, 0f, 0f,
      1f, 1f, 1f, 1f,
      -1f, -1f, 0f, 0f,
      1f, -1f, 1f, 0f
  };

  // 数据中有多少个顶点，管线就调用多少次顶点着色器
  private static final String VERTEX_SHADER = "" +
      "attribute vec4 aPosition;\n" + // 顶点着色器的顶点坐标,由外部程序传入
      "uniform mat4 uTextureMatrix;\n" + //纹理矩阵
      "attribute vec4 aTextureCoordinate;\n" + //自己定义的纹理坐标
      "varying vec2 vTextureCoord;\n" +  //传给片元着色器的纹理坐标
      "void main()\n" +
      "{\n" +
      "    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n" + //根据自己定义的纹理坐标和纹理矩阵求取传给片元着色器的纹理坐标
      "    gl_Position = aPosition;\n" + // 最终顶点位置
      "}";

  // 光栅化后产生了多少个片段，就会插值计算出多少个varying变量，同时渲染管线就会调用多少次片段着色器
  private static final String FRAGMENT_SHADER = "" +
      "#extension GL_OES_EGL_image_external : require\n" + //使用外部纹理必须支持此扩展
      "precision mediump float;\n" +
      "uniform samplerExternalOES uTextureSampler;\n" +  //外部纹理采样器
      "varying vec2 vTextureCoord;\n" +
      "void main()\n" +
      "{\n" +
      "     gl_FragColor = texture2D(uTextureSampler, vTextureCoord);\n" +  //获取此纹理（预览图像）对应坐标的颜色值
      "}";

  //每行前两个值为顶点坐标，后两个为纹理坐标
  private static final float[] mVertexData_normal = {
      1f, -1f, 1f, 1f,
      -1f, -1f, 0f, 1f,
      -1f, 1f, 0f, 0f,
      1f, -1f, 1f, 1f,
      -1f, 1f, 0f, 0f,
      1f, 1f, 1f, 0f
  };

  // 数据中有多少个顶点，管线就调用多少次顶点着色器
  private static final String VERTEX_SHADER_NORMAL = "" +
      "attribute vec4 aPosition;\n" + // 顶点着色器的顶点坐标,由外部程序传入
      "uniform mat4 uTextureMatrix;\n" + //纹理矩阵
      "attribute vec4 aTextureCoordinate;\n" + //自己定义的纹理坐标
      "varying vec2 vTextureCoord;\n" +  //传给片元着色器的纹理坐标
      "void main()\n" +
      "{\n" +
      "    vTextureCoord = (aTextureCoordinate).xy;\n" + //根据自己定义的纹理坐标和纹理矩阵求取传给片元着色器的纹理坐标
      "    gl_Position = aPosition;\n" + // 最终顶点位置
      "}";

  private static final String FRAGMENT_SHADER_NORMAL = "" +
      "precision mediump float;\n" +
      "uniform sampler2D uTextureSampler;\n" +
      "varying vec2 vTextureCoord;\n" +
      "void main()\n" +
      "{\n" +
      "     gl_FragColor = texture2D(uTextureSampler, vTextureCoord);\n" +  // 调用函数 进行纹理贴图
      "}";

  private FloatBuffer mBuffer;
  private int vertexShader = -1;
  private int fragmentShader = -1;

  private int mShaderProgram = -1;

  private static final String POSITION_ATTRIBUTE = "aPosition";
  private static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";
  private static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
  private static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

  private int aPositionLocation = -1;
  private int aTextureCoordLocation = -1;
  private int uTextureMatrixLocation = -1;
  private int uTextureSamplerLocation = -1;

  public volatile boolean mbSurfaceTexture = true;

  public OpenGLUtils(boolean bSurfaceTexture) {
    mbSurfaceTexture = bSurfaceTexture;
    if (mbSurfaceTexture) {
      mBuffer = createBuffer(mVertexData);
      vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
      fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
    } else {
      mBuffer = createBuffer(mVertexData_normal);
      vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NORMAL);
      fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NORMAL);
    }
    mShaderProgram = linkProgram(vertexShader, fragmentShader);
  }

  public void onDraw(int OESTextureId, float[] transformMatrix) {
    GLES20.glUseProgram(mShaderProgram);
    //获取Shader中定义的变量在program中的位置
    aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, POSITION_ATTRIBUTE);
    aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, TEXTURE_COORD_ATTRIBUTE);
    uTextureMatrixLocation = GLES20.glGetUniformLocation(mShaderProgram, TEXTURE_MATRIX_UNIFORM);
    uTextureSamplerLocation = GLES20.glGetUniformLocation(mShaderProgram, TEXTURE_SAMPLER_UNIFORM);

    if (mbSurfaceTexture) {
      //激活纹理单元0
      GLES20.glActiveTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
      //绑定外部纹理到纹理单元0
      GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, OESTextureId);
    } else {
      GLES20.glActiveTexture(GLES11.GL_TEXTURE0);
      GLES20.glBindTexture(GLES11.GL_TEXTURE_2D, OESTextureId);
    }
    //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
    GLES20.glUniform1i(uTextureSamplerLocation, 0);
    //将纹理矩阵传给片段着色器
    GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

    //将顶点和纹理坐标传给顶点着色器
    if (mBuffer != null) {
      // 顶点着色器的顶点坐标，从位置0开始读取
      mBuffer.position(0);
      //使能顶点属性
      GLES20.glEnableVertexAttribArray(aPositionLocation);
      //顶点坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
      GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 16, mBuffer);
      // 顶点着色器的纹理坐标，纹理坐标从位置2开始读取
      mBuffer.position(2);
      //使能顶点属性
      GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
      //纹理坐标每次读取两个顶点值，之后间隔16（每行4个值 * 4个字节）的字节继续读取两个顶点值
      GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, mBuffer);
    }
    //绘制两个三角形（6个顶点）
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
  }

  public FloatBuffer createBuffer(float[] vertexData) {
    FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    buffer.put(vertexData).position(0);
    return buffer;
  }

  //加载着色器，GL_VERTEX_SHADER代表生成顶点着色器，GL_FRAGMENT_SHADER代表生成片段着色器
  private int loadShader(int type, String shaderSource) {
    //创建Shader
    int shader = GLES20.glCreateShader(type);
    if (shader == 0) {
      throw new RuntimeException("Create Shader Failed!" + GLES20.glGetError());
    }
    //加载Shader代码
    GLES20.glShaderSource(shader, shaderSource);
    //编译Shader
    GLES20.glCompileShader(shader);
    return shader;
  }

  //将两个Shader链接至program中
  private int linkProgram(int verShader, int fragShader) {
    //创建program
    int program = GLES20.glCreateProgram();
    if (program == 0) {
      throw new RuntimeException("Create Program Failed!" + GLES20.glGetError());
    }
    //附着顶点和片元着色器
    GLES20.glAttachShader(program, verShader);
    GLES20.glAttachShader(program, fragShader);
    //链接program
    GLES20.glLinkProgram(program);
    //告诉OpenGL ES使用此program
    GLES20.glUseProgram(program);
    return program;
  }

  public static int createOESTextureObject() {
    int[] tex = new int[1];
    //生成一个纹理
    GLES20.glGenTextures(1, tex, 0);
    //将此纹理绑定到外部纹理上
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
    //设置纹理过滤参数
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    //解除纹理绑定
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    return tex[0];
  }
}

