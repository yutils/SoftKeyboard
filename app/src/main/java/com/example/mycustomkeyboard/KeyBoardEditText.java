package com.example.mycustomkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

public class KeyBoardEditText extends AppCompatEditText implements KeyboardView.OnKeyboardActionListener {

    /**
     * 数字键盘
     */
    private Keyboard keyboardNumber;
    /**
     * 字母键盘
     */
    private Keyboard keyboardLetter;
    private ViewGroup viewGroup;
    private KeyboardView keyboardView;

    /**
     * 是否发生键盘切换
     */
    private boolean changeLetter = false;
    /**
     * 是否为大写
     */
    private boolean isCapital = false;
    private int[] arrays = new int[]{Keyboard.KEYCODE_SHIFT, Keyboard.KEYCODE_MODE_CHANGE,
            Keyboard.KEYCODE_CANCEL, Keyboard.KEYCODE_DONE, Keyboard.KEYCODE_DELETE,
            Keyboard.KEYCODE_ALT, 32};
    private List<Integer> noLists = new ArrayList<>();
    private OnKeyboardStateChangeListener listener;

    public KeyBoardEditText(Context context) {
        super(context);
        initEditView();
    }

    public KeyBoardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditView();
    }

    public KeyBoardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditView();
    }

    /**
     * 初始化数字和字母键盘
     */
    private void initEditView() {
        keyboardNumber = new Keyboard(getContext(), R.xml.keyboard_num);
        keyboardLetter = new Keyboard(getContext(), R.xml.keyboard_letter);

        for (int i = 0; i < arrays.length; i++) {
            noLists.add(arrays[i]);
        }
    }

    /**
     * 设置软键盘刚弹出的时候显示字母键盘还是数字键盘
     *
     * @param vg           包裹KeyboardView的ViewGroup
     * @param kv           KeyboardView
     * @param keyboard_num 是否显示数字键盘
     */
    public void setKeyboardType(ViewGroup vg, KeyboardView kv, boolean keyboard_num) {

        viewGroup = vg;
        keyboardView = kv;
        if (keyboard_num) {
            keyboardView.setKeyboard(keyboardNumber);
            changeLetter = false;
        } else {
            keyboardView.setKeyboard(keyboardLetter);
            changeLetter = true;
        }

        //显示预览
        keyboardView.setPreviewEnabled(true);
        //为KeyboardView设置按键监听
        keyboardView.setOnKeyboardActionListener(this);
    }

    public void setOnKeyBoardStateChangeListener(OnKeyboardStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPress(int primaryCode) {

        canShowPreview(primaryCode);
    }

    /**
     * 判断是否需要预览Key
     *
     * @param primaryCode keyCode
     */
    private void canShowPreview(int primaryCode) {

        if (noLists.contains(primaryCode)) {
            keyboardView.setPreviewEnabled(false);
        } else {
            keyboardView.setPreviewEnabled(true);
        }
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        Editable editable = getText();
        int start = getSelectionStart();
        switch (primaryCode) {

            case Keyboard.KEYCODE_DELETE://删除
                if (editable != null && editable.length() > 0 && start > 0) {
                    editable.delete(start - 1, start);
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE://字母键盘与数字键盘切换
                changeKeyBoard(!changeLetter);
                break;
            case Keyboard.KEYCODE_DONE://完成
                keyboardView.setVisibility(View.GONE);
                viewGroup.setVisibility(GONE);
                if (listener != null) {
                    listener.hide();
                }
                break;
            case Keyboard.KEYCODE_SHIFT://大小写切换
                changeCapital(!isCapital);
                keyboardView.setKeyboard(keyboardLetter);
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    /**
     * 切换键盘大小写
     */
    private void changeCapital(boolean b) {

        isCapital = b;
        List<Keyboard.Key> lists = keyboardLetter.getKeys();
        for (Keyboard.Key key : lists) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                } else {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            } else if (key.label != null && key.label.toString().equals("小写")) {
                key.label = "大写";
            } else if (key.label != null && key.label.toString().equals("大写")) {
                key.label = "小写";
            }
        }
    }

    /**
     * 判断此key是否正确，且存在 * * @param key * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }


    /**
     * 切换键盘类型
     */
    private void changeKeyBoard(boolean b) {
        changeLetter = b;
        if (changeLetter) {
            keyboardView.setKeyboard(keyboardLetter);
        } else {
            keyboardView.setKeyboard(keyboardNumber);
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

    public interface OnKeyboardStateChangeListener {
        void show();

        void hide();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSystemSoftInput();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (keyboardView.getVisibility() != VISIBLE) {
                keyboardView.setVisibility(VISIBLE);
                viewGroup.setVisibility(VISIBLE);
                if (listener != null)
                    listener.show();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && (viewGroup.getVisibility() != GONE
                || keyboardView.getVisibility() != GONE)) {
            viewGroup.setVisibility(GONE);
            keyboardView.setVisibility(GONE);
            if (listener != null)
                listener.hide();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        hideSystemSoftInput();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideSystemSoftInput();
    }

    /**
     * 隐藏系统软键盘
     */
    private void hideSystemSoftInput() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
