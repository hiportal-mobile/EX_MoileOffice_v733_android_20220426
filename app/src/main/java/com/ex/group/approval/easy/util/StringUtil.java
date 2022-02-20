package com.ex.group.approval.easy.util;

import java.util.ArrayList;

import com.ex.group.approval.easy.addressbook.data.GuntaeCdVo;

public class StringUtil {

    public static final boolean isEmptyString(String src) {
        if(src == null || src.length() == 0) {
            return true;
        } else {
            if(!"null".equals(src))
                return false;
            return true;
        }
    }

    public static final boolean isNotEmptyString(String src) {
        if(src == null || src.length() == 0) {
            return false;
        } else {
            if(!"null".equals(src))
                return true;
            return false;
        }
    }
    
    public static final boolean isNotEmptyString(ArrayList<GuntaeCdVo> vo) {
        if(vo == null || vo.size() == 0) {
            return false;
        } else {
             return true;
          
        }
    }



  

}
