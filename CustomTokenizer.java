package com.example.final_pro;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.MultiTapKeyListener;
import android.text.method.TextKeyListener;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.MultiAutoCompleteTextView;

public class CustomTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private final String delimiter;

    public CustomTokenizer() {
        delimiter = " ";
    }

    public CustomTokenizer(String delimiter) {
        this.delimiter = delimiter;
    }
    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && !isDelimiter(text.charAt(i - 1))) {
            i--;
        }

        // handle cases where cursor is at the start of the text or right after a delimiter
        if (i < cursor && (i == 0 || isDelimiter(text.charAt(i - 1)))) {
            return i;
        }

        // handle cases where cursor is in the middle of a word
        while (i < cursor && isDelimiter(text.charAt(i))) {
            i++;
        }

        return i;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len && !isDelimiter(text.charAt(i))) {
            i++;
        }

        return i;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && isDelimiter(text.charAt(i - 1))) {
            i--;
        }

        if (i > 0 && isDelimiter(text.charAt(i - 1))) {
            return text;
        } else {
            if (text instanceof Editable) {
                return text + delimiter;
            } else {
                return text.toString() + delimiter;
            }
        }
    }

    private boolean isDelimiter(char c) {
        return delimiter.contains(String.valueOf(c));
    }
}
