# ScreenAdaptionDemo
屏幕适配方案

使用方法：<br>
在Application的onCreate中引用：
//设计图标注的宽度
int designWidth = 750;
new RudenessScreenHelper(this, designWidth).activate();


******************************************************************
 * 
******************************************************************
 * FAQ:
 * 1、若存在webview导致适配失效的问题.
 * 可以先继承WebView并重写setOverScrollMode(int mode)方法，在方法中调用super之后调用一遍RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 2、若存在dialog中适配失效的问题.
 * 可以在dialog的oncreate中调用一遍RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 3、旋转屏幕之后适配失效.
 * 可以在onConfigurationChanged中调用RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 4、特定国产机型ROM中偶先fragment失效.
 * 可以在fragment的onCreateView中调用RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
******************************************************************
 * 
******************************************************************


原理：将pt这个单位重定义成我们所需要的相对长度单位

系统进行长度计算的方法：
<code>
public static float applyDimension(int unit, float value,DisplayMetrics metrics){
        switch (unit) {
        case COMPLEX_UNIT_PX:
            return value;
        case COMPLEX_UNIT_DIP:
            return value * metrics.density;
        case COMPLEX_UNIT_SP:
            return value * metrics.scaledDensity;
        case COMPLEX_UNIT_PT:
            return value * metrics.xdpi * (1.0f/72);
        case COMPLEX_UNIT_IN:
            return value * metrics.xdpi;
        case COMPLEX_UNIT_MM:
            return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
}
</code>
pt的原意是长度单位磅，根据当前屏幕与设计图尺寸将metrics.xdpi进行修改就可以实现将pt这个单位重定义成我们所需要的相对长度单位，
使修改之后计算出的1pt实际对应的px/屏幕宽度px=1px/设计图宽度px。













