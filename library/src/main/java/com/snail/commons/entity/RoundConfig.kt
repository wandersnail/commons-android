package com.snail.commons.entity

/**
 *
 *
 * date: 2019/4/12 17:22
 * author: zengfansheng
 */
open class RoundConfig {
    protected var cornerRadius = 0f
    protected var leftTopRadiusX = -1f
    protected var leftTopRadiusY = -1f
    protected var rightTopRadiusX = -1f
    protected var rightTopRadiusY = -1f
    protected var leftBottomRadiusX = -1f
    protected var leftBottomRadiusY = -1f
    protected var rightBottomRadiusX = -1f
    protected var rightBottomRadiusY = -1f

    /**
     * @param cornerRadius 四个角的圆角，优先级最低
     */
    fun round(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
    }

    /**
     * 圆左上角
     */
    fun roundLeftTop(radiiX: Float, radiiY: Float) {
        leftTopRadiusX = radiiX
        leftTopRadiusY = radiiY
    }

    /**
     * 圆左下角
     */
    fun roundLeftBottom(radiiX: Float, radiiY: Float) {
        leftBottomRadiusX = radiiX
        leftBottomRadiusY = radiiY
    }

    /**
     * 圆右上角
     */
    fun roundRightTop(radiiX: Float, radiiY: Float) {
        rightTopRadiusX = radiiX
        rightTopRadiusY = radiiY
    }

    /**
     * 圆右下角
     */
    fun roundRightBottom(radiiX: Float, radiiY: Float) {
        rightBottomRadiusX = radiiX
        rightBottomRadiusY = radiiY
    }

    /**
     * @param leftTopRadius     左上是否圆角，优先级高于[cornerRadius]
     * @param rightTopRadius    右上是否圆角，优先级高于[cornerRadius]
     * @param leftBottomRadius  左下是否圆角，优先级高于[cornerRadius]
     * @param rightBottomRadius 右下是否圆角，优先级高于[cornerRadius]
     */
    fun round(leftTopRadius: Float = -1f, rightTopRadius: Float = -1f, leftBottomRadius: Float = -1f, rightBottomRadius: Float = -1f) {
        this.leftTopRadiusX = leftTopRadius
        this.leftTopRadiusY = leftTopRadius
        this.rightTopRadiusX = rightTopRadius
        this.rightTopRadiusY = rightTopRadius
        this.leftBottomRadiusX = leftBottomRadius
        this.leftBottomRadiusY = leftBottomRadius
        this.rightBottomRadiusX = rightBottomRadius
        this.rightBottomRadiusY = rightBottomRadius
    }

    protected fun getCornerRadii(): FloatArray {
        val arr = FloatArray(8)
        arr[0] = if (leftTopRadiusX >= 0) leftTopRadiusX else cornerRadius
        arr[1] = if (leftTopRadiusY >= 0) leftTopRadiusY else cornerRadius
        arr[2] = if (rightTopRadiusX >= 0) rightTopRadiusX else cornerRadius
        arr[3] = if (rightTopRadiusY >= 0) rightTopRadiusY else cornerRadius
        arr[4] = if (rightBottomRadiusX >= 0) rightBottomRadiusX else cornerRadius
        arr[5] = if (rightBottomRadiusY >= 0) rightBottomRadiusY else cornerRadius
        arr[6] = if (leftBottomRadiusX >= 0) leftBottomRadiusX else cornerRadius
        arr[7] = if (leftBottomRadiusY >= 0) leftBottomRadiusY else cornerRadius
        return arr
    }
}