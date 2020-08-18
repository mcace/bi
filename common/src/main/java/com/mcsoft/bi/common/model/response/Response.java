package com.mcsoft.bi.common.model.response;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
// @Data
// @AllArgsConstructor
public class Response<T> {

    private T data;

    public Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
