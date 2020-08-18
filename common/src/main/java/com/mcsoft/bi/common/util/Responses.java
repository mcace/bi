package com.mcsoft.bi.common.util;

import com.mcsoft.bi.common.model.response.Response;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
public class Responses {

    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public static Response<String> success() {
        return new Response<>("success");
    }

}
