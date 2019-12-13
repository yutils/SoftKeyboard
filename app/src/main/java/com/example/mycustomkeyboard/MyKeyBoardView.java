package com.example.mycustomkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 自定义键盘在布局文件中须确保在整体布局的底部，不要和输入框在相同的根布局内
 */
public class MyKeyBoardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    //数字键盘
    private Keyboard keyboard;
    //对应的输入框
    private EditText editText;
    //为防止自定义键盘覆盖输入框，根布局向上的移动高度
    private int height = 0;
    //输入框所在的根布局
    private ViewGroup root;
    //自定义软键盘所在的根布局
    private ViewGroup keyBoardRoot;
    //完成按钮
    private TextView complete;

    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 关联自定义键盘与输入框，以及输入框所在的根布局
     * 需要注意此方法需要在输入框的OnTouchListener中当MotionEvent为MotionEvent.ACTION_UP时调用，
     * 否则无法正确阻止系统软键盘的弹出
     * @param et 输入框
     * @param root 输入框所在的根布局
     */
    public void setAttachToEditText(EditText et, ViewGroup root, ViewGroup keyBoardRoot) {

        if (keyboard == null) {
            keyboard = new Keyboard(getContext(), R.xml.keyboard_random_num);
        }
        this.editText = et;
        this.root = root;
        this.keyBoardRoot = keyBoardRoot;
        complete = keyBoardRoot.findViewById(R.id.complete);
        complete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });
        editText.requestFocus();
        hideSystemSoftInput();
        showMyKeyBoard();
    }

    /**显示自定随机数键盘*/
    private void showMyKeyBoard() {

        randomKeyboardNumber();
        setKeyboard(keyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        showResize();
        keyBoardRoot.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        setOnKeyboardActionListener(this);
    }

    /**根据输入框的底部坐标与自定义键盘的顶部坐标之间的差值height，
     * 判断自定义键盘是否覆盖住了输入框，如果覆盖则使输入框所在的根布局移动height*/
    private void showResize() {

        root.post(new Runnable() {
            @Override
            public void run() {

                int[] pos = new int[2];
                //获取编辑框在整个屏幕中的坐标
                editText.getLocationOnScreen(pos);
                //编辑框的Bottom坐标和键盘Top坐标的差
                height = (pos[1] + editText.getHeight()) -
                        (getScreenHeight(getContext()) - keyBoardRoot.getHeight());
                if (height > 0) {
                    root.scrollBy(0, height + dp2px(getContext(), 16));
                }
            }
        });
    }

    /**自定义键盘隐藏时，判断输入框所在的根布局是否向上移动了height，如果移动了则需再移回来*/
    private void hideResize() {
        if (height > 0) {
            root.scrollBy(0, -(height + dp2px(getContext(), 16)));
        }
    }

    /**获取手机屏幕高度*/
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**将px转换成dp*/
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**打乱数字键盘顺序*/
    private void randomKeyboardNumber() {

        List<Keyboard.Key> keyList = keyboard.getKeys();
        // 查找出0-9的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isNumber(keyList.get(i))) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(),
                    temp.get(num).getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i)
                    .getCode();
        }
    }

    private class KeyModel {

        private int code;
        private String lable;

        public KeyModel(int code, String lable) {
            this.code = code;
            this.lable = lable;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getLable() {
            return lable;
        }

        public void setLable(String lable) {
            this.lable = lable;
        }
    }

    /**判断key是数字键还是完成键*/
    private boolean isNumber(Keyboard.Key key) {

        if (key.codes[0] < 0) {
            return false;
        }

        return true;
    }

    /**隐藏系统键盘*/
    private void hideSystemSoftInput() {

        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        Editable editable = editText.getText();
        //获取焦点光标的所在位置
        int start = editText.getSelectionStart();

        switch (primaryCode) {

            case Keyboard.KEYCODE_DELETE://删除
                if (editable != null && editable.length() > 0 && start > 0) {
                    editable.delete(start-1, start);
                }
                break;
            case Keyboard.KEYCODE_DONE://完成
                break;
            case Keyboard.KEYCODE_CANCEL://取消、隐藏
                hideKeyBoard();
                break;
            default://插入数字
                editable.insert(start, Character.toString((char)primaryCode));
        }
    }

    /**隐藏键盘*/
    private void hideKeyBoard() {

        if (getVisibility() == VISIBLE) {
            keyBoardRoot.setVisibility(GONE);
            setVisibility(GONE);
            hideResize();
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
