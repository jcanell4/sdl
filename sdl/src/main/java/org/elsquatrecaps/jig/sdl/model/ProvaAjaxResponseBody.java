package org.elsquatrecaps.jig.sdl.model;

import java.util.List;

public class ProvaAjaxResponseBody {

    String msg;
    List<ProvaUser> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ProvaUser> getResult() {
        return result;
    }

    public void setResult(List<ProvaUser> result) {
        this.result = result;
    }

}
