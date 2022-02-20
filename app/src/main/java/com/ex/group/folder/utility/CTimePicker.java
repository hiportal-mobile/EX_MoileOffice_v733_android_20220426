package com.ex.group.folder.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

public class CTimePicker extends TimePicker {

    public CTimePicker(Context context) {

        super(context);
        Create(context,null);
    }

    public CTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        Create(context,null);
    }

    public CTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Create(context,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Create(context,null);
    }

    private void Create(Context clsContext, AttributeSet attrs )
    {
        try
        {
            Class<?> clsParent = Class.forName( "com.android.internal.R$id" );
            NumberPicker clsAmPm = (NumberPicker)findViewById( clsParent.getField( "amPm" ).getInt( null ) );
            NumberPicker clsHour = (NumberPicker)findViewById( clsParent.getField( "hour" ).getInt( null ) );
            NumberPicker clsMin = (NumberPicker)findViewById( clsParent.getField( "minute" ).getInt( null ) );
            Class<?> clsNumberPicker = Class.forName( "android.widget.NumberPicker" );
            Field clsSelectionDivider = clsNumberPicker.getDeclaredField( "mSelectionDivider" );

            clsSelectionDivider.setAccessible( true );
            ColorDrawable clsDrawable = new ColorDrawable(Color.parseColor("#dddddd"));

            // 오전/오후, 시, 분 구분 라인의 색상을 변경한다.
            clsSelectionDivider.set( clsAmPm, clsDrawable );
            clsSelectionDivider.set( clsHour, clsDrawable );
            clsSelectionDivider.set( clsMin, clsDrawable );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }


}
