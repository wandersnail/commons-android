package cn.wandersnail.commons.helper;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/6 16:08
 * author: zengfansheng
 */
public class RoundConfig {
    protected float cornerRadius;
    protected float leftTopRadiusX = -1;
    protected float leftTopRadiusY = -1;
    protected float rightTopRadiusX = -1;
    protected float rightTopRadiusY = -1;
    protected float leftBottomRadiusX = -1;
    protected float leftBottomRadiusY = -1;
    protected float rightBottomRadiusX = -1;
    protected float rightBottomRadiusY = -1;

    /**
     * @param cornerRadius 四个角的圆角，优先级最低
     */
    public void round(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    /**
     * 圆左上角
     */
    public void roundLeftTop(float radiiX, float radiiY) {
        leftTopRadiusX = radiiX;
        leftTopRadiusY = radiiY;
    }

    /**
     * 圆左下角
     */
    public void roundLeftBottom(float radiiX, float radiiY) {
        leftBottomRadiusX = radiiX;
        leftBottomRadiusY = radiiY;
    }

    /**
     * 圆右上角
     */
    public void roundRightTop(float radiiX, float radiiY) {
        rightTopRadiusX = radiiX;
        rightTopRadiusY = radiiY;
    }

    /**
     * 圆右下角
     */
    public void roundRightBottom(float radiiX, float radiiY) {
        rightBottomRadiusX = radiiX;
        rightBottomRadiusY = radiiY;
    }

    /**
     * @param leftTopRadius 左上是否圆角，优先级高于{@link #cornerRadius}
     */
    public void roundLeftTop(float leftTopRadius) {
        this.leftTopRadiusX = leftTopRadius;
        this.leftTopRadiusY = leftTopRadius;
    }

    /**
     * @param rightTopRadius    右上是否圆角，优先级高于{@link #cornerRadius}
     */
    public void roundRightTopRadius(float rightTopRadius) {
        this.rightTopRadiusX = rightTopRadius;
        this.rightTopRadiusY = rightTopRadius;
    }

    /**
     * @param leftBottomRadius  左下是否圆角，优先级高于{@link #cornerRadius}
     */
    public void roundLeftBottomRadius(float leftBottomRadius) {
        this.leftBottomRadiusX = leftBottomRadius;
        this.leftBottomRadiusY = leftBottomRadius;
    }

    /**
     * @param rightBottomRadius 右下是否圆角，优先级高于[cornerRadius]
     */
    public void roundRightBottomRadius(float rightBottomRadius) {
        this.rightBottomRadiusX = rightBottomRadius;
        this.rightBottomRadiusY = rightBottomRadius;
    }

    /**
     * @param leftTopRadius     左上是否圆角，优先级高于{@link #cornerRadius}
     * @param rightTopRadius    右上是否圆角，优先级高于{@link #cornerRadius}
     */
    public void round(float leftTopRadius, float rightTopRadius) {
        this.leftTopRadiusX = leftTopRadius;
        this.leftTopRadiusY = leftTopRadius;
        this.rightTopRadiusX = rightTopRadius;
        this.rightTopRadiusY = rightTopRadius;
    }

    /**
     * @param leftTopRadius     左上是否圆角，优先级高于{@link #cornerRadius}
     * @param rightTopRadius    右上是否圆角，优先级高于{@link #cornerRadius}
     * @param leftBottomRadius  左下是否圆角，优先级高于{@link #cornerRadius}
     */
    public void round(float leftTopRadius, float rightTopRadius, float leftBottomRadius) {
        this.leftTopRadiusX = leftTopRadius;
        this.leftTopRadiusY = leftTopRadius;
        this.rightTopRadiusX = rightTopRadius;
        this.rightTopRadiusY = rightTopRadius;
        this.leftBottomRadiusX = leftBottomRadius;
        this.leftBottomRadiusY = leftBottomRadius;
    }
    
    /**
     * @param leftTopRadius     左上是否圆角，优先级高于{@link #cornerRadius}
     * @param rightTopRadius    右上是否圆角，优先级高于{@link #cornerRadius}
     * @param leftBottomRadius  左下是否圆角，优先级高于{@link #cornerRadius}
     * @param rightBottomRadius 右下是否圆角，优先级高于{@link #cornerRadius}
     */
    public void round(float leftTopRadius, float rightTopRadius, float leftBottomRadius, float rightBottomRadius) {
        this.leftTopRadiusX = leftTopRadius;
        this.leftTopRadiusY = leftTopRadius;
        this.rightTopRadiusX = rightTopRadius;
        this.rightTopRadiusY = rightTopRadius;
        this.leftBottomRadiusX = leftBottomRadius;
        this.leftBottomRadiusY = leftBottomRadius;
        this.rightBottomRadiusX = rightBottomRadius;
        this.rightBottomRadiusY = rightBottomRadius;
    }

    @NonNull
    protected float[] getCornerRadii() {
        float[] arr = new float[8];
        arr[0] = leftTopRadiusX >= 0 ? leftTopRadiusX : cornerRadius;
        arr[1] = leftTopRadiusY >= 0 ? leftTopRadiusY : cornerRadius;
        arr[2] = rightTopRadiusX >= 0 ? rightTopRadiusX : cornerRadius;
        arr[3] = rightTopRadiusY >= 0 ? rightTopRadiusY : cornerRadius;
        arr[4] = rightBottomRadiusX >= 0 ? rightBottomRadiusX : cornerRadius;
        arr[5] = rightBottomRadiusY >= 0 ? rightBottomRadiusY : cornerRadius;
        arr[6] = leftBottomRadiusX >= 0 ? leftBottomRadiusX : cornerRadius;
        arr[7] = leftBottomRadiusY >= 0 ? leftBottomRadiusY : cornerRadius;
        return arr;
    }
}
