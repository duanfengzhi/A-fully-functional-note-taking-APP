package com.hfut.cqyzs.memorandum.utils.tool

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.PointF
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi

class CircleMenu {


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    fun showSectorMenu(imageViews: ArrayList<ImageView>) {
        /***第一步，遍历所要展示的菜单ImageView */
        for (i in imageViews.indices) {
            val point = PointF()
            /***第二步，根据菜单个数计算每个菜单之间的间隔角度 */
            val space = 200 / (imageViews.size - 1)
            /**第三步，根据间隔角度计算出每个菜单相对于水平线起始位置的真实角度 */
            val distance = space * (i + 1)
            /**
             * 圆点坐标：(x0,y0)
             * 半径：r
             * 角度：a0
             * 则圆上任一点为：（x1,y1）
             * x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
             * y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
             */
            /**第四步，根据每个菜单真实角度计算其坐标值 */
            point.x = (-distance.toFloat())

            /**第五步，根据坐标执行位移动画 */
            /**
             * 第一个参数代表要操作的对象
             * 第二个参数代表要操作的对象的属性
             * 第三个参数代表要操作的对象的属性的起始值
             * 第四个参数代表要操作的对象的属性的终止值
             */
            val objectAnimatorX = ObjectAnimator.ofFloat(imageViews[i], "translationX", 0f, point.x)
//            val objectAnimatorY = ObjectAnimator.ofFloat(imageViews[i], "translationY", 0f, point.y)
            /**动画集合，用来编排动画 */
            val animatorSet = AnimatorSet()
            /**设置动画时长 */
            animatorSet.duration = 500
            /**设置同时播放x方向的位移动画和y方向的位移动画 */
//            animatorSet.play(objectAnimatorX).with(objectAnimatorY)
            animatorSet.play(objectAnimatorX)
            /**开始播放动画 */
            animatorSet.start()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    fun closeSectorMenu(imageViews: ArrayList<ImageView>) {
        for (i in imageViews.indices) {
            val point = PointF()
            val space = 200 / (imageViews.size - 1)
            val distance = space * (i + 1)
            point.x = (-distance.toFloat())

            val objectAnimatorX = ObjectAnimator.ofFloat(imageViews[i], "translationX", point.x, 0f)
            val animatorSet = AnimatorSet()
            animatorSet.duration = 500
            animatorSet.play(objectAnimatorX)
            animatorSet.start()
        }
    }


}